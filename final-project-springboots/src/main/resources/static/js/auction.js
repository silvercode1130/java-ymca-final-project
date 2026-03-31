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