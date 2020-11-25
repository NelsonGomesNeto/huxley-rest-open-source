package com.thehuxley

class QuestionnaireUserPenalty {

	Date dateCreated
	Date lastUpdated
	Double penalty = 0.0

	QuestionnaireProblem questionnaireProblem
	QuestionnaireUser questionnaireUser

	static constraints = {
		questionnaireProblem nullabe: false, blank: false, unique: "questionnaireUser"
		questionnaireUser nullabe: false, blank: false
		penalty nullabe: false, blank: false
	}
}
