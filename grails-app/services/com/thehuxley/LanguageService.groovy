package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException

class LanguageService {

	def redisService
	def cacheService

	def final EXPIRE_CACHE = 60 * 60 * 24


	def get(Language language) {
		try {
			redisService.memoize(cacheService.generateKey(Language, language), EXPIRE_CACHE) {
				(language as JSON) as String
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def list(Map params) {
		redisService.memoizeHash(cacheService.generateKey(Language, params)) {
			def resultList = Language.createCriteria().list(params) {
				or {
					like("name", "%$params.q%")
					like("label", "%$params.q%")
				}
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def save(Language language) {
		cacheService.expireCache(Language, language)

		language.save(flush: true)
		get(language)
	}

	def delete(Language language) {
		cacheService.expireCache(Language, language)

		try {
			language.delete(flush: true)
		} catch (e) {
			e.printStackTrace()
			e.finalize()
			return false
		}

		return true
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)
		params.q = params.q ?: ""

		return params
	}

	boolean isSortable(param) {
		[
				"id",
				"name",
				"plagConfig",
				"execParams",
				"compileParams",
				"compiler",
				"script",
				"extension",
				"label"
		].contains(param)
	}

}
