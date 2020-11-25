package com.thehuxley

import com.thehuxley.marshaller.CustomMarshallerRegistrar
import grails.plugin.redis.RedisService;
import grails.test.mixin.TestFor;
import grails.test.mixin.TestMixin;
import grails.test.mixin.gorm.Domain;
import grails.test.mixin.hibernate.HibernateTestMixin
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import spock.lang.*;

@TestFor(QuestionnaireController)
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
class QuestionnaireControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def problemService = new ProblemService()
		def submissionService = new SubmissionService()
		def questionnaireService = new QuestionnaireService()
		def userService = new UserService()

		controller.problemService = problemService
		controller.submissionService = submissionService
		controller.questionnaireService = questionnaireService
		controller.userService = userService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the questionnaire's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.show(1L)

		then:
		response.json.title == "Questionnaire 1"
		response.json.description == "abcde abcde abcde"
		response.json.score == 10
		response.json.startDate == formatter.parseDateTime(response.json.startDate as String).toDateTime().toString(formatter)
		response.json.endDate == formatter.parseDateTime(response.json.endDate as String).toDateTime().toString(formatter)
		response.json.group.id == 1

	}

	void "when call index must return a list with all questionnaire"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.QUESTIONNAIRE_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
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
		controller.index()

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.QUESTIONNAIRE_COUNT - offset)
		response.json.id.contains(TestUtils.QUESTIONNAIRE_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getProblems must return questionnaire's problem"() {
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
		response.json.suggestedBy.id == 1
	}

	void "when call getProblems must return a list with questionnaire's problems"() {
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
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getUsers must return questionnaire's user"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.userService.redisService = redisServiceMock.createMock()

		when:
		controller.getUsers(1L, 1L)

		then:
		response.json.name == "User Name 1"
		response.json.email == null
		response.json.username == null
		response.json.password == null
	}

	void "when call getUsers must return a list with questionnaire's users"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.userService.redisService = redisServiceMock.createMock()

		when:
		controller.getUsers(1L, null)

		then:
		response.json.size() == TestUtils.USER_COUNT
	}

	void "when call getUsers with params max, offset and sort must return a array with max elements in correct order"() {
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
		controller.getUsers(1L, null)

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.USER_COUNT - offset)
		response.json.id.contains(TestUtils.USER_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}


	void "when call getProblemSubmissions must return problem's submission"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.getProblemSubmissions(1L, 1L, 1L)

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

	void "when call getProblemSubmissions must return a list with problem's submissions"() {
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
}