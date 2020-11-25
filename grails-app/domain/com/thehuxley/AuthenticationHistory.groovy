package com.thehuxley

import org.joda.time.DateTime

class AuthenticationHistory {

	Date dateCreated
	Date accessedDate = new DateTime().withTimeAtStartOfDay().toDate()

	User user

	static mapping = {
		version false
	}

}
