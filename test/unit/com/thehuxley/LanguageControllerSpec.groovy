package com.thehuxley

import com.thehuxley.marshaller.CustomMarshallerRegistrar
import grails.plugin.redis.RedisService;
import grails.test.mixin.TestFor;
import grails.test.mixin.TestMixin;
import grails.test.mixin.gorm.Domain;
import grails.test.mixin.hibernate.HibernateTestMixin
import spock.lang.*;

@TestFor(LanguageController)
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
class LanguageControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def languageService = new LanguageService()

		controller.languageService = languageService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the language's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.languageService.redisService = redisServiceMock.createMock()

		when:
		controller.show(1L)

		then:
		response.json.name == "Language 1"
		response.json.plagConfig == "abcde abcde abcde"
		response.json.execParams == "abcde abcde abcde"
		response.json.compileParams == "abcde abcde abcde"
		response.json.compiler == "compile1"
		response.json.script == "abcde abcde abcde"
		response.json.extension == ".1"

	}

	void "when call index must return a list with all languages"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.languageService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.LANGUAGE_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.languageService.redisService = redisServiceMock.createMock()

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
		response.json.id.contains(TestUtils.LANGUAGE_COUNT - offset)
		response.json.id.contains(TestUtils.LANGUAGE_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}
}