package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.security.crypto.codec.Hex

import java.security.MessageDigest

class GroupService {

	def redisService
	def userService
	def institutionService
	def cacheService
	def emailService
	def dataService

	def final EXPIRE_CACHE = 60 * 60 * 24

	def get(Group group, User currentUser = null) {
		try {
			def groupJSON = redisService.memoize(cacheService.generateKey(Group, group), EXPIRE_CACHE) {
				(group as JSON) as String
			}
			if (groupJSON) {
				def json = JSON.parse(groupJSON as String)

				json.putAt("teachers", JSON.parse(userService.findAllByGroupAndRole(group, UserGroup.Role.TEACHER, [:]).getAt("searchResults") as String))
				json.putAt("teacherAssistants", JSON.parse(userService.findAllByGroupAndRole(group, UserGroup.Role.TEACHER_ASSISTANT, [:]).getAt("searchResults") as String))

				if (currentUser) {
					json.putAt("role", UserGroup.findByUserAndGroup(currentUser, group)?.role)
				}

				return (json as JSON) as String
			}

			return groupJSON
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def getData(Group group) {
		dataService.getData([group: group])
	}

	def changeRole(Group group, User user, UserGroup.Role role) {
		try {
			def userGroup = UserGroup.findByUserAndGroup(user, group)

			if (userGroup) {
				userGroup.role = role
				userGroup.save(flush: true)
			} else {
				new UserGroup(user: user, group: group, role: role).save(flush: true)

				if (role == UserGroup.Role.STUDENT) {

					Questionnaire.findAllByGroup(group).each {Questionnaire questionnaire ->
						if (QuestionnaireUser.findByUserAndQuestionnaire(user, questionnaire)) {
							new QuestionnaireUser(questionnaire: questionnaire, user: user).save()
						}
					}

					if (!UserInstitution.findByUserAndInstitution(user, group.institution)) {
						institutionService.addToInstitution(user, group.institution, UserInstitution.Role.STUDENT)
					}
				}
			}

			if (role == UserGroup.Role.STUDENT) {
				QuestionnaireService.updateScores(group, true)
				QuestionnaireService.updateScores(group, user)
			}
		} catch (Exception e) {
			e.printStackTrace()
			return false
		}
	}


	def inviteToGroup(String email, Group group) {
		emailService.sendInviteToGroup(email, group)
	}


	def addToGroup(User user, Group group, UserGroup.Role role = UserGroup.Role.STUDENT) {
		cacheService.expireCache(User, user, [group])
		cacheService.expireCache(Group, group)

		def userInstitution = UserInstitution.findByUserAndInstitution(user, group.institution)


		if (role == UserGroup.Role.TEACHER) {

			if (userInstitution) {
				if (userInstitution.role != UserInstitution.Role.TEACHER) {
					institutionService.changeRoleKeepGreatest(user, group.institution, UserInstitution.Role.TEACHER)
				}

			} else {
				institutionService.addToInstitution(user, group.institution, UserInstitution.Role.TEACHER)
			}

			changeRole(group, user, role)

			return true

		} else if (role == UserGroup.Role.TEACHER_ASSISTANT) {

			if (userInstitution) {
				if (userInstitution.role != UserInstitution.Role.TEACHER_ASSISTANT) {
					institutionService.changeRoleKeepGreatest(user, group.institution, UserInstitution.Role.TEACHER_ASSISTANT)
				}
			} else {
				institutionService.addToInstitution(user, group.institution, UserInstitution.Role.TEACHER_ASSISTANT)
			}

			changeRole(group, user, role)
			return true

		} else {

			if (userInstitution) {
				if (userInstitution.role != UserInstitution.Role.STUDENT) {
					institutionService.changeRoleKeepGreatest(user, group.institution, UserInstitution.Role.STUDENT)
				}
			} else {
				institutionService.addToInstitution(user, group.institution, UserInstitution.Role.STUDENT)
			}

			Questionnaire.findAllByGroup(group).each {Questionnaire questionnaire ->
				if (!QuestionnaireUser.findByUserAndQuestionnaire(user, questionnaire)) {
					new QuestionnaireUser(questionnaire: questionnaire, user: user).save()
				}
			}

			changeRole(group, user, role)
		}

		return true
	}

	def removeFromGroup(User user, Group group) {
		cacheService.expireCache(User, user, [group])
		UserGroup.findAllByUserAndGroup(user, group)*.delete()
	}

	def save(Group group, User user = null) {
		cacheService.expireCache(Group, group)

		try {
			group.save()

			if (user) {
				changeRole(group, user, UserGroup.Role.TEACHER)
			}

			get(group)
		} catch (e) {
			e.finalize()
		}
	}

	def list(Map params) {
		redisService.memoizeHash(cacheService.generateKey(Group, params), EXPIRE_CACHE) {
			def resultList = Group.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findByInstitution(Group group, Institution institution) {
		redisService.memoize(cacheService.generateKey(Group, group, [institution]), EXPIRE_CACHE) {
			try {
				group.institution?.id == institution.id ? (group as JSON) as String : null
			} catch (ObjectNotFoundException e) {
				e.finalize()
			}
		}
	}

	def findAllByInstitution(Institution institution, Map params) {
		redisService.memoizeHash(cacheService.generateKey(Group, params, [institution]), EXPIRE_CACHE) {
			params.institution = institution.id

			def resultList = Group.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findByUser(Group group, User user) {
		redisService.memoize(cacheService.generateKey(Group, group, [user]), EXPIRE_CACHE) {
			(UserGroup.findByGroupAndUser(group, user)?.group as JSON) as String
		}
	}

	def findAllByUser(User user, Map params) {
		redisService.memoizeHash(cacheService.generateKey(Group, params, [user]), EXPIRE_CACHE) {
			def resultList = UserGroup.createCriteria().list([max: params.max, offset: params.offset]) {
				eq("user", user)

				group getCriteria(params)
			}

			resultList ? ["searchResults": (resultList.group as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	def findAllInTopCoder(Group group, Map params) {
		redisService.memoizeHash(cacheService.generateKey(Group, params, "topcoder"), EXPIRE_CACHE) {

			def users = UserGroup.findAllByGroup(group).user

			def resultList = TopCoder.createCriteria().list([max: params.max, offset: params.offset]) {
				and {
					if (users && !users.empty) {
						inList('user', users)
					}

					user {
						or {
							if (params.q) {
								like("name", "%$params.q%")
								like("email", "%$params.q%")
								like("username", "%$params.q%")
							}
						}
					}
				}
				order("points", "desc")
			}

			def json = new JSONArray()

			resultList.each {
				def jsonElement = JSON.parse((it.user as JSON) as String)
				jsonElement.put("points", it.points)
				jsonElement.put("position", it.position)
				json.add(jsonElement)
			}

			resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	Closure getCriteria(Map params) {

		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		return {
			and {
				or {
					if (params.q) {
						like("name", "%$params.q%")
						like("url", "%$params.q%")
						like("description", "%$params.q%")
					}
				}

				!params.institution ?: eq("institution", Institution.load(params.institution as Long))

				!params.startDate ?: eq("startDate", formatter.parseDateTime(params.startDate as String).toDate())
				!params.startDateGt ?: gt("startDate", formatter.parseDateTime(params.startDateGt as String).toDate())
				!params.startDateGe ?: ge("startDate", formatter.parseDateTime(params.startDateGe as String).toDate())
				!params.startDateLt ?: lt("startDate", formatter.parseDateTime(params.startDateLt as String).toDate())
				!params.startDateLe ?: le("startDate", formatter.parseDateTime(params.startDateLe as String).toDate())
				!params.startDateNe ?: ne("startDate", formatter.parseDateTime(params.startDateNe as String).toDate())

				!params.endDate ?: eq("endDate", formatter.parseDateTime(params.endDate as String).toDate())
				!params.endDateGt ?: gt("endDate", formatter.parseDateTime(params.endDateGt as String).toDate())
				!params.endDateGe ?: ge("endDate", formatter.parseDateTime(params.endDateGe as String).toDate())
				!params.endDateLt ?: lt("endDate", formatter.parseDateTime(params.endDateLt as String).toDate())
				!params.endDateLe ?: le("endDate", formatter.parseDateTime(params.endDateLe as String).toDate())
				!params.endDateNe ?: ne("endDate", formatter.parseDateTime(params.endDateNe as String).toDate())

				!params.dateCreated ?: eq("dateCreated", formatter.parseDateTime(params.dateCreated as String).toDate())
				!params.dateCreatedGt ?: gt("dateCreated", formatter.parseDateTime(params.dateCreatedGt as String).toDate())
				!params.dateCreatedGe ?: ge("dateCreated", formatter.parseDateTime(params.dateCreatedGe as String).toDate())
				!params.dateCreatedLt ?: lt("dateCreated", formatter.parseDateTime(params.dateCreatedLt as String).toDate())
				!params.dateCreatedLe ?: le("dateCreated", formatter.parseDateTime(params.dateCreatedLe as String).toDate())
				!params.dateCreatedNe ?: ne("dateCreated", formatter.parseDateTime(params.dateCreatedNe as String).toDate())

				!params.lastUpdated ?: eq("lastUpdated", formatter.parseDateTime(params.lastUpdated as String).toDate())
				!params.lastUpdatedGt ?: gt("lastUpdated", formatter.parseDateTime(params.lastUpdatedGt as String).toDate())
				!params.lastUpdatedGe ?: ge("lastUpdated", formatter.parseDateTime(params.lastUpdatedGe as String).toDate())
				!params.lastUpdatedLt ?: lt("lastUpdated", formatter.parseDateTime(params.lastUpdatedLt as String).toDate())
				!params.lastUpdatedLe ?: le("lastUpdated", formatter.parseDateTime(params.lastUpdatedLe as String).toDate())
				!params.lastUpdatedNe ?: ne("lastUpdated", formatter.parseDateTime(params.lastUpdatedNe as String).toDate())
			}

			order(params.sort ?: "name", params.order ?: "asc")
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
				"id",
				"name",
				"url",
				"description",
				"startDate",
				"endDate",
				"dateCreated",
				"lastUpdated",
				"institution"
		].contains(param)
	}

	def refreshAccessKey(Group group) {
		group.accessKey = generateAccessKey(group.id)
		group.save(flush: true)
	}

	def generateAccessKey(Long id) {
		def hashKey = new String(Hex.encode(MessageDigest.getInstance("SHA1").digest((new Random().nextInt() + new Date().toString()).bytes)))

		(toAlpha(id) + hashKey.substring(0, 4)).toUpperCase()
	}

	def toAlpha(Long n) {

		def alpha = ((0..9).collect { it as String }) + ('A'..'Z')
		def size = alpha.size()
		def result = []
		def div = (n / size).toInteger()
		result.push(n - (div * size))
		n = div

		while (n > size) {
			div = (n / size).toInteger()
			result.push(n - (div * size))
			n = div
		}

		if (div > 0) {
			result.push(div.toInteger())
		}

		def ret = ""

		result.each {
			ret += alpha[it as Integer]
		}

		ret.reverse()
	}

}
