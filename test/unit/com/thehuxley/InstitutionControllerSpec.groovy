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

@TestFor(InstitutionController)
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
class InstitutionControllerSpec extends Specification  {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def groupService = new GroupService()
		def institutionService = new InstitutionService()

		controller.institutionService = institutionService
		controller.groupService = groupService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the institution's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.institutionService.redisService = redisServiceMock.createMock()

		when:
		controller.show(1L)

		then:
		response.json.name == "Institution 1"
		response.json.phone == null
		response.json.photo == "abcdefg.png"
		response.json.status == null

	}

	void "when call index must return a list with all institutions"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.institutionService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.INSTITUTION_APPROVED_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
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
		controller.index()

		then:
		response.json.size() == max
		response.json.id.contains(TestUtils.INSTITUTION_APPROVED_COUNT - offset)
		response.json.id.contains(TestUtils.INSTITUTION_APPROVED_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

	void "when call getGroups must return institution's group"() {
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


	void "when call getGroups must return a list with institution's groups"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.groupService.redisService = redisServiceMock.createMock()

		when:
		controller.getGroups(1L, null)

		then:
		response.json.size() == TestUtils.GROUP_COUNT
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
		response.json.id.contains(TestUtils.GROUP_COUNT - offset)
		response.json.id.contains(TestUtils.GROUP_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

}
