package com.thehuxley

import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class QuestionnaireService {

	def cacheService
	def groupService
	def problemService
	def dataService

	def final EXPIRE_CACHE = 60 * 60 * 24 * 7


	def get(Questionnaire questionnaire, User currentUser) {
		try {
			def questionnaireJSON = (questionnaire as JSON) as String

			if (currentUser) {
				def json = JSON.parse(questionnaireJSON as String)

				json.putAt("group", JSON.parse(groupService.get(Group.load(json.getAt("group").getAt("id") as Long), currentUser)))

				return (json as JSON) as String
			}

			return questionnaireJSON
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def getData(Questionnaire questionnaire) {
		dataService.getData([questionnaire: questionnaire])
	}

	@Transactional
	def save(Questionnaire questionnaire, User currentUser) {
		cacheService.expireCache(Questionnaire, questionnaire)

		try {
			questionnaire.save(flush: true)

			updateScores(questionnaire, false)

			get(questionnaire, currentUser)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	@Transactional
	def save(Questionnaire questionnaire, User currentUser, long questionnaireToCloneId) {
		cacheService.expireCache(Questionnaire, questionnaire)

			try {
				Questionnaire questionnaireToClone = Questionnaire.get(questionnaireToCloneId)
				questionnaire.score = questionnaireToClone.score;
				questionnaire.save(flush: true)

				QuestionnaireProblem.findAllByQuestionnaire(questionnaireToClone).each {
					new QuestionnaireProblem(score: it.score, questionnaire: questionnaire, problem: it.problem).save()
				}

				updateScores(questionnaire, false)

				get(questionnaire, currentUser)
			} catch (Exception e) {
				e.printStackTrace()
			}
		}


	@Transactional
	def delete(Questionnaire questionnaire) {
		cacheService.expireCache(Questionnaire, questionnaire)

		try {

			Questionnaire.withNewTransaction {
				QuestionnaireUser.findAllByQuestionnaire(questionnaire)*.delete()
				QuestionnaireProblem.findAllByQuestionnaire(questionnaire)*.delete()
			}

			questionnaire.delete()

			return !questionnaire.hasErrors()

		} catch (Exception e) {
			log.error(e.stackTrace)
		}
	}

	def list(Map params) {
		def resultList = Questionnaire.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

		if (params.filter.contains("OWN") && params.groups?.isEmpty()) {
			["searchResults": ([] as JSON) as String, "total": "0"]
		} else {
			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findByGroup(Questionnaire questionnaire, Group group) {
		questionnaire = Questionnaire.get(questionnaire.id)
		questionnaire?.group?.id == group.id ? (questionnaire as JSON) as String : null
	}

	def findAllByGroup(Group group, Map params) {
		params.group = group.id

		def resultList = Questionnaire.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

		["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
	}

	def findByUser(Questionnaire questionnaire, User user) {

		QuestionnaireUser questionnaireUser = QuestionnaireUser.findByQuestionnaireAndUser(questionnaire, user)

		if (questionnaireUser) {
			def json = JSON.parse((questionnaireUser.questionnaire as JSON) as String)
			json.put("currentUser", ["score": questionnaireUser.score])

			(json as JSON) as String
		}
	}

	def findAllByUser(User user, Map params) {
		params.problemsGe = 0

		def resultList = QuestionnaireUser.createCriteria().list([max: params.max, offset: params.offset]) {
			eq("user", user)
			questionnaire getCriteria(params)
		}

		def json = new JSONArray()

		resultList.each {
			def jsonElement = JSON.parse((it.questionnaire as JSON) as String)
			jsonElement.put("currentUser", ["score": it.score])
			json.add(jsonElement)
		}

		resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
				["searchResults": [] as String, "total": "0"]
	}

	@Transactional
	def addProblem(Questionnaire questionnaire, Problem problem, Double score, Map params) {

		def questionnaireProblem = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)

		if (questionnaireProblem) {
			questionnaireProblem.score = score
		} else {
			questionnaireProblem = new QuestionnaireProblem(questionnaire: questionnaire, problem: problem, score: score)
		}

		questionnaireProblem.save(flush: true)

		def totalScore = 0

		QuestionnaireProblem.findAllByQuestionnaire(questionnaire).each {
			totalScore += it.score
		}

		questionnaire.score = totalScore
		questionnaire.save(flush: true)

		updateScores(questionnaire, false)
		cacheService.expireCache(Problem, problem)
		problemService.findAllByQuestionnaire(questionnaire, params, null)
	}

	@Transactional
	def removeProblem(Questionnaire questionnaire, Problem problem, Map params) {

		def questionnaireProblem = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)

		if (questionnaireProblem) {
			questionnaireProblem.delete(flush: true)

			def totalScore = 0

			QuestionnaireProblem.findAllByQuestionnaire(questionnaire).each {
				totalScore += it.score
			}

			questionnaire.score = totalScore
			questionnaire.save(flush: true)

			updateScores(questionnaire, false)
			cacheService.expireCache(Problem, problem)
			problemService.findAllByQuestionnaire(questionnaire, params, null)
		}
	}

	static def updateScores(User user) {
		QuestionnaireUser.findAllByUser(user).each {
			updateScores(it.questionnaire, user)
		}
	}

	static def updateScores(Questionnaire questionnaire, User user) {
			def score = 0

			questionnaire.problems.each { Problem problem ->
				def correctSubmissionsInTime = Submission.createCriteria().list {
					eq("problem", problem)
					eq("user", user)
					eq("evaluation", Submission.Evaluation.CORRECT)
					le("submissionDate", questionnaire.endDate)
				}

				if (!correctSubmissionsInTime.empty) {
					def problemScore = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem).score
					score += problemScore
					if (log.infoEnabled)
						log.info("\t\t\t\t\tO usuário $user.username ganhou $problemScore pontos por esse problema ($problem.name). PONTUAÇÂO TOTAL: $score")
				}
			}

			def questionnaireUser = QuestionnaireUser.findByQuestionnaireAndUser(questionnaire, user)
			questionnaireUser.score = score
			questionnaireUser.save(flush: true)
	}

	static def updateScores(Group group, Boolean addOnly = true) {

		def users = UserGroup.findAllByGroupAndRole(group, UserGroup.Role.STUDENT).user
		def questionnaires = Questionnaire.findAllByGroup(group)

		users.each { User user ->
			questionnaires.each { Questionnaire questionnaire ->
				updateScores(questionnaire, addOnly)
			}
		}
	}

	static def updateScores(Group group, User user) {
		Questionnaire.findAllByGroup(group).each { Questionnaire questionnaire ->
			updateScores(questionnaire, user)
		}
	}

	static def updateScores(Questionnaire questionnaire, Boolean addOnly = true) {

		def users = UserGroup.findAllByGroupAndRole(questionnaire.group, UserGroup.Role.STUDENT).user

		users.each { User user ->
			if (!QuestionnaireUser.findByQuestionnaireAndUser(questionnaire, user)) {
				new QuestionnaireUser(questionnaire: questionnaire, user: user).save()
			}
		}

		if (!addOnly) {
			users.each { updateScores(questionnaire, it) }
		}
	}

	Closure getCriteria(Map params) {

		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()
		def now = new Date()

		return {
			and {

				or {
					if (params.q) {
						like("title", "%$params.q%")
						like("description", "%$params.q%")
					}
				}

				!params.group ?: eq("group", Group.load(params.group as Long))

				if (params.groups && !params.groups.empty) {
					inList("group", params.groups)
				}

				if (params.filter &&!params.filter.empty) {
					if (params.filter.contains("OPEN")) {
						le("startDate", now)
						ge("endDate", now)
					}

					if (params.filter.contains("CLOSED")) {
						le("endDate", now)
					}
				}

				!params.problemsGe ?: sizeGe("problems", 0)

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

				!params.score ?: eq("score", params.score as Double)
				!params.scoreGt ?: gt("score", params.scoreGt as Double)
				!params.scoreGe ?: ge("score", params.scoreGe as Double)
				!params.scoreLt ?: lt("score", params.scoreLt as Double)
				!params.scoreLe ?: le("score", params.scoreLe as Double)
				!params.scoreNe ?: ne("score", params.scoreNe as Double)
			}

			order(params.sort ?: "startDate", params.order ?: "desc")
			order(params.sort ?: "endDate", params.order ?: "asc")
		}
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)
		params.q = params.q ?: ""
		params.filter ? params.list("filter")*.asType(String) : []

		if (params.filter instanceof String) {
			params.filter = [params.filter]
		}

		params.filter = params.filter*.toUpperCase()


		return params
	}

	boolean isSortable(param) {
		[
				"id",
				"title",
				"description",
				"score",
				"startDate",
				"endDate",
				"dateCreated",
				"lastUpdated",
				"group"
		].contains(param)
	}

}
