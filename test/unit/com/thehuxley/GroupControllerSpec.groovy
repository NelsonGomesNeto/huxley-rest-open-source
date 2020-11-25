package com.thehuxley

import com.thehuxley.marshaller.CustomMarshallerRegistrar
import grails.plugin.redis.RedisService;
import grails.test.mixin.TestFor;
import grails.test.mixin.TestMixin;
import grails.test.mixin.gorm.Domain;
import grails.test.mixin.hibernate.HibernateTestMixin
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat;
import spock.lang.Specification;

@TestFor(GroupController)
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
public class GroupControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def userService = new UserService()
		def groupService = new GroupService()
		def submissionService = new SubmissionService()
		def questionnaireService = new QuestionnaireService()

		controller.userService = userService
		controller.groupService = groupService
		controller.submissionService = submissionService
		controller.questionnaireService = questionnaireService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the group's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		when:
		controller.show(1L)

		then:
		response.json.name == "Group 1"
		response.json.url == "group1"
		response.json.description == "abcde abcde abcde"
		response.json.accessKey == null
		response.json.startDate == formatter.parseDateTime(response.json.startDate as String).toDateTime().toString(formatter)
		response.json.endDate == formatter.parseDateTime(response.json.endDate as String).toDateTime().toString(formatter)
		response.json.institution.name == "Institution 1"
	}

	void "when call index must return a list with all groups"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.GROUP_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
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
		controller.index()

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.GROUP_COUNT - offset)
		response.json.id.contains(TestUtils.GROUP_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getUsers must return group's user"() {
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


	void "when call getUsers must return a list with group's users"() {
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

	void "when call getQuestionnaire must return group's questionnaire"() {
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

	void "when call getQuestionnaire must return a list with group's questionnaires"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.questionnaireService.redisService = redisServiceMock.createMock()

		when:
		controller.getQuestionnaires(1L, null)

		then:
		response.json.size() == TestUtils.QUESTIONNAIRE_COUNT
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
		response.json.id.contains(TestUtils.QUESTIONNAIRE_COUNT - offset)
		response.json.id.contains(TestUtils.QUESTIONNAIRE_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getSubmissions must return group's submission"() {
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

	void "when call getSubmissions must return a list with group's submissions"() {
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

}
