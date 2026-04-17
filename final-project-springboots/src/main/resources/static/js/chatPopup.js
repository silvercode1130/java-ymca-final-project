(function () {
    const state = {
        isOpen: false,
        selectedRoomId: null,
        selectedRoomName: "",
        rooms: [],
        stompClient: null,
        roomSubscription: null,
        pollTimer: null,
    };

    function el(id) {
        return document.getElementById(id);
    }

    function isMobile() {
        return window.innerWidth < 768;
    }

    function getLoginContext() {
        const context = el("chat-context");
        if (!context) {
            return { isLogin: false, memIdx: 0 };
        }
        return {
            isLogin: context.dataset.login === "Y",
            memIdx: Number(context.dataset.loginMemIdx || 0),
        };
    }

    function updateScrollButtons() {
        const topBtn = el("scrollTopBtn");
        const bottomBtn = el("scrollBottomBtn");
        if (!topBtn || !bottomBtn) {
            return;
        }

        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        const scrollHeight = document.documentElement.scrollHeight;
        const clientHeight = document.documentElement.clientHeight;

        topBtn.disabled = scrollTop <= 0;
        bottomBtn.disabled = scrollTop + clientHeight >= scrollHeight - 2;
    }

    function scrollToTop() {
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    function scrollToBottom() {
        window.scrollTo({ top: document.documentElement.scrollHeight, behavior: "smooth" });
    }

    function updateUnreadBadge(count) {
        const badge = el("chatUnreadBadge");
        if (!badge) {
            return;
        }

        const value = Number(count || 0);
        if (value > 0) {
            badge.classList.remove("hidden");
            badge.textContent = value > 99 ? "99+" : String(value);
        } else {
            badge.classList.add("hidden");
            badge.textContent = "0";
        }
    }

    function requestJson(url, options) {
        return fetch(url, options).then((res) => {
            if (res.status === 401) {
                throw new Error("NOT_LOGIN");
            }
            if (!res.ok) {
                return res.text().then((text) => {
                    throw new Error(text || "REQUEST_FAILED");
                });
            }
            return res.json();
        });
    }

    function fetchUnreadCount() {
        const context = getLoginContext();
        if (!context.isLogin) {
            updateUnreadBadge(0);
            return Promise.resolve(0);
        }

        return requestJson("/api/chats/unread-count")
            .then((data) => {
                updateUnreadBadge(data.unreadCount || 0);
                return data.unreadCount || 0;
            })
            .catch(() => {
                updateUnreadBadge(0);
                return 0;
            });
    }

    function escapeHtml(text) {
        const div = document.createElement("div");
        div.textContent = text == null ? "" : String(text);
        return div.innerHTML;
    }

    function normalizeRoom(room) {
        return {
            chatroomIdx: Number(room.chatroomIdx),
            opponentName: room.opponentName || "상대방",
            lastMessage: room.lastMessage || "",
            unreadCount: Number(room.unreadCount || 0),
        };
    }

    function renderRoomList() {
        const roomList = el("chatRoomList");
        if (!roomList) {
            return;
        }

        if (!state.rooms.length) {
            roomList.innerHTML = '<div class="h-full flex items-center justify-center text-sm text-gray-400">채팅방이 없습니다.</div>';
            return;
        }

        roomList.innerHTML = state.rooms.map((room) => {
            const activeClass = room.chatroomIdx === state.selectedRoomId
                ? "bg-gray-100 border-l-4 border-l-[#7CBD00]"
                : "hover:bg-gray-50";
            const dot = room.unreadCount > 0
                ? '<span class="w-2 h-2 bg-red-500 rounded-full"></span>'
                : "";

            return (
                '<button type="button" data-room-id="' + room.chatroomIdx + '" '
                + 'class="chat-room-item w-full text-left px-3 py-3 border-b border-gray-100 flex items-center justify-between gap-2 ' + activeClass + '">'
                + '<div class="min-w-0">'
                + '<p class="text-sm font-semibold text-gray-800 truncate">' + escapeHtml(room.opponentName) + '</p>'
                + '<p class="text-xs text-gray-500 truncate">' + escapeHtml(room.lastMessage || "대화를 시작해보세요") + '</p>'
                + '</div>'
                + '<div class="shrink-0">' + dot + '</div>'
                + '</button>'
            );
        }).join("");

        roomList.querySelectorAll(".chat-room-item").forEach((button) => {
            button.addEventListener("click", () => {
                const roomId = Number(button.dataset.roomId);
                selectRoom(roomId);
            });
        });
    }

    function syncMobilePanels() {
        const roomPane = el("chatRoomPane");
        const conversationPane = el("chatConversationPane");
        const backBtn = el("chatMobileBackBtn");
        const placeholder = el("chatPlaceholder");
        const conversationWrap = el("chatConversationWrap");

        if (!roomPane || !conversationPane || !backBtn || !placeholder || !conversationWrap) {
            return;
        }

        if (!isMobile()) {
            roomPane.classList.remove("hidden");
            conversationPane.classList.remove("hidden");
            backBtn.classList.add("hidden");
            backBtn.classList.remove("flex");

            if (state.selectedRoomId) {
                placeholder.classList.add("hidden");
                conversationWrap.classList.remove("hidden");
                conversationWrap.classList.add("flex");
            } else {
                placeholder.classList.remove("hidden");
                conversationWrap.classList.add("hidden");
                conversationWrap.classList.remove("flex");
            }
            return;
        }

        if (state.selectedRoomId) {
            roomPane.classList.add("hidden");
            conversationPane.classList.remove("hidden");
            backBtn.classList.remove("hidden");
            backBtn.classList.add("flex");
            placeholder.classList.add("hidden");
            conversationWrap.classList.remove("hidden");
            conversationWrap.classList.add("flex");
        } else {
            roomPane.classList.remove("hidden");
            conversationPane.classList.add("hidden");
            backBtn.classList.add("hidden");
            backBtn.classList.remove("flex");
        }
    }

    function scrollMessagesToBottom() {
        const messageList = el("chatMessageList");
        if (!messageList) {
            return;
        }
        messageList.scrollTop = messageList.scrollHeight;
    }

    function renderMessages(messages) {
        const messageList = el("chatMessageList");
        if (!messageList) {
            return;
        }

        const context = getLoginContext();
        messageList.innerHTML = (messages || []).map((msg) => {
            const mine = Number(msg.senderIdx) === Number(context.memIdx);
            const rowClass = mine ? "justify-end" : "justify-start";
            const bubbleClass = mine
                ? "bg-[#222222] text-white"
                : "bg-white text-gray-800 border border-gray-200";

            return (
                '<div class="w-full flex ' + rowClass + '">'
                + '<div class="max-w-[82%] rounded-2xl px-3 py-2 text-sm leading-relaxed ' + bubbleClass + '">'
                + escapeHtml(msg.messageContent)
                + '</div>'
                + '</div>'
            );
        }).join("");

        scrollMessagesToBottom();
    }

    function appendMessage(message) {
        const messageList = el("chatMessageList");
        if (!messageList) {
            return;
        }

        const context = getLoginContext();
        const mine = Number(message.senderIdx) === Number(context.memIdx);
        const wrapper = document.createElement("div");
        wrapper.className = "w-full flex " + (mine ? "justify-end" : "justify-start");

        const bubble = document.createElement("div");
        bubble.className = "max-w-[82%] rounded-2xl px-3 py-2 text-sm leading-relaxed "
            + (mine ? "bg-[#222222] text-white" : "bg-white text-gray-800 border border-gray-200");
        bubble.textContent = message.messageContent || "";

        wrapper.appendChild(bubble);
        messageList.appendChild(wrapper);
        scrollMessagesToBottom();
    }

    function markSelectedRoomRead() {
        if (!state.selectedRoomId) {
            return Promise.resolve();
        }
        return requestJson("/api/chats/rooms/" + state.selectedRoomId + "/read", {
            method: "POST",
        }).catch(() => {});
    }

    function loadMessages(roomId) {
        return requestJson("/api/chats/rooms/" + roomId + "/messages")
            .then((data) => {
                renderMessages(data.messages || []);
            })
            .catch((error) => {
                if (error.message !== "NOT_LOGIN") {
                    renderMessages([]);
                }
            });
    }

    function subscribeRoom(roomId) {
        if (!state.stompClient || !state.stompClient.connected) {
            return;
        }
        if (state.roomSubscription) {
            state.roomSubscription.unsubscribe();
        }

        state.roomSubscription = state.stompClient.subscribe("/topic/chatroom/" + roomId, (frame) => {
            const message = JSON.parse(frame.body);
            if (Number(message.chatroomIdx) === Number(state.selectedRoomId)) {
                appendMessage(message);
                markSelectedRoomRead().then(() => {
                    fetchRooms();
                    fetchUnreadCount();
                });
            } else {
                fetchRooms();
                fetchUnreadCount();
            }
        });
    }

    function selectRoom(roomId) {
        const room = state.rooms.find((item) => Number(item.chatroomIdx) === Number(roomId));
        if (!room) {
            return;
        }

        state.selectedRoomId = room.chatroomIdx;
        state.selectedRoomName = room.opponentName;

        const roomTitle = el("chatRoomTitle");
        if (roomTitle) {
            roomTitle.textContent = room.opponentName;
        }

        renderRoomList();
        syncMobilePanels();
        loadMessages(room.chatroomIdx).then(() => {
            subscribeRoom(room.chatroomIdx);
            fetchRooms();
            fetchUnreadCount();
        });
    }

    function fetchRooms(preferredRoomId) {
        return requestJson("/api/chats/rooms")
            .then((data) => {
                state.rooms = (data.rooms || []).map(normalizeRoom);
                renderRoomList();

                if (!state.rooms.length) {
                    state.selectedRoomId = null;
                    syncMobilePanels();
                    return;
                }

                if (preferredRoomId) {
                    selectRoom(preferredRoomId);
                    return;
                }

                const stillExists = state.rooms.some((room) => room.chatroomIdx === state.selectedRoomId);
                if (stillExists) {
                    syncMobilePanels();
                    return;
                }

                if (!isMobile()) {
                    selectRoom(state.rooms[0].chatroomIdx);
                } else {
                    state.selectedRoomId = null;
                    syncMobilePanels();
                }
            })
            .catch(() => {
                state.rooms = [];
                renderRoomList();
                state.selectedRoomId = null;
                syncMobilePanels();
            });
    }

    function connectWebSocket() {
        if (state.stompClient && state.stompClient.connected) {
            return;
        }
        if (typeof SockJS === "undefined" || typeof Stomp === "undefined") {
            return;
        }

        const socket = new SockJS("/ws-chat");
        state.stompClient = Stomp.over(socket);
        state.stompClient.debug = null;

        state.stompClient.connect({}, () => {
            if (state.selectedRoomId) {
                subscribeRoom(state.selectedRoomId);
            }
        }, () => {});
    }

    function sendMessage(content) {
        if (!state.selectedRoomId || !content.trim()) {
            return;
        }

        const context = getLoginContext();
        const payload = {
            chatroomIdx: state.selectedRoomId,
            senderIdx: context.memIdx,
            messageContent: content.trim(),
        };

        if (state.stompClient && state.stompClient.connected) {
            state.stompClient.send("/app/chat/send", {}, JSON.stringify(payload));
            return;
        }

        requestJson("/api/chats/rooms/" + state.selectedRoomId + "/messages", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ messageContent: content.trim() }),
        }).then((data) => {
            appendMessage(data);
            fetchRooms();
            fetchUnreadCount();
        }).catch(() => {});
    }

    function bindCompose() {
        const form = el("chatComposeForm");
        const input = el("chatMessageInput");
        if (!form || !input) {
            return;
        }

        form.addEventListener("submit", (event) => {
            event.preventDefault();
            const content = input.value;
            if (!content || !content.trim()) {
                return;
            }
            sendMessage(content);
            input.value = "";
            input.focus();
        });

        input.addEventListener("keydown", (event) => {
            if (event.key === "Enter" && !event.shiftKey) {
                event.preventDefault();
                form.requestSubmit();
            }
        });
    }

    function openChatModal(preferredRoomId) {
        const context = getLoginContext();
        if (!context.isLogin) {
            location.href = "/members/login?redirect=" + encodeURIComponent(location.pathname + location.search);
            return;
        }

        state.isOpen = true;
        const root = el("chatModalRoot");
        if (!root) {
            return;
        }

        root.classList.remove("hidden");
        syncMobilePanels();
        connectWebSocket();

        fetchRooms(preferredRoomId).then(() => {
            fetchUnreadCount();
            if (window.feather) {
                window.feather.replace();
            }
        });

        if (state.pollTimer) {
            clearInterval(state.pollTimer);
        }
        state.pollTimer = window.setInterval(() => {
            if (!state.isOpen) {
                return;
            }
            fetchRooms();
            fetchUnreadCount();
        }, 10000);
    }

    function closeChatModal() {
        state.isOpen = false;
        const root = el("chatModalRoot");
        if (root) {
            root.classList.add("hidden");
        }

        state.selectedRoomId = null;
        syncMobilePanels();

        if (state.roomSubscription) {
            state.roomSubscription.unsubscribe();
            state.roomSubscription = null;
        }

        if (state.pollTimer) {
            clearInterval(state.pollTimer);
            state.pollTimer = null;
        }

        fetchUnreadCount();
    }

    function bindModalButtons() {
        const closeBtn = el("chatModalCloseBtn");
        const backBtn = el("chatMobileBackBtn");

        if (closeBtn) {
            closeBtn.addEventListener("click", closeChatModal);
        }

        if (backBtn) {
            backBtn.addEventListener("click", () => {
                state.selectedRoomId = null;
                syncMobilePanels();
            });
        }

        window.addEventListener("resize", syncMobilePanels);
    }

    window.openChatModal = function () {
        openChatModal();
    };

    window.openChatPopup = function (chatroomIdx) {
        if (!chatroomIdx) {
            openChatModal();
            return;
        }
        openChatModal(Number(chatroomIdx));
    };

    window.scrollToTop = scrollToTop;
    window.scrollToBottom = scrollToBottom;

    document.addEventListener("DOMContentLoaded", () => {
        bindCompose();
        bindModalButtons();
        updateScrollButtons();
        fetchUnreadCount();

        window.addEventListener("scroll", updateScrollButtons);

        if (window.feather) {
            window.feather.replace();
        }
    });
})();