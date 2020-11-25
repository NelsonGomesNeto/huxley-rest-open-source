package com.thehuxley.security.web.authentication.www

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HuxleyAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_OK);
	}

}
