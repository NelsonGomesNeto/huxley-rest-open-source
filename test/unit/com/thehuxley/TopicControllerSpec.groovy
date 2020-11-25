package com.thehuxley

import com.thehuxley.marshaller.CustomMarshallerRegistrar
import grails.plugin.redis.RedisService;
import grails.test.mixin.TestFor;
import grails.test.mixin.TestMixin;
import grails.test.mixin.gorm.Domain;
import grails.test.mixin.hibernate.HibernateTestMixin
import spock.lang.*;

@TestFor(TopicController)
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
class TopicControllerSpec extends Specification {

	def redisServiceMock

	def setup() {

		CustomMarshallerRegistrar.registerMarshallers()

		def topicService = new TopicService()

		controller.topicService = topicService

		redisServiceMock = mockFor(RedisService)

		TestUtils.populateDataBase()
	}

	void "when call show must return the topic's data"() {
		given:
		redisServiceMock.demand.memoizeObject { Class clazz, String key, Closure c -> c() }
		controller.topicService.redisService = redisServiceMock.createMock()

		when:
		controller.show(1L)

		then:
		response.json.id == 1
		response.json.name == "Topic 1"
	}

	void "when call index must return a list with all topics"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.topicService.redisService = redisServiceMock.createMock()

		when:
		controller.index()

		then:
		response.json.size() == TestUtils.TOPIC_COUNT
	}

	void "when call index with params max, offset and sort must return a array with max elements in correct order"() {
		given:
		redisServiceMock.demand.memoizeHash { String key, Closure c -> c() }
		controller.topicService.redisService = redisServiceMock.createMock()

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
		response.json.id.contains(TestUtils.TOPIC_COUNT - offset)
		response.json.id.contains(TestUtils.TOPIC_COUNT - offset.next())
		response.json.get(0).id > response.json.get(1).id
	}

}