package com.thehuxley

import grails.converters.JSON
import grails.transaction.Transactional

class TopCoderService {

	def cacheService

	def refreshTopCoder() {

		cacheService.expireCache(cacheService.generateKey(User, null, "topcoder"))

		def users = User.list()
		def total = users.size()
		def count = 1
		users.each { user ->
			if (log.debugEnabled)
				log.debug("**************\t\tCalculando a pontuação do usuário $user.username #$user.id... ${count++}/$total")
			refreshTopCoder(user, false)
		}

		refreshPositions()
	}

	@Transactional
	def refreshPositions() {
		def position = 1
		if (log.infoEnabled)
			log.info(" **** Iniciando a atualização da posição do TOPCODER **** ")
		TopCoder.list([order: "desc", sort: "points"]).each { TopCoder topCoder ->
			topCoder.position = position++
			topCoder.save()
		}
		if (log.infoEnabled)
			log.info(" **** Encerrando a atualização da posição do TOPCODER **** ")
	}

	@Transactional
	def refreshTopCoder(User user, updatePositions = true) {

		cacheService.expireCache(cacheService.generateKey(User, user, "topcoder"))

		Double points = 0

		TopCoder topCoder

		if (user.authorities.authority.contains("ROLE_ADMIN")
				|| user.authorities.authority.contains("ROLE_TEACHER")
				|| user.authorities.authority.contains("ROLE_TEACHER_ASSISTANT")) {

			TopCoder.findByUser(user)?.delete()

		} else {
			Submission.createCriteria().list {

				problem {
					eq("status", Problem.Status.ACCEPTED)
				}

				eq("user", user)
				eq("evaluation", Submission.Evaluation.CORRECT)

				projections {
					distinct("problem")
				}
			}.each {
				points += it.nd
			}

			if (log.debugEnabled)
				log.debug("\t\t\t > A pontuação do usuário $user.username é $points**** \n")

			topCoder = TopCoder.findByUser(user)

			if (topCoder) {
				if (points > 0) {
					topCoder.points = points
					topCoder.save()
				} else {
					topCoder.delete()
				}
			} else if (points > 0) {
				def tp = new TopCoder(points: points, user: user, position: TopCoder.count() + 1)
				tp.save()

				if (log.debugEnabled && tp.hasErrors()) {
					log.debug(tp.errors)
				}
			}
		}
        if (updatePositions && topCoder) {
			if (topCoder.position) {
				def topCoderAbove = TopCoder.findByPosition(topCoder.position - 1)

				if (topCoderAbove && (topCoder.points > topCoderAbove.points)) {
					def position = topCoderAbove.position
					topCoder.position = position
					topCoderAbove.position = position + 1

					topCoder.save()
					topCoderAbove.save(flush: true)

				}
			}
		}
	}

	@Transactional
	def updateNd(check = false) {

		//GParsPool.withPool {

			def problemData = [:]
			def usersWhoSolvedCount = []
			def result = [
					problemsChanged: []
			]

			Problem.findAllByStatus(Problem.Status.ACCEPTED).each { Problem problem ->

				def counts = [:]

				def countSolvedProblems = Submission.createCriteria().get() {
					eq("evaluation", Submission.Evaluation.CORRECT)
					eq("problem", problem)
					projections {
						countDistinct("user")
					}
				}

				usersWhoSolvedCount.add(countSolvedProblems)
				counts.put("usersWhoSolvedCount", countSolvedProblems)

				counts.put("usersWhoTriedCount",
						Submission.createCriteria().get() {
							eq("problem", problem)
							projections {
								countDistinct("user")
							}
						}
				)

				problemData.put(problem, counts)
			}

			def clusters = getClusters(usersWhoSolvedCount)

			problemData.keySet().each { Problem problem ->
				if (problemData[problem]["usersWhoTriedCount"] > 5) {
					def index = 0, nd = 10

					while ((index < 10) && (clusters[index] < problemData[problem]["usersWhoSolvedCount"])) {
						index++
						nd--
					}

					if (nd != problem.nd) {
						def graphic = ""

						if (problem.nd < nd) {
							(1..(nd - problem.nd)).each {
								graphic += "+"
							}
						} else {
							(1..(problem.nd - nd)).each {
								graphic += "-"
							}
						}

						result.problemsChanged.add([(problem.id): [
								name              : problem.name,
								new               : nd,
								old               : problem.nd,
								graphic           : graphic,
								userWhoSolvedCount: problemData[problem]["usersWhoSolvedCount"]
						]])

						if (!check) {
							problem.nd = nd
							problem.save()
						}
					}
				}
			}

			if (!check) {
				refreshTopCoder()
			}

			result.put("distribution", clusters)
			(result as JSON) as String
		//}
	}

	List getClusters(List usersWhoSolvedCount) {

		List<BigDecimal> DISTRIBUTION = [0.05, 0.075, 0.1, 0.125, 0.2, 0.125, 0.1, 0.1, 0.075, 0.05]
		def clusters= []
		def indexes = []

		usersWhoSolvedCount = usersWhoSolvedCount.sort()

		(0..(DISTRIBUTION.size() - 1)).each {Integer index ->
			indexes[index] = index == 0 ? (usersWhoSolvedCount.size() * DISTRIBUTION[index])
					: (indexes[index - 1] + (usersWhoSolvedCount.size() * DISTRIBUTION[index]))

			clusters[index] = usersWhoSolvedCount[(Math.floor(indexes[index] as BigDecimal) - 1) as Integer]
		}

		return clusters
	}

}
