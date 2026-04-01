// 입찰 이미지 미리보기
    document.getElementById('bidImageFile').addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = e => {
                document.getElementById('bidPreviewImg').src = e.target.result;
                document.getElementById('bidPreviewBox').style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            document.getElementById('bidPreviewBox').style.display = 'none';
        }
    });

    // 입찰 폼 유효성 검사
    document.getElementById('bidForm').addEventListener('submit', function(e) {
        const bidPrice = Number(document.getElementById('bidPrice').value);

        if (bidPrice <= 0) {
            alert('제안 가격은 0원보다 커야 합니다.');
            e.preventDefault(); return;
        }
        if (bidPrice % 100 !== 0) {
            alert('제안 가격은 100원 단위로 입력해주세요.');
            e.preventDefault(); return;
        }
    });