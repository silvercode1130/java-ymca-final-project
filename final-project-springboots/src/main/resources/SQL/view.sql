-- 뷰 테이블 만들기 (Auction)

-- 기존 뷰 삭제 후 재생성 (MySQL은 CREATE OR REPLACE VIEW 지원)

-- AuctionList 뷰
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
    COUNT(b.bid_idx)              AS bid_count,
    IFNULL(MIN(b.bid_price), 0)   AS min_bid_price,
    a.auction_regdate,
    a.auction_is_deleted
FROM auction a
JOIN auction_status s ON a.auction_status_idx = s.auction_status_idx
LEFT JOIN item_category ic ON a.item_category_idx = ic.item_category_idx
LEFT JOIN bid b ON a.auction_idx = b.auction_idx
GROUP BY
    a.auction_idx, a.buyer_idx, a.item_category_idx,
    a.auction_thumbnail_img, a.auction_title, a.auction_target_price,
    a.auction_end_at, a.auction_status_idx, s.auction_status_name,
    ic.item_category_name, a.auction_regdate, a.auction_is_deleted;


-- AuctionDetail 뷰
CREATE OR REPLACE VIEW auction_detail_view AS
SELECT
    a.auction_idx,
    a.buyer_idx,
    a.item_category_idx,
    a.auction_thumbnail_img,
    a.auction_title,
    a.auction_desc,
    a.auction_target_price,
    a.auction_end_at,
    a.auction_decision_deadline,
    a.auction_status_idx,
    s.auction_status_name,
    ic.item_category_name,
    COUNT(b.bid_idx)              AS bid_count,
    IFNULL(MIN(b.bid_price), 0)   AS min_bid_price,
    a.auction_is_deleted
FROM auction a
JOIN auction_status s ON a.auction_status_idx = s.auction_status_idx
LEFT JOIN item_category ic ON a.item_category_idx = ic.item_category_idx
LEFT JOIN bid b ON a.auction_idx = b.auction_idx
GROUP BY
    a.auction_idx, a.buyer_idx, a.item_category_idx,
    a.auction_thumbnail_img, a.auction_title, a.auction_desc,
    a.auction_target_price, a.auction_end_at, a.auction_decision_deadline,
    a.auction_status_idx, s.auction_status_name, ic.item_category_name,
    a.auction_is_deleted;


-- BidList 뷰
CREATE OR REPLACE VIEW bid_list_view AS
SELECT
    b.bid_idx,
    b.auction_idx,
    b.bidder_idx,
    m.mem_name      AS mem_name,       -- 구매자에게 보여줄 실명 (bid.memName)
    m.mem_name      AS real_mem_name,  -- 입찰 상세에서 사용할 실명 (bid.realMemName)
    b.bid_price,
    b.bid_quantity,
    b.bid_message,
    b.bid_status_idx,
    bs.bid_status_name,
    b.bid_regdate,
    i.item_idx,
    i.item_name,
    i.item_brand,
    i.item_thumbnail_img
FROM bid b
JOIN member m ON b.bidder_idx = m.mem_idx
JOIN bid_status bs ON b.bid_status_idx = bs.bid_status_idx
LEFT JOIN item i ON b.item_idx = i.item_idx;