package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.http.HttpStatus

class QuestionnaireController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def plagiarismService
	def questionnaireService
	def problemService
	def userService
	def submissionService
	def springSecurityService

	def beforeInterceptor = {
		params.q = params.q ?: ""
		params.max = Math.min(params.max as Integer ?: 10, 100)

		if (params.order) {
			if(!["asc", "desc"].contains(params.order)) {
				forward(controller: "Error", action: "wrongOrderParam")
			}
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_ADMIN_INST'])
	@Transactional
	def clone(long questionnaireId) {
		Questionnaire questionnaire = deserialize(false, null)
		User currentUser = springSecurityService.currentUser as User

		if (currentUser && UserGroup.findByUserAndGroupAndRole(currentUser, questionnaire.group, UserGroup.Role.TEACHER)) {
			onValid questionnaire, {
				customRender(questionnaireService.save(questionnaire, currentUser, questionnaireId))
			}
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	def customRender(content) {
		if (content) {
			if (content instanceof String) {
				render(contentType: "application/json", text: content)
			} else {
				response.setHeader("total", content["total"] as String)
				render(contentType: "application/json", text: content["searchResults"])
			}
		} else {
			forward(controller: "Error", action: "entityNotFound")
		}
	}

	@Secured('isAuthenticated()')
	def index() {
		if (params.sort && !questionnaireService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		def currentUser = springSecurityService.currentUser as User

		def parameters = questionnaireService.normalize(params)

		if (parameters.filter && parameters.filter.contains("OWN")) {
			parameters.groups = []
			parameters.groups.addAll(UserGroup.findAllByUserAndRole(currentUser, UserGroup.Role.TEACHER).group)
			parameters.groups.addAll(UserGroup.findAllByUserAndRole(currentUser, UserGroup.Role.TEACHER_ASSISTANT).group)
		}

		customRender questionnaireService.list(parameters)
	}

	@Secured('permitAll()')
	def show(Long id) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.get(id)
		def now = new Date()

		if (questionnaire) {
			if (currentUser &&
					(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

					UserInstitution.findByUserAndInstitutionAndRole(
							currentUser,
							questionnaire.group.institution,
							UserInstitution.Role.ADMIN_INST
					) ||

					UserGroup.findByUserAndGroupAndRole(
							currentUser,
							questionnaire.group,
							UserGroup.Role.TEACHER
					) ||

					UserGroup.findByUserAndGroupAndRole(
							currentUser,
							questionnaire.group,
							UserGroup.Role.TEACHER_ASSISTANT
					) ||

					(questionnaire.startDate < now && !QuestionnaireProblem.findAllByQuestionnaire(questionnaire).empty))
			) {
				customRender questionnaireService.get(questionnaire, currentUser)
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured('isAuthenticated()')
	def delete(Long id) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.get(id)

		if (currentUser.authorities.authority.contains("ROLE_ADMIN") ||
				UserGroup.findByUserAndGroupAndRole(currentUser, questionnaire.group, UserGroup.Role.TEACHER)) {

			if (questionnaireService.delete(questionnaire)) {
				render status: HttpStatus.NO_CONTENT
			} else {
				render status: HttpStatus.INTERNAL_SERVER_ERROR
			}
		} else {
			render status: HttpStatus.FORBIDDEN
		}

	}

	@Secured('permitAll()')
	def getProblems(Long questionnaireId, Long problemId) {

		def questionnaire = Questionnaire.load(questionnaireId)
		def currentUser = springSecurityService.currentUser as User
		def now = new Date()

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						) ||

						questionnaire.startDate < now)
		) {
			if (problemId) {
				customRender problemService.findByQuestionnaire(Problem.load(problemId), questionnaire, Problem.Status.ACCEPTED)
			} else {

				if (params.sort && !problemService.isSortable(params.sort)) {
					forward (controller: "Error", action: "wrongSortParam")
					return
				}

				customRender problemService.findAllByQuestionnaire(questionnaire, problemService.normalize(params), Problem.Status.ACCEPTED)
			}

		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured('permitAll()')
	def getUsers(Long questionnaireId, Long userId) {

		def questionnaire = Questionnaire.load(questionnaireId)
		def currentUser = springSecurityService.currentUser as User

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroup(
								currentUser,
								questionnaire.group,
						) ||

						QuestionnaireUser.findByUserAndQuestionnaire(
								currentUser,
								questionnaire
						))
		) {

			if (userId) {
				customRender userService.findByQuestionnaire(User.load(userId), questionnaire)
			} else {

				if (params.sort && !userService.isSortable(params.sort)) {
					forward (controller: "Error", action: "wrongSortParam")
					return
				}

				customRender userService.findAllByQuestionnaire(questionnaire, userService.normalize(params))
			}

		} else {
			render status: HttpStatus.FORBIDDEN
		}

	}

	@Secured('permitAll()')
	def getProblemSubmissions(Long questionnaireId, Long problemId, Long submissionId) {

		def questionnaire = Questionnaire.load(questionnaireId)
		def problem = Problem.load(problemId)
		def currentUser = springSecurityService.currentUser as User

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroup(
								currentUser,
								questionnaire.group,
						) ||

						QuestionnaireUser.findByUserAndQuestionnaire(
								currentUser,
								questionnaire
						))
		) {

			if (submissionId) {
				customRender submissionService.findByQuestionnaireAndProblem(Submission.load(submissionId), questionnaire, problem)
			} else {

				if (params.sort && !submissionService.isSortable(params.sort)) {
					forward (controller: "Error", action: "wrongSortParam")
					return
				}

				customRender submissionService.findAllByQuestionnaireAndProblem(questionnaire, problem, submissionService.normalize(params))
			}

		} else {
			render status: HttpStatus.FORBIDDEN
		}

	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def getSimilarity(Long questionnaireId, Long plagiarismId) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.load(questionnaireId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {
			customRender plagiarismService.get(Plagiarism.load(plagiarismId))
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def confirmSimilarity(Long questionnaireId, Long plagiarismId) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.load(questionnaireId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {

			customRender plagiarismService.changeStatus(Plagiarism.load(plagiarismId), Plagiarism.Status.CONFIRMED)
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def discardSimilarity(Long questionnaireId, Long plagiarismId) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.load(questionnaireId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {

			customRender plagiarismService.changeStatus(Plagiarism.load(plagiarismId), Plagiarism.Status.DISCARDED)
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def getSimilarities(Long questionnaireId) {

		def currentUser = springSecurityService.currentUser as User
		def questionnaire = Questionnaire.load(questionnaireId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {

			if (params.sort && !plagiarismService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender plagiarismService.findAllByQuestionnaire(Questionnaire.load(questionnaireId), plagiarismService.normalize(params))


		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def save() {
		Questionnaire questionnaire = deserialize(false, null)
		User currentUser = springSecurityService.currentUser as User

		if (currentUser && UserGroup.findByUserAndGroupAndRole(currentUser, questionnaire.group, UserGroup.Role.TEACHER)) {
			onValid questionnaire, {
				customRender questionnaireService.save(questionnaire, currentUser)
			}
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def update(Long id) {
		User currentUser = springSecurityService.currentUser as User
		Questionnaire questionnaire = Questionnaire.get(id)

		if (questionnaire) {
			if (currentUser && UserGroup.findByUserAndGroupAndRole(currentUser, questionnaire.group, UserGroup.Role.TEACHER)) {
				questionnaire = deserialize(true, questionnaire.id)
				onValid questionnaire, {
					customRender questionnaireService.save(questionnaire, currentUser)
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def validate() {
		onValid deserialize(false, null) as Questionnaire, {
			render status: HttpStatus.ACCEPTED
		}
	}

	def canUpdate(Questionnaire questionnaire) {

		User currentUser = springSecurityService.currentUser as User

		if(UserGroup.findByUserAndGroupAndRole(currentUser, questionnaire.group, UserGroup.Role.TEACHER) ||
				currentUser.authorities.authority.contains('ROLE_ADMIN')) {
			return true
		}

		return false
	}


	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def addProblem(Long questionnaireId, Long problemId) {

		def questionnaire = Questionnaire.get(questionnaireId)

		if (canUpdate(questionnaire)) {
			def problem = Problem.get(problemId)
			def score = 0

			if (params.score) {
				score = params.score
			} else {
				if (request.JSON["score"]) {
					score = request.JSON["score"]
				}
			}

			try {
				score = score as Double
			} catch (NumberFormatException e) {
				score = score.replace(",", ".")
				score = score as Double
			}

			customRender questionnaireService.addProblem(questionnaire, problem, score as Double, problemService.normalize(params))
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def removeProblem(Long questionnaireId, Long problemId) {
		def questionnaire = Questionnaire.get(questionnaireId)

		if (canUpdate(questionnaire)) {
			customRender questionnaireService.removeProblem(
					questionnaire,
					Problem.get(problemId),
					params
			)
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured('permitAll()')
	def getData(Long questionnaireId) {
		customRender questionnaireService.getData(Questionnaire.load(questionnaireId))
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def addPenalty(Long userId, Long questionnaireId, Long problemId) {

		def questionnaire = Questionnaire.load(questionnaireId)
		def currentUser = User.load(userId)
		def problem = Problem.load(problemId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {
			def penalty

			if (params.penalty) {
				try {
					penalty = params.getDouble("penalty")
				} catch (Exception e) {
					e.finalize()
					render status: HttpStatus.BAD_REQUEST
					return
				}
			} else if (request.JSON["penalty"]) {
				try {
					penalty = request.JSON["penalty"] as Double
				} catch (Exception e) {
					e.finalize()
					render status: HttpStatus.BAD_REQUEST
					return
				}
			} else {
				render status: HttpStatus.BAD_REQUEST
				return
			}

			def questionnaireProblem = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)
			def questionnaireUser = QuestionnaireUser.findByQuestionnaireAndUser(questionnaire, currentUser)

			if (questionnaireProblem && questionnaireUser) {
				def questionnaireUserPenalty = QuestionnaireUserPenalty.findByQuestionnaireUserAndQuestionnaireProblem(
						questionnaireUser,
						questionnaireProblem
				)

				if (questionnaireUserPenalty) {
					questionnaireUserPenalty.penalty = penalty
				} else {
					questionnaireUserPenalty = new QuestionnaireUserPenalty(
							questionnaireProblem: questionnaireProblem,
							questionnaireUser: questionnaireUser,
							penalty: penalty
					)
				}

				questionnaireUserPenalty.save(flush: true)

				questionnaireUserPenalty.hasErrors() ?
						render(status: HttpStatus.BAD_REQUEST) :
						customRender(problemService.findByUserAndQuestionnaire(problem, currentUser, questionnaire, Problem.Status.ACCEPTED))

			} else {
				render status: HttpStatus.BAD_REQUEST
			}
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def removePenalty(Long userId, Long questionnaireId, Long problemId) {
		def questionnaire = Questionnaire.load(questionnaireId)
		def currentUser = User.load(userId)
		def problem = Problem.load(problemId)

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {

			def questionnaireProblem = QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)
			def questionnaireUser = QuestionnaireUser.findByQuestionnaireAndUser(questionnaire, currentUser)

			if (questionnaireProblem && questionnaireUser) {
				def questionnaireUserPenalty = QuestionnaireUserPenalty.findByQuestionnaireUserAndQuestionnaireProblem(
						questionnaireUser,
						questionnaireProblem
				)

				if (questionnaireUserPenalty) {
					try {
						questionnaireUserPenalty.delete(flush: true)
						render status: HttpStatus.NO_CONTENT
					} catch (Exception e) {
						e.printStackTrace()
						render status: HttpStatus.INTERNAL_SERVER_ERROR
					}
				} else {
					render status: HttpStatus.NOT_FOUND
				}

			} else {
				render status: HttpStatus.BAD_REQUEST
			}
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}


	def onValid(Questionnaire questionnaire, c) {
		try {
			if (questionnaire.hasErrors()) {
				params["entity"] = questionnaire
				forward(controller: "Error", action: "invalidQuestionnaire")
			} else {
				c()
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (JSONException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (ConverterException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (IllegalArgumentException e){
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (Exception e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		}
	}


	Questionnaire deserialize(update, id) {
		def questionnaire = update ? Questionnaire.get(id as Long) : new Questionnaire()

		def json = request.JSON
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		questionnaire.title = json["title"] as String ?: questionnaire.title
		questionnaire.description = json["description"] as String ?: questionnaire.description

		if (json["group"] && json["group"]["id"]) {
			questionnaire.group = Group.get(json["group"]["id"] as Long) ?: questionnaire.group
		} else {
			questionnaire.group = questionnaire.group
		}


		try {
			questionnaire.startDate = json["startDate"] ?
					formatter.parseDateTime(json["startDate"] as String).toDate() :
					questionnaire.startDate

			questionnaire.endDate = json["endDate"] ?
					formatter.parseDateTime(json["endDate"] as String).toDate() :
					questionnaire.endDate

		} catch (IllegalArgumentException e) {
			e.finalize()
		}

		questionnaire.validate()

		return questionnaire
	}

	@Secured(['ROLE_ADMIN'])
	def forceUpdate(Long questionnaireId) {
		QuestionnaireService.updateScores(Questionnaire.get(questionnaireId), false)
	}

	@Secured(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN_INST'])
	def getSubmissions(Long questionnaireId, Long submissionId) {
		def questionnaire = Questionnaire.load(questionnaireId)
		def currentUser = springSecurityService.currentUser as User

		if (currentUser &&
				(currentUser.authorities.authority.contains('ROLE_ADMIN') ||

						UserInstitution.findByUserAndInstitutionAndRole(
								currentUser,
								questionnaire.group.institution,
								UserInstitution.Role.ADMIN_INST
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER
						) ||

						UserGroup.findByUserAndGroupAndRole(
								currentUser,
								questionnaire.group,
								UserGroup.Role.TEACHER_ASSISTANT
						))
		) {
			if (submissionId) {
				Submission submission = Submission.load(submissionId)

				if (submission && submission.submissionDate < questionnaire.endDate) {
					customRender submissionService.get(submission)
				} else {
					render status: HttpStatus.NOT_FOUND
				}
			} else {

				if (params.sort && !submissionService.isSortable(params.sort)) {
					forward (controller: "Error", action: "wrongSortParam")
					return
				}

				customRender submissionService.findAllByQuestionnaire(questionnaire, submissionService.normalize(params))
			}

		} else {
			render status: HttpStatus.FORBIDDEN
		}


	}

}
