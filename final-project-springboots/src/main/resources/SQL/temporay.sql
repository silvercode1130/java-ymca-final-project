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
   
   
   
   
   
   