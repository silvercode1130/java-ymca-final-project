package com.springbootstudy.bbs.domain;


import java.time.LocalDateTime;

import lombok.Data;

//아이템(상품) 정보
@Data
public class ItemVO {

<<<<<<< HEAD
  private Long itemIdx; // PK
  private String itemName; // 상품명
  private Integer itemCategoryIdx; // FK → item_category
  private String itemBrand; // 브랜드
  private String itemCondition; // 상태 (NEW / USED_A / USED_B 등)
  private String itemThumbnailImg; // 썸네일 이미지
  private String itemDetailImg; // 상세 이미지
  private java.time.LocalDateTime itemRegdate; // 등록일
  private String itemIsDeleted; // 'Y' / 'N'
=======
 private Long    		itemIdx;           // PK
 private String  		itemName;          // 상품명
 private Integer 		itemCategoryIdx;   // FK → item_category
 private String  		itemBrand;         // 브랜드
 private String  		itemCondition;     // 상태 (NEW / USED_A / USED_B 등)
 private String  		itemThumbnailImg;  // 썸네일 이미지
 private String  		itemDetailImg;     // 상세 이미지
 private LocalDateTime 	itemRegdate;   	   // 등록일
 private String  		itemIsDeleted;     // 'Y' / 'N'
>>>>>>> 79d91f471be66091855e1ab616e702897bc0f5e1

}
