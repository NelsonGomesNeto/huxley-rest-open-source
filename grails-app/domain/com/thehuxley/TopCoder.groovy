package com.thehuxley

class TopCoder {

	Double points
	Long position

	User user

	static constraints = {
		user unique: true
		position nullable: true
	}

}
