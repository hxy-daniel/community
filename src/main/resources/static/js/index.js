$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
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