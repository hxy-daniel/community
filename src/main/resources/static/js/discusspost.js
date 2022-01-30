$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discussPost/top",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 200) {
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discussPost/wonderful",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 200) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discussPost/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 200) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}

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