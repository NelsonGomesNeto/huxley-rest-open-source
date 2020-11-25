package com.thehuxley

class Plagiarism implements Serializable {

	enum Status {
		WAITING, CONFIRMED, DISCARDED
	}

	Submission submission1
	Submission submission2
	double percentage
	Status status = Status.WAITING

	static mapping = {
		table "plagium"
		status enumType: "ordinal"
	}

}
