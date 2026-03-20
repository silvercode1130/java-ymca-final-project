/* ==========================================
   RESET (DROP TABLES)
   ========================================== */

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS board_type;

DROP TABLE IF EXISTS bid;
DROP TABLE IF EXISTS bid_status;

DROP TABLE IF EXISTS auction;
DROP TABLE IF EXISTS auction_status;

DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS item_category;

DROP TABLE IF EXISTS member_addr;
DROP TABLE IF EXISTS member_profile;
DROP TABLE IF EXISTS member;

DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS role;

SET FOREIGN_KEY_CHECKS = 1;



/* ==========================================
   0. 코드 테이블
   ========================================== */

-- 0-1) ROLE (권한 코드: 1 USER, 2 ADMIN)
CREATE TABLE role (
    role_idx   BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
    role_name  VARCHAR(20)  NOT NULL COMMENT '권한 등급 명칭 (USER/ADMIN)',
    PRIMARY KEY (role_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='권한 코드 테이블';

-- 0-2) GRADE (회원 등급 코드)
CREATE TABLE grade (
    grade_idx     INT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    grade_name    VARCHAR(20)  NOT NULL COMMENT '등급명 (basic/silver/gold/vip)',
    grade_credit  DOUBLE       NOT NULL COMMENT '신용도 기준 (평균 별점 등)',
    PRIMARY KEY (grade_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 등급 코드 테이블';

-- 0-3) ITEM_CATEGORY (아이템 카테고리 코드)
CREATE TABLE item_category (
    item_category_idx   INT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    item_category_code  VARCHAR(50)  NOT NULL COMMENT '카테고리 코드 (BALL, RACKET 등)',
    item_category_name  VARCHAR(100) NOT NULL COMMENT '카테고리 이름 (구기 종목 등)',
    PRIMARY KEY (item_category_idx),
    UNIQUE KEY ux_item_category_code (item_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='아이템 카테고리 코드 테이블';

-- 0-4) AUCTION_STATUS (경매 상태 코드)
CREATE TABLE auction_status (
    auction_status_idx   INT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    auction_status_code  VARCHAR(50)  NOT NULL COMMENT '상태 코드 (open/closed/failed/canceled)',
    auction_status_name  VARCHAR(50)  NOT NULL COMMENT '한글 상태명 (진행중/마감/유찰/취소 등)',
    PRIMARY KEY (auction_status_idx),
    UNIQUE KEY ux_auction_status_code (auction_status_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='경매 상태 코드 테이블';

-- 0-5) BID_STATUS (입찰 상태 코드)
CREATE TABLE bid_status (
    bid_status_idx   INT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    bid_status_code  VARCHAR(50)  NOT NULL COMMENT '상태 코드 (normal/won/lost/canceled)',
    bid_status_name  VARCHAR(50)  NOT NULL COMMENT '한글 상태명 (일반/낙찰 등)',
    PRIMARY KEY (bid_status_idx),
    UNIQUE KEY ux_bid_status_code (bid_status_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='입찰 상태 코드 테이블';

-- 0-6) BOARD_TYPE (게시판 타입 코드)
CREATE TABLE board_type (
    board_type_idx    INT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    board_type_code   VARCHAR(50)  NOT NULL COMMENT '게시판 코드 (GOLF_BOARD 등)',
    board_type_name   VARCHAR(100) NOT NULL COMMENT '게시판 이름',
    board_can_comment CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT 'Y / N 댓글 가능 여부',
    board_min_role    BIGINT       NOT NULL DEFAULT 1 COMMENT '최소 권한 (role_idx)',
    PRIMARY KEY (board_type_idx),
    UNIQUE KEY ux_board_type_code (board_type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시판 타입 코드 테이블';



/* ==========================================
   1. 회원 관련
   ========================================== */

-- 1-1) MEMBER (회원 기본 정보)
CREATE TABLE member (
    mem_idx		   BIGINT		 NOT NULL AUTO_INCREMENT COMMENT 'PK',
    mem_id         VARCHAR(50)   NOT NULL COMMENT '로그인 ID',
    mem_pwd        VARCHAR(255)  NOT NULL COMMENT '비밀번호 해시',
    mem_name       VARCHAR(50)   DEFAULT NULL COMMENT '성명',
    mem_tel        VARCHAR(20)   DEFAULT NULL COMMENT '전화번호',
    mem_email      VARCHAR(100)  DEFAULT NULL COMMENT '이메일',
    mem_ip         VARCHAR(100)  NOT NULL COMMENT 'IP 주소',
    mem_role_idx   BIGINT        NOT NULL COMMENT 'FK → role.role_idx (권한 등급)',
    mem_grade_idx  INT           NOT NULL COMMENT 'FK → grade.grade_idx (신용도 등급)',
    mem_bday       DATE          DEFAULT NULL COMMENT '생일',
    mem_regdate    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    mem_is_deleted CHAR(1)       NOT NULL DEFAULT 'N' COMMENT 'Y / N (삭제 여부)',
    mem_deldate    DATETIME      DEFAULT NULL COMMENT '탈퇴일',
    PRIMARY KEY (mem_idx),
    UNIQUE KEY ux_member_mem_id (mem_id),
    CONSTRAINT ck_member_is_deleted CHECK (mem_is_deleted IN ('Y','N')),
    CONSTRAINT fk_member_role  FOREIGN KEY (mem_role_idx)  REFERENCES role(role_idx),
    CONSTRAINT fk_member_grade FOREIGN KEY (mem_grade_idx) REFERENCES grade(grade_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 기본 정보 테이블';

-- 1-2) MEMBER_PROFILE (회원 프로필)
CREATE TABLE member_profile (
    mem_idx       BIGINT        NOT NULL COMMENT 'PK & FK → member.mem_idx',
    mem_nickname  VARCHAR(50)   DEFAULT NULL COMMENT '닉네임',
    mem_intro     VARCHAR(255)  DEFAULT NULL COMMENT '자기소개',
    mem_img       VARCHAR(255)  DEFAULT NULL COMMENT '프로필 이미지',
    PRIMARY KEY (mem_idx),
    CONSTRAINT fk_profile_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 프로필 테이블';

-- 1-3) MEMBER_ADDR (회원 배송지)
CREATE TABLE member_addr (
    addr_idx        BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
    mem_idx         BIGINT        NOT NULL COMMENT 'FK → member.mem_idx',
    mem_zipcode     VARCHAR(10)   DEFAULT NULL COMMENT '우편번호',
    mem_addr        VARCHAR(255)  DEFAULT NULL COMMENT '주소',
    mem_addr_detail VARCHAR(255)  DEFAULT NULL COMMENT '상세 주소',
    is_primary      CHAR(1)       NOT NULL DEFAULT 'N' COMMENT 'Y / N 대표 주소 여부',
    PRIMARY KEY (addr_idx),
    KEY idx_member_addr_mem_idx (mem_idx),
    CONSTRAINT ck_addr_is_primary CHECK (is_primary IN ('Y','N')),
    CONSTRAINT fk_addr_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 배송지 테이블';



/* ==========================================
   2. 아이템 / 카테고리
   ========================================== */

-- 2-1) ITEM (아이템)
CREATE TABLE item (
    item_idx           BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
    item_name          VARCHAR(200)  NOT NULL COMMENT '상품명',
    item_category_idx  INT           NOT NULL COMMENT 'FK → item_category',
    item_brand         VARCHAR(100)  DEFAULT NULL COMMENT '브랜드',
    item_condition     VARCHAR(50)   NOT NULL COMMENT '상태 (NEW / USED_A / USED_B 등)',
    item_thumbnail_img VARCHAR(255)  DEFAULT NULL COMMENT '썸네일 이미지',
    item_detail_img    VARCHAR(255)  DEFAULT NULL COMMENT '상세 이미지',
    item_regdate       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    item_is_deleted    CHAR(1)       NOT NULL DEFAULT 'N' COMMENT 'Y / N',
    PRIMARY KEY (item_idx),
    KEY idx_item_category (item_category_idx),
    CONSTRAINT ck_item_is_deleted CHECK (item_is_deleted IN ('Y','N')),
    CONSTRAINT fk_item_item_category
        FOREIGN KEY (item_category_idx) REFERENCES item_category(item_category_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='아이템 테이블';



/* ==========================================
   3. 역경매 / 입찰
   ========================================== */

-- 3-1) AUCTION (역경매 요청)
CREATE TABLE auction (
    auction_idx               BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
    buyer_idx                 BIGINT        NOT NULL COMMENT 'FK → member.mem_idx (구매자)',
    item_category_idx         INT           NOT NULL COMMENT 'FK → item_category',
    auction_title             VARCHAR(200)  NOT NULL COMMENT '경매 제목',
    auction_desc              TEXT          NOT NULL COMMENT '경매 설명',
    auction_target_price      BIGINT        DEFAULT NULL COMMENT '희망 최대가 (nullable)',
    auction_start_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '경매 시작일시',
    auction_end_at            DATETIME      NOT NULL COMMENT '입찰 마감일시',
    auction_decision_deadline DATETIME      NOT NULL COMMENT '결정 마감일',
    auction_winning_bid_idx   BIGINT        DEFAULT NULL COMMENT 'FK → bid.bid_idx (nullable)',
    auction_status_idx        INT           NOT NULL COMMENT 'FK → auction_status',
    auction_regdate           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    auction_view_count        BIGINT        NOT NULL DEFAULT 0 COMMENT '조회수',
    PRIMARY KEY (auction_idx),
    KEY idx_auction_buyer (buyer_idx),
    KEY idx_auction_item_category (item_category_idx),
    KEY idx_auction_status (auction_status_idx),
    CONSTRAINT fk_auction_buyer
        FOREIGN KEY (buyer_idx)         REFERENCES member(mem_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_auction_item_category
        FOREIGN KEY (item_category_idx) REFERENCES item_category(item_category_idx),
    CONSTRAINT fk_auction_status
        FOREIGN KEY (auction_status_idx) REFERENCES auction_status(auction_status_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='역경매 요청 테이블';

-- 3-2) BID (입찰)
CREATE TABLE bid (
    bid_idx           BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
    auction_idx       BIGINT        NOT NULL COMMENT 'FK → auction',
    bidder_idx        BIGINT        NOT NULL COMMENT 'FK → member (입찰자)',
    item_idx          BIGINT        NOT NULL COMMENT 'FK → item (실제 제안 상품)',
    item_category_idx INT           NOT NULL COMMENT 'FK → item_category',
    bid_price         BIGINT        NOT NULL COMMENT '제안 가격',
    bid_quantity      INT           NOT NULL DEFAULT 1 COMMENT '수량',
    bid_message       VARCHAR(500)  DEFAULT NULL COMMENT '제안 조건/설명',
    bid_status_idx    INT           NOT NULL COMMENT 'FK → bid_status',
    bid_regdate       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    PRIMARY KEY (bid_idx),
    KEY idx_bid_auction (auction_idx),
    KEY idx_bid_bidder (bidder_idx),
    KEY idx_bid_item (item_idx),
    KEY idx_bid_item_category (item_category_idx),
    KEY idx_bid_status (bid_status_idx),
    CONSTRAINT fk_bid_auction
        FOREIGN KEY (auction_idx)       REFERENCES auction(auction_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_bid_bidder
        FOREIGN KEY (bidder_idx)        REFERENCES member(mem_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_bid_item
        FOREIGN KEY (item_idx)          REFERENCES item(item_idx),
    CONSTRAINT fk_bid_item_category
        FOREIGN KEY (item_category_idx) REFERENCES item_category(item_category_idx),
    CONSTRAINT fk_bid_status
        FOREIGN KEY (bid_status_idx)    REFERENCES bid_status(bid_status_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='입찰 테이블';

-- auction_winning_bid_idx FK (bid 생성 후)
ALTER TABLE auction
    ADD CONSTRAINT fk_auction_winning_bid
        FOREIGN KEY (auction_winning_bid_idx) REFERENCES bid(bid_idx);



/* ==========================================
   4. 커뮤니티 (게시판 / 댓글)
   ========================================== */

-- 4-1) BOARD (게시글)
CREATE TABLE board (
    board_idx        BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
    mem_idx          BIGINT        NOT NULL COMMENT 'FK → member.mem_idx',
    board_title      VARCHAR(200)  NOT NULL COMMENT '제목',
    board_content    TEXT          DEFAULT NULL COMMENT '내용',
    board_ip         VARCHAR(40)   NOT NULL COMMENT 'IP',
    board_thumbnail  VARCHAR(200)  DEFAULT NULL COMMENT '썸네일',
    board_view_count BIGINT        NOT NULL DEFAULT 0 COMMENT '조회수',
    board_type_idx   INT           NOT NULL COMMENT 'FK → board_type.board_type_idx',
    board_regdate    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    board_moddate    DATETIME      DEFAULT NULL COMMENT '수정일',
    board_is_deleted CHAR(1)       NOT NULL DEFAULT 'N' COMMENT 'Y / N',
    board_deldate    DATETIME      DEFAULT NULL COMMENT '삭제일',
    PRIMARY KEY (board_idx),
    KEY idx_board_mem (mem_idx),
    KEY idx_board_board_type (board_type_idx),
    CONSTRAINT ck_board_is_deleted CHECK (board_is_deleted IN ('Y','N')),
    CONSTRAINT fk_board_member
        FOREIGN KEY (mem_idx) REFERENCES member(mem_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_board_board_type
        FOREIGN KEY (board_type_idx) REFERENCES board_type(board_type_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글 테이블';

-- 4-2) REPLY (댓글)
CREATE TABLE reply (
    reply_idx        BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'PK',
    board_idx        BIGINT         NOT NULL COMMENT 'FK → board.board_idx',
    mem_idx          BIGINT         NOT NULL COMMENT 'FK → member.mem_idx',
    reply_content    VARCHAR(1000)  DEFAULT NULL COMMENT '댓글 내용',
    reply_ip         VARCHAR(40)    DEFAULT NULL COMMENT 'IP',
    reply_regdate    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    reply_moddate    DATETIME       DEFAULT NULL COMMENT '수정일',
    reply_is_deleted CHAR(1)        NOT NULL DEFAULT 'N' COMMENT 'Y / N',
    reply_deldate    DATETIME       DEFAULT NULL COMMENT '삭제일',
    reply_ref        INT            DEFAULT NULL COMMENT '원댓',
    reply_step       INT            DEFAULT NULL COMMENT '댓글 순서',
    reply_depth      INT            DEFAULT NULL COMMENT '댓글 깊이',
    PRIMARY KEY (reply_idx),
    KEY idx_reply_board (board_idx),
    KEY idx_reply_mem (mem_idx),
    KEY idx_reply_ref (reply_ref),
    CONSTRAINT ck_reply_is_deleted CHECK (reply_is_deleted IN ('Y','N')),
    CONSTRAINT fk_reply_board
        FOREIGN KEY (board_idx) REFERENCES board(board_idx)
        ON DELETE CASCADE,
    CONSTRAINT fk_reply_member
        FOREIGN KEY (mem_idx)  REFERENCES member(mem_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='댓글 테이블';



/* ==========================================
   5. 코드 테이블 기본 데이터
   ========================================== */

-- 5-1) ROLE 코드 (1 USER, 2 ADMIN)
INSERT INTO role (role_idx, role_name) VALUES (1, 'USER');
INSERT INTO role (role_idx, role_name) VALUES (2, 'ADMIN');

-- 5-2) GRADE 코드 예시
INSERT INTO grade (grade_idx, grade_name, grade_credit) VALUES (1, 'basic',  0.00);
INSERT INTO grade (grade_idx, grade_name, grade_credit) VALUES (2, 'silver', 3.50);
INSERT INTO grade (grade_idx, grade_name, grade_credit) VALUES (3, 'gold',   4.00);
INSERT INTO grade (grade_idx, grade_name, grade_credit) VALUES (4, 'vip',    4.50);

-- 5-3) AUCTION_STATUS 코드 예시
INSERT INTO auction_status (auction_status_idx, auction_status_code, auction_status_name)
VALUES (1, 'open',    '진행중');
INSERT INTO auction_status (auction_status_idx, auction_status_code, auction_status_name)
VALUES (2, 'closed',  '마감');
INSERT INTO auction_status (auction_status_idx, auction_status_code, auction_status_name)
VALUES (3, 'failed',  '유찰');
INSERT INTO auction_status (auction_status_idx, auction_status_code, auction_status_name)
VALUES (4, 'canceled','취소');

-- 5-4) BID_STATUS 코드 예시
INSERT INTO bid_status (bid_status_idx, bid_status_code, bid_status_name)
VALUES (1, 'normal',   '일반');
INSERT INTO bid_status (bid_status_idx, bid_status_code, bid_status_name)
VALUES (2, 'won',      '낙찰');
INSERT INTO bid_status (bid_status_idx, bid_status_code, bid_status_name)
VALUES (3, 'lost',     '실패');
INSERT INTO bid_status (bid_status_idx, bid_status_code, bid_status_name)
VALUES (4, 'canceled', '취소');

-- 5-5) BOARD_TYPE 코드 예시
INSERT INTO board_type (board_type_idx, board_type_code, board_type_name, board_can_comment, board_min_role)
VALUES (1, 'GOLF_BOARD', '골프 게시판', 'Y', 1);
INSERT INTO board_type (board_type_idx, board_type_code, board_type_name, board_can_comment, board_min_role)
VALUES (2, 'SKI_BOARD',  '스키 게시판',  'Y', 1);
