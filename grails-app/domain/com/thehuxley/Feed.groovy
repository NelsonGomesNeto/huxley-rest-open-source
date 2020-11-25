package com.thehuxley

class Feed {
	enum Type {
		USER_SUBMISSION_STATUS
	}

	Date dateCreated
	Date lastUpdated
	Type type
	Map body
	User recipient

	static mapping = {
		type enumType: "ordinal"
	}

	static constraints = {
		body nullable: true
		type nullable: false
	}
}
