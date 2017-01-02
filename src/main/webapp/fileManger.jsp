<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>elFinder 2.1</title>

		<!-- jQuery and jQuery UI (REQUIRED) -->
		<link rel="stylesheet" type="text/css" href="${ctx}/resources/js/jqueryui/jquery-ui.theme.css">
		<script src="${ctx}/resources/js/jquery/jquery-2.1.4.min.js" ></script>
		<script src="${ctx}/resources/js/jqueryui/jquery-ui.min.js" ></script>

		<!-- elFinder CSS (REQUIRED) -->
		<link rel="stylesheet" type="text/css" href="${ctx}/resources/elfinder/2.1.11/css/elfinder.full.css">
		<link rel="stylesheet" type="text/css" href="${ctx}/resources/elfinder/2.1.11/css/theme.css">

		<!-- elFinder JS (REQUIRED) -->
		<script src="${ctx}/resources/elfinder/2.1.11/js/elfinder.full.js"></script>

		<!-- elFinder translation (OPTIONAL) -->
		<script src="${ctx}/resources/elfinder/2.1.11/js/i18n/elfinder.zh_CN.js"></script>

		<!-- elFinder initialization (REQUIRED) -->
		<script type="text/javascript" charset="utf-8">
			// Documentation for client options:
			// https://github.com/Studio-42/elFinder/wiki/Client-configuration-options
			$(document).ready(function() {
				$('#elfinder').elfinder({
					url : 'elfinder-servlet/connector',
					lang: 'zh_CN'                    // language (OPTIONAL)
				});
			});
		</script>
	</head>
	<body>

		<!-- Element where elFinder will be created (REQUIRED) -->
		<div id="elfinder"></div>

	</body>
</html>
