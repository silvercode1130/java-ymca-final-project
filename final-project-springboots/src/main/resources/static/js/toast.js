// 토스트 메시지 표시 함수
function showToast(message, type) {
    // 기존 토스트 제거
    const existing = document.getElementById('toast-box');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'toast-box';

    // 타입별 색상
    const colors = {
        success: { bg: '#2ecc71', icon: '✅' },
        error:   { bg: '#e74c3c', icon: '⚠️' },
        info:    { bg: '#3498db', icon: 'ℹ️' }
    };
    const style = colors[type] || colors.info;

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
        z-index: 9999;
        display: flex;
        align-items: center;
        gap: 8px;
        max-width: 320px;
        word-break: keep-all;
        opacity: 0;
        transform: translateY(20px);
        transition: opacity 0.3s ease, transform 0.3s ease;
    `;

    toast.innerHTML = `<span>${style.icon}</span><span>${message}</span>`;
    document.body.appendChild(toast);

    // 올라오는 애니메이션
    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    // 3초 후 사라짐
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// 페이지 로드 시 Thymeleaf 플래시 메시지 자동 감지
window.addEventListener('pageshow', function (event) {

    // 뒤로가기(bfcache 복원)면 토스트 띄우지 않음
    if (event.persisted) return;

    // 이미 이 페이지에서 토스트를 띄웠으면 다시 안 띄움
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