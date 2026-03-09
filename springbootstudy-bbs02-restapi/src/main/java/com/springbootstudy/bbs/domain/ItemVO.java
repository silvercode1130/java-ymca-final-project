package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ItemVO {
    private Long   itemIdx;             // PK
    private String itemName;
    private Integer itemCategoryIdx;    // FK → sports_category.sports_category_idx (대분류)
    private Long   itemOriginPrice;
    private String itemBrand;
    private String itemThumbnailImg;
    private String itemDetailImg;
    private Date   itemRegdate;
    private String itemIsDeleted;       // 'Y' / 'N'
}
