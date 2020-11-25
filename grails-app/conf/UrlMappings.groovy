class UrlMappings {

	//static excludes = ["/js/*", "/css/*", "/images/*", "/fonts/*"]

	static mappings = {

		"/login/$action?/$id?(.$format)?"(controller: "login")

		//USER

		"/v1/user" (controller: "CurrentUser", action: [GET: "getCurrentUser", PUT: "update"])
		"/v1/user/stats" (controller: "CurrentUser", action: "getUserData")
		"/v1/user/password" (controller: "CurrentUser", action: [PUT: "updatePassword"])
		"/v1/user/avatar" (controller: "CurrentUser", action: [GET: "getAvatar", POST: "uploadAvatar", PUT: "cropAvatar"])
		"/v1/user/institutions/$institutionId?" (controller: "CurrentUser", action: "getInstitutions")
		"/v1/user/groups/$groupId?" (controller: "CurrentUser", action: "getGroups")
		"/v1/user/problems/$problemId?" (controller: "CurrentUser", action: "getProblems")
		"/v1/user/problems/$problemId/examples/$testCaseId?" (controller: "Problem", action: "getExampleTestCases")
		"/v1/user/problems/$problemId/stats" (controller: "CurrentUser", action: "getProblemData")
		"/v1/user/problems/$problemId/submissions/$submissionId?" (controller: "CurrentUser", action: [GET: "getProblemSubmissions", POST: "createSubmission"])
		"/v1/user/problems/suggestions" (controller: "CurrentUser", action: "getProblemSuggestion")
		"/v1/user/submissions/$submissionId?" (controller: "CurrentUser") {
			action = [GET : "getSubmissions", POST : "createSubmission" ]
		}
		"/v1/user/quizzes/$questionnaireId?" (controller: "CurrentUser", action: "getQuestionnaires")
		"/v1/user/quizzes/$questionnaireId/problems/$problemId?" (controller: "CurrentUser", action: "getQuestionnaireProblems")
		"/v1/user/quizzes/$questionnaireId/problems/$problemId/submissions/$submissionId?" (controller: "CurrentUser", action: "getQuestionnaireProblemSubmissions")
		"/v1/user/messages/$messageId?" (controller: "CurrentUser", action: [
		        GET: "getMessages",
				POST: "sendMessage",
				PUT: "editMessage",
				DELETE: "deleteMessage"
		])
		"/v1/user/messages/$messageId/read" (controller: "CurrentUser", action: [
				POST: "markMessageAsRead"
		])
		"/v1/user/messages/$messageId/response" (controller: "CurrentUser", action: [
				POST: "responseMessage"
		])
		"/v1/user/messages/count" (controller: "CurrentUser", action: [
				POST: "getMessageCount"
		])
		"/v1/user/contact" (controller: "CurrentUser", action: [
		        POST: "sendContactEmail"
		])
		"/v1/user/feed/$feedId?" (controller: "CurrentUser", action: [
				GET: "getUserFeed",
				POST: "createFeedTest"
		])





		//USERS

		"/v1/users" (resources: "User")
		"/v1/users/validate" (controller: "User", action: [POST: "validate"])
		"/v1/users/avatar/$key" (controller: "User", action: "getAvatarByKey")
		"/v1/users/password/$key" (controller: "User", action: [PUT: "updatePassword"])
		"/v1/users/recoveryPassword" (controller: "User", action: [POST: "recoveryPassword"])
		"/v1/users/anonymizer" (controller: "User", action: [POST: "anonymizer"])
		"/v1/users/$userId/stats" (controller: "User", action: "getUserData")
		"/v1/users/$userId/avatar" (controller: "User", action: "getAvatar")
		"/v1/users/$userId/institutions/$institutionId?" (controller: "User", action: "getInstitutions")
		"/v1/users/$userId/groups/$groupId?" (controller: "User", action: "getGroups")
		"/v1/users/$userId/problems/$problemId?" (controller: "User", action: "getProblems")
		"/v1/users/$userId/problems/$problemId/examples/$testCaseId?" (controller: "Problem", action: "getExampleTestCases")
		"/v1/users/$userId/problems/$problemId/stats" (controller: "User", action: "getProblemData")
		"/v1/users/$userId/problems/$problemId/submissions/$submissionId?" (controller: "User", action: "getProblemSubmissions")
		"/v1/users/$userId/problems/suggestions" (controller: "User", action: "getProblemSuggestion")
		"/v1/users/$userId/submissions/$submissionId?" (controller: "User", action: "getSubmissions")
		"/v1/users/$userId/quizzes/$questionnaireId?" (controller: "User", action: "getQuestionnaires")
		"/v1/users/$userId/quizzes/$questionnaireId/problems/$problemId?" (controller: "User", action: "getQuestionnaireProblems")
		"/v1/users/$userId/quizzes/$questionnaireId/problems/$problemId/addPenalty" (controller: "Questionnaire", action: "addPenalty")
		"/v1/users/$userId/quizzes/$questionnaireId/problems/$problemId/removePenalty" (controller: "Questionnaire", action: "removePenalty")
		"/v1/users/$userId/quizzes/$questionnaireId/problems/$problemId/penalty" (controller: "Questionnaire", action: [
		        POST: "addPenalty",
				PUT: "addPenalty",
				DELETE: "removePenalty",
		])
		"/v1/users/$userId/quizzes/$questionnaireId/problems/$problemId/submissions/$submissionId?" (controller: "User", action: "getQuestionnaireProblemSubmissions")



		//INSTITUTIONS

		"/v1/institutions" (resources: "Institution")
		"/v1/institutions/validate" (controller: "Institution", action: [POST: "validate"])
		"/v1/institutions/logo/$key" (controller: "Institution", action: "getLogoByKey")
		"/v1/institutions/$institutionId/logo" (controller: "Institution", action: [
				GET: "getLogo",
				POST: "uploadLogo",
				PUT: "cropImage"
		])
		"/v1/institutions/$institutionId/users/$userId?" (controller: "Institution", action: [
				GET: "getUsers",
				PUT: "addUser",
				DELETE: "removeUser"
		])
		"/v1/institutions/$institutionId/users/add" (controller: "Institution", action: [POST: "addUsers"])
		"/v1/institutions/$institutionId/changeStatus" (controller: "Institution", action: [PUT: "changeStatus"])
		"/v1/institutions/$institutionId/groups/$groupId?" (controller: "Institution", action: "getGroups")
		"/v1/institutions/$institutionId/normalizeRoles" (controller: "Institution", action: [POST: "normalizeRoles"])



		//GROUPS


		"/v1/groups" (controller: "Group", action: [
		        GET: "index",
				POST: "save"
		])
		"/v1/groups/$id" (controller: "Group", action: [
				GET: "show",
				PUT: "update"
		])
		"/v1/groups/validate" (controller: "Group", action: [POST: "validate"])
		"/v1/groups/$groupId/forceQuizzesUpdate" (controller: "Questionnaire", action: [POST: "forceQuizzesUpdate"])

		"/v1/groups/join/$key?" (controller: "Group", action: [POST: "addByKey", PUT: "addByKey"])
		"/v1/groups/$groupId/key" (controller: "Group", action: [GET: "getKey", PUT: "refreshKey"])
		"/v1/groups/key/$key?" (controller: "Group", action: "getByKey")
		"/v1/groups/$groupId/users/$userId?" (controller: "Group", action: [
				GET: "getUsers",
				PUT: "addUser",
				DELETE: "removeUser"
		])
		"/v1/groups/$groupId/users/failingStudents" (controller: "Group", action: "getFailingStudents")
		"/v1/groups/$groupId/stats" (controller: "Group", action: "getData")
		"/v1/groups/$groupId/users/add" (controller: "Group", action: [POST: "addUsers"])
		"/v1/groups/$groupId/quizzes/$questionnaireId?" (controller: "Group", action: "getQuestionnaires")
		"/v1/groups/$groupId/submissions/$submissionId?" (controller: "Group", action: "getSubmissions")
		"/v1/groups/normalizeUrl" (controller: "Group", action: [POST: "normalizeUrlGroups"])


		//PROBLEMS

		"/v1/problems" (resources: "Problem")
		"/v1/problems/validate" (controller: "Problem", action: [POST: "validate"])
		"/v1/problems/$problemId/stats" (controller: "Problem", action: "getData")
		"/v1/problems/$problemId/oracle/$hash?" (controller: "Problem", action: [POST: "sendToOracle", GET: "getOracleConsult"])
		"/v1/problems/$problemId/examples/$testCaseId?" (controller: "Problem", action: "getExampleTestCases")
		"/v1/problems/$problemId/submissions/reevaluate" (controller: "Submission", action: [POST: "reevaluateByProblem"])
		"/v1/problems/$problemId/submissions/$submissionId?" (controller: "Problem", action: "getSubmissions")
		"/v1/problems/$problemId/testcases-list" (controller: "Problem", action: "getTestCaseList")
		"/v1/problems/$problemId/testcases/$testCaseId/input" (controller: "Problem", action: "getInputTestCasePlainText")
		"/v1/problems/$problemId/testcases/$testCaseId/output" (controller: "Problem", action: "getOutputTestCasePlainText")
		"/v1/problems/$problemId/testcases/$testCaseId?" (controller: "Problem", action: [
				GET: "getTestCases",
				POST: "saveTestCase",
				PUT: "updateTestCase",
				DELETE: "deleteTestCase",
		])
		"/v1/problems/image/$key?" (controller: "Problem", action: [GET: "getImageByKey", POST: "uploadImage"])


		//PUSH
		"/v1/pull" (controller: "Push", action: "pull")
		"/v1/push" (controller: "Push", action: "publish")


		//SUBMISSIONS

		"/v1/submissions" (resources: "Submission")
		"/v1/submissions/reevaluate" (controller: "Submission", action: [POST: "reevaluateAll"])
		"/v1/submissions/$submissionId/reevaluate" (controller: "Submission", action: [POST: "reevaluate"])
		"/v1/submissions/$submissionId/sourcecode" (controller: "Submission", action: "getSubmissionFile")
		"/v1/submissions/$submissionId/diff" (controller: "Submission", action: "getDiffFile")



		//QUESTIONNAIRES

		"/v1/quizzes" (resources: "Questionnaire")
		"/v1/quizzes/$questionnaireId/stats" (controller: "Questionnaire", action: "getData")
		"/v1/quizzes/$questionnaireId/forceUpdate" (controller: "Questionnaire", action: [POST: "forceUpdate"])
		"/v1/quizzes/$questionnaireId/clone" (controller: "Questionnaire", action: [POST: "clone"])
		"/v1/quizzes/$questionnaireId/problems/$problemId?" (controller: "Questionnaire", action: [
				GET: "getProblems",
				POST: "addProblem",
				PUT: "addProblem",
				DELETE: "removeProblem"
		])
		"/v1/quizzes/$questionnaireId/problems/$problemId/examples/$testCaseId?" (controller: "Problem", action: "getExampleTestCases")
		"/v1/quizzes/$questionnaireId/similarities" (controller: "Questionnaire", action: "getSimilarities")
		"/v1/quizzes/$questionnaireId/similarities/$plagiarismId" (controller: "Questionnaire", action: "getSimilarity")
		"/v1/quizzes/$questionnaireId/similarities/$plagiarismId/confirm" (controller: "Questionnaire", action: [POST: "confirmSimilarity"])
		"/v1/quizzes/$questionnaireId/similarities/$plagiarismId/discard" (controller: "Questionnaire", action: [POST: "discardSimilarity"])
		"/v1/quizzes/$questionnaireId/problems/$problemId/submissions/$submissionId?" (controller: "Questionnaire", action: "getProblemSubmissions")
		"/v1/quizzes/$questionnaireId/users/$userId?" (controller: "Questionnaire", action: "getUsers")
		"/v1/quizzes/$questionnaireId/users/$userId/problems/$problemId?" (controller: "User", action: "getQuestionnaireProblems")
		"/v1/quizzes/$questionnaireId/export" (controller: "File", action : "createQuestionnaireExportKey")
		"/v1/quizzes/$questionnaireId/submissions/$submissionId?" (controller: "Questionnaire", action: "getSubmissions")



		//TOPCODER

		"/v1/topcoders" (resources: "TopCoder")
		"/v1/topcoders/updateNds" (controller: "TopCoder", action: "updateNds")
		"/v1/topcoders/$userId/refreshTopCoder" (controller: "TopCoder", action: [POST: "refreshTopCoder"])
		"/v1/topcoders/refreshTopCoder" (controller: "TopCoder", action: [POST: "refreshTopCoder"])


		//TOPICS

		"/v1/topics" (resources: "Topic")



		//LANGUAGES

		"/v1/languages" (resources: "Language")



		//LICENSE PACK

		"/v1/licenses" (resources: "LicensePack")



		//PENDENCY

		"/v1/pendencies" (resources: "Pendency")


		//ADMIN
		"/v1/admin/tokens" (controller: "Admin", action: "getAccessTokens")
		"/v1/version" (controller: "Admin", action: "getVersion")

		//FILE
		"/v1/download/$key" (controller: "File", action: "download")



		//ERRORS

		"400" (controller: "Error", action: "badRequest")

		"401" (controller: "Error", action: "unauthorized")

		"403" (controller: "Error", action: "forbidden")

		"404" (controller: "Error", action: "notFound")

		"406" (controller: "Error", action: "notAcceptable")

		"500" (controller: "Error", action: "internalServerError")
	}

}