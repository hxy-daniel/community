function like(btn, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like/doLike",
        {"entityType": entityType, "entityId": entityId},
        function (data) {
            var data = $.parseJSON(data);
            if (data.code == 200) {
                $(btn).children("b").text(data.isLiked ? '已赞' : '赞');
                $(btn).children("i").text(data.likeCount);
            } else {
                alert(data.msg)
            }
        }
    );
}