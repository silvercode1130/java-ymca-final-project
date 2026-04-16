/**
 * PickQ 통합 토스트 시스템
 * - 일반 토스트: 우측 하단 (초록/빨강/파랑)
 * - 채팅 토스트: 우측 상단 (카카오톡 스타일, 클릭 시 채팅창 이동)
 * 두 종류가 겹치지 않도록 위치를 분리함
 */

// ── 일반 토스트 (우측 하단) ──────────────────────────────
function showToast(message, type, onClick) {
    const colors = {
        success: { bg: '#2ecc71', icon: '✅' },
        error:   { bg: '#e74c3c', icon: '⚠️' },
        info:    { bg: '#3498db', icon: 'ℹ️'  }
    };
    const style = colors[type] || colors.info;

    const toast = document.createElement('div');
    toast.className = 'pickq-toast';
    toast.style.cssText = `
        position: fixed;
        bottom: 30px;
        right: 30px;
        background: ${style.bg};
        color: white;
        padding: 14px 20px;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 500;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        z-index: 9998;
        display: flex;
        align-items: center;
        gap: 8px;
        max-width: 320px;
        word-break: keep-all;
        opacity: 0;
        transform: translateY(20px);
        transition: opacity 0.3s ease, transform 0.3s ease;
        cursor: ${onClick ? 'pointer' : 'default'};
    `;
    toast.innerHTML = `<span>${style.icon}</span><span>${message}</span>`;
    if (onClick) toast.addEventListener('click', onClick);
    document.body.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    const timer = setTimeout(() => _removeToast(toast), 4000);
    toast.addEventListener('click', () => clearTimeout(timer));
}

function _removeToast(el) {
    el.style.opacity = '0';
    el.style.transform = 'translateY(20px)';
    setTimeout(() => el.remove(), 300);
}


// ── 채팅 토스트 (우측 상단, 카카오 스타일) ──────────────
let _chatToastQueue = [];
let _chatToastShowing = false;

function showChatToast(senderName, message, chatroomIdx) {
    _chatToastQueue.push({ senderName, message, chatroomIdx });
    if (!_chatToastShowing) _showNextChatToast();
}

function _showNextChatToast() {
    if (_chatToastQueue.length === 0) { _chatToastShowing = false; return; }
    _chatToastShowing = true;

    const { senderName, message, chatroomIdx } = _chatToastQueue.shift();

    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 80px;
        right: 20px;
        background: #FEE500;
        color: #3C1E1E;
        padding: 12px 16px;
        border-radius: 12px;
        font-size: 13px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.18);
        z-index: 9999;
        display: flex;
        flex-direction: column;
        gap: 4px;
        max-width: 280px;
        word-break: keep-all;
        cursor: pointer;
        opacity: 0;
        transform: translateX(40px);
        transition: opacity 0.3s ease, transform 0.3s ease;
        border-left: 4px solid #3C1E1E;
    `;
    toast.innerHTML = `
        <div style="display:flex;align-items:center;gap:6px;">
            <span style="font-size:16px;">💬</span>
            <strong style="font-size:13px;">${senderName}</strong>
            <span style="font-size:10px;margin-left:auto;opacity:0.6;">채팅</span>
        </div>
        <div style="font-size:12px;opacity:0.85;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:240px;">
            ${message}
        </div>
    `;

    // 클릭 시 채팅창으로 이동
    toast.addEventListener('click', () => {
        _removeToast(toast);
        if (chatroomIdx) {
            window.open(
                '/chats/' + chatroomIdx,
                'chat_' + chatroomIdx,
                'width=420,height=600,menubar=no,toolbar=no,location=no,resizable=yes,scrollbars=yes'
            );
        }
    });

    document.body.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateX(0)';
    });

    setTimeout(() => {
        _removeToast(toast);
        setTimeout(_showNextChatToast, 300);
    }, 4000);
}


// ── 페이지 로드 시 Thymeleaf 플래시 메시지 자동 감지 ──────
window.addEventListener('pageshow', function (event) {
    if (event.persisted) return;

    const toastShownKey = 'toastShown_' + location.pathname;
    if (sessionStorage.getItem(toastShownKey)) {
        sessionStorage.removeItem(toastShownKey);
        return;
    }

    const successEl = document.getElementById('toast-success');
    const errorEl   = document.getElementById('toast-error');
    const bidErrEl  = document.getElementById('toast-bid-error');

    if (successEl && successEl.dataset.msg) {
        showToast(successEl.dataset.msg, 'success');
        sessionStorage.setItem(toastShownKey, '1');
    }
    if (errorEl && errorEl.dataset.msg) {
        showToast(errorEl.dataset.msg, 'error');
        sessionStorage.setItem(toastShownKey, '1');
    }
    if (bidErrEl && bidErrEl.dataset.msg) {
        showToast(bidErrEl.dataset.msg, 'error');
        sessionStorage.setItem(toastShownKey, '1');
    }
});