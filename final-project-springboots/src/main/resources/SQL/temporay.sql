/*
임시 db (ctrl + enter 하지마세요!)
문제 없다면 db 확정할게요! 
-> 지금은 테스트 중! 
*/ 

/* ==========================================
   5. 리뷰 (review)
   ========================================== */
CREATE TABLE review (
    review_idx        BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',

    buyer_idx         BIGINT        NOT NULL COMMENT '구매자 (FK → member.mem_idx)',
    bidder_idx        BIGINT        NOT NULL COMMENT '판매자/입찰자 (FK → member.mem_idx)',
    
    auction_idx       BIGINT        NOT NULL COMMENT 'FK → auction', 
    bid_idx           BIGINT        NOT NULL COMMENT 'FK → bid (선택된 입찰)',

    review_title      VARCHAR(200)  NOT NULL COMMENT '리뷰 제목',
    review_content    TEXT          NOT NULL COMMENT '리뷰 내용 (20자 이상 - db말고 컨트롤러에서 조건줄 것!)',
    review_star       INT           NOT NULL COMMENT '1~5점',

    review_regdate    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',

    review_is_deleted CHAR(1)       NOT NULL DEFAULT 'N', 
    review_deldate    DATETIME      DEFAULT NULL,

    PRIMARY KEY (review_idx),

    KEY idx_review_buyer (buyer_idx),
    KEY idx_review_bidder (bidder_idx),
    KEY idx_review_auction (auction_idx),
    KEY idx_review_bid (bid_idx),

    -- 한 거래당 하나씩 리뷰 남기기! (같은 사람에게 여러번 리뷰 남기는건 가능 / 같은 거래에 중복 리뷰 불가능)
    UNIQUE KEY ux_review_unique (bid_idx), 

    -- 별점 제한 (1 ~ 5)
    CONSTRAINT ck_review_star CHECK (review_star BETWEEN 1 AND 5),

    -- 삭제 여부 (기본은 N)
    CONSTRAINT ck_review_is_deleted CHECK (review_is_deleted IN ('Y','N')),

    -- FK
    CONSTRAINT fk_review_buyer
        FOREIGN KEY (buyer_idx) REFERENCES member(mem_idx) ON DELETE CASCADE,

    CONSTRAINT fk_review_bidder
        FOREIGN KEY (bidder_idx) REFERENCES member(mem_idx) ON DELETE CASCADE,

    CONSTRAINT fk_review_auction
        FOREIGN KEY (auction_idx) REFERENCES auction(auction_idx) ON DELETE CASCADE,

    CONSTRAINT fk_review_bid
        FOREIGN KEY (bid_idx) REFERENCES bid(bid_idx) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 테이블';
   
   
   
INSERT INTO review (
    buyer_idx, bidder_idx, auction_idx, bid_idx,
    review_title, review_content, review_star
) VALUES
-- auction 4 (낙찰 성공 케이스)
(10, 10, 4, 9,
 '빠른 거래 감사합니다!',
 '제품 상태도 설명과 동일했고 거래도 빠르게 진행되어 매우 만족합니다. 다음에도 거래하고 싶어요!',
 5),
-- auction 5 (낙찰 성공 케이스)
(8, 8, 5, 12,
 '좋은 상품 감사합니다',
 '수영 세트 상태도 좋고 가격도 합리적이어서 만족스러운 거래였습니다. 배송도 빨랐어요.',
 4),
-- auction 1 (진행중이지만 테스트용)
(1, 9, 1, 1,
 '괜찮은 제안이었어요',
 '가격과 상품 상태 모두 괜찮았고 응답도 빨라서 좋았습니다. 다음에도 기회되면 거래하고 싶어요.',
 4),
-- auction 2
(2, 4, 2, 4,
 '가성비 좋네요',
 '중고지만 상태 괜찮고 가격도 합리적이라 만족합니다. 잘 쓰겠습니다!',
 4),
-- auction 3
(9, 1, 3, 7,
 '추천합니다',
 '헬멧 상태 좋고 설명 그대로였습니다. 안전하게 잘 쓰겠습니다.',
 5);
   
   