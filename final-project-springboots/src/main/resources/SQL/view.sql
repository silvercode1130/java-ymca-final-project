-- 뷰 테이블 만들기 (Auction)

-- AuctionList
CREATE OR REPLACE VIEW auction_list_view AS
SELECT
    a.auction_idx,
    a.buyer_idx,
    a.item_category_idx,
    a.auction_thumbnail_img,
    a.auction_title,
    a.auction_target_price,
    a.auction_end_at,
    a.auction_status_idx,
    s.auction_status_name,
    ic.item_category_name,
    a.auction_regdate, -- 리스트 정렬용으로 꼭 필요
    a.auction_is_deleted,
    COUNT(b.bid_idx) AS bid_count, -- DTO 변수명에 맞춰서 b_count -> bid_count
    IFNULL(MIN(b.bid_price), 0) AS min_bid_price -- DTO 변수명에 맞춰서 min_price -> min_bid_price
FROM auction a
JOIN auction_status s ON a.auction_status_idx = s.auction_status_idx
LEFT JOIN item_category ic ON a.item_category_idx = ic.item_category_idx
LEFT JOIN bid b ON a.auction_idx = b.auction_idx AND b.bid_status_idx != 4
GROUP BY a.auction_idx, a.buyer_idx, a.item_category_idx, a.auction_thumbnail_img, 
         a.auction_title, a.auction_target_price, a.auction_end_at, 
         a.auction_status_idx, s.auction_status_name, ic.item_category_name, 
         a.auction_regdate, a.auction_is_deleted;

-- AuctionDetail
CREATE VIEW auction_detail_view AS
SELECT
    a.*,
    m.mem_name AS buyer_name, -- 상세페이지에서 작성자 이름 보여줘야 함
    s.auction_status_name,
    ic.item_category_name,
    COUNT(b.bid_idx) AS b_count,
    IFNULL(MIN(b.bid_price), 0) AS min_price
FROM auction a
JOIN member m ON a.buyer_idx = m.mem_idx
JOIN auction_status s ON a.auction_status_idx = s.auction_status_idx
LEFT JOIN item_category ic ON a.item_category_idx = ic.item_category_idx
LEFT JOIN bid b ON a.auction_idx = b.auction_idx
GROUP BY
    a.auction_idx, 
    m.mem_name, 
    s.auction_status_name, 
    ic.item_category_name;
    
    
-- BidList
CREATE OR REPLACE VIEW bid_list_view AS
SELECT 
    b.*,
    m.mem_name AS mem_name,
    s.bid_status_name,         -- 상태명(일반/낙찰 등)
    i.item_name,                -- 상품명
    i.item_brand,               -- 상세용 브랜드
    i.item_thumbnail_img,       -- 상세용 이미지
    ic.item_category_name
FROM bid b
JOIN member m ON b.bidder_idx = m.mem_idx
JOIN bid_status s ON b.bid_status_idx = s.bid_status_idx
LEFT JOIN item i ON b.item_idx = i.item_idx -- 상품 정보 조인
LEFT JOIN item_category ic ON i.item_category_idx = ic.item_category_idx;