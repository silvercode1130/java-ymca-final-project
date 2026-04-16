# Requirements Document

## Introduction

boardList 페이지의 좌측 사이드바(종목별 게시판 목록)를 모바일 화면에서 아코디언(접기/펼치기) 형태로 변경한다. 데스크탑(lg 이상)에서는 기존 사이드바 레이아웃을 그대로 유지하고, 모바일/태블릿(lg 미만)에서만 아코디언 UI로 전환하여 화면 공간을 효율적으로 활용한다.

현재 boardList.html의 사이드바는 `<aside class="lg:col-span-1">` 내부에 "종목별 게시판" 헤더와 게시판 링크 목록으로 구성되어 있다. 모바일에서는 이 사이드바가 본문 위에 전체 너비로 펼쳐져 공간을 많이 차지하므로, 아코디언으로 접어두고 필요할 때만 펼칠 수 있도록 개선한다.

## Glossary

- **Sidebar**: boardList 페이지 좌측의 종목별 게시판 목록 영역 (`<aside>` 태그)
- **Accordion**: 헤더를 클릭하면 내용이 펼쳐지고, 다시 클릭하면 접히는 UI 패턴
- **Mobile_View**: 뷰포트 너비가 lg 브레이크포인트(1024px) 미만인 화면 상태
- **Desktop_View**: 뷰포트 너비가 lg 브레이크포인트(1024px) 이상인 화면 상태
- **Accordion_Header**: 아코디언의 클릭 가능한 헤더 영역으로, "종목별 게시판" 텍스트와 펼침/접힘 상태를 나타내는 아이콘을 포함
- **Accordion_Body**: 아코디언 헤더 아래에 위치하며, 게시판 링크 목록을 담고 있는 영역
- **Active_Board_Type**: 현재 선택된 게시판 종목 (URL의 typeCode에 해당)

## Requirements

### Requirement 1: 모바일 아코디언 표시

**User Story:** 모바일 사용자로서, 종목별 게시판 목록이 아코디언 형태로 표시되길 원한다. 그래야 화면 공간을 절약하면서도 필요할 때 게시판 목록에 접근할 수 있다.

#### Acceptance Criteria

1. WHILE Mobile_View 상태일 때, THE Sidebar SHALL "종목별 게시판" 텍스트를 포함한 Accordion_Header를 표시한다
2. WHILE Mobile_View 상태일 때, THE Accordion_Body SHALL 기본적으로 접힌(숨김) 상태로 표시된다
3. WHILE Desktop_View 상태일 때, THE Sidebar SHALL 기존의 항상 펼쳐진 사이드바 레이아웃을 그대로 유지한다

### Requirement 2: 아코디언 토글 동작

**User Story:** 모바일 사용자로서, 아코디언 헤더를 탭하여 게시판 목록을 펼치거나 접을 수 있길 원한다. 그래야 원하는 게시판으로 쉽게 이동할 수 있다.

#### Acceptance Criteria

1. WHEN 사용자가 Accordion_Header를 클릭하면, THE Accordion_Body SHALL 접힌 상태에서 펼쳐진 상태로 전환된다
2. WHEN 사용자가 펼쳐진 상태의 Accordion_Header를 클릭하면, THE Accordion_Body SHALL 펼쳐진 상태에서 접힌 상태로 전환된다
3. WHEN 아코디언 상태가 전환되면, THE Accordion_Header SHALL 현재 상태를 나타내는 아이콘(펼침: 위쪽 화살표, 접힘: 아래쪽 화살표)을 표시한다
4. WHEN 아코디언 상태가 전환되면, THE Accordion_Body SHALL 부드러운 슬라이드 애니메이션과 함께 펼쳐지거나 접힌다

### Requirement 3: 현재 선택된 게시판 표시

**User Story:** 모바일 사용자로서, 아코디언이 접힌 상태에서도 현재 어떤 게시판을 보고 있는지 알 수 있길 원한다. 그래야 페이지 이동 없이 현재 위치를 파악할 수 있다.

#### Acceptance Criteria

1. WHILE Active_Board_Type이 존재하고 Accordion_Body가 접힌 상태일 때, THE Accordion_Header SHALL 현재 선택된 게시판 이름을 헤더 영역에 함께 표시한다
2. WHILE Active_Board_Type이 존재하지 않고 Accordion_Body가 접힌 상태일 때, THE Accordion_Header SHALL "전체 게시판" 텍스트를 헤더 영역에 함께 표시한다
3. WHILE Accordion_Body가 펼쳐진 상태일 때, THE Sidebar SHALL Active_Board_Type에 해당하는 게시판 링크를 시각적으로 강조(하이라이트) 표시한다

### Requirement 4: 기존 기능 유지

**User Story:** 사용자로서, 아코디언 변경 후에도 기존 게시판 네비게이션 기능이 동일하게 동작하길 원한다. 그래야 기존 사용 경험이 유지된다.

#### Acceptance Criteria

1. THE Sidebar SHALL 기존과 동일한 게시판 링크 목록("전체 게시판" 및 개별 종목 게시판)을 아코디언 내부에 포함한다
2. WHEN 사용자가 아코디언 내부의 게시판 링크를 클릭하면, THE Sidebar SHALL 해당 게시판 페이지로 정상적으로 이동한다
3. THE Sidebar SHALL 데스크탑과 모바일 모두에서 동일한 게시판 데이터(boardTypes, typeCode)를 사용한다

### Requirement 5: 접근성 지원

**User Story:** 모든 사용자로서, 아코디언이 키보드 및 스크린 리더로 접근 가능하길 원한다. 그래야 보조 기술을 사용하는 사용자도 게시판 목록에 접근할 수 있다.

#### Acceptance Criteria

1. THE Accordion_Header SHALL 키보드 포커스를 받을 수 있는 버튼(`<button>`) 요소로 구현된다
2. THE Accordion_Header SHALL `aria-expanded` 속성을 통해 현재 펼침/접힘 상태를 스크린 리더에 전달한다
3. THE Accordion_Header SHALL `aria-controls` 속성을 통해 제어 대상인 Accordion_Body를 참조한다
4. THE Accordion_Body SHALL `role="region"` 및 `aria-labelledby` 속성을 통해 Accordion_Header와 연결된다
