package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class LicensePackService {

	def redisService
	def cacheService

	def final EXPIRE_CACHE = 60 * 60 * 1


	def get(LicensePack licensePack) {
		redisService.memoize(cacheService.generateKey(LicensePack, licensePack)) {
			try {
				(licensePack as JSON) as String
			} catch (ObjectNotFoundException e) {
				e.finalize()
			}
		}
	}

	def list(Map params) {
		redisService.memoizeHash("LicensePackService:list:$params.hashCode()") {

			DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

			def resultList = LicensePack.createCriteria().list([max: params.max, offset: params.offset]) {

				!params.institution ?: eq("institution", Institution.load(params.institution as Long))

				institution {
					like("name", "%$params.q%")
				}

				!params.startDate ?: eq("startDate",
						formatter.parseDateTime(params.startDate as String).toDate())
				!params.startDateGt ?: gt("startDate",
						formatter.parseDateTime(params.startDateGt as String).toDate())
				!params.startDateGe ?: ge("startDate",
						formatter.parseDateTime(params.startDateGe as String).toDate())
				!params.startDateLt ?: lt("startDate",
						formatter.parseDateTime(params.startDateLt as String).toDate())
				!params.startDateLe ?: le("startDate",
						formatter.parseDateTime(params.startDateLe as String).toDate())
				!params.startDateNe ?: ne("startDate",
						formatter.parseDateTime(params.startDateNe as String).toDate())

				!params.endDate ?: eq("endDate",
						formatter.parseDateTime(params.endDate as String).toDate())
				!params.endDateGt ?: gt("endDate",
						formatter.parseDateTime(params.endDateGt as String).toDate())
				!params.endDateGe ?: ge("endDate",
						formatter.parseDateTime(params.endDateGe as String).toDate())
				!params.endDateLt ?: lt("endDate",
						formatter.parseDateTime(params.endDateLt as String).toDate())
				!params.endDateLe ?: le("endDate",
						formatter.parseDateTime(params.endDateLe as String).toDate())
				!params.endDateNe ?: ne("endDate",
						formatter.parseDateTime(params.endDateNe as String).toDate())

				!params.dateCreated ?: eq("dateCreated",
						formatter.parseDateTime(params.dateCreated as String).toDate())
				!params.dateCreatedGt ?: gt("dateCreated",
						formatter.parseDateTime(params.dateCreatedGt as String).toDate())
				!params.dateCreatedGe ?: ge("dateCreated",
						formatter.parseDateTime(params.dateCreatedGe as String).toDate())
				!params.dateCreatedLt ?: lt("dateCreated",
						formatter.parseDateTime(params.dateCreatedLt as String).toDate())
				!params.dateCreatedLe ?: le("dateCreated",
						formatter.parseDateTime(params.dateCreatedLe as String).toDate())
				!params.dateCreatedNe ?: ne("dateCreated",
						formatter.parseDateTime(params.dateCreatedNe as String).toDate())

				!params.lastUpdated ?: eq("lastUpdated",
						formatter.parseDateTime(params.lastUpdated as String).toDate())
				!params.lastUpdatedGt ?: gt("lastUpdated",
						formatter.parseDateTime(params.lastUpdatedGt as String).toDate())
				!params.lastUpdatedGe ?: ge("lastUpdated",
						formatter.parseDateTime(params.lastUpdatedGe as String).toDate())
				!params.lastUpdatedLt ?: lt("lastUpdated",
						formatter.parseDateTime(params.lastUpdatedLt as String).toDate())
				!params.lastUpdatedLe ?: le("lastUpdated",
						formatter.parseDateTime(params.lastUpdatedLe as String).toDate())
				!params.lastUpdatedNe ?: ne("lastUpdated",
						formatter.parseDateTime(params.lastUpdatedNe as String).toDate())

				!params.total ?: eq("total", params.total as Integer)
				!params.totalGt ?: gt("total", params.totalGt as Integer)
				!params.totalGe ?: ge("total", params.totalGe as Integer)
				!params.totalLt ?: lt("total", params.totalLt as Integer)
				!params.totalLe ?: le("total", params.totalLe as Integer)
				!params.totalNe ?: ne("total", params.totalNe as Integer)

				order(params.sort ?: "dateCreated", params.order ?: "asc")
			}

			["searchResults": (resultList as JSON) as String, total: resultList.totalCount as String]
		}
	}

	def save(LicensePack licensePack) {
		cacheService.expireCache(LicensePack, licensePack)

		try {
			licensePack.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}

		get(licensePack)
	}

	def delete(LicensePack licensePack) {
		try {
			licensePack.delete(flush: true)
		} catch (Exception e) {
			e.finalize()
			return false
		}
		return true
	}

	def update(LicensePack licensePack) {
		cacheService.expireCache(LicensePack, licensePack)

		try {
			licensePack.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}

		get(licensePack)
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
				"total",
				"startDate",
				"endDate",
				"dateCreated",
				"lastUpdated",
				"institution"
		].contains(param)
	}

}
