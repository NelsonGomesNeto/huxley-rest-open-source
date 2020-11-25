package com.thehuxley

class QuestionnaireUser implements Serializable {

	Double score = 0.0
	Integer submissionsCount = 0//pre-processing
	Integer problemsTried = 0//pre-processing
	Integer problemsCorrect = 0//pre-processing

	Questionnaire questionnaire
	User user

	static mapping = {
		table "questionnaire_user"
	}

	static constraints = {
    	user unique: ['questionnaire']
  	}

}
