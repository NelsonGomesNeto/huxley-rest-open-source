package com.thehuxley

class TestCase {


	static final def MIN_OUTPUT_SIZE = 1024 * 1024 //bytes (1MB)


	String input
	String output
	String tip
	Date dateCreated
	Date lastUpdated
	Boolean example = false
	double maxOutputSize = MIN_OUTPUT_SIZE
	int rank = 0
	int unrank = 0

	Problem problem

	static mapping = {
		input type: "text"
		output type: "text"
		tip type: "text"
	}

	static constraints = {
		tip nullable: true
		dateCreated nullable: true
		lastUpdated nullable: true
	}

	def beforeInsert() {
		maxOutputSize = getLength()
	}

	def beforeUpdate() {
		if (isDirty("output")) {
			maxOutputSize = getLength()
		}
	}

	private def getLength() {
		def length = 0
		if (output) {
			length = output.getBytes("UTF-8").length
		}
		length > MIN_OUTPUT_SIZE ? (length + (length * 0.1)) : MIN_OUTPUT_SIZE
	}

}
