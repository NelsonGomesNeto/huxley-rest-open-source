package com.thehuxley.security.web.authentication.www

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HuxleyLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_OK);
	}

}
