# Implementation Plan: Mobile Sidebar Accordion

## Overview

boardList.html의 `<aside>` 사이드바를 모바일(< 1024px)에서 아코디언 UI로 전환한다. 데스크탑(≥ 1024px)에서는 기존 레이아웃을 유지한다. 변경 범위는 `boardList.html` 단일 파일이며, 백엔드 변경은 없다.

## Tasks

- [x] 1. Add mobile accordion header and modify desktop header
  - [x] 1.1 Add the mobile-only accordion `<button>` header
    - Create `<button id="sidebar-accordion-toggle">` with `lg:hidden` class inside the sidebar `<div>`, before the existing header
    - Include "종목별 게시판" text, current board name display (`#sidebar-current-board`), and chevron icon (`#sidebar-accordion-icon`)
    - Set initial state: `aria-expanded="false"`, `aria-controls="sidebar-accordion-body"`
    - Use Thymeleaf expression for current board name: `th:text="${currentType != null} ? '· ' + ${currentType.boardTypeName} : '· 전체 게시판'"`
    - _Requirements: 1.1, 1.2, 3.1, 3.2, 5.1, 5.2, 5.3_

  - [x] 1.2 Modify existing desktop header to be desktop-only
    - Change the existing `<div class="px-4 py-3 border-b border-gray-100">` header to use `hidden lg:block` classes
    - This ensures the original "종목별 게시판" header only shows on desktop (≥ 1024px)
    - _Requirements: 1.3_

- [x] 2. Wrap board link list in accordion body container
  - [x] 2.1 Create accordion body wrapper around existing board links
    - Wrap the existing `<div class="px-2 py-2">` (containing "전체 게시판" and individual board links) inside a new `<div id="sidebar-accordion-body">`
    - Add attributes: `role="region"`, `aria-labelledby="sidebar-accordion-toggle"`
    - Add Tailwind classes: `overflow-hidden transition-[max-height] duration-300 ease-in-out max-h-0 lg:max-h-none lg:overflow-visible`
    - Mobile default: collapsed (`max-h-0` + `overflow-hidden`); Desktop: always visible (`lg:max-h-none lg:overflow-visible`)
    - Existing board links and their Thymeleaf logic remain unchanged inside the wrapper
    - _Requirements: 1.2, 1.3, 3.3, 4.1, 4.2, 4.3, 5.4_

- [x] 3. Implement toggleSidebarAccordion() JavaScript function
  - [x] 3.1 Add the toggle function in an inline `<script>` block
    - Add `toggleSidebarAccordion()` function after the existing `<script>` block at the bottom of the template
    - Implement expand logic: set `max-height` to `scrollHeight`, update `aria-expanded="true"`, rotate icon 180deg, hide current board name
    - Implement collapse logic: set `max-height` from `scrollHeight` to `0px` using `requestAnimationFrame`, update `aria-expanded="false"`, reset icon rotation, show current board name
    - Add `transitionend` listener (one-time) to set `max-height: none` after expand completes
    - Add defensive null checks for all `getElementById` calls with early return
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 5.2_

- [x] 4. Checkpoint - Verify accordion functionality
  - Ensure all changes compile and render correctly by reviewing the modified boardList.html
  - Verify: mobile accordion header appears with correct Thymeleaf expressions
  - Verify: desktop header uses `hidden lg:block` classes
  - Verify: accordion body wrapper has correct ARIA attributes and Tailwind classes
  - Verify: toggle function handles expand/collapse with animation
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Final review and regression check
  - [x] 5.1 Verify no existing functionality is broken
    - Confirm board link URLs (`th:href`) are unchanged
    - Confirm `th:each` loop for boardTypes is preserved
    - Confirm active board highlighting (`th:classappend`) still works
    - Confirm desktop layout (`lg:col-span-1`, sticky sidebar) is unaffected
    - Confirm search, pagination, and category modal are unaffected
    - _Requirements: 4.1, 4.2, 4.3_

- [x] 6. Final checkpoint - Confirm implementation complete
  - Ensure all changes are correct and complete, ask the user if questions arise.

## Notes

- This is a single-file change: only `boardList.html` is modified
- No backend (Controller, Service, Mapper) changes needed
- No new dependencies — uses existing Tailwind CSS (CDN) and Bootstrap Icons
- The accordion uses `aria-expanded` as the single source of truth for state
- Desktop layout is completely unaffected due to Tailwind `lg:` responsive prefixes
- No property-based tests — this is a pure UI/CSS change with no testable universal properties
