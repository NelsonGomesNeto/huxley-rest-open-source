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

@TestFor(SubmissionController)
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
class SubmissionControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def submissionService = new SubmissionService()

		controller.submissionService = submissionService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the submission's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.show(1L)

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

	void "when call index must return a list with all submissions"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.submissionService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.SUBMISSION_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
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
		controller.index()

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset)
		response.json.id.contains(TestUtils.SUBMISSION_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

}