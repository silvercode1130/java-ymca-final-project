// static/js/payment.js

const IMP = window.IMP;
IMP.init("imp13114020"); // 실제 식별코드로 변경

/**
 * 에스크로 결제 요청 함수
 * @param {Object} data - 결제에 필요한 정보 (auction, bid, member, address 데이터)
 */
function requestEscrowPay(data) {
  const merchantUid = "pay_" + new Date().getTime();

  IMP.request_pay(
    {
      pg: "html5_inicis",
      pay_method: "card",
      escrow: true,
      merchant_uid: merchantUid,
      name: data.auctionTitle,
      amount: data.bidPrice,
      buyer_name: data.memName,
      buyer_tel: data.memTel,
      buyer_addr: data.memAddr + " " + data.memAddrDetail,
      buyer_postcode: data.memZipcode,
    },
    function (rsp) {
      if (rsp.success) {
        // 서버 검증 요청
        fetch("/payments/verify", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            impUid: rsp.imp_uid,
            merchantUid: rsp.merchant_uid,
            auctionIdx: data.auctionIdx,
            bidIdx: data.bidIdx,
            payAmount: rsp.paid_amount,
            buyerName: rsp.buyer_name,
            buyerTel: rsp.buyer_tel,
            buyerAddr: rsp.buyer_addr,
            buyerZipcode: rsp.buyer_postcode,
          }),
        })
          .then((res) => res.json())
          .then((result) => {
            if (result.status === "success") {
              alert("에스크로 결제가 완료되었습니다.");
              location.href = "/mypage/auctions";
            } else {
              alert("검증 실패: " + result.message);
            }
          });
      } else {
        alert("결제 실패: " + rsp.error_msg);
      }
    },
  );
}
