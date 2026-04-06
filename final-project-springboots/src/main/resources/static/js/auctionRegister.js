const endAtInput = document.getElementById('auctionEndAt');
const deadlineInput = document.getElementById('auctionDecisionDeadline');

function toLocalDateTimeString(date) {
    const pad = n => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

// 페이지 열릴 때 현재 시간 이후만 선택 가능하도록 min 설정
endAtInput.min = toLocalDateTimeString(new Date());
deadlineInput.min = toLocalDateTimeString(new Date());

// 1분마다 min 갱신 (오래 켜두면 과거가 될 수 있어서)
setInterval(() => {
    endAtInput.min = toLocalDateTimeString(new Date());
}, 60000);

// 입찰 마감일 바뀌면 결정 마감일 min/max 자동 조정
endAtInput.addEventListener('change', function () {
    if (this.value) {
        const endDate = new Date(this.value);
        deadlineInput.min = toLocalDateTimeString(endDate);
        const maxDate = new Date(endDate);
        maxDate.setDate(maxDate.getDate() + 3);
        deadlineInput.max = toLocalDateTimeString(maxDate);
        // 기존 결정마감일이 범위 벗어나면 초기화
        if (deadlineInput.value && new Date(deadlineInput.value) <= endDate) {
            deadlineInput.value = '';
        }
    }
});

// 이미지 미리보기
document.getElementById('thumbnailFile').addEventListener('change', function () {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = e => {
            document.getElementById('previewImg').src = e.target.result;
            document.getElementById('previewBox').style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        document.getElementById('previewBox').style.display = 'none';
    }
});

// 에러 메시지 표시 함수 - 해당 필드 아래에만 표시, 3초 후 사라짐
function showFieldError(fieldName, message) {
    // 기존 에러 제거
    const existing = document.querySelector('[data-error="' + fieldName + '"]');
    if (existing) existing.remove();

    const input = document.querySelector('[name="' + fieldName + '"], #' + fieldName);
    if (!input) return;

    const errorEl = document.createElement('p');
    errorEl.setAttribute('data-error', fieldName);
    errorEl.style.cssText = 'color:red; font-size:12px; margin:4px 0 0;';
    errorEl.textContent = '⚠️ ' + message;
    input.parentNode.insertBefore(errorEl, input.nextSibling);

    setTimeout(() => errorEl.remove(), 3000);
}

// 폼 제출 유효성 검사 - 문제 있는 필드만 초기화
document.getElementById('registerForm').addEventListener('submit', function (e) {
    const targetPriceInput = document.querySelector('[name="auctionTargetPrice"]');
    const targetPrice = Number(targetPriceInput.value);
    const now = new Date();
    let hasError = false;

    // 희망 최대가 검사
    if (!targetPriceInput.value || targetPrice <= 0) {
        showFieldError('auctionTargetPrice', '희망 최대가는 0원보다 커야 합니다.');
        targetPriceInput.value = '';
        targetPriceInput.focus();
        hasError = true;
    } else if (targetPrice % 1000 !== 0) {
        showFieldError('auctionTargetPrice', '1000원 단위로 입력해주세요.');
        targetPriceInput.value = '';
        targetPriceInput.focus();
        hasError = true;
    }

    // 입찰 마감일 검사
    if (!endAtInput.value || new Date(endAtInput.value) <= now) {
        showFieldError('auctionEndAt', '현재 시간 이후로 선택해주세요.');
        endAtInput.value = ''; // 날짜만 초기화
        hasError = true;
    }

    // 결정 마감일 검사
    if (!deadlineInput.value) {
        showFieldError('auctionDecisionDeadline', '결정 마감일을 선택해주세요.');
        hasError = true;
    } else if (endAtInput.value && new Date(deadlineInput.value) <= new Date(endAtInput.value)) {
        showFieldError('auctionDecisionDeadline', '입찰 마감일 이후여야 합니다.');
        deadlineInput.value = ''; // 결정마감일만 초기화
        hasError = true;
    }

    if (hasError) e.preventDefault();
});