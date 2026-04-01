const endAtInput = document.getElementById('auctionEndAt');
   const deadlineInput = document.getElementById('auctionDecisionDeadline');

   function toLocalDateTimeString(date) {
       const pad = n => String(n).padStart(2, '0');
       return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
   }

   // 현재 시간 이후만 선택 가능
   endAtInput.min = toLocalDateTimeString(new Date());

   // 입찰 마감일 바뀌면 결정 마감일 범위 자동 조정
   endAtInput.addEventListener('change', function () {
       if (this.value) {
           const endDate = new Date(this.value);
           deadlineInput.min = toLocalDateTimeString(endDate);
           const maxDate = new Date(endDate);
           maxDate.setDate(maxDate.getDate() + 3);
           deadlineInput.max = toLocalDateTimeString(maxDate);
           deadlineInput.value = '';
       }
   });

   // 이미지 미리보기
   document.getElementById('thumbnailFile').addEventListener('change', function () {
       const file = this.files[0];
       if (file) {
           const reader = new FileReader();
           reader.onload = e => {
               document.getElementById('previewImg').src = e.target.result;
               document.getElementById('previewBox').style.display = 'block';
           };
           reader.readAsDataURL(file);
       } else {
           document.getElementById('previewBox').style.display = 'none';
       }
   });

   // 폼 제출 유효성 검사
   document.getElementById('registerForm').addEventListener('submit', function(e) {
       const targetPrice = Number(document.querySelector('[name="auctionTargetPrice"]').value);
       const endAt = endAtInput.value;
       const deadline = deadlineInput.value;

       if (targetPrice <= 0) {
           alert('희망 최대가는 0원보다 커야 합니다.');
           e.preventDefault(); return;
       }
       if (targetPrice % 100 !== 0) {
           alert('희망 최대가는 100원 단위로 입력해주세요.');
           e.preventDefault(); return;
       }
       if (!endAt) {
           alert('입찰 마감일을 선택해주세요.');
           e.preventDefault(); return;
       }
       if (!deadline) {
           alert('결정 마감일을 선택해주세요.');
           e.preventDefault(); return;
       }
       if (new Date(deadline) <= new Date(endAt)) {
           alert('결정 마감일은 입찰 마감일 이후여야 합니다.');
           e.preventDefault(); return;
       }
   });