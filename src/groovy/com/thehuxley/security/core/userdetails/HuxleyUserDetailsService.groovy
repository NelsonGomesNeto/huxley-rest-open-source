package com.thehuxley.security.core.userdetails

import com.thehuxley.AuthenticationHistory
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import org.joda.time.DateTime
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class HuxleyUserDetailsService extends GormUserDetailsService {

	@Override
	UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {

		def conf = SpringSecurityUtils.securityConfig
		String userClassName = conf.userLookup.userDomainClassName
		def dc = grailsApplication.getDomainClass(userClassName)
		if (!dc) {
			throw new IllegalArgumentException("The specified user domain class '$userClassName' is not a domain class")
		}

		Class<?> User = dc.clazz

		User.withTransaction { status ->
			def user = User.findWhere((conf.userLookup.usernamePropertyName): username)

			if (!user) {
				user = User.findWhere("email": username)
			}

			if (!user) {
				log.warn "User not found: $username"
				throw new NoStackUsernameNotFoundException()
			}


			def now = new Date()

			if (!AuthenticationHistory.findByUserAndAccessedDate(user as com.thehuxley.User, new DateTime(now).withTimeAtStartOfDay().toDate())) {
				new AuthenticationHistory(user: user, accessedDate: new DateTime(now).withTimeAtStartOfDay().toDate()).save()
			}

			Collection<GrantedAuthority> authorities = loadAuthorities(user, username, loadRoles)
			createUserDetails user, authorities
		}
	}

}
