package com.thehuxley

class Profile {

	String photo
	String smallPhoto
	Integer problemsCorrect //pre-processing
	Integer problemsTried  //pre-processing
	Integer submissionCorrectCount //pre-processing
	Integer submissionCount //pre-processing
	Long topCoderPosition //pre-processing
	Double topCoderScore //pre-processing
	Date lastLogin
	Date dateCreated
	Date lastUpdated

	User user
	Institution institution

	static constraints = {
		user unique: true
		institution nullable: false
	}

	static mapping = {
		problemsTried column: "problems_tryed"
	}
}
