(function () {
    function byId(id) {
        return document.getElementById(id);
    }

    function escapeHtml(value) {
        var div = document.createElement('div');
        div.textContent = value == null ? '' : String(value);
        return div.innerHTML;
    }

    function formatDate(value) {
        if (!value) {
            return '';
        }

        var parsed = new Date(value);
        if (isNaN(parsed.getTime())) {
            return String(value).replace('T', ' ').slice(0, 16);
        }

        var y = parsed.getFullYear();
        var m = String(parsed.getMonth() + 1).padStart(2, '0');
        var d = String(parsed.getDate()).padStart(2, '0');
        var h = String(parsed.getHours()).padStart(2, '0');
        var min = String(parsed.getMinutes()).padStart(2, '0');
        return y + '-' + m + '-' + d + ' ' + h + ':' + min;
    }

    function setUnreadDotVisible(visible) {
        var dot = byId('notification-bell-dot');
        if (!dot) {
            return;
        }

        if (visible) {
            dot.classList.remove('hidden');
        } else {
            dot.classList.add('hidden');
        }
    }

    function ensureListWrap() {
        var wrap = byId('notification-list-wrap');
        if (wrap) {
            return wrap;
        }

        var section = byId('notification-list-section');
        if (!section) {
            return null;
        }

        var created = document.createElement('div');
        created.id = 'notification-list-wrap';
        created.className = 'space-y-4';
        section.appendChild(created);
        return created;
    }

    function hideEmptyStateIfNeeded() {
        var empty = byId('notification-empty-state');
        if (empty) {
            empty.remove();
        }
    }

    function buildNotificationItemHtml(notification) {
        var isRead = notification && notification.isRead === 'Y';
        var cardClass = isRead
            ? 'bg-white rounded-lg border border-gray-200 transition-all hover:shadow-md'
            : 'bg-[#f5f5f5] rounded-lg border border-gray-300 transition-all hover:shadow-md';
        var iconWrapClass = isRead
            ? 'flex-shrink-0 w-11 h-11 rounded-full bg-gray-100 flex items-center justify-center'
            : 'flex-shrink-0 w-11 h-11 rounded-full bg-gray-200 flex items-center justify-center';
        var iconClass = isRead ? 'text-gray-400' : 'text-gray-700 text-lg';
        var titleClass = isRead ? 'text-base font-semibold text-gray-700' : 'text-base font-semibold text-gray-900';
        var messageClass = isRead ? 'text-sm mb-2 text-gray-500' : 'text-sm mb-2 text-gray-700';
        var targetUrl = notification && notification.targetUrl ? notification.targetUrl : null;

        return ''
            + '<div class="notification-item ' + cardClass + '">'
            + '  <div class="p-4 sm:p-5">'
            + '    <div class="flex items-start gap-4">'
            + '      <div class="' + iconWrapClass + '">'
            + '        <span class="' + iconClass + '">⏰</span>'
            + '      </div>'
            + '      <div class="flex-1 min-w-0">'
            + '        <div class="flex items-start justify-between gap-2 mb-2">'
            + '          <h3 class="' + titleClass + '">' + escapeHtml(notification.notificationTitle || '새 알림') + '</h3>'
            + '        </div>'
            + '        <p class="' + messageClass + '">' + escapeHtml(notification.notificationMessage || '') + '</p>'
            + '        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">'
            + '          <div>'
            + (targetUrl
            + '            <a href="' + escapeHtml(targetUrl) + '" class="inline-block text-sm text-[#7CBD00] hover:text-[#6BAD00] font-medium hover:underline">관련 페이지로 이동 →</a>'
            : '            <span class="text-xs text-gray-400">이동 가능한 페이지가 없습니다</span>')
            + '          </div>'
            + '          <p class="text-xs text-gray-400">' + escapeHtml(formatDate(notification.createdAt)) + '</p>'
            + '        </div>'
            + '      </div>'
            + '    </div>'
            + '  </div>'
            + '</div>';
    }

    function prependNotificationIfOnPage(notification) {
        var pageRoot = byId('notification-page-root');
        if (!pageRoot) {
            return;
        }

        var filter = pageRoot.getAttribute('data-filter') || 'all';
        if (filter === 'unread' && notification && notification.isRead === 'Y') {
            return;
        }

        hideEmptyStateIfNeeded();
        var wrap = ensureListWrap();
        if (!wrap) {
            return;
        }

        wrap.insertAdjacentHTML('afterbegin', buildNotificationItemHtml(notification || {}));
    }

    function fetchUnreadState() {
        fetch('/notifications/has-unread', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.text();
            })
            .then(function (text) {
                setUnreadDotVisible(text === 'Y');
            })
            .catch(function () {
                // 초기 unread 상태 조회 실패는 연결 기능에 영향 없으므로 무시
            });
    }

    function connectNotificationSocket(memIdx) {
        if (!window.SockJS || !window.Stomp) {
            return;
        }

        var socket = new SockJS('/ws-notification');
        var stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({}, function () {
            stompClient.subscribe('/topic/notifications/' + memIdx, function (frame) {
                var payload = {};

                try {
                    payload = JSON.parse(frame.body || '{}');
                } catch (e) {
                    payload = {};
                }

                console.log('[notification] received:', payload);
                setUnreadDotVisible(true);
                prependNotificationIfOnPage(payload);
            });

            stompClient.send('/app/notifications/ping', {}, '{}');
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        var context = byId('notification-context');
        if (!context) {
            return;
        }

        var memIdx = context.getAttribute('data-mem-idx');
        if (!memIdx) {
            return;
        }

        fetchUnreadState();
        connectNotificationSocket(memIdx);
    });
})();
