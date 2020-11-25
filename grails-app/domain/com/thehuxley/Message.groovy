package com.thehuxley

class Message {

	enum Type {
		USER_CHAT,
		GROUP_BULLETIN_BOARD,
	}

	String subject
	String body
	Boolean unread = true
	Boolean deleted = false
	Boolean firstMessage = false
	Date dateCreated
	Date lastUpdated
	Date readDate
	Map<String, String> information

	Type type
	Group group
	User sender
	User recipient

	static hasMany = [responses: Message]

	static mapping = {
		body type: "text"
		type enumType: "ordinal"
	}

	static constraints = {
		subject nullable: true
		body nullable: false, blank: false
		readDate nullable: true
		information nullable: true
		group nullable: true
		responses nullable: true
	}

}
