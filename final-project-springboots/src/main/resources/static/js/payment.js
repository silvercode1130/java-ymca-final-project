/**
 * 결제 통합 모듈 (payment-master.js)
 */

// 1. 토스페이먼츠 초기화 (키는 본인의 것으로 유지)
const clientKey = "test_ck_GePWvyJnrKPx4nM5Kogb8gLzN97E";
const tossPayments = TossPayments(clientKey);

const PaymentModule = {
  /**
   * [함수 1] 토스 결제창 호출 (직접 호출용)
   */
  request: function (data) {
    // 성공 URL에 비즈니스 데이터(bidIdx 등)를 쿼리 스트링으로 포함
    const url = new URL(data.successUrl);
    url.searchParams.append("bidIdx", data.bidIdx);
    url.searchParams.append("memIdx", data.memIdx);
    url.searchParams.append("buyerName", data.buyerName || "구매자");
    url.searchParams.append("buyerTel", data.buyerTel || "010-0000-0000");
    url.searchParams.append("buyerAddr", data.buyerAddr || "주소없음");
    url.searchParams.append("buyerZipcode", data.buyerZipcode || "00000");

    tossPayments
      .requestPayment(data.payMethod || "카드", {
        amount: data.amount,
        orderId: data.orderId,
        orderName: data.orderName,
        customerName: data.customerName,
        successUrl: url.toString(),
        failUrl: data.failUrl,
      })
      .catch((err) => alert("결제 요청 에러: " + err.message));
  },

  /**
   * [함수 2] 서버 최종 승인 요청 (Fetch API)
   */
  confirm: function (requestData) {
    return fetch("/api/payment/confirm", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestData),
    }).then((res) => {
      if (!res.ok)
        return res.text().then((text) => {
          throw new Error(text);
        });
      return res.json();
    });
  },

  /**
   * [함수 3] 페이지 내 결제 버튼(.pay-btn) 자동 연결
   */
  initButtons: function () {
    document.querySelectorAll(".pay-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const payData = {
          amount: btn.getAttribute("data-price"),
          orderId:
            "ORD_" + btn.getAttribute("data-idx") + "_" + new Date().getTime(),
          orderName: btn.getAttribute("data-title"),
          customerName: "홍길동", // 실제론 전역변수나 세션에서 주입
          bidIdx: btn.getAttribute("data-idx"),
          memIdx: 1, // 실제론 전역변수나 세션에서 주입
          successUrl: window.location.origin + "/payment/success",
          failUrl: window.location.origin + "/payment/fail",
        };
        this.request(payData);
      });
    });
  },
};

// 페이지 로드 시 버튼 자동 활성화
document.addEventListener("DOMContentLoaded", () =>
  PaymentModule.initButtons(),
);

document.addEventListener("DOMContentLoaded", function () {
  // 현재 페이지 URL이 성공 페이지인지 확인
  if (window.location.pathname.includes("/payment/success")) {
    const urlParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlParams.entries());

    // 이미 정의된 confirm 함수 호출
    PaymentModule.confirm(params)
      .then((data) => {
        alert("결제가 완료되었습니다!");
        location.href = "/auctions";
      })
      .catch((err) => {
        alert("결제 승인 실패: " + err.message);
        location.href = "/payment/fail";
      });
  }
});

// payment-master.js 하단에 추가 가능
if (window.location.pathname.includes("/payment/fail")) {
  const urlParams = new URLSearchParams(window.location.search);
  const msg = urlParams.get("message");

  // 화면에 'fail-reason'이라는 아이디를 가진 태그가 있다면 메시지 출력
  const failElem = document.getElementById("fail-reason");
  if (failElem) failElem.innerText = "사유: " + (msg || "알 수 없는 오류");
}
