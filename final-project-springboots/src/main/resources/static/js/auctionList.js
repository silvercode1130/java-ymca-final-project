function updateTimer() {
    const timerElements = document.querySelectorAll('.timer-display');
    const now = new Date();
    let needsReload = false;

    timerElements.forEach(el => {
        const status   = parseInt(el.getAttribute('data-status'));
        const endTime  = new Date(el.getAttribute('data-end'));
        const deadline = new Date(el.getAttribute('data-deadline'));

        // 진행중(1): 입찰마감 카운트
        if (status === 1) {
            const diff = endTime - now;
            if (diff <= 0) {
                el.innerText = '';
                needsReload  = true;
                return;
            }
            const d = Math.floor(diff / 86400000);
            const h = Math.floor((diff / 3600000) % 24);
            const m = Math.floor((diff / 60000)   % 60);
            const s = Math.floor((diff / 1000)    % 60);
            let str = '입찰 ';
            if (d > 0) str += d + '일 ';
            str += pad(h) + ':' + pad(m) + ':' + pad(s);
            if (el.innerText !== str) el.innerText = str;
        }
        // 결정대기(2): 결정마감 카운트
        else if (status === 2) {
            const diff = deadline - now;
            if (diff <= 0) {
                el.innerText = '';
                needsReload  = true;
                return;
            }
            const d = Math.floor(diff / 86400000);
            const h = Math.floor((diff / 3600000) % 24);
            const m = Math.floor((diff / 60000)   % 60);
            const s = Math.floor((diff / 1000)    % 60);
            let str = '결정 ';
            if (d > 0) str += d + '일 ';
            str += pad(h) + ':' + pad(m) + ':' + pad(s);
            if (el.innerText !== str) el.innerText = str;
        }
        // 그 외는 표시 없음
        else {
            el.innerText = '';
        }
    });

    // 마감된 항목이 있으면 1회 자동 리로드 (중복 방지)
    if (needsReload && !window._reloadScheduled) {
        window._reloadScheduled = true;
        setTimeout(() => location.reload(), 1500);
    }
}

function pad(n) { return String(n).padStart(2, '0'); }

if (window.listInterval) clearInterval(window.listInterval);
window.listInterval = setInterval(updateTimer, 1000);
updateTimer();