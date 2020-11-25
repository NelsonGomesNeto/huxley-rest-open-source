package com.thehuxley

class UserProblem { //pre-processing

	Submission.Evaluation status

	Problem problem
	User user

	static mapping = {
		table "user_problem"
		status enumType: "ordinal"
	}

}
