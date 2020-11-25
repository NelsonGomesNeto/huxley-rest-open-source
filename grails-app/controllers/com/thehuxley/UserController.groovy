package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import grails.util.Environment
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class UserController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def userService
	def institutionService
	def groupService
	def questionnaireService
	def problemService
	def submissionService
	def springSecurityService
	def mailService

	def beforeInterceptor = {
		params.q = params.q ?: ""
		params.max = Math.min(params.max as Integer ?: 10, 100)

		if (params.order) {
			if(!["asc", "desc"].contains(params.order)) {
				forward(controller: "Error", action: "wrongOrderParam")
			}
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

	@Secured('permitAll()')
	def index() {
		if (params.sort && !userService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender userService.list(userService.normalize(params))
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender userService.get(User.load(id))
	}

	@Secured('permitAll()')
	def getUserData(Long userId) {
		customRender userService.getData(User.load(userId))
	}

	@Secured('permitAll()')
	def getInstitutions(Long userId, Long institutionId) {

		def user = User.load(userId)

		if (institutionId) {
			customRender institutionService.findByUser(Institution.load(institutionId), user, Institution.Status.APPROVED)
		} else {

			if (params.sort && !institutionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender institutionService.findAllByUser(user, institutionService.normalize(params), Institution.Status.APPROVED)
		}
	}

	@Secured('permitAll()')
	def getGroups(Long userId, Long groupId) {

		def user = User.load(userId)

		if (groupId) {
			customRender groupService.findByUser(Group.load(groupId), user)
		} else {

			if (params.sort && !groupService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender groupService.findAllByUser(user, groupService.normalize(params))
		}
	}

	@Secured('permitAll()')
	def getProblemData(Long userId, Long problemId) {
		customRender problemService.getData(Problem.load(problemId), User.load(userId))
	}

	@Secured('permitAll()')
	def getProblems(Long userId, Long problemId) {

		def user = User.load(userId)

		Problem.Status status = Problem.Status.ACCEPTED
		if (user) {
			def authorities = user.getAuthorities().authority

			if ((authorities.contains("ROLE_TEACHER")
					|| authorities.contains("ROLE_ADMIN_INST")
					|| authorities.contains("ROLE_ADMIN"))) {
				status = null
			}
		}

		if (problemId) {
			customRender problemService.findByUser(Problem.load(problemId), user, status)
		} else {

			if (params.sort && !problemService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender problemService.findAllByUser(user, problemService.normalize(params), status)
		}
	}

	@Secured('permitAll()')
	def getProblemSubmissions(Long userId, Long problemId, Long submissionId) {

		def user = User.load(userId)
		def problem = Problem.load(problemId)

		if (submissionId) {
			customRender submissionService.findByUserAndProblem(Submission.load(submissionId), problem, user)
		} else {

			if (params.sort && !submissionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender submissionService.findAllByUserAndProblem(problem, user, submissionService.normalize(params))
		}
	}


	/**
	 * Retorna uma sugestão de problema para um determinado usuário autenticado
	 */
	@Secured('permitAll()')
	def getProblemSuggestion() {
		//def user = User.load(userId)
		//respond problemService.getSuggestion(user)
	}


	@Secured('permitAll()')
	def getSubmissions(Long userId, Long submissionId) {

		def user = User.load(userId)

		if (submissionId) {
			customRender submissionService.findByUser(Submission.load(submissionId), user)
		} else {

			if (params.sort && !submissionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender submissionService.findAllByUser(user, submissionService.normalize(params))
		}
	}



	@Secured('permitAll()')
	def getQuestionnaires(Long userId, Long questionnaireId) {

		def user = User.load(userId)

		if (questionnaireId) {
			customRender questionnaireService.findByUser(Questionnaire.load(questionnaireId), user)
		} else {

			if (params.sort && !questionnaireService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			def parameters = questionnaireService.normalize(params)
			if (parameters.filter && parameters.filter.contains("OWN")) {
				parameters.groups = UserGroup.findAllByUserAndRole(user, UserGroup.Role.TEACHER).group
				customRender questionnaireService.list(parameters)
			} else {
				customRender questionnaireService.findAllByUser(user, parameters)
			}
		}
	}



	@Secured('permitAll()')
	def getQuestionnaireProblems(Long userId, Long questionnaireId, Long problemId) {

		def user = User.load(userId)
		def questionnaire = Questionnaire.load(questionnaireId)

		if (problemId) {
			customRender problemService.findByUserAndQuestionnaire(Problem.load(problemId), user, questionnaire, Problem.Status.ACCEPTED)
		} else {

			if (params.sort && !problemService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender problemService.findAllByUserAndQuestionnaire(
					user,
					questionnaire,
					problemService.normalize(params),
					Problem.Status.ACCEPTED)
		}
	}



	@Secured('permitAll()')
	def getQuestionnaireProblemSubmissions(Long userId, Long questionnaireId, Long problemId, Long submissionId ) {

		def user = User.load(userId)
		def questionnaire = Questionnaire.load(questionnaireId)
		def problem = Problem.load(problemId)

		if (submissionId) {
			customRender submissionService.findByUserAndQuestionnaireAndProblem(
					Submission.load(submissionId),
					user,
					questionnaire,
					problem)
		} else {

			if (params.sort && !submissionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender submissionService.findAllByUserAndQuestionnaireAndProblem(
					user,
					questionnaire,
					problem,
					submissionService.normalize(params)
			)
		}
	}

	@Transactional(readOnly = false)
	@Secured('permitAll()')
	def save() {
		try {
			User user = deserialize(request.JSON, false, null)

			if (user?.hasErrors()) {
				params["entity"] = user
				forward(controller: "Error", action: "invalidUser")
			} else {
				customRender userService.save(user)
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (ConverterException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (IllegalArgumentException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Transactional(readOnly = false)
	@Secured('permitAll()')
	def update(Long id) {
		try {
			User user = deserialize(request.JSON, true, id)

			if (user.id == (springSecurityService.currentUser as User).id) {
				if (user?.hasErrors()) {
					params["entity"] = user
					forward(controller: "Error", action: "invalidUser")
				} else {
					customRender userService.save(user)
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (ConverterException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (IllegalArgumentException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Transactional(readOnly = false)
	@Secured('permitAll()')
	def recoveryPassword() {
		def query = params.k ? params.k as String : request.JSON["k"] as String

		if (query) {
			User user = User.findByEmailOrUsername(query, query)
			if (user) {
				def key = (userService.generateRecoveryKey(user) as PendencyKey).hashKey

				def url = "http://dev.thehuxley.com"

				if (Environment.current == Environment.PRODUCTION) {
					url = "https://www.thehuxley.com"
				}

				try {
					mailService.sendMail {
						to user.email
						subject message(code: "email.recoveryPassword.subject")
						body message(code: "email.recoveryPassword.body", args: [user.name, url, key])
					}

					render status: HttpStatus.NO_CONTENT
				} catch (Exception e) {
					e.finalize()
					render status: HttpStatus.SERVICE_UNAVAILABLE
				}

			} else {
				render status: HttpStatus.NOT_FOUND
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Transactional(readOnly = false)
	@Secured('permitAll()')
	def updatePassword(String key) {
		User user = springSecurityService.currentUser as User

		def json = request.JSON
		def action = null
		def pendencyKey = null

		if (!user) {
			pendencyKey = PendencyKey.findByHashKey(key)

			if (pendencyKey && pendencyKey.type == PendencyKey.Type.CHANGE_PASSWORD) {
				user = User.get(pendencyKey.entity)
			} else {
				render status: HttpStatus.FORBIDDEN
				return
			}

			if (!user) {
				render status: HttpStatus.NOT_FOUND
				return
			}
		} else {
			def password = springSecurityService?.passwordEncoder ?
					springSecurityService.encodePassword(json["password"] as String) : json["password"] as String

			if (user.password != password) {
				action = "passwordWrong"
			}
		}

		def newPassword = json["newPassword"] as String
		def confirmNewPassword = json["confirmNewPassword"] as String

		if (newPassword && confirmNewPassword && (newPassword == confirmNewPassword)) {
			if (newPassword.size() < 6 || newPassword.size() > 255) {
				action = "passwordInvalid"
			}
		} else {
			action = "passwordNotMatch"
		}

		if (!action) {
			user.password = newPassword
			if (user.validate()) {
				if (pendencyKey) {
					pendencyKey.delete()
				}
				customRender userService.save(user)
				return
			} else {
				action = "invalidUser"
			}
		}

		forward(controller: "Error", action: action)
	}

	@Secured('permitAll()')
	def getAvatar(Long userId) {
		User user = User.get(userId)

		if (user) {
			getAvatarByKey(user.avatar)
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured('permitAll()')
	def getAvatarByKey(String key) {

		def width = params["width"] as Integer ?: 0
		def height = params["height"] as Integer ?: 0

		File file = userService.getAvatar(key, width, height)

		if (file) {
			response.setContentType("image/png")
			response.setContentLength(file.bytes.length)
			response.setHeader("Content-disposition", "filename=${file.name}")
			response.outputStream << file.bytes
			response.outputStream.flush()
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Transactional @Secured(['ROLE_ADMIN'])
	def anonymizer() {
		if (Environment.current == Environment.DEVELOPMENT) {
			User.list().each { User user ->
				if (user.id != 1L) {
					def key = groupService.generateAccessKey(user.id).toLowerCase()

					user.username = key
					user.email = "contato+${key}@thehuxley.com"
					user.name = "User $key"
					user.password = key
					user.avatar = "default.png"
					println user.username
					user.save()
				}
			}

			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.METHOD_NOT_ALLOWED
		}
	}

	@Secured('permitAll()')
	def validate() {
		try {
			User user = springSecurityService.currentUser as User

			if (user) {
				user = deserialize(request.JSON, true, user.id)
			} else {
				user = deserialize(request.JSON, false, null)
			}

			if (user.hasErrors()) {
				params["entity"] = user
				forward(controller: "Error", action: "invalidUser")
			} else {
				render status: HttpStatus.ACCEPTED
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (ConverterException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (IllegalArgumentException e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
		} catch (Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}


	User deserialize(json, update, userId) {
		def user = update ? User.get(userId as Long) : new User()

		user.username = json["username"] as String ?: user.username
		user.email = json["email"] as String ?: user.email
		user.name = json["name"] as String ?: user.name

		if (!update) {
			user.password = json["password"] as String ?: user.password
		}

		if (json["institution"]) {
			def institution = Institution.load(request.JSON["institution"]["id"] as Long)
			if (institution && UserInstitution.findByUserAndInstitution(user, institution)) {
				user.institution = institution
			}
		}

		user.validate()

		return user
	}

}


