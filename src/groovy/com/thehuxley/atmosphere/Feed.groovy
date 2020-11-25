package com.thehuxley.atmosphere

class Feed {

	enum Type {
		USER_SUBMISSION_STATUS,
		USER_ORACLE_REQUEST,
		GROUP_SCRAP,
		GROUP_MEMBER_SOLVED_PROBLEM,
		USER_CHAT_MESSAGE
	}

	Type type
	Map body

}
