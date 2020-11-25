package com.thehuxley

class Questionnaire {

	String title
	String description
	Double score = 0.0
	Date startDate
	Date endDate
	Date dateCreated
	Date lastUpdated

	Group group

	static belongsTo = Group

	static hasMany = [users: User, problems: Problem]

	static mapping = {
		description type: "text"
		users joinTable: [name: "questionnaire_user", column: "user_id", key: "questionnaire_id"]
		problems joinTable: [name: "questionnaire_problem", column: "problem_id", key: "questionnaire_id"]
	}

	static constraints = {
		title blank: false
		description nullable: true, blank: true
		startDate nullable: false
		endDate nullable: false
	}

}