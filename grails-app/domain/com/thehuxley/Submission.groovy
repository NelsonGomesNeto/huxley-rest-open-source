package com.thehuxley

class Submission {

	enum Evaluation {
		CORRECT,
		WRONG_ANSWER,
		RUNTIME_ERROR,
		COMPILATION_ERROR,
		EMPTY_ANSWER,
		TIME_LIMIT_EXCEEDED,
		WAITING,
		EMPTY_TEST_CASE,
		WRONG_FILE_NAME,
		PRESENTATION_ERROR,
		HUXLEY_ERROR
	}

	Double time = -1
	Integer tries = 0
	String diffFile
	String submission
	String output
	String errorMsg
	String comment
	Date submissionDate = new Date()
	Evaluation evaluation = Evaluation.WAITING

	User user
	Problem problem
	Language language
	TestCase testCase

	static searchable = {
		only = ["time", "tries", "submissionDate", "evaluation"]
		user parent: true, component: true
		problem parent: true, component: true
		language parent: true, component: true
	}

	static constraints = {
		submission blank: false, nullable: false
		diffFile blank: true, nullable: true
		tries nullable: false
		output nullable: true
		submissionDate nullable: false
		errorMsg nullable: true
		comment nullable: true
		testCase nullable: true
	}

	static mapping = {
		evaluation enumType: "ordinal"
		diffFile type: "text"
		output type: "text"
		errorMsg type: "text"
		comment type: "text"
		problem cascade: "evict"
	}

}
