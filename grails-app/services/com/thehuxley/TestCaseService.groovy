package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException

class TestCaseService {

	def redisService
	def cacheService

	def final EXPIRE_CACHE = 60 * 60 * 24



	def save(TestCase testCase) {
		try {
			testCase.save(flush: true)
			cacheService.expireCache(TestCase, testCase)
			findByProblem(testCase, testCase.problem)
		} catch (Exception e) {
			e.finalize()
		}
	}

	def update(TestCase testCase) {
		save(testCase)
	}

	def delete(TestCase testCase) {
		try {
			testCase.delete(flush: true)
			cacheService.expireCache(TestCase, testCase)
			return true
		} catch (Exception e) {
			e.finalize()
			return false
		}
	}

	def findByProblem(TestCase testCase, Problem problem, Boolean exampleOnly = false) {
		try {
			redisService.memoize(cacheService.generateKey(TestCase, testCase, [problem], exampleOnly as String), EXPIRE_CACHE) {
				if (testCase.problem.id == problem.id) {
					if (exampleOnly) {
						if (testCase.example) {
							(testCase as JSON) as String
						}
					} else {
						(testCase as JSON) as String
					}
				}
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def findAllByProblem(Problem problem, Map params, Boolean exampleOnly = false) {
		redisService.memoizeHash(cacheService.generateKey(TestCase, params, [problem], exampleOnly as String)) {
			def resultList = TestCase.createCriteria().list(params) {
				eq("problem", problem)
				if (exampleOnly) {
					eq("example", true)
				}
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findByProblemInputPlainText(TestCase testCase, Problem problem, Boolean exampleOnly = false) {
		try {
			if (testCase.problem.id == problem.id) {
				if (exampleOnly) {
					if (testCase.example) {
						return testCase.input
					}
				} else {
					return testCase.input
				}
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def findByProblemOutputPlainText(TestCase testCase, Problem problem, Boolean exampleOnly = false) {
		try {
			if (testCase.problem.id == problem.id) {
				if (exampleOnly) {
					if (testCase.example) {
						return testCase.output
					}
				} else {
					return testCase.output
				}
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def getTestCaseList(Problem problem) {
		try {
			def resultList = TestCase.createCriteria().list([:]) {
				eq("problem", problem)
			}

			resultList.each {
				it.input = null
				it.output = null
				it.discard()
			}

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)
		params.q = params.q ?: ""

		return params
	}

	boolean isSortable(param) {
		[
				"dateCreated",
				"lastUpdated",
				"example",

		].contains(param)
	}

}