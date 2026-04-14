function openChatPopup(chatroomIdx) {
    if (!chatroomIdx) {
        alert('채팅방 정보가 없습니다.');
        return;
    }

    // 컨텍스트 패스 없으면 그냥 /chats/ 로 시작한다고 가정
    var url = '/chats/' + chatroomIdx;

    window.open(
        url,
        'chat_' + chatroomIdx,
        'width=420,height=600,menubar=no,toolbar=no,location=no,resizable=yes,scrollbars=yes'
    );
}/**
 * 
 */