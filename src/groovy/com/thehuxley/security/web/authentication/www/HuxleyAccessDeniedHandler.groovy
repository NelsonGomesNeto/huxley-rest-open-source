package com.thehuxley.security.web.authentication.www

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HuxleyAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

}