package com.thehuxley

class UserGroup {

	enum Role {
		STUDENT, TEACHER_ASSISTANT, TEACHER
	}

	Boolean enabled = true

	User user
	Group group
	Role role = Role.STUDENT

	static constraints = {
		user unique: ['group']
	}

	static mapping = {
		table "user_cluster"
		role enumType: "ordinal"
		group column: "cluster_id"
	}

}
