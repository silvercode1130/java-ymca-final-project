// DOM이 준비되면 실행될 콜백 함수
$(function() {
	
	// 회원 로그인 폼이 submit 될 때 폼 유효성 검사를 위한 이벤트 처리
	$("#loginForm").submit(function() {
		var id = $("#userId").val();
		var pass = $("#userPass").val();
		
		if(id.length <= 0) {
			alert("아이디가 입력되지 않았습니다.\n아이디를 입력해주세요");
			$("#userId").focus();
			return false;
		}
		if(pass.length <= 0) {
			alert("비밀번호가 입력되지 않았습니다.\n비밀번호를 입력해주세요");
			$("#userPass").focus();
			return false;
		}
	});

	// 모달 로그인 폼이 submit 될 때 폼 유효성 검사를 위한 이벤트 처리
	$("#modalLoginForm").submit(function() {
		var id = $("#modalUserId").val();
		var pass = $("#modalUserPass").val();
		
		if(id.length <= 0) {
			alert("아이디가 입력되지 않았습니다.\n아이디를 입력해주세요");
			$("#modalUserId").focus();
			return false;
		}
		if(pass.length <= 0) {
			alert("비밀번호가 입력되지 않았습니다.\n비밀번호를 입력해주세요");
			$("#modalUserPass").focus();
			return false;
		}
	});	
	
	/* 회원 가입 폼, 회원정보 수정 폼에서 폼 컨트롤에서 키보드 입력을
	 * 체크해 유효한 값을 입력 받을 수 있도록 keyup 이벤트를 처리 했다. 
	 **/	
	$("#id").on("keyup", function() {		
		// 아래와 같이 정규표현식을 이용해 영문 대소문자, 숫자만 입력되었는지 체크할 수 있다. 
		var regExp = /[^A-Za-z0-9]/gi;	
		if(regExp.test($(this).val())) {
			alert("영문 대소문자, 숫자만 입력할 수 있습니다.");
			$(this).val($(this).val().replace(regExp, ""));
		}
	});
	
	/* 바로 위에서 키보드의 키가 눌려질 때 사용자가 아이디 입력란에 영문 대소문자 
	 * 또는 숫자만 입력하였는지를 체크하는 이벤트 처리 코드를 작성하였다. 이런 이벤트
	 * 처리는 아이디 입력 체크에만 사용할 수 있는 것이 아니라 비밀번호 입력이나 이메일
	 * 입력에서도 유효한 데이터가 입력되었는지 체크해야 된다. 이렇게 여러 문서 객체에
	 * 동일한 이벤트 처리를 해야 하므로 각각 이벤트 처리를 구현하게 되면 동일한 코드가
	 * 계속해서 중복되는 현상이 발생하게 된다. 이럴 경우에는 동일한 코드를 하나의 
	 * 함수로 만들어 놓고 여러 곳에서 사용하면 코드의 중복을 막고 코드의 재사용성을
	 * 높일 수 있다. 아래는 동일한 코드에 대한 별도의 함수를 이 자바스크립트 파일의
	 * 아래 부분에 작성해 놓고 이 함수를 이벤트 핸들러를 등록해 사용하는 방식이다.
	 **/	
	$("#pass1").on("keyup", inputCharReplace);	
	$("#pass2").on("keyup", inputCharReplace);	
	$("#emailId").on("keyup", inputCharReplace);	
	$("#emailDomain").on("keyup", inputEmailDomainReplace);
		
	/* 회원 가입 폼에서 아이디 중복확인 버튼이 클릭되면
	 * 아이디 중복을 확인할 수 있는 새 창을 띄워주는 함수
	 **/ 
	$("#btnOverlapId").on("click", function() {
		var id = $("#id").val();
		url="overlapIdCheck?id=" + id;
		
		if(id.length == 0) {
			alert("아이디를 입력해주세요");
			return false;
		}
		
		if(id.length < 5) {
			alert("아이디는 5자 이상 입력해주세요.");
			return false;
		}
		
		window.open(url, "idCheck", "toolbar=no, scrollbars=no, resizeable=no, " 
				+  "status=no, memubar=no, width=500, height=320");
	});
	
	/* 새 창으로 띄운 아이디 찾기 폼에서
	 * 아이디 중복확인 버튼이 클릭되면 유효성 검사를 하는 함수	 
	 **/
	$("#idCheckForm").on("submit", function() {
		var id = $("#checkId").val();	
		
		if(id.length == 0)  {
			alert("아이디를 입력해주세요");
			return false;
		}
		
		if(id.length < 5) {
			alert("아이디는 5자 이상 입력해주세요.");
			return false;
		}
	});
		
	/* 새 창으로 띄운 아이디 중복확인 창에서 "아이디 사용 버튼"이 클릭되면 
	 * 새 창을 닫고 입력된 아이디를 부모창의 회원가입 폼에 입력해 주는 함수
	 **/
	$("#btnIdCheckClose").on("click", function() {
		var id = $(this).attr("data-id-value");
		opener.document.joinForm.id.value = id;
		opener.document.joinForm.isIdCheck.value = true;
		window.close();
	});
		
	/* 회원 가입 폼과 회원정보 수정 폼에서 "우편번호 검색" 버튼의 클릭 이벤트 처리
	 * findZipcode() 함수는 다음 우편번호 API를 사용해 우편번호를 검색하는 함수로
	 * 두 페이지에서 사용되어 중복된 코드가 발생하므로 아래에 별도의 함수로 정의하였다.
	 */
	$("#btnZipcode").click(findZipcode);
		
	// 이메일 입력 셀렉트 박스에서 선택된 도메인을 설정하는 함수 
	$("#selectDomain").on("change", function() {
		var str = $(this).val();
		
		if(str == "직접입력") {	
			$("#emailDomain").val("");
			$("#emailDomain").prop("readonly", false);
		} else if(str == "네이버"){	
			$("#emailDomain").val("naver.com");			
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "다음") {		
			$("#emailDomain").val("daum.net");
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "한메일"){	
			$("#emailDomain").val("hanmail.net");
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "구글") {		
			$("#emailDomain").val("gmail.com");
			$("#emailDomain").prop("readonly", true);
		}
	});
		
	// 회원 가입 폼이 서브밋 될 때 이벤트 처리 - 폼 유효성 검사
	$("#joinForm").on("submit", function() {
		
		/* 회원 가입 폼에서 서브밋 이벤트가 발생하면 true를 인수로 지정한다. 
		 * 회원 가입 폼과 회원 정보 수정 폼 유효성 검사를 하는 기능도 중복 되는
		 * 코드가 많으므로 joinFormCheck()라는 별도의 함수를 만들어서 사용하였다. 
		 * joinFormChcek() 함수에서 폼 유효성 검사를 통과하지 못하면
		 * false가 반환되기 때문에 그대로 반환하면 폼이 서브밋 되지 않는다. 
		 **/		
		return joinFormCheck(true);
	});
	
	/* 회원정보 수정 폼에서 "비밀번호 확인" 버튼이 클릭될 때 이벤트 처리
	 * 회원정보 수정 폼에서 기존 비밀번호가 맞는지를 Ajax 통신을 통해 확인한다.
	 **/
	$("#btnPassCheck").click(function() {
		var oldId = $("#id").val();
		var oldPass = $("#oldPass").val();
		
		if($.trim(oldPass).length == 0) {
			alert("기존 비밀번호가 입력되지 않았습니다.\n기존 비밀번호를 입력해주세요");
			return false;
		}
		var data = "id=" + oldId + "&pass="+oldPass;
		console.log("data : " + data);
		
		$.ajax({
			"url": "passCheck.ajax",
			"type": "get",
			"data": data,
			"dataType": "json",			
			"success": function(resData) {				
				if(resData.result) {
					alert("비밀번호가 확인되었습니다.\n비밀번호를 수정해주세요");
					$("#btnPassCheck").prop("disabled", true);
					$("#oldPass").prop("readonly", true);
					$("#pass1").focus();
					
				} else {
					alert("비밀번호가 틀립니다.\n비밀번호를 다시 확인해주세요");
					$("#oldPass").val("").focus();
				}
			},
			"error": function(xhr, status) {
				console.log("error : " + status);
			}
		});		
	});
			
	// 회원정보 수정 폼에서 수정하기 버튼이 클릭되면 유효성 검사를 하는 함수
	$("#memberUpdateForm").on("submit", function() {
		
		/* 회원정보 수정 폼에서 "비밀번호 확인" 버튼이 disabled 상태가 아니면
		 * 기존 비밀번호를 확인하지 않았기 때문에 확인하라는 메시지를 띄운다.
		 **/
		if(! $("#btnPassCheck").prop("disabled")) {
			alert("기존 비밀번호를 확인해야 비밀번호를 수정할 수 있습니다.\n"
				+ "기존 비밀번호를 입력하고 비밀번호 확인 버튼을 클릭해 주세요");
			return false;
		}
		
		/* 회원정보 수정 폼에서 서브밋 이벤트가 발생하면 false를 인수로 지정한다.
		 * joinFormChcek() 함수에서 폼 유효성 검사를 통과하지 못하면
		 * false가 반환되기 때문에 그대로 반환하면 폼이 서브밋 되지 않는다.
		 **/ 
		return joinFormCheck(false);
	});	
});

/* 회원 아이디, 비밀번호, 비밀번호 확인, 이메일 아이디 폼 컨트롤에
 * 사용자가 입력한 값이 영문 대소문자, 숫자 만 입력되도록 수정하는 함수
 **/
function inputCharReplace() {
	// 아래와 같이 정규표현식을 이용해 영문 대소문자, 숫자만 입력되었는지 체크할 수 있다. 
	var regExp = /[^A-Za-z0-9]/gi;	
	if(regExp.test($(this).val())) {
		alert("영문 대소문자, 숫자만 입력할 수 있습니다.");
		$(this).val($(this).val().replace(regExp, ""));
	}
}

/* 이메일 도메인 입력 폼 컨트롤에 사용자가 입력한 값이 
 * 영문 대소문자, 숫자, 점(.)만 입력되도록 수정하는 함수 
 **/ 
function inputEmailDomainReplace() {
	var regExp = /[^a-z0-9\.]/gi;	
	if(regExp.test($(this).val())) {
		alert("이메일 도메인은 영문 소문자, 숫자, 점(.)만 입력할 수 있습니다.");
		$(this).val($(this).val().replace(regExp, ""));
	}
}

/* 회원 가입 폼과 회원정보 수정 폼의 유효성 검사를 하는 함수
 * 두 페이지에서 처리하는 코드가 중복되어 하나의 함수로 정의하였다. 
 **/
function joinFormCheck(isJoinForm) {
	var name = $("#name").val();
	var id = $("#id").val();
	var pass1 = $("#pass1").val();
	var pass2 = $("#pass2").val();
	var zipcode = $("#zipcode").val();
	var address1 = $("#address1").val();
	var emailId = $("#emailId").val();
	var emailDomain = $("#emailDomain").val();
	var mobile2 = $("#mobile2").val();
	var mobile3 = $("#mobile2").val();
	var isIdCheck = $("#isIdCheck").val();
	
	if(name.length == 0) {		
		alert("이름이 입력되지 않았습니다.\n이름을 입력해주세요");
		return false;
	}	
	if(id.length == 0) {		
		alert("아이디가 입력되지 않았습니다.\n아이디를 입력해주세요");
		return false;
	}		
	if(isJoinForm && isIdCheck == 'false') {		
		alert("아이디 중복 체크를 하지 않았습니다.\n아이디 중복 체크를 해주세요");
		return false;
	}	
	if(pass1.length == 0) {		
		alert("비밀번호가 입력되지 않았습니다.\n비밀번호를 입력해주세요");
		return false;
	}
	
	if(pass2.length == 0) {		
		alert("비밀번호 확인이 입력되지 않았습니다.\n비밀번호 확인을 입력해주세요");
		return false;
	}
	if(pass1 != pass2) {
		alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		return false;
	}	
	if(zipcode.length == 0) {		
		alert("우편번호가 입력되지 않았습니다.\n우편번호를 입력해주세요");
		return false;
	}	
	if(address1.length == 0) {		
		alert("주소가 입력되지 않았습니다.\n주소를 입력해주세요");
		return false;
	}	
	if(emailId.length == 0) {		
		alert("이메일 아이디가 입력되지 않았습니다.\n이메일 아이디를 입력해주세요");
		return false;
	}	
	if(emailDomain.length == 0) {		
		alert("이메일 도메인이 입력되지 않았습니다.\n이메일 도메인을 입력해주세요");
		return false;
	}	
	if(mobile2.length == 0 || mobile3.length == 0) {		
		alert("휴대폰 번호가 입력되지 않았습니다.\n휴대폰 번호를 입력해주세요");
		return false;
	}
}

/* 우편번호 찾기 - daum 우편번소 찾기 API 이용
 * 회원 가입 폼과 회원정보 수정 폼에서 "우편번호 검색" 버튼이 클릭되면 호출되는 함수
 * 
 * 	새로운 5자리 우편번호로 회원 주소를 입력 받기 위해 daum.net에서
 * 	제공하는 우편번호 찾기 API를 사용하였다.
 * 	참고 사이트 : http://postcode.map.daum.net/guide
 **/
function findZipcode() {
	new daum.Postcode({
        oncomplete: function(data) {
            // 우편번호 검색 결과 항목을 클릭했을때 실행할 코드를 여기에 작성한다.
            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고 항목 변수

            //사용자가 선택한 주소 타입과 상관없이 모두 도로명 주소로 처리            
            addr = data.roadAddress;			
            
            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                extraAddr += data.bname;
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if(data.buildingName !== '' && data.apartment === 'Y'){
                extraAddr += (extraAddr !== '' ? 
										', ' + data.buildingName : data.buildingName);
            }
            
            // 표시할 참고 항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if(extraAddr !== ''){
                extraAddr = ' (' + extraAddr + ')';
            }
			
            // 조합된 참고 항목을 상세주소에 추가한다.
            addr += extraAddr;
            

			// 우편번호와 주소 정보를 해당 입력상자에 출력한다.
			$("#zipcode").val(data.zonecode);
			$("#address1").val(addr);
			
			// 커서를 상세주소 입력상자로 이동한다.
			$("#address2").focus();
       	}
	}).open();
}
