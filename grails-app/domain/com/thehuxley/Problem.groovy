package com.thehuxley

class Problem {

    enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    String name
    String description
    String inputFormat
    String outputFormat
    String source
    Integer level = 1
    Integer timeLimit = 1
    Double nd
    Date dateCreated
    Date lastUpdated
    Date lastUserUpdate
    Status status = Status.PENDING

	User userApproved
	User userSuggest

	static hasMany = [topics: Topic]

	static belongsTo = [Topic]

    static mapping = {
        description type: "text"
        inputFormat type: "text"
        outputFormat type: "text"
        status enumType: "ordinal"
		topics joinTable: [name: "topic_problems", key: "problem_id", column: "topic_id"]
    }

    static constraints = {
		name blank: false, unique: true
		description blank: true, nullable: true
		userApproved nullable: true
		userSuggest nullable: true
		inputFormat blank: true, nullable: true
		outputFormat blank: true, nullable: true
		source nullable: true
		level range: 1..10
		nd nullable: true
		lastUserUpdate nullable: true
	}

	def beforeInsert() {
		nd = level
	}

	def beforeUpdate() {
		if (!nd) {
			nd = level
		}
	}
}
