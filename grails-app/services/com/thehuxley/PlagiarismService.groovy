package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException

class PlagiarismService {

	def redisService
	def cacheService
	def final EXPIRE_CACHE = 60 * 60 * 24

	def get(Plagiarism plagiarism) {
		try {
			(plagiarism as JSON) as String
		} catch (ObjectNotFoundException e) {
			throw e
		}
	}

	def changeStatus(Plagiarism plagiarism, Plagiarism.Status status) {
		try {
			plagiarism.status = status
			plagiarism.save(flush: true)
			return get(plagiarism)
		} catch (ObjectNotFoundException e) {
			throw e
		}
	}

	def findAllByQuestionnaire(Questionnaire questionnaire, Map params) {
		if ((questionnaire.users && !questionnaire.users.empty)
				&& (questionnaire.problems && !questionnaire.problems.empty)) {
			def resultList = Plagiarism.createCriteria().list(params) {

				and {
					ge("percentage", 0.8D)

					or {
						submission1 {
							inList("user", questionnaire.users)
							inList("problem", questionnaire.problems)
							between("submissionDate", questionnaire.startDate, questionnaire.endDate)
						}

						submission2 {
							inList("user", questionnaire.users)
							inList("problem", questionnaire.problems)
							between("submissionDate", questionnaire.startDate, questionnaire.endDate)
						}
					}
				}

				order(params.sort ?: "percentage", params.order ?: "asc")
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		} else {
			["searchResults": [] as String, "total": "0"]
		}
	}


	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)

		return params
	}



	boolean isSortable(param) {
		[
				"id",
				"percentage",
				"submission1",
				"submission2",
				"status"
		].contains(param)
	}

}
