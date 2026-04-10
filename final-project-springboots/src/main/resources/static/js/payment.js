/**
 * 결제 통합 모듈 (payment.js)
 */

const clientKey = "test_ck_GePWvyJnrKPx4nM5Kogb8gLzN97E";
const tossPayments = TossPayments(clientKey);

const PaymentModule = {
  // [함수 1] 토스 결제창 호출
  request: function (data) {
    tossPayments
      .requestPayment(data.payMethod || "카드", {
        amount: data.amount,
        orderId: data.orderId, // "ORD_bidIdx_timestamp" 형식
        orderName: data.orderName,
        customerName: data.customerName,
        successUrl: window.location.origin + "/payment/success",
        failUrl: window.location.origin + "/payment/fail",
      })
      .catch((err) => alert("결제 요청 에러: " + err.message));
  },

  // [함수 2] 서버 최종 승인 요청
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

  // [함수 3] 결제 버튼 자동 연결
  initButtons: function () {
    document.querySelectorAll(".pay-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const bidIdx = btn.getAttribute("data-idx");
        const payData = {
          amount: btn.getAttribute("data-price"),
          orderId: "ORD_" + bidIdx + "_" + new Date().getTime(), // bidIdx 포함
          orderName: btn.getAttribute("data-title"),
          customerName: loginMemName,
          payMethod: "카드",
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

// 결제 성공 페이지 처리
document.addEventListener("DOMContentLoaded", function () {
  if (window.location.pathname.includes("/payment/success")) {
    const urlParams = new URLSearchParams(window.location.search);
    const paymentKey = urlParams.get("paymentKey");
    const orderId = urlParams.get("orderId");
    const amount = urlParams.get("amount");

    // orderId에서 bidIdx 추출 (ORD_bidIdx_timestamp)
    const bidIdx = orderId.split("_")[1];

    PaymentModule.confirm({
      paymentKey: paymentKey,
      orderId: orderId,
      amount: amount,
      bidIdx: bidIdx, // orderId에서 추출
      buyerName: loginMemName, // 세션에서 주입된 값
      buyerTel: loginMemTel,
      buyerAddr: loginMemAddr,
      buyerZipcode: loginMemZipcode,
    })
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

// 결제 실패 페이지 처리
if (window.location.pathname.includes("/payment/fail")) {
  const urlParams = new URLSearchParams(window.location.search);
  const msg = urlParams.get("message");

  const failElem = document.getElementById("fail-reason");
  if (failElem) failElem.innerText = "사유: " + (msg || "알 수 없는 오류");
}
