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
    
    review_writer_idx BIGINT        NOT NULL COMMENT 'FK → writer',
    review_target_idx BIGINT        NOT NULL COMMENT 'FK → target',
    auction_idx       BIGINT        NOT NULL COMMENT '거래 기준',

    review_title      VARCHAR(200)  DEFAULT NULL,
    review_content    TEXT          DEFAULT NULL,
    review_star       INT           NOT NULL COMMENT '1~5점',
    
    review_regdate    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    review_is_deleted CHAR(1)       NOT NULL DEFAULT 'N',
    review_deldate    DATETIME      DEFAULT NULL,  

    PRIMARY KEY (review_idx),

    KEY idx_review_writer (review_writer_idx),
    KEY idx_review_target (review_target_idx),

    UNIQUE KEY ux_review_unique (review_writer_idx, review_target_idx, auction_idx),

    CONSTRAINT ck_review_star CHECK (review_star BETWEEN 1 AND 5),
    CONSTRAINT ck_review_self CHECK (review_writer_idx <> review_target_idx),
    CONSTRAINT ck_review_is_deleted CHECK (review_is_deleted IN ('Y','N')),

    CONSTRAINT fk_review_writer 
        FOREIGN KEY (review_writer_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_target 
        FOREIGN KEY (review_target_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_auction
        FOREIGN KEY (auction_idx) REFERENCES auction(auction_idx)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 테이블';
   
   
   
   
   
   