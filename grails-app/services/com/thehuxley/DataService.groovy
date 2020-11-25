package com.thehuxley

import grails.converters.JSON
import org.hibernate.ObjectNotFoundException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class DataService {

	def redisService
	def cacheService

	def final MAX_HISTORY_RANGE = 180
	def final DEFAULT_HISTORY_RANGE = 180
	def final MAX_LAST_SUBMISSIONS = 10
	def final MAX_LAST_CORRECT_SUBMISSIONS = 10
	def final MAX_FASTEST_SUBMISSIONS = 10
	def final EXPIRE_CACHE = null

	String getData(Map params) {
		try {
			if (params.group) {
				if (params.user) {

					return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
						userContext(
								params.user as User,
								Submission.createCriteria().list([
										order: "desc",
										sort : "submissionDate"
								], getSubmissionCriteria([
										user     : params.user as User,
										startDate: params.group.startDate as Date,
										endDate  : params.group.endDate as Date
								])),

								params.group.startDate as Date,
								params.group.endDate as Date
						) as JSON
					}

				}

				return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
					groupContext(
							params.group as Group,
							Submission.createCriteria().list([
									order: "desc",
									sort : "submissionDate"
							], getSubmissionCriteria([
									users: UserGroup.findAllByGroupAndRoleAndEnabled(
											params.group as Group,
											UserGroup.Role.STUDENT,
											true
									)?.user,
									startDate: params.group.startDate as Date,
									endDate  : params.group.endDate as Date
							])),

							params.group.startDate as Date,
							params.group.endDate as Date
					) as JSON
				}

			} else if (params.questionnaire) {
				if (params.user) {

					return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
						userContext(
								params.user as User,
								Submission.createCriteria().list([
										order: "desc",
										sort : "submissionDate"
								], getSubmissionCriteria([
										user     : params.user as User,
										startDate: params.questionnaire.startDate as Date,
										endDate  : params.questionnaire.endDate as Date
								])),

								params.questionnaire.startDate as Date,
								params.questionnaire.endDate as Date
						) as JSON
					}

				} else if (params.problem) {

					return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
						extractSubmissionsData(
								Submission.createCriteria().list([
										order: "desc",
										sort : "submissionDate"
								], getSubmissionCriteria([
										problem  : params.problem as Problem,
										startDate: params.questionnaire.startDate as Date,
										endDate  : params.questionnaire.endDate as Date
								])),

								params.questionnaire.startDate as Date,
								params.questionnaire.endDate as Date
						) as JSON
					}

				}

				return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
					questionnaireContext(
							params.questionnaire as Questionnaire,
							Submission.createCriteria().list([
									order: "desc",
									sort : "submissionDate"
							], getSubmissionCriteria([
									users    : UserGroup.findAllByGroupAndRoleAndEnabled(
											params.questionnaire.group as Group,
											UserGroup.Role.STUDENT,
											true
									)?.user,
									startDate: params.questionnaire.startDate as Date,
									endDate  : params.questionnaire.endDate as Date
							])),

							params.questionnaire.startDate as Date,
							params.questionnaire.endDate as Date
					) as JSON
				}

			} else if (params.user) {
				if (params.problem) {
					return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
						userProblemContext(
								Submission.createCriteria().list([
										order: "desc",
										sort : "submissionDate"
								], getSubmissionCriteria([
										user   : params.user as User,
										problem: params.problem as Problem
								]))
						) as JSON
					}
				}

				return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
					userContext(
							params.user as User,
							Submission.createCriteria().list([
									order: "desc",
									sort : "submissionDate"
							], getSubmissionCriteria([
									user: params.user as User
							]))
					) as JSON
				}

			} else if (params.problem) {
				return redisService.memoize(generateKey(params), EXPIRE_CACHE) {
					extractSubmissionsData(
							Submission.createCriteria().list([
									order: "desc",
									sort : "submissionDate"
							], getSubmissionCriteria([
									problem: params.problem as Problem
							]))
					) as JSON
				}
			}
		} catch (ObjectNotFoundException e) {
			e.finalize()
			return null
		}
		return null
	}

	def expireCache(Submission submission) {

		cacheService.expireCache(generateKey([user: submission.user]), true)
		cacheService.expireCache(generateKey([problem: submission.problem]), true)
		cacheService.expireCache(generateKey([user: submission.user, problem: submission.problem]), true)

		def submissionDate = submission.submissionDate;
		def groups = UserGroup.findByUserAndRoleAndEnabled(
				submission.user,
				UserGroup.Role.STUDENT,
				true
		)?.group

		groups.each { Group group ->
			if (submissionDate && group.endDate && group.startDate) {
				if (submissionDate.before(group.endDate) && submissionDate.after(group.startDate)) {
					cacheService.expireCache(generateKey([group: group]), true)
					cacheService.expireCache(generateKey([group: group, user: submission.user]), true)

					def questionnaires = Questionnaire.findAllByGroup(group)

					questionnaires.each { Questionnaire questionnaire ->
						if (submissionDate.before(questionnaire.endDate) && submissionDate.after(questionnaire.startDate)) {
							cacheService.expireCache(generateKey([questionnaire: questionnaire]), true)
							cacheService.expireCache(generateKey([questionnaire: questionnaire, problem: submission.user]), true)
							cacheService.expireCache(generateKey([questionnaire: questionnaire, problem: submission.problem]), true)
						}
					}
				}
			}
		}
	}

	def generateKey(Map params) {
		def key = "data-submission"

		if (params.group) {
			key += ":group:${params.group.id}"

			if (params.user) {
				key += ":user:${params.user.id}"
			}
		} else if (params.questionnaire) {
			key += ":questionnaire:${params.questionnaire.id}"

			if (params.user) {
				key += ":user:${params.user.id}"
			} else if (params.problem) {
				key += ":problem:${params.problem.id}"
			}
		} else if (params.user) {
			key += ":user:${params.user.id}"
			if (params.problem) {
				key += ":problem:${params.problem.id}"
			}
		} else if (params.problem) {
			key += ":problem:${params.problem.id}"
		}

		return key
	}

	def userProblemContext(List<Submission> submissions, Date startDate = null, Date endDate = null) {
		def submissionsData = extractSubmissionsData(submissions, startDate, endDate)

		def status = null

		if(!submissionsData.lastCorrectSubmissions.empty) {
			status = Submission.Evaluation.CORRECT
		} else if (!submissionsData.lastSubmissions.empty) {
			status = (submissionsData.lastSubmissions.first() as Submission).evaluation
		}


		return [
				submissionsCount              : submissionsData.submissionsCount,
				submissionsCountByLanguage    : submissionsData.submissionsCountByLanguage,
				submissionsCountByEvaluation  : submissionsData.submissionsCountByEvaluation,
				submissionsCountHistory       : submissionsData.submissionsCountHistory,
				lastSubmissions               : submissionsData.lastSubmissions,
				lastCorrectSubmissions        : submissionsData.lastCorrectSubmissions,
				fastestSubmissions            : submissionsData.fastestSubmissions,
				status                        : status
		]
	}

	def questionnaireContext(Questionnaire questionnaire, List<Submission> submissions, Date startDate = null, Date endDate = null) {

		def problems = QuestionnaireProblem.findAllByQuestionnaire(questionnaire).problem
		def submissionsInQuestionnaire = submissions.findAll { problems.contains(it.problem) }
		def usersScores = [:]

		def submissionsData = extractSubmissionsData(
				submissionsInQuestionnaire,
				startDate,
				endDate
		)


		questionnaire.users.each { User user ->
			usersScores.put(user.id, [:])
			def total = 0
			problems.each { Problem problem ->

				def questionnaireProblem = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)

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

				def data = [
						status : evaluation,
						score  : evaluation == Submission.Evaluation.CORRECT ? questionnaireProblem.score : 0,
						penalty: QuestionnaireUserPenalty.findByQuestionnaireProblemAndQuestionnaireUser(
								questionnaireProblem,
								QuestionnaireUser.findByUserAndQuestionnaire(
										user,
										questionnaire
								)
						)?.penalty
				]

				total += ((data.score as Double) - ((data.penalty ?: 0) as Double))
				usersScores.get(user.id).put(problem.id, data)
			}

			usersScores.get(user.id).put("score", total)
		}

		return [
				submissionsCount              : submissionsData.submissionsCount,
				usersWhoTriedCount            : submissionsData.usersWhoTriedCount,
				usersWhoSolvedCount           : submissionsData.usersWhoSolvedCount,
				triedProblemsCount			  : submissionsData.triedProblemsCount,
				solvedProblemsCount			  : submissionsData.solvedProblemsCount,
				ndCount						  : submissionsData.ndCount,
				submissionsCountByLanguage    : submissionsData.submissionsCountByLanguage,
				submissionsCountByEvaluation  : submissionsData.submissionsCountByEvaluation,
				solvedProblemsCountByTopic    : submissionsData.solvedProblemsCountByTopic,
				solvedProblemsCountByNd		  : submissionsData.solvedProblemsCountByNd,
				submissionsCountHistory       : submissionsData.submissionsCountHistory,
				submissionsCountByProblem	  : submissionsData.submissionsCountByProblem,
				usersWhoTriedByProblemCount   : submissionsData.usersWhoTriedByProblemCount,
				usersWhoSolvedByProblemCount  : submissionsData.usersWhoSolvedByProblemCount,
				lastSubmissions               : submissionsData.lastSubmissions,
				lastCorrectSubmissions        : submissionsData.lastCorrectSubmissions,
				fastestSubmissions            : submissionsData.fastestSubmissions,
				ndCountHistory				  : submissionsData.ndCountHistory,
				usersScores				      : usersScores
		]
	}

	def groupContext(Group group, List<Submission> submissions, Date startDate = null, Date endDate = null) {
		def submissionsData = extractSubmissionsData(submissions, startDate, endDate)

		def now = new Date()

		def openQuizzesCount = Questionnaire.countByStartDateGreaterThanAndEndDateLessThanAndGroup(now, now, group)
		def quizzesCount = Questionnaire.countByGroup(group)
		def closedQuizzesCount = quizzesCount - openQuizzesCount
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		if (!startDate || !endDate) {
			endDate = submissions.get(0).submissionDate
			startDate = endDate - DEFAULT_HISTORY_RANGE
		}

		def accessCountHistory =  emptyDateTimeISOMap(startDate, endDate)
		def openQuizzesCountHistory = emptyDateTimeISOMap(startDate, endDate)

		def userGroups = UserGroup.findAllByGroupAndRole(group, UserGroup.Role.STUDENT)

		AuthenticationHistory.createCriteria().list() {
			if (userGroups && !userGroups.empty) {
				inList("user", userGroups)
			}
			between("accessedDate", startDate, endDate)
		}.each {
			def key = new DateTime(it.accessedDate).withTimeAtStartOfDay().toString(formatter)

			if (accessCountHistory.containsKey(key)) {
				accessCountHistory[key]++
			}
		}
		Questionnaire.findAllByGroup(group).each { Questionnaire questionnaire ->
			(0..(questionnaire.endDate - questionnaire.startDate)).each {
				def key = new DateTime(questionnaire.startDate.plus(it as Integer)).withTimeAtStartOfDay().toString(formatter)
				if (openQuizzesCountHistory.containsKey(key)) {
					openQuizzesCountHistory[key]++
				}
			}
		}

		def studentCount = UserGroup.countByGroupAndRole(group, UserGroup.Role.STUDENT)
		def teacherCount = UserGroup.countByGroupAndRole(group, UserGroup.Role.TEACHER)
		def teacherAssistantCount = UserGroup.countByGroupAndRole(group, UserGroup.Role.TEACHER_ASSISTANT)


		return [
				submissionsCount              : submissionsData.submissionsCount,
				usersWhoTriedCount            : submissionsData.usersWhoTriedCount,
				usersWhoSolvedCount           : submissionsData.usersWhoSolvedCount,
				triedProblemsCount			  : submissionsData.triedProblemsCount,
				solvedProblemsCount			  : submissionsData.solvedProblemsCount,
				ndCount						  : submissionsData.ndCount,
				submissionsCountByLanguage    : submissionsData.submissionsCountByLanguage,
				submissionsCountByEvaluation  : submissionsData.submissionsCountByEvaluation,
				solvedProblemsCountByTopic    : submissionsData.solvedProblemsCountByTopic,
				solvedProblemsCountByNd		  : submissionsData.solvedProblemsCountByNd,
				submissionsCountHistory       : submissionsData.submissionsCountHistory,
				usersWhoTriedCountHistory     : submissionsData.usersWhoTriedCountHistory,
				submissionsCountByProblem	  : submissionsData.submissionsCountByProblem,
				usersWhoTriedByProblemCount   : submissionsData.usersWhoTriedByProblemCount,
				usersWhoSolvedByProblemCount  : submissionsData.usersWhoSolvedByProblemCount,
				lastSubmissions               : submissionsData.lastSubmissions,
				lastCorrectSubmissions        : submissionsData.lastCorrectSubmissions,
				fastestSubmissions            : submissionsData.fastestSubmissions,
				ndCountHistory				  : submissionsData.ndCountHistory,
				openQuizzesCount              : openQuizzesCount,
				closedQuizzesCount            : closedQuizzesCount,
				quizzesCount                  : quizzesCount,
				accessCountHistory            : accessCountHistory,
				studentCount				  : studentCount,
				teacherCount                  : teacherCount,
				teacherAssistantCount         : teacherAssistantCount,
				openQuizzesCountHistory       : openQuizzesCountHistory
		]
	}

	def userContext(User user, List<Submission> submissions, Date startDate = null, Date endDate = null) {
		def submissionsData = extractSubmissionsData(submissions, startDate, endDate)

		def problemsCountByTopic = [:]
		def problemsCountByNd = [:]

		Problem.findAllByStatus(Problem.Status.ACCEPTED).each {
			it.topics.each {
				if (!problemsCountByTopic[it.name]) {
					problemsCountByTopic.put(it.name, 0)
				}
				problemsCountByTopic[it.name]++
			}

			if (!problemsCountByNd[it.nd]) {
				problemsCountByNd.put(it.nd, 0)
			}
			problemsCountByNd[it.nd]++
		}

		def topcoderRank = ++TopCoder.list([order: "desc", sort: "points"]).findIndexOf {
			it.user.id == user.id
		}

		return [
				submissionsCount              : submissionsData.submissionsCount,
				triedProblemsCount			  : submissionsData.triedProblemsCount,
				solvedProblemsCount			  : submissionsData.solvedProblemsCount,
				ndCount						  : submissionsData.ndCount,
				submissionsCountByLanguage    : submissionsData.submissionsCountByLanguage,
				submissionsCountByEvaluation  : submissionsData.submissionsCountByEvaluation,
				solvedProblemsCountByTopic    : submissionsData.solvedProblemsCountByTopic,
				problemsCountByTopic		  : problemsCountByTopic,
				submissionsCountHistory       : submissionsData.submissionsCountHistory,
				submissionsCountByProblem	  : submissionsData.submissionsCountByProblem,
				lastSubmissions               : submissionsData.lastSubmissions,
				lastCorrectSubmissions        : submissionsData.lastCorrectSubmissions,
				ndCountHistory				  : submissionsData.ndCountHistory,
				solvedProblemsCountByNd		  : submissionsData.solvedProblemsCountByNd,
				problemsCountByNd			  : problemsCountByNd,
				topcoderRank                  : topcoderRank
		]
	}

	Map extractSubmissionsData(List<Submission> submissions, Date startDate = null, Date endDate = null) {
		def data = [
				submissionsCount              : 0,
				usersWhoTriedCount            : 0,
				usersWhoSolvedCount           : 0,
				triedProblemsCount			  : 0,
				solvedProblemsCount			  : 0,
				ndCount						  : 0,
				submissionsCountByLanguage    : emptySubmissionsByLanguageMap(),
				submissionsCountByEvaluation  : emptySubmissionsByEvaluationMap(),
				solvedProblemsCountByTopic    : [:],
				solvedProblemsCountByNd		  : [:],
				submissionsCountHistory       : [:],
				submissionsCountByProblem	  : [:],
				usersWhoTriedByProblemCount   : [:],
				usersWhoSolvedByProblemCount  : [:],
				lastSubmissions               : [],
				lastCorrectSubmissions        : [],
				fastestSubmissions            : new ArrayList<Submission>(),
				ndCountHistory				  : [:],
				usersWhoTriedCountHistory     : [:]
		]

		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()
		def evaluationMap = emptySubmissionsByEvaluationMap()
		def usersWhoTried = []
		def usersWhoSolved = []
		def correctSubmissions = []
		def triedProblems = []
		def solvedProblems = []
		HashMap<String, List> usersWhoTriedCountHistory = [:]
		HashMap<Problem, List> userWhoTriedProblem = [:]
		HashMap<Problem, List> userWhoSolvedProblem = [:]


		if (!submissions.empty) {
			if (!startDate || !endDate) {
				endDate = submissions.get(0).submissionDate
				startDate = endDate - DEFAULT_HISTORY_RANGE
			}

			data.submissionsCountHistory = emptySubmissionsHistoryMap(startDate, endDate)
			data.usersWhoTriedCountHistory = emptyDateTimeISOMap(startDate, endDate)
			usersWhoTriedCountHistory = emptyDateTimeListISOMap(startDate, endDate)

			def ndCountHistory = emptyDateTimeMap(endDate, submissions.last().submissionDate)


			submissions.each { Submission submission ->

				data.submissionsCount++

				if (!userWhoTriedProblem[submission.problem]) {
					userWhoTriedProblem.put(submission.problem, [])
				}

				if (!userWhoTriedProblem.get(submission.problem).contains(submission.user)) {
					userWhoTriedProblem.get(submission.problem).add(submission.user)

					if (!data.usersWhoTriedByProblemCount[submission.problem.id]) {
						data.usersWhoTriedByProblemCount[submission.problem.id] = 0
					}

					data.usersWhoTriedByProblemCount[submission.problem.id]++
				}

				if (submission.evaluation == Submission.Evaluation.CORRECT) {
					if (!userWhoSolvedProblem[submission.problem]) {
						userWhoSolvedProblem.put(submission.problem, [])
					}

					if (!userWhoSolvedProblem.get(submission.problem).contains(submission.user)) {
						userWhoSolvedProblem.get(submission.problem).add(submission.user)

						if (!data.usersWhoSolvedByProblemCount[submission.problem.id]) {
							data.usersWhoSolvedByProblemCount[submission.problem.id] = 0
						}

						data.usersWhoSolvedByProblemCount[submission.problem.id]++
					}
				}

				if (!usersWhoTried.contains(submission.user)) {
					usersWhoTried.add(submission.user)
					data.usersWhoTriedCount++
				}

				def usersWhoTriedCountHistoryKey = new DateTime(submission.submissionDate).withTimeAtStartOfDay().toString(formatter)

				if(!usersWhoTriedCountHistory[usersWhoTriedCountHistoryKey]?.contains(submission.user)) {
					usersWhoTriedCountHistory[usersWhoTriedCountHistoryKey]?.add(submission.user)
					if (data.usersWhoTriedCountHistory.containsKey(usersWhoTriedCountHistoryKey)) {
						data.usersWhoTriedCountHistory[usersWhoTriedCountHistoryKey]++
					}
				}

				if (!usersWhoSolved.contains(submission.user) &&
						(submission.evaluation == Submission.Evaluation.CORRECT)) {
					usersWhoSolved.add(submission.user)
					data.usersWhoSolvedCount++
				}

				if (!triedProblems.contains(submission.problem)) {
					triedProblems.add(submission.problem)
					data.triedProblemsCount++
				}

				if (!solvedProblems.contains(submission.problem) &&
						(submission.evaluation == Submission.Evaluation.CORRECT)) {
					solvedProblems.add(submission.problem)
					data.solvedProblemsCount++
					data.ndCount += submission.problem.nd

					for (Topic topic : submission.problem.topics) {
						if (!data.solvedProblemsCountByTopic[topic.name]) {
							data.solvedProblemsCountByTopic.put(topic.name, 0)
						}

						data.solvedProblemsCountByTopic[topic.name]++
					}

					if (!data.solvedProblemsCountByNd[submission.problem.nd]) {
						data.solvedProblemsCountByNd.put(submission.problem.nd, 0)
					}
					data.solvedProblemsCountByNd[submission.problem.nd]++

					def key = new DateTime(submission.submissionDate).withTimeAtStartOfDay()

					if (ndCountHistory.containsKey(key)) {
						ndCountHistory[key] += submission.problem.nd
					}
				}

				data.submissionsCountByLanguage[submission.language.label][submission.evaluation as String]++
				data.submissionsCountByLanguage[submission.language.label]["TOTAL"]++

				data.submissionsCountByEvaluation[submission.evaluation as String]++
				data.submissionsCountByEvaluation["TOTAL"]++

				def key = new DateTime(submission.submissionDate).withTimeAtStartOfDay().toString(formatter)
				if (data.submissionsCountHistory.containsKey(key)) {
					data.submissionsCountHistory[key][submission.evaluation as String]++
					data.submissionsCountHistory[key]["TOTAL"]++
				}

				if (submission.problem) {
					if (!data.submissionsCountByProblem[submission.problem.name]) {
						data.submissionsCountByProblem.put(submission.problem.name, evaluationMap.clone())
					}

					data.submissionsCountByProblem[submission.problem.name][submission.evaluation as String]++
					data.submissionsCountByProblem[submission.problem.name]["TOTAL"]++
				}

				if ((submission.evaluation == Submission.Evaluation.CORRECT) && (submission.time > 0)) {
					correctSubmissions.add(submission)

					if (correctSubmissions.size() - 1 <= MAX_LAST_CORRECT_SUBMISSIONS) {
						data.lastCorrectSubmissions.add(submission)
					}
				}

				if (data.submissionsCount - 1 <= MAX_LAST_SUBMISSIONS) {
					data.lastSubmissions.add(submission)
				}
			}

			correctSubmissions.sort { a, b -> a.time == b.time ? 0 : a.time < b.time ? -1 : 1 }

			Iterator<Submission> correctSubmissionsIterator = correctSubmissions.iterator()
			while (data.fastestSubmissions.size() <= MAX_FASTEST_SUBMISSIONS && correctSubmissionsIterator.hasNext()) {
				Submission submission = correctSubmissionsIterator.next()
				if (!data.fastestSubmissions.user.id.contains(submission.user.id)
						&& !submission.user.authorities.authority.contains("ROLE_ADMIN")) {
					data.fastestSubmissions.add(submission)
				}
			}

			def keys = ndCountHistory.keySet().sort()
			def acc = 0
			def size = keys.size()
			keys.eachWithIndex { key, i ->
				acc += ndCountHistory.get(key)
				if ((size - i) <= Math.min(endDate - startDate, MAX_HISTORY_RANGE)) {
					data.ndCountHistory.put(((key as DateTime).toString(formatter)), acc)
				}
			}
		}
		return data
	}

	Map emptyDateTimeMap(Date startDate, Date endDate) {
		def map = [:]

		(0..(endDate - startDate)).each {
			map.put(new DateTime(endDate - (it as Integer)).withTimeAtStartOfDay(), 0)
		}

		return map
	}

	Map emptyDateTimeISOMap(Date startDate, Date endDate) {
		def map = [:]
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		(0..(endDate - startDate)).each {
			map.put(new DateTime(endDate - (it as Integer)).withTimeAtStartOfDay().toString(formatter), 0)
		}

		return map
	}

	Map emptyDateTimeListISOMap(Date startDate, Date endDate) {
		def map = [:]
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		(0..(endDate - startDate)).each {
			map.put(new DateTime(endDate - (it as Integer)).withTimeAtStartOfDay().toString(formatter), [])
		}

		return map
	}


	Map emptySubmissionsByLanguageMap() {
		def map = [:]
		def evaluationMap = emptySubmissionsByEvaluationMap()

		Language.list().each {
			map.put(it.label, evaluationMap.clone())
		}

		return map
	}

	Map emptySubmissionsByEvaluationMap() {
		def map = [:]

		map.put('TOTAL', 0)

		Submission.Evaluation.values().each {
			map.put(it as String, 0)
		}

		return map
	}

	Map emptySubmissionsHistoryMap(Date startDate, Date endDate) {

		def map = [:]
		def HISTORY_RANGE = Math.min(endDate - startDate, MAX_HISTORY_RANGE)
		def evaluationMap = emptySubmissionsByEvaluationMap()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		(0..HISTORY_RANGE).each {
			def key = new DateTime(endDate - it).withTimeAtStartOfDay().toString(formatter)
			map.put(key, evaluationMap.clone())
		}

		return map
	}

	Closure getSubmissionCriteria(Map params) {
		return {
			and {
				if (params.problem) {
					eq("problem", params.problem)
				}

				if (params.user) {
					eq("user", params.user)
				} else if (params.users && !params.users.empty) {
					inList("user", params.users)
				}

				if (params.startDate && params.endDate) {
					between("submissionDate", params.startDate, params.endDate)
				}
			}
		}
	}
}
