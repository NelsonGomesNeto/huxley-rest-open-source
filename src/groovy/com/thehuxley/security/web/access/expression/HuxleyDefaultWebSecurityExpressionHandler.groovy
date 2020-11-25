package com.thehuxley.security.web.access.expression

import com.thehuxley.SecurityService
import org.springframework.security.access.expression.SecurityExpressionOperations
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.Authentication
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler

class HuxleyDefaultWebSecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {

	SecurityService securityService

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation filterInvocation) {
		HuxleyWebSecurityExpressionRoot root = new HuxleyWebSecurityExpressionRoot(authentication, filterInvocation);
		root.setPermissionEvaluator(this.getPermissionEvaluator());
		root.setTrustResolver(this.trustResolver);
		root.setRoleHierarchy(this.getRoleHierarchy());
		root.setSecurityService(securityService)
		return root;
	}
}
