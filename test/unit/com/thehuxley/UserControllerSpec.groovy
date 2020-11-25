package com.thehuxley

import com.thehuxley.marshaller.CustomMarshallerRegistrar
import grails.plugin.redis.RedisService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import spock.lang.*


@TestFor(UserController)
@Domain([
		User,
		Group,
		Institution,
		Role,
		UserRole,
		Profile,
		Questionnaire,
		Problem,
		Topic,
		Submission,
		Language,
		TestCase,
		UserInstitution,
		UserGroup,
		UserProblem,
		QuestionnaireUser,
		QuestionnaireProblem
])
@TestMixin(HibernateTestMixin)
class UserControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def userService = new UserService()
		def institutionService = new InstitutionService()
		def groupService = new GroupService()
		def problemService = new ProblemService()
		def submissionService = new SubmissionService()
		def questionnaireService = new QuestionnaireService()

		controller.userService = userService
		controller.institutionService = institutionService
		controller.groupService = groupService
		controller.problemService = problemService
		controller.submissionService = submissionService
		controller.questionnaireService = questionnaireService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the user's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.userService.redisService = redisServiceMock.createMock()

		when:
		controller.show(1L)

		then:
		response.json.name == "User Name 1"
		response.json.email == null
		response.json.username == null
		response.json.password == null
	}

	void "when call index must return a list with all users"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.userService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.USER_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.userService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.index()

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.USER_COUNT - offset)
		response.json.id.contains(TestUtils.USER_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getInstitutions must return users' institution"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.institutionService.redisService = redisServiceMock.createMock()

		when:
		controller.getInstitutions(1L, 1L)

		then:
		response.json.name == "Institution 1"
		response.json.phone == null
		response.json.photo == "abcdefg.png"
		response.json.status == null
		response.json.active == true
		response.json.role == UserInstitution.Role.STUDENT as String
	}


	void "when call getInstitutions must return a list with users' institutions"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.institutionService.redisService = redisServiceMock.createMock()

		when:
		controller.getInstitutions(1L, null)

		then:
		response.json.size() == TestUtils.USER_INSTITUTION_COUNT
	}


	void "when call getInstitutions with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.institutionService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getInstitutions(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.INSTITUTION_APPROVED_COUNT - offset)
		response.json.id.contains(TestUtils.INSTITUTION_APPROVED_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getGroups must return users' group"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.getGroups(1L, 1L)

		then:
		response.json.name == "Group 1"
		response.json.url == "group1"
		response.json.description == "abcde abcde abcde"
		response.json.accessKey == null
		response.json.startDate == formatter.parseDateTime(response.json.startDate as String).toDateTime().toString(formatter)
		response.json.endDate == formatter.parseDateTime(response.json.endDate as String).toDateTime().toString(formatter)
		response.json.institution.name == "Institution 1"
	}


	void "when call getGroups must return a list with users' groups"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()

		when:
		controller.getGroups(1L, null)

		then:
		response.json.size() == TestUtils.USER_GROUP_COUNT
	}

	void "when call getGroups with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getGroups(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.USER_GROUP_COUNT - offset)
		response.json.id.contains(TestUtils.USER_GROUP_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getProblems must return users' problem"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		when:
		controller.getProblems(1L, 1L)

		then:
		response.json.name == "Problem 1"
		response.json.description == "abcde abcde abcde"
		response.json.inputFormat == "abcde abcde abcde"
		response.json.outputFormat == "abcde abcde abcde"
		response.json.source == "abcde abcde abcde"
		response.json.timeLimit == 1
		response.json.nd == 1.0
		response.json.bestRunTime.evaluation == Submission.Evaluation.CORRECT as String
		response.json.userAttempts.bestRunTime.evaluation == Submission.Evaluation.CORRECT as String
		response.json.suggestedBy.id == 1
	}

	void "when call getProblems must return a list with users' problems"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		when:
		controller.getProblems(1L, null)

		then:
		response.json.size() == TestUtils.PROBLEM_COUNT
	}

	void "when call getProblems with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getProblems(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.PROBLEM_COUNT - offset)
		response.json.id.contains(TestUtils.PROBLEM_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).ids
	}


	void "when call getSubmissions must return users' submission"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.getSubmissions(1L, 1L)

		then:
		response.json.time == 0.1
		response.json.tries == 1
		response.json.diffFile == null
		response.json.submission == null
		response.json.output == null
		response.json.errorMsg == "abcde"
		response.json.comment == "abcde"
		response.json.submissionDate == formatter.parseDateTime(response.json.submissionDate as String).toDateTime().toString(formatter)
		response.json.evaluation == Submission.Evaluation.CORRECT as String
		response.json.user.name == "User Name 1"
		response.json.problem.id == 1
		response.json.language.id == 1
	}

	void "when call getSubmissions must return a list with users' submissions"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		when:
		controller.getSubmissions(1L, null)

		then:
		response.json.size() == TestUtils.SUBMISSION_COUNT
	}

	void "when call getSubmissions with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getSubmissions(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset)
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getQuestionnaire must return users' questionnaire"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.getQuestionnaires(1L, 1L)

		then:
		response.json.title == "Questionnaire 1"
		response.json.description == "abcde abcde abcde"
		response.json.score == 10
		response.json.startDate == formatter.parseDateTime(response.json.startDate as String).toDateTime().toString(formatter)
		response.json.endDate == formatter.parseDateTime(response.json.endDate as String).toDateTime().toString(formatter)
		response.json.group.id == 1
	}

	void "when call getQuestionnaire must return a list with users' questionnaires"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()

		when:
		controller.getQuestionnaires(1L, null)

		then:
		response.json.size() == TestUtils.USER_COUNT
	}

	void "when call getQuestionnaire with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getQuestionnaires(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.USER_COUNT - offset)
		response.json.id.contains(TestUtils.USER_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getProblemSubmissions must return a list with users' submissions to the problem"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		when:
		controller.getProblemSubmissions(1L, 1L, null)

		then:
		response.json.size() == TestUtils.SUBMISSION_COUNT
	}

	void "when call getProblemSubmissions with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getProblemSubmissions(1L, 1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset)
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getQuestionnaireProblems must return users' problem"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		when:
		controller.getQuestionnaireProblems(1L, 1L, 1L)

		then:
		response.json.name == "Problem 1"
		response.json.description == "abcde abcde abcde"
		response.json.inputFormat == "abcde abcde abcde"
		response.json.outputFormat == "abcde abcde abcde"
		response.json.source == "abcde abcde abcde"
		response.json.timeLimit == 1
		response.json.nd == 1.0
		response.json.bestRunTime.evaluation == Submission.Evaluation.CORRECT as String
		response.json.userAttempts.bestRunTime.evaluation == Submission.Evaluation.CORRECT as String
		response.json.suggestedBy.id == 1
	}


	void "when call getQuestionnaireProblems must return a list with questionnaire's problems of the user"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		when:
		controller.getQuestionnaireProblems(1L, 1L, null)

		then:
		response.json.size() == TestUtils.PROBLEM_COUNT
	}

	void "when call getQuestionnaireProblems with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.problemService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getQuestionnaireProblems(1L, 1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.PROBLEM_COUNT - offset)
		response.json.id.contains(TestUtils.PROBLEM_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getQuestionnaireProblemSubmissions must return users' submission"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.getQuestionnaireProblemSubmissions(1L, 1L, 1L, 1L)

		then:
		response.json.time == 0.1
		response.json.tries == 1
		response.json.diffFile == null
		response.json.submission == null
		response.json.output == null
		response.json.errorMsg == "abcde"
		response.json.comment == "abcde"
		response.json.submissionDate == formatter.parseDateTime(response.json.submissionDate as String).toDateTime().toString(formatter)
		response.json.evaluation == Submission.Evaluation.CORRECT as String
		response.json.user.name == "User Name 1"
		response.json.problem.id == 1
		response.json.language.id == 1
	}

	void "when call getQuestionnaireProblemSubmissions must return a list with users' submissions to the problem in one questionnaire"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		when:
		controller.getQuestionnaireProblemSubmissions(1L, 1L, 1L, null)

		then:
		response.json.size() == TestUtils.SUBMISSION_COUNT
	}

	void "when call getQuestionnaireProblemSubmissions with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		def offset = 2
		def max = 2

		when:
		params.sort = "id"
		params.order = "desc"
		params.max = max
		params.offset = offset
		controller.getQuestionnaireProblemSubmissions(1L, 1L, 1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset)
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

}