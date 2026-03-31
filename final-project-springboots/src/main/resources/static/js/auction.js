document.addEventListener("DOMContentLoaded", function() {
    const submitBtn = document.getElementById("submitBtn");
    const bidForm = document.getElementById("bidForm");
    
    if (!submitBtn || !bidForm) return;

    // 데이터 속성 읽기
    const isLogin = submitBtn.getAttribute("data-is-login") === "true";

    function checkInputs() {
        // 로그인 안 했으면 무조건 리턴
        if (!isLogin) return; 

        const price = bidForm.querySelector('input[name="bidPrice"]').value.trim();
        const count = bidForm.querySelector('input[name="bidQuantity"]').value.trim(); // VO랑 이름 맞춤
        const message = bidForm.querySelector('textarea[name="bidMessage"]').value.trim();

        if (price !== "" && count !== "" && message !== "") {
            submitBtn.disabled = false;
            submitBtn.style.backgroundColor = "#adff2f";
            submitBtn.style.cursor = "pointer";
        } else {
            submitBtn.disabled = true;
            submitBtn.style.backgroundColor = "#ccc";
        }
    }

    bidForm.addEventListener("input", checkInputs);
    checkInputs(); // 시작하자마자 상태 체크
});

const endAtInput = document.getElementById('auctionEndAt');
const deadlineInput = document.getElementById('auctionDecisionDeadline');

function toLocalDateTimeString(date) {
    const pad = n => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

// 입찰 마감일: 현재 이후만 선택 가능
endAtInput.min = toLocalDateTimeString(new Date());

// 입찰 마감일 바뀌면 결정 마감일 min/max 자동 조정
endAtInput.addEventListener('change', function () {
    if (this.value) {
        const endDate = new Date(this.value);

        // 결정 마감일 min = 입찰 마감일 직후
        deadlineInput.min = toLocalDateTimeString(endDate);

        // 결정 마감일 max = 입찰 마감일 + 3일
        const maxDate = new Date(endDate);
        maxDate.setDate(maxDate.getDate() + 3);
        deadlineInput.max = toLocalDateTimeString(maxDate);

        // 기존 값이 범위 벗어나면 초기화
        deadlineInput.value = '';
    }
});

// 폼 제출 전 유효성 검사
document.getElementById('registerForm').addEventListener('submit', function(e) {
    const targetPrice = Number(document.querySelector('[name="auctionTargetPrice"]').value);
    const endAt = endAtInput.value;
    const deadline = deadlineInput.value;

    if (targetPrice <= 0) {
        alert('희망 최대가는 0원보다 커야 합니다.');
        e.preventDefault(); return;
    }
    if (targetPrice % 100 !== 0) {
        alert('희망 최대가는 100원 단위로 입력해주세요.');
        e.preventDefault(); return;
    }
    if (!endAt) {
        alert('입찰 마감일을 선택해주세요.');
        e.preventDefault(); return;
    }
    if (!deadline) {
        alert('결정 마감일을 선택해주세요.');
        e.preventDefault(); return;
    }
    if (new Date(deadline) <= new Date(endAt)) {
        alert('결정 마감일은 입찰 마감일 이후여야 합니다.');
        e.preventDefault(); return;
    }
});

