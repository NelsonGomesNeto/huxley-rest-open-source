package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.security.crypto.codec.Hex
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.security.MessageDigest

class ProblemService {

	def redisService
	def dataService
	def cacheService
	def grailsApplication

	def final EXPIRE_CACHE = 60 * 60 * 24 * 7

	def get(Problem problem, Problem.Status status) {
		try {
			redisService.memoize(cacheService.generateKey(Problem, problem, [status]), EXPIRE_CACHE) {
				status ? (problem.status == status ? (problem as JSON) as String : null) : (problem as JSON) as String
			}
		} catch (ObjectNotFoundException e)  {
			e.finalize()
		}

	}

	def getData(Problem problem) {
		dataService.getData([problem: problem])
	}

	def list(Map params, Problem.Status status) {
		redisService.memoizeHash(cacheService.generateKey(Problem, params, [status]), EXPIRE_CACHE) {
			def inProblems = checkInProblems(params)

			if (inProblems != null) {
					params.inProblems = inProblems
					def resultList = Problem.createCriteria().list([
							max: params.max,
							offset: params.offset
					], getCriteria(params, status))

					return [
							"total": resultList.totalCount as String,
							"searchResults": (resultList as JSON) as String
					]
			} else {
				return ["searchResults": [] as String, "total": "0"]
			}
		}
	}

	def findByQuestionnaire(Problem problem, Questionnaire questionnaire, Problem.Status status) {
		redisService.memoize(cacheService.generateKey(Problem, problem, [questionnaire, status]), EXPIRE_CACHE) {
			def questionnaireProblem = QuestionnaireProblem.findByProblemAndQuestionnaire(problem, questionnaire)

			if (questionnaireProblem && questionnaireProblem.problem.status == status) {
				def json = JSON.parse((questionnaireProblem.problem as JSON) as String)

				json.putAt("score", questionnaireProblem.score)

				return (json as JSON) as String
			}
		}
	}

	def findAllByQuestionnaire(Questionnaire questionnaire, Map params, Problem.Status status) {
		redisService.memoizeHash(cacheService.generateKey(Problem, params, [questionnaire, status]), EXPIRE_CACHE) {
			def inProblems = checkInProblems(params)

			if (inProblems != null) {
				params.inProblems = inProblems
				def resultList = QuestionnaireProblem.createCriteria().list([max: params.max, offset: params.offset]) {
					eq("questionnaire", questionnaire)

					problem getCriteria(params, status)
				}

				def json = new JSONArray()

				resultList.each {
					def jsonElement = JSON.parse((it.problem as JSON) as String)
					jsonElement.putAt("score", it.score)
					json.add(jsonElement)
				}

				["searchResults": (json as JSON) as String, "total": resultList.totalCount as String]
			} else {
				["searchResults": [] as String, "total": "0"]
			}
		}
	}

	def getData(Problem problem, User user) {
		dataService.getData([problem: problem, user: user])
	}

	def findByUser(Problem problem, User user, Problem.Status status) {
		try {
			redisService.memoize(cacheService.generateKey(Problem, problem, [user, status]), EXPIRE_CACHE) {
				def problemJSON= get(problem, status)

				if (problemJSON) {
					def json = JSON.parse(problemJSON as String)
					def evaluation

					def submission = Submission.findByProblemAndUserAndEvaluation(
							problem,
							user,
							Submission.Evaluation.CORRECT
					)

					if (submission) {
						evaluation = submission.evaluation
					} else {
						evaluation = Submission.findByProblemAndUser(
								problem,
								user,
								[sort: "submissionDate", order: "desc"]
						)?.evaluation
					}


					json.putAt("currentUser", [status: evaluation])

					return (json as JSON) as String
				}

				return problemJSON
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def findAllByUser(User user, Map params, Problem.Status status) {
		redisService.memoizeHash(cacheService.generateKey(Problem, params, [user, status]), EXPIRE_CACHE) {

			params.user = user.id

			def inProblems = checkInProblems(params)

			if (inProblems != null) {
				params.inProblems = inProblems
				def resultList = Problem.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params, status))

				def json = new JSONArray()

				resultList.each {
					def jsonElement = JSON.parse((it as JSON) as String)

					def problem =  Problem.get(jsonElement.getAt("id") as Long)

					def evaluation

					def submission = Submission.findByProblemAndUserAndEvaluation(
							problem,
							user,
							Submission.Evaluation.CORRECT
					)

					if (submission) {
						evaluation = submission.evaluation
					} else {
						evaluation = Submission.findByProblemAndUser(
								problem,
								user,
								[sort: "submissionDate", order: "desc"]
						)?.evaluation
					}

					jsonElement.putAt("currentUser", [status: evaluation])
					json.add(jsonElement)
				}

				["searchResults": (json as JSON) as String, "total": resultList.totalCount as String]

			} else {
				["searchResults": [] as String, "total": "0"]
			}
		}
	}

	def findByUserAndQuestionnaire(Problem problem, User user, Questionnaire questionnaire, Problem.Status status) {
		redisService.memoize(cacheService.generateKey(Problem, problem, [user, questionnaire, status]), EXPIRE_CACHE) {
			try {

				QuestionnaireProblem questionnaireProblem =  QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)

				if (problem.status == status && questionnaireProblem && questionnaire.users.contains(user)) {

					def problemJSON = get(problem, Problem.Status.ACCEPTED)

					if (problemJSON) {
						def json = JSON.parse(problemJSON as String)
						def evaluation

						def submission = Submission.findByProblemAndUserAndEvaluationAndSubmissionDateLessThan(
								problem,
								user,
								Submission.Evaluation.CORRECT,
								questionnaire.endDate
						)

						if (submission) {
							evaluation = submission.evaluation
						} else {
							evaluation = Submission.findByProblemAndUserAndSubmissionDateLessThan(
									problem,
									user,
									questionnaire.endDate,
									[sort: "submissionDate", order: "desc"]
							)?.evaluation
						}


						json.putAt("currentUser", [status: evaluation, score: evaluation == Submission.Evaluation.CORRECT
												   ? questionnaireProblem.score : 0,
												   penalty: QuestionnaireUserPenalty.findByQuestionnaireProblemAndQuestionnaireUser(
														   		questionnaireProblem,
														   		QuestionnaireUser.findByUserAndQuestionnaire(
																		user,
																		questionnaire
																)
												   )?.penalty
						])
						json.putAt("score", questionnaireProblem.score)


						return (json as JSON) as String
					}

					return problemJSON

				}
			} catch(ObjectNotFoundException e) {
				e.finalize()
			}
		}
	}

	def findAllByUserAndQuestionnaire(User user, Questionnaire questionnaire, Map params, Problem.Status status) {
		redisService.memoizeHash(cacheService.generateKey(Problem, params, [user, questionnaire, status]), EXPIRE_CACHE) {

			params.user = user.id
			def inProblems = checkInProblems(params)

			try {
				if (questionnaire.users.contains(user) && inProblems != null) {
					params.inProblems = inProblems
					def resultList = QuestionnaireProblem.createCriteria().list([max: params.max, offset: params.offset]) {
						eq("questionnaire", questionnaire)

						problem getCriteria(params, status)
					}

					def json = new JSONArray()

					resultList.each {
						def jsonElement = JSON.parse((it.problem as JSON) as String)

						def problem =  Problem.get(jsonElement.getAt("id") as Long)

						def evaluation

						def submission = Submission.findByProblemAndUserAndEvaluationAndSubmissionDateLessThan(
								problem,
								user,
								Submission.Evaluation.CORRECT,
								questionnaire.endDate
						)

						if (submission) {
							evaluation = submission.evaluation
						} else {
							evaluation = Submission.findByProblemAndUserAndSubmissionDateLessThan(
									problem,
									user,
									questionnaire.endDate,
									[sort: "submissionDate", order: "desc"]
							)?.evaluation
						}

						def penalty = null

						def questionnaireUser = QuestionnaireUser.findByUserAndQuestionnaire(
								user,
								questionnaire
						)

						if (questionnaireUser) {
							penalty = QuestionnaireUserPenalty.findByQuestionnaireProblemAndQuestionnaireUser(
									it,
									questionnaireUser
							)?.penalty
						}


						def userScore = 0

						if (evaluation && (evaluation == Submission.Evaluation.CORRECT)) {
							userScore = it.score
						}

						jsonElement.putAt("currentUser", [
								status: evaluation,
								score: userScore,
								penalty: penalty
						])

						jsonElement.putAt("score", it.score)
						json.add(jsonElement)
					}

					["searchResults": (json as JSON) as String, "total": resultList.totalCount as String]
				} else {
					["searchResults": [] as String, "total": "0"]
				}
			} catch (ObjectNotFoundException e) {
				e.finalize()
				return [searchResults: [], total: 0]
			}
		}
	}

	def checkInProblems(Map params) {
		def inProblems = []
		def user = null

		if (params.user) {
			user = User.load(params.user as Long)
		}

		if (user && params.attempted) {
			inProblems = Submission.createCriteria().list() {
				eq("user", user)
				projections {
					distinct("problem")
				}
			}.id
		} else if (user && params.suggested) {
			inProblems = Problem.findAllByUserSuggest(user)?.id
		} else if (user && params.approved) {
			inProblems = Problem.findAllByUserApproved(user)?.id
		}


		if (params.topics) {
			inProblems = Problem.createCriteria().list() {
				topics {
					inList("id", params.topics as List<Long>)
				}

				if (inProblems) {
					inList("id", inProblems)
				}
			}?.id
		}

		if (params.group) {
			def users = UserGroup.findAllByGroup(Group.load(params.group as Long)).user
			inProblems = inProblems ?: Problem.list().id
			inProblems -= Submission.createCriteria().list() {
				and {
					if (users && !users.empty) {
						inList('user', users)
					}
					eq('evaluation', Submission.Evaluation.CORRECT)
				}

				projections {
					distinct("problem")
				}
			}?.id
		}

		if (params.excludeTopics) {
			inProblems = inProblems ?: Problem.list().id
			inProblems -= Problem.createCriteria().list() {
				topics {
					inList("id", params.excludeTopics as List<Long>)
				}
			}?.id
		}

		if (user && params.excludeCorrect) {
			inProblems = inProblems ?: Problem.list().id
			inProblems -= Submission.createCriteria().list() {
				eq("user", user)
				eq("evaluation", Submission.Evaluation.CORRECT)
				projections {
					distinct("problem")
				}
			}?.id
		}

		if (params.exclude && !params.exclude.empty) {
			inProblems = inProblems ?: Problem.list().id
			inProblems -= Submission.createCriteria().list() {
				inList("id", params.exclude as List<Long>)
			}?.id
		}

		if ((params.topics
				|| params.excludeTopics
				|| (user && params.attempted)
				|| (user && params.suggested)
				|| (user && params.approved)
				|| (user && params.excludeCorrects)) && inProblems.isEmpty()) return null

		return inProblems
	}

	def save(Problem problem) {
		try {
			problem.lastUserUpdate = new Date()
			problem.save(flush: true)
			cacheService.expireCache(Problem, problem)

			get(problem, problem.status)
		} catch (Exception e) {
			e.finalize()
		}
	}

	Closure getCriteria(Map params, Problem.Status status) {

		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		return {
			and {
				if (params.q) {
					or {
						like("name", "%$params.q%")

						try {
							eq("id", params.q as Long)
						} catch (Exception e) {
							e.finalize()
						}

					}
				}

				if (status) {
					eq("status", status)
				} else {
					ne("status", Problem.Status.REJECTED)
				}

				if (params.inProblems && !params.inProblems.empty) {
					inList("id", params.inProblems)
				}

				!params.level ?: eq("level", params.level as Integer)
				!params.levelGt ?: gt("level", params.levelGt as Integer)
				!params.levelGe ?: ge("level", params.levelGe as Integer)
				!params.levelLt ?: lt("level", params.levelLt as Integer)
				!params.levelLe ?: le("level", params.levelLe as Integer)
				!params.levelNe ?: ne("level", params.levelNe as Integer)

				!params.timeLimit ?: eq("timeLimit", params.timeLimit as Integer)
				!params.timeLimitGt ?: gt("timeLimit", params.timeLimitGt as Integer)
				!params.timeLimitGe ?: ge("timeLimit", params.timeLimitGe as Integer)
				!params.timeLimitLt ?: lt("timeLimit", params.timeLimitLt as Integer)
				!params.timeLimitLe ?: le("timeLimit", params.timeLimitLe as Integer)
				!params.timeLimitNe ?: ne("timeLimit", params.timeLimitNe as Integer)

				!params.nd ?: eq("nd", params.nd as Double)
				!params.ndGt ?: gt("nd", params.ndGt as Double)
				!params.ndGe ?: ge("nd", params.ndGe as Double)
				!params.ndLt ?: lt("nd", params.ndLt as Double)
				!params.ndLe ?: le("nd", params.ndLe as Double)
				!params.ndNe ?: ne("nd", params.ndNe as Double)

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

			order(params.sort ?: "nd", params.order ?: "asc")
		}
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		try {
			params.max = Math.min(params.int("max", 0) ?: 10, 100)
			params.offset = params.int("offset", 0)
			params.excludeCorrect = params.boolean("excludeCorrect", false)
			params.attempted = params.filter?.toLowerCase() == "attempted"
			params.suggested = params.filter?.toLowerCase() == "suggested"
			params.approved = params.filter?.toLowerCase() == "approved"
			params.group = params.long("group")
			params.q = params.q ?: ""
			params.order = params.order ?: "asc"
			params.sort = params.sort ?: "nd"
			params.topics = params.topics ? params.list("topics")*.asType(Long) : []
			params.excludeTopics = params.excludeTopics ? params.list("excludeTopics")*.asType(Long) : []
			params.exclude = params.exclude ? params.list("exclude")*.asType(Long) : []
			params.level = params.int("level")
			params.levelGt = params.int("levelGt")
			params.levelGe = params.int("levelGe")
			params.levelLt = params.int("levelLt")
			params.levelLe = params.int("levelLe")
			params.levelNe = params.int("levelNe")
			params.nd = params.double("nd")
			params.ndGt = params.double("ndGt")
			params.ndGe = params.double("ndGe")
			params.ndLt = params.double("ndLt")
			params.ndLe = params.double("ndLe")
			params.ndNe = params.double("ndNe")

			return params
		} catch (Exception e) {
			e.finalize()
			return null
		}
	}

	boolean isSortable(param) {
		[
				"id",
				"name",
				"description",
				"inputFormat",
				"outputFormat",
				"source",
				"level",
				"timeLimit",
				"nd",
				"dateCreated",
				"lastUpdated",
				"status",
				"userApproved",
				"userSuggest",
		].contains(param)
	}

	/**
	 * Retorna uma recomendacão de problema para um usuário.
	 * Não utiliza cache.
	 */
	def getSuggestion(User user) {

		def orderedTopicMap,
			topicMap = [:],
			maxNd = [:],
			tempNd,
			suggestedNd,
			suggestedProblems,
			suggestedProblem,
			random = new Random()

		log.debug("Buscando todos os problemas acertados")
		// buscar os tópicos mais acertados pelo usuário.
		List<Problem> allProblems = UserProblem.findAllByUserAndStatus(user, Submission.Evaluation.CORRECT).problem
		log.debug("FIM : Buscando todos os problemas acertados")
		allProblems.each { problem ->
			problem.topics.each { topic ->
				topicMap[topic] = ++(topicMap[topic] ?: 0)

				// Atualiza os maiores e menores ND resolvidos daquele tópico.
				tempNd = (maxNd[topic] ?: 0)
				maxNd[topic] = problem.nd > (tempNd as Double) ? problem.nd : tempNd
			}
		}

		//ordenar : uma lista já ordenada pelos tópicos mais acertados
		orderedTopicMap = topicMap.sort {
			topicA, topicB -> topicB.value <=> topicA.value
		}


		for (topic in orderedTopicMap.keySet()) {
			log.debug("Procurando por:" + topic)

			suggestedNd = maxNd[(topic)] + 1

			suggestedProblems = Problem.createCriteria().list() {
				and {
					topics {
						eq("id", (topic as Topic).id)
					}

					if (allProblems && !allProblems.empty) {
						not {
							inList("id", allProblems.id)
						}
					}

					le("nd", suggestedNd)
				}
			}

			if (suggestedProblems.size() > 0) {
				try {
					suggestedProblem = suggestedProblems[random.nextInt(suggestedProblems.size())]
					get(suggestedProblem as Problem, Problem.Status.ACCEPTED)
				}catch (NullPointerException e) {
					e.finalize()
				}
			}
		}
		/* Se ele chegar aqui é por que não conseguiu achar nenhum problema para sugerir,
		 nesse caso, seleciona uma lista qualquer de 10 problemas ordenados do nível
		 mais fácil para o mais difícil
		 */
		suggestedProblems = UserProblem.createCriteria().list(max: 10) {
			problem {
				and {
					if (allProblems && !allProblems.empty) {
						not {
							inList("id", allProblems.id)
						}
					}
				}

				order("nd", "asc")
			}

		}
		log.debug("Não foi possível encontrar uma recomendacao de problema, de acordo com o perfil do usuário. Sendo assim, retornando simplesmente um problema que ele não tenha acertado")
		for (int i=0; i< 10; ++i ) {
			suggestedProblem = suggestedProblems[random.nextInt(suggestedProblems.size())]
			try {
				return get(suggestedProblem.problem, Problem.Status.ACCEPTED)
			}catch (NullPointerException e) {
				log.warn("Não foi possível sugerir o problema.", e)
			}
		}
		log.error("Todas as tentativas de sugestão de problemas falharam")
	}

	def uploadImage(CommonsMultipartFile file) {
		String path = grailsApplication.config.huxleyFileSystem.problem.images.dir + System.getProperty("file.separator")

		File dir = new File(path)
		dir.mkdirs()

		def originalFilename = file.originalFilename
		def index = originalFilename.lastIndexOf('.')
		def extension = ""
		if ((index > 0) && (originalFilename.size() > index)) {
			extension = originalFilename.substring(index - 1)
		}

		def filename = new String(Hex.encode(MessageDigest.getInstance("SHA1").digest(file.bytes))) + extension
		def destFile = new File(dir, filename)

		file.transferTo(destFile)

		return destFile
	}

	def getImage(String key) {
		try {

			String path = grailsApplication.config.huxleyFileSystem.problem.images.dir + System.getProperty("file.separator") + key
			def originalFile = new File(path)

			return originalFile
		} catch (Exception e) {
			e.finalize()
		}
	}

}
