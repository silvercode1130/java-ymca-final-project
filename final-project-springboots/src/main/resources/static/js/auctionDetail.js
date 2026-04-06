// 에러 메시지 표시 함수
function showFieldError(fieldName, message) {
    const existing = document.querySelector('[data-error="' + fieldName + '"]');
    if (existing) existing.remove();

    const input = document.getElementById(fieldName) || document.querySelector('[name="' + fieldName + '"]');
    if (!input) return;

    const errorEl = document.createElement('p');
    errorEl.setAttribute('data-error', fieldName);
    errorEl.style.cssText = 'color:red; font-size:12px; margin:4px 0 0;';
    errorEl.textContent = '⚠️ ' + message;
    input.parentNode.insertBefore(errorEl, input.nextSibling);

    setTimeout(() => errorEl.remove(), 3000);
}

// 입찰 이미지 미리보기
document.getElementById('bidImageFile').addEventListener('change', function () {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = e => {
            document.getElementById('bidPreviewImg').src = e.target.result;
            document.getElementById('bidPreviewBox').style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        document.getElementById('bidPreviewBox').style.display = 'none';
    }
});

// 입찰 폼 유효성 검사 - 가격 필드만 초기화
document.getElementById('bidForm').addEventListener('submit', function (e) {
    const bidPriceInput = document.getElementById('bidPrice');
    const bidPrice = Number(bidPriceInput.value);
    let hasError = false;

    if (!bidPriceInput.value || bidPrice <= 0) {
        showFieldError('bidPrice', '제안 가격은 0원보다 커야 합니다.');
        bidPriceInput.value = ''; // 가격 필드만 초기화
        bidPriceInput.focus();
        hasError = true;
    } else if (bidPrice % 1000 !== 0) {
        showFieldError('bidPrice', '1000원 단위로 입력해주세요.');
        bidPriceInput.value = ''; // 가격 필드만 초기화
        bidPriceInput.focus();
        hasError = true;
    }

    if (hasError) e.preventDefault();
});