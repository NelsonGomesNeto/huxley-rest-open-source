package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException

class TopicService {

	def redisService
	def cacheService
	def final EXPIRE_CACHE = 60 * 60 * 24 * 7


	def get(Topic topic) {
		try {
			redisService.memoize(cacheService.generateKey(Topic, topic), EXPIRE_CACHE) {
				(topic as JSON) as String
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def list(Map params) {
		redisService.memoizeHash(cacheService.generateKey(Topic, params), EXPIRE_CACHE) {

			def topics = checkInTopics(params)

			def resultList = Topic.createCriteria().list(params) {
				like("name", "%$params.q%")
				if (topics && !topics.empty)
					inList("id", topics)
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def save(Topic topic) {
		cacheService.expireCache(Topic, topic)

		topic.save(flush: true)
		get(topic)
	}

	def delete(Topic topic) {
		cacheService.expireCache(Topic, topic)

		try {
			topic.delete(flush: true)
		} catch (e) {
			e.printStackTrace()
			e.finalize()
			return false
		}

		return true
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.q = params.q ?: ""
		params.haveInCommon = params.haveInCommon ? params.list("haveInCommon")*.asType(Long) : []

		return params
	}

	def checkInTopics(Map params) {

		LinkedHashSet<Long> inTopics = []

		if (params.haveInCommon && !params.haveInCommon.empty) {
			Problem.createCriteria().list() {
				topics {
					inList("id", params.haveInCommon as List<Long>)
				}
			}.topics*.each {
				inTopics << (it.id as Long)
			}
		}

		(inTopics - (params.haveInCommon as List))
	}

	boolean isSortable(param) {
		[
				"id",
				"name"
		].contains(param)
	}

}
