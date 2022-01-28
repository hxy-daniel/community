function like(btn, entityType, entityId, targetUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like/doLike",
        {"entityType": entityType, "entityId": entityId, "targetUserId":targetUserId, "postId":postId},
        function (data) {
            var data = $.parseJSON(data);
            if (data.code == 200) {
                $(btn).children("b").text(data.isLiked== 1 ? '已赞' : '赞');
                $(btn).children("i").text(data.likeCount);
            } else {
                alert(data.msg)
            }
        }
    );
}