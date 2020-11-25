package com.thehuxley

class Group {

    String name
	String url
	String description
	String accessKey
	Date startDate = new Date()
	Date endDate = new Date().plus(6 * 30)
	Date dateCreated
	Date lastUpdated

	static belongsTo = [institution: Institution]

	static hasMany = [users: User, questionnaires: Questionnaire]

	static searchable = true

    static mapping = {
        table "cluster"
		users joinTable: [name: "user_cluster", key: "user_id", column: "cluster_id"]
    }

    static constraints = {
		name blank: false, unique: true
        url unique: true, nullable: false, matches: "[a-zA-Z0-9-]+"
		description nullable: true
		accessKey nullable: true
		startDate nullable: false
		endDate nullable: false
    }

}