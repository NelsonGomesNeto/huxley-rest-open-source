package com.thehuxley

import org.joda.time.DateTime

class Authentication {

	String token
	String username

	Date dateCreated
	Date lastUpdated
	Date lastAccess = new DateTime().withTimeAtStartOfDay().toDate()
	Integer accessCount = 1

	static mapping = {
		version false
	}

	def afterInsert() {
		def now = new Date()
		def user = User.findByUsername(username)

		if (!AuthenticationHistory.findByUserAndAccessedDate(user, new DateTime(now).withTimeAtStartOfDay().toDate())) {
			new AuthenticationHistory(user: user, accessedDate: new DateTime(now).withTimeAtStartOfDay().toDate()).save()
		}
	}

	def afterLoad() {
		def now = new DateTime().withTimeAtStartOfDay()
		if (lastAccess < now.toDate()) {
			def user = User.findByUsername(username)

			if (!AuthenticationHistory.findByUserAndAccessedDate(user, now.toDate())) {
				new AuthenticationHistory(user: user, accessedDate: now.toDate()).save()
			}

			accessCount++
			lastAccess = now.toDate()
			this.save()
		}
	}

}
