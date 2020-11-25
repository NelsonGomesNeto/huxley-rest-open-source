package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException

class PendencyService {

	def redisService
	def cacheService

	def final EXPIRE_CACHE = 60 * 60 * 24


	def get(Pendency pendency) {
		redisService.memoize(cacheService.generateKey(Pendency, pendency), EXPIRE_CACHE) {
			try {
				(pendency as JSON) as String
			} catch(ObjectNotFoundException e) {
				e.finalize()
			}
		}
	}

	def list(Map params) {
		redisService.memoizeHash(cacheService.generateKey(Pendency, params)) {
			def resultList = Pendency.createCriteria().list(params) {
				and {

					or {
						if (params.q) {
							institution {
								like("name", "%$params.q%")
							}

							group {
								like("name", "%$params.q%")
							}

							user {
								like("name", "%$params.q%")
							}
						}
					}

					if (params.kind) {
						eq("kind", params.kind)
					}

					if (params.status) {
						eq("status", params.status)
					}

					if (params.user) {
						eq("user", User.load(params.user as Long))
					}

					if (params.institution) {
						eq("institution", Institution.load(params.institution as Long))
					}

					if (params.group) {
						eq("group", Group.load(params.group as Long))
					}
				}

				order(params.sort ?: "status", params.order ?: "asc")
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def save(Pendency pendency) {
		cacheService.expireCache(Pendency, pendency)

		try {
			pendency.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}

		get(pendency)
	}

	def update(Pendency pendency) {
		cacheService.expireCache(Pendency, pendency)

		try {
			pendency.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}

		get(pendency)
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.q = params.q ?: ""
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)
		params.kind = params.kind ? Pendency.PendencyKind.valueOf(params.kind as String) : null
		params.status = params.status ? Pendency.Status.valueOf(params.status as String) : null
		params.institution = params.institution ? params.long("institution") : null
		params.group = params.group ? params.long("group") : null
		params.user = params.user ? params.long("user") : null

		return params
	}

	boolean isSortable(param) {
		[
				"id",
				"status",
				"institution",
				"dateCreated",
				"lastUpdated",
				"status"
		].contains(param)
	}
}