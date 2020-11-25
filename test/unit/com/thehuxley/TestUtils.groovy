package com.thehuxley

class TestUtils {

	final static int USER_COUNT = 10
	final static int GROUP_COUNT = 10
	final static int USER_GROUP_COUNT = 4
	final static int INSTITUTION_APPROVED_COUNT = 4
	final static int USER_INSTITUTION_COUNT = 4
	final static int PROBLEM_COUNT = 10
	final static int SUBMISSION_COUNT = 10
	final static int LANGUAGE_COUNT = 4
	final static int TEST_CASE_COUNT = 4
	final static int QUESTIONNAIRE_COUNT = 10
	final static int TOPIC_COUNT = 10


	def static populateDataBase() {
		if (Institution.count() == 0) {
			(1..INSTITUTION_APPROVED_COUNT).each {
				new Institution(
						name: "Institution $it",
						phone: "000-0000",
						photo: "abcdefg.png",
						status: Institution.Status.APPROVED
				).save(failOnError: true, flush: true)
			}
		}

		if (Group.count() == 0) {
			(1..GROUP_COUNT).each {
				new Group(
						name: "Group $it",
						url: "group$it",
						description: "abcde abcde abcde",
						accessKey: "a1b2c3d4",
						startDate: new Date(),
						endDate: new Date() + 1,
						institution: Institution.load(1L)
				).save(failOnError: true, flush: true)
			}
		}

		if (User.count() == 0) {
			(1..USER_COUNT).each {
				def user = new User(
						name: "User Name $it",
						email: "username+$it@thehuxley.com",
						username: "username$it",
						password: "password"
				).save(failOnError: true, flush: true)

				(1..USER_INSTITUTION_COUNT).each {
					new UserInstitution(
							user: user,
							institution: Institution.load(it)
					).save(failOnError: true, flush: true)
				}

				(1..USER_GROUP_COUNT).each {
					new UserGroup(
							user: user,
							group: Group.load(it)
					).save(failOnError: true, flush: true)
				}

			}
		}


		if (Problem.count() == 0) {
			(1..PROBLEM_COUNT).each {
				new Problem(
						name: "Problem $it",
						description: "abcde abcde abcde",
						inputFormat: "abcde abcde abcde",
						outputFormat: "abcde abcde abcde",
						source: "abcde abcde abcde",
						timeLimit: 1,
						nd: 1,
						status: Problem.Status.ACCEPTED,
						userApproved: User.load(1),
						userSuggest: User.load(1)
				).save(failOnError: true, flush: true)
			}
		}

		if (Language.count() == 0) {
			(1..LANGUAGE_COUNT).each {
				new Language(
						name: "Language $it",
						plagConfig: "abcde abcde abcde",
						execParams: "abcde abcde abcde",
						compileParams: "abcde abcde abcde",
						compiler: "compile$it",
						script: "abcde abcde abcde",
						extension: ".$it"
				).save(failOnError: true, flush: true)
			}
		}

		if (TestCase.count() == 0) {
			(1..TEST_CASE_COUNT).each {
				new TestCase(
						input: "abcde abcde abcde",
						output: "abcde abcde abcde",
						type: 0,
						tip: "abcde abcde abcde",
						problem: Problem.load(1)
				).save(failOnError: true, flush: true)
			}
		}

		if (Submission.count() == 0) {
			(1..SUBMISSION_COUNT).each {
				new Submission(
						time: 0.1,
						tries: 1,
						diffFile: "abcde",
						submission: "abcde",
						output: "abcde",
						errorMsg: "abcde",
						comment: "abcde",
						submissionDate: new Date(),
						evaluation: Submission.Evaluation.CORRECT,
						user: User.load(1),
						problem: Problem.load(1),
						language: Language.load(1),
						testCase: TestCase.load(1)
				).save(failOnError: true, flush: true)
			}
		}

		if (Questionnaire.count() == 0) {
			(1..QUESTIONNAIRE_COUNT).each {
				new Questionnaire(
						title: "Questionnaire $it",
						description: "abcde abcde abcde",
						score: 10,
						startDate: new Date() - 1,
						endDate:new Date() + 1,
						group: Group.load(1)
				).save(failOnError: true, flush: true)
			}
		}

		if (QuestionnaireUser.count == 0) {
			(1..USER_COUNT).each { user ->
				(1..QUESTIONNAIRE_COUNT).each {
					new QuestionnaireUser(
							score: 1,
							questionnaire: Questionnaire.load(it),
							user: User.load(user)
					).save(failOnError: true, flush: true)
				}
			}
		}

		if (QuestionnaireProblem.count == 0) {
			(1..PROBLEM_COUNT).each {
				new QuestionnaireProblem(
						score: 1,
						questionnaire: Questionnaire.load(1),
						problem: Problem.load(it)
				).save(failOnError: true, flush: true)
			}
		}

		if (Topic.count == 0) {
			(1..TOPIC_COUNT).each {
				new Topic(
					name: "Topic $it"
				).save(failOnError: true, flush: true)
			}
		}
	}
}
