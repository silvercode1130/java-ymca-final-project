-- 역할 코드
CREATE TABLE role (
    role_idx      NUMBER        PRIMARY KEY,
    role_name     VARCHAR2(20)  NOT NULL          -- 'USER', 'ADMIN', 'SELLER' 등
);

-- 회원 등급 (크레딧 등급 등)
CREATE TABLE grade (
    grade_idx             NUMBER        PRIMARY KEY,
    grade_name            VARCHAR2(20)  NOT NULL,  -- BASIC / SILVER / GOLD / VIP
    grade_credit_rate     NUMBER(5,2)   NOT NULL   -- 0.00 ~ 1.00 (가중치/적립률 등)
);

-- 스포츠 대분류
CREATE TABLE sports_category (
    sports_category_idx   NUMBER        PRIMARY KEY,
    sports_category_code  VARCHAR2(50)  UNIQUE NOT NULL,  -- BALL / RACKET / HEALTH / OUTDOOR / WATER / RUN_GOLF
    sports_category_name  VARCHAR2(100) NOT NULL          -- 구기 종목, 라켓 스포츠 등
);

-- 경매 상태
CREATE TABLE auction_status (
    auction_status_idx    NUMBER        PRIMARY KEY,
    auction_status_name   VARCHAR2(20)  NOT NULL          -- OPEN / CLOSED_WAITING / COMPLETED / FAILED
);

-- 입찰 상태
CREATE TABLE bid_status (
    bid_status_idx        NUMBER        PRIMARY KEY,
    bid_status_name       VARCHAR2(20)  NOT NULL          -- NORMAL / WON / LOST / CANCELED
);

-- 게시판 타입 (스포츠 종목별 게시판 등)
CREATE TABLE board_type (
    board_type_idx     NUMBER         PRIMARY KEY,
    board_type_code    VARCHAR2(50)   UNIQUE NOT NULL,    -- GOLF_BOARD, SKI_BOARD ...
    board_type_name    VARCHAR2(100)  NOT NULL,           -- 골프 게시판 등
    board_can_comment  CHAR(1)        DEFAULT 'Y' CHECK (board_can_comment IN ('Y','N')),
    board_min_role     NUMBER         NOT NULL            -- 최소 권한 (role.role_idx)
);

-- 회원
CREATE TABLE member (
    mem_idx        NUMBER         PRIMARY KEY,
    mem_id         VARCHAR2(50)   UNIQUE NOT NULL,     -- 로그인 ID
    mem_pwd        VARCHAR2(255)  NOT NULL,            -- 비밀번호 해시
    mem_name       VARCHAR2(50),
    mem_tel        VARCHAR2(20),
    mem_email      VARCHAR2(100),
    mem_ip         VARCHAR2(100),
    mem_role_idx   NUMBER         NOT NULL,
    mem_grade_idx  NUMBER         NOT NULL,
    mem_bday       DATE,
    mem_regdate    DATE           DEFAULT SYSDATE,
    mem_is_deleted CHAR(1)        DEFAULT 'N',
    mem_deldate    DATE,
    CONSTRAINT fk_member_role
        FOREIGN KEY (mem_role_idx)  REFERENCES role(role_idx),
    CONSTRAINT fk_member_grade
        FOREIGN KEY (mem_grade_idx) REFERENCES grade(grade_idx),
    CONSTRAINT ck_mem_is_deleted
        CHECK (mem_is_deleted IN ('Y','N'))
);

-- 회원 프로필
CREATE TABLE member_profile (
    mem_idx       NUMBER        PRIMARY KEY,          -- member PK = FK
    mem_nickname  VARCHAR2(50)  UNIQUE,
    mem_intro     VARCHAR2(255),
    mem_img       VARCHAR2(255),
    CONSTRAINT fk_profile_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE
);

-- 회원 주소 (여러 개 등록 가능)
CREATE TABLE member_addr (
    addr_idx        NUMBER        PRIMARY KEY,
    mem_idx         NUMBER        NOT NULL,
    mem_zipcode     VARCHAR2(10),
    mem_addr        VARCHAR2(255),
    mem_addr_detail VARCHAR2(255),
    is_primary      CHAR(1)       DEFAULT 'N',
    CONSTRAINT fk_addr_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE,
    CONSTRAINT ck_addr_is_primary
        CHECK (is_primary IN ('Y','N'))
);

-- 상품 (경매에 사용될 스포츠 용품)
CREATE TABLE item (
    item_idx             NUMBER          PRIMARY KEY,
    item_name            VARCHAR2(200)   NOT NULL,
    item_category_idx    NUMBER          NOT NULL,      -- FK → sports_category
    item_origin_price    NUMBER(10),
    item_brand           VARCHAR2(50),
    item_thumbnail_img   VARCHAR2(255),
    item_detail_img      VARCHAR2(255),
    item_regdate         DATE            DEFAULT SYSDATE,
    item_is_deleted      CHAR(1)         DEFAULT 'N',
    CONSTRAINT fk_item_category
        FOREIGN KEY (item_category_idx) REFERENCES sports_category(sports_category_idx),
    CONSTRAINT ck_item_is_deleted
        CHECK (item_is_deleted IN ('Y','N'))
);

-- 경매 방 / 구매 요청
CREATE TABLE auction (
    auction_idx              NUMBER          PRIMARY KEY,
    buyer_idx                NUMBER          NOT NULL,  -- FK → member
    item_idx                 NUMBER          NOT NULL,  -- FK → item
    auction_title            VARCHAR2(200)   NOT NULL,
    auction_desc             VARCHAR2(1000),
    auction_target_price     NUMBER(10),                -- 희망가(최대)
    auction_start_at         DATE            NOT NULL,
    auction_end_at           DATE            NOT NULL,  -- 입찰 마감
    auction_decision_deadline DATE,                     -- 낙찰 결정 마감 (선택)
    auction_winning_bid_idx  NUMBER,                    -- FK → bid (nullable)
    auction_status_idx       NUMBER          NOT NULL,  -- FK → auction_status
    auction_regdate          DATE            DEFAULT SYSDATE,
    CONSTRAINT fk_auction_buyer
        FOREIGN KEY (buyer_idx)          REFERENCES member(mem_idx),
    CONSTRAINT fk_auction_item
        FOREIGN KEY (item_idx)           REFERENCES item(item_idx),
    CONSTRAINT fk_auction_status
        FOREIGN KEY (auction_status_idx) REFERENCES auction_status(auction_status_idx)
    -- auction_winning_bid_idx는 bid 테이블 생성 후 FK 추가 가능
);

-- 입찰
CREATE TABLE bid (
    bid_idx       NUMBER          PRIMARY KEY,
    auction_idx   NUMBER          NOT NULL,
    bidder_idx    NUMBER          NOT NULL,
    bid_price     NUMBER(10)      NOT NULL,
    bid_quantity  NUMBER          DEFAULT 1,
    bid_message   VARCHAR2(1000),
    bid_status_idx NUMBER         NOT NULL,            -- FK → bid_status
    bid_regdate   DATE            DEFAULT SYSDATE,
    CONSTRAINT fk_bid_auction
        FOREIGN KEY (auction_idx) REFERENCES auction(auction_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_bid_member
        FOREIGN KEY (bidder_idx)  REFERENCES member(mem_idx),
    CONSTRAINT fk_bid_status
        FOREIGN KEY (bid_status_idx) REFERENCES bid_status(bid_status_idx)
);

-- 경매 낙찰 FK (순환 참조 분리용, 필요 시 별도 ALTER로 추가)
ALTER TABLE auction
    ADD CONSTRAINT fk_auction_winning_bid
        FOREIGN KEY (auction_winning_bid_idx) REFERENCES bid(bid_idx);

-- 게시판
CREATE TABLE board (
    board_idx        NUMBER          PRIMARY KEY,
    mem_idx          NUMBER          NOT NULL,
    board_title      VARCHAR2(200)   NOT NULL,
    board_content    CLOB,
    board_ip         VARCHAR2(40),
    board_thumbnail  VARCHAR2(200),
    board_readhit    NUMBER          DEFAULT 0,
    board_type_idx   NUMBER          NOT NULL,        -- FK → board_type
    board_regdate    DATE            DEFAULT SYSDATE,
    board_moddate    DATE,
    board_is_deleted CHAR(1)         DEFAULT 'N',
    board_deldate    DATE,
    CONSTRAINT fk_board_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_board_type
        FOREIGN KEY (board_type_idx) REFERENCES board_type(board_type_idx),
    CONSTRAINT ck_board_is_deleted
        CHECK (board_is_deleted IN ('Y','N'))
);

-- 댓글
CREATE TABLE reply (
    reply_idx        NUMBER          PRIMARY KEY,
    board_idx        NUMBER          NOT NULL,
    mem_idx          NUMBER          NOT NULL,
    reply_content    VARCHAR2(1000),
    reply_ip         VARCHAR2(40),
    reply_regdate    DATE            DEFAULT SYSDATE,
    reply_moddate    DATE,
    reply_is_deleted CHAR(1)         DEFAULT 'N',
    reply_deldate    DATE,
    CONSTRAINT fk_reply_board
        FOREIGN KEY (board_idx) REFERENCES board(board_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_reply_member
        FOREIGN KEY (mem_idx)  REFERENCES member(mem_idx),
    CONSTRAINT ck_reply_is_deleted
        CHECK (reply_is_deleted IN ('Y','N'))
);
