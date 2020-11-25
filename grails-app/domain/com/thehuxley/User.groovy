package com.thehuxley

class User {

	transient springSecurityService

	String username
	String password
	Boolean enabled = true
	Boolean accountExpired = false
	Boolean accountLocked = false
	Boolean passwordExpired = false
    String email
    String name
	Date dateCreated
	Date lastUpdated
	String avatar = "default.png"

	Institution institution

	static hasOne = [profile: Profile]

	static hasMany = [groups: Group]

	static belongsTo = [Group]

	static searchable = {
		only = ["username", "name", "email"]
	}

	static transients = ["springSecurityService"]

	static mapping = {
		groups joinTable: [name: "user_cluster", column: "user_id", key: "cluster_id"]
	}

	static constraints = {
		username blank: false, unique: true, matches: "[a-zA-Z0-9]+", size: 1..255, nullable: false
		password blank: false
        email blank: false, unique: true, email: true
		name blank: false
		dateCreated nullable: true
		lastUpdated nullable: true
		avatar nullable: true
		institution nullable: true , lazy: false
		profile nullable: true , lazy: false
	}


	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role }
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty("password")) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

}
