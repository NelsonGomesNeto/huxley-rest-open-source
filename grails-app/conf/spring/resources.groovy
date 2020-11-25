import com.thehuxley.atmosphere.PushHandler
import com.thehuxley.marshaller.CustomMarshallerRegistrar
import com.thehuxley.security.core.userdetails.HuxleyUserDetailsService
import com.thehuxley.security.authentication.dao.HuxleyDaoAuthenticationProvider
import com.thehuxley.security.web.access.expression.HuxleyDefaultWebSecurityExpressionHandler
import com.thehuxley.security.web.authentication.www.HuxleyAccessDeniedHandler
import com.thehuxley.security.web.authentication.www.HuxleyAuthenticationEntryPoint
import com.thehuxley.security.web.authentication.www.HuxleyAuthenticationFailureHandler
import com.thehuxley.security.web.authentication.www.HuxleyAuthenticationSuccessHandler
import com.thehuxley.security.web.authentication.www.HuxleyBasicAuthenticationEntryPoint
import com.thehuxley.security.web.authentication.www.HuxleyLogoutSuccessHandler

// Place your Spring DSL code here
beans = {

	userDetailsService(HuxleyUserDetailsService) {
		grailsApplication = ref('grailsApplication')
	}

	daoAuthenticationProvider(HuxleyDaoAuthenticationProvider) {
		userDetailsService = ref('userDetailsService')
		passwordEncoder = ref('passwordEncoder')
		userCache = ref('userCache')
		saltSource = ref('saltSource')
		preAuthenticationChecks = ref('preAuthenticationChecks')
		postAuthenticationChecks = ref('postAuthenticationChecks')
		authoritiesMapper = ref('authoritiesMapper')
		hideUserNotFoundExceptions = true
	}


//	logoutSuccessHandler(HuxleyLogoutSuccessHandler)
//
//	authenticationEntryPoint(HuxleyAuthenticationEntryPoint)

	basicAuthenticationEntryPoint(HuxleyBasicAuthenticationEntryPoint) {
		realmName = application.config.grails.plugin.springsecurity.basic.realmName // 'Grails Realm'
	}

//	authenticationFailureHandler(HuxleyAuthenticationFailureHandler)
//
//	authenticationSuccessHandler(HuxleyAuthenticationSuccessHandler)
//
//	accessDeniedHandler(HuxleyAccessDeniedHandler)

	publicMarshallerRegistrar(CustomMarshallerRegistrar) {
		grailsLinkGenerator = ref('grailsLinkGenerator')
	}

//	webExpressionHandler(HuxleyDefaultWebSecurityExpressionHandler) {
//		roleHierarchy = ref('roleHierarchy')
//		expressionParser = ref('voterExpressionParser')
//		permissionEvaluator = ref('permissionEvaluator')
//		securityService = ref('securityService')
//	}

   	multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver) {
        maxInMemorySize=1048576
        maxUploadSize=1048576
        uploadTempDir='/tmp'
    }

	pushHandler(PushHandler)
}
