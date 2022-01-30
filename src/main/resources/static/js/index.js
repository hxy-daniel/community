$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
//    var token = $("meta[name='_csrf']").attr("content");
//    var header = $("meta[name='_csrf_header']").attr("content");
//    $(document).ajaxSend(function(e, xhr, options){
//        xhr.setRequestHeader(header, token);
//    });

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(CONTEXT_PATH + "/discussPost/addDiscussPost",
		{"title": title, "content": content}, function (data) {
			var jsonData = $.parseJSON(data);
			$("#hintBody").text(jsonData.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if (data.code = 200) {
					window.location.reload();
				}
			}, 2000);
		});


}