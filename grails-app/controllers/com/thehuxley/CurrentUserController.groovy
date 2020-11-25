package com.thehuxley

import com.thehuxley.atmosphere.Feed
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartFile

class CurrentUserController {

	static responseFormats = ['json']
	static allowedMethods = []

	def springSecurityService
	def userService
	def submissionService
	def grailsLinkGenerator
	def messageService
	def mailService
	def pushService
	def feedService
    def queueService

	def beforeInterceptor = {
		params.userId = (springSecurityService.currentUser as User).id
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
	def getCurrentUser() {
		customRender userService.getFullUser(springSecurityService.currentUser as User)
	}

	@Secured('isAuthenticated()')
	def getUserData() {
		forward(controller: "User", action: "getUserData")
	}

	@Secured('isAuthenticated()')
	def getInstitutions(Long institutionId) {
		params.institutionId = institutionId

		forward controller: "User", action: "getInstitutions", params: params
	}

	@Secured('isAuthenticated()')
	def getGroups(Long groupId) {
		params.groupId = groupId

		forward controller: "User", action: "getGroups", params: params
	}

	@Secured('isAuthenticated()')
	def getProblemData(Long problemId) {
		params.problemId = problemId

		forward controller: "User", action: "getProblemData", params: params
	}

	@Secured('isAuthenticated()')
	def getProblems(Long problemId) {
		params.problemId = problemId

		forward controller: "User", action: "getProblems", params: params
	}

	@Secured('isAuthenticated()')
	def getProblemSubmissions(Long problemId, Long submissionId) {
		params.problemId = problemId
		params.submissionId = submissionId

		forward controller: "User", action: "getProblemSubmissions", params: params
	}

	@Secured('isAuthenticated()')
	def getSubmissions(Long submissionId) {
		params.submissionId = submissionId

		forward controller: "User", action: "getSubmissions", params: params
	}

	@Secured('isAuthenticated()')
	def getQuestionnaires(Long questionnaireId) {
		params.questionnaireId = questionnaireId

		forward controller: "User", action: "getQuestionnaires", params: params
	}

	@Secured('isAuthenticated()')
	def getQuestionnaireProblems(Long questionnaireId, Long problemId) {
		params.questionnaireId = questionnaireId
		params.problemId = problemId

		forward controller: "User", action: "getQuestionnaireProblems", params: params
	}

	@Secured('isAuthenticated()')
	def getQuestionnaireProblemSubmissions(Long questionnaireId, Long problemId, Long submissionId ) {
		params.questionnaireId = questionnaireId
		params.problemId = problemId
		params.submissionId = submissionId

		forward controller: "User", action: "getQuestionnaireProblemSubmissions", params: params
	}

	@Secured('isAuthenticated()')
	def getProblemSuggestion() {
		forward controller: "User", action: "getProblemSuggestion", params : params
	}

	@Secured('isAuthenticated()')
	def createSubmission(Long problemId) {
		try {
			def user = springSecurityService.currentUser as User

			if (params.language && params.file) {

				def filename = (params.file as CommonsMultipartFile).getOriginalFilename()

				if (filename.split('\\.').length < 2) {
					render status: HttpStatus.BAD_REQUEST
					return
				}

				Problem problem = Problem.get(problemId)
				Language language = Language.get(params.language as Long)

				Integer tries = (Submission.countByUserAndProblem(user, problem) + 1)
				def file = params.file as CommonsMultipartFile

				def dir = new File(submissionService.mountSubmissionPath(problem.id, user.id, language.name, tries))
				dir.mkdirs()
				file.transferTo(new File(dir.absolutePath, file.originalFilename))

				def submission = submissionService.createSubmission(
						user,
						problem,
						language,
						file.originalFilename,
						tries
				)

                def testCases = TestCase.findAllByProblem(submission.problem)
                def sourceCode = submissionService.getSubmissionFile(submission).getText("UTF-8")
                queueService.sendSubmissionToJudge(submission, sourceCode, testCases)

                customRender ((submission as JSON) as String)
			} else {
				render status: HttpStatus.BAD_REQUEST
			}
		} catch (Exception e) {
            log.error("Problemas ao criar uma submissão", e)
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def uploadAvatar() {
		if (params.file) {

			def kb = 1024
			def MIN_SIZE = 1 * kb
			def MAX_SIZE = 5 * (kb * kb)
			def ALLOWED_MIME_TYPE = ["image/jpg", "image/jpeg", "image/png"]

			def fileSize = (params.file as CommonsMultipartFile).size

			if (ALLOWED_MIME_TYPE.contains((params.file as CommonsMultipartFile).contentType)) {
				if ((fileSize >= MIN_SIZE) && (fileSize <= MAX_SIZE)) {
					def file = userService.uploadAvatar(params.file as CommonsMultipartFile)
					customRender(
							([
									_links: [
											self: grailsLinkGenerator.link(
													controller: "users",
													action: "avatar",
													absolute: true
											) + "/" + file.name
									],
									name  : file.name
							] as JSON) as String
					)
				} else {
					forward(controller: "Error", action: "invalidAvatarSize")
				}
			} else {
				forward(controller: "Error", action: "invalidAvatarMimeType")
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def cropAvatar() {

		def json = request.JSON

		if (json["filename"]) {
			User user = springSecurityService.currentUser as User
			customRender userService.crop(
					user,
					json["filename"] as String,
					json["x"] as Integer ?: 0,
					json["y"] as Integer ?: 0,
					json["width"] as Integer ?: 400,
					json["height"] as Integer ?: 300
			)
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('isAuthenticated()')
	def getAvatar() {
		forward controller: "User", action: "getAvatar", params : params
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def update() {
		params.id = params.userId
		forward controller: "User", action: "update", params : params
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def updatePassword() {
		params.id = params.userId
		forward controller: "User", action: "updatePassword", params : params
	}

	@Secured('isAuthenticated()')
	def getMessageCount() {
		def currentUser = springSecurityService.currentUser as User

		def countUnread = messageService.countUnread(currentUser)

		if (countUnread) {
			respond(["unreadCount": countUnread] as JSON)
		} else {
			respond(["unreadCount": 0] as JSON)
		}
	}

	@Secured('isAuthenticated()')
	def getMessages(Long messageId) {
		def currentUser = springSecurityService.currentUser as User

		if (messageId) {

			def message = Message.get(messageId)

			if (message) {
				if ((message.recipient.id == currentUser.id) || (message.sender.id == currentUser.id)) {
					respond message
				} else {
					render status: HttpStatus.FORBIDDEN
				}
			} else {
				render status: HttpStatus.NOT_FOUND
			}

		} else {
			respond messageService.findAllByRecipient(currentUser, params)
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def sendMessage() {
		def currentUser = springSecurityService.currentUser as User
		def message = new Message()

		message.body = request.JSON["body"] as String
		message.sender = currentUser
		message.firstMessage = true
		message.recipient = User.load(request.JSON["recipient"]["id"] as Long)

		pushService.publish(new Feed(
				type: Feed.Type.USER_CHAT_MESSAGE,
				body: [
						sender: currentUser
				]),
				User.load(request.JSON["recipient"]["id"] as Long))

		respond messageService.save(message)

	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def editMessage(Long messageId) {
		def currentUser = springSecurityService.currentUser as User
		def message = Message.get(messageId)

		if (message) {
			if (message.sender.id == currentUser.id) {
				message.body = request.JSON["body"] as String
				message.unread = true

				respond messageService.save(message)
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def responseMessage(Long messageId) {
		def currentUser = springSecurityService.currentUser as User
		def message = Message.get(messageId)

		if (message) {

			def responseMessage = new Message()

			responseMessage.body = request.JSON["body"] as String
			responseMessage.sender = currentUser
			responseMessage.recipient = User.load(request.JSON["recipient"]["id"] as Long)

			messageService.save(responseMessage)

			message.addToResponses(responseMessage)
			message.save(flush: true)

			pushService.publish(new Feed(
					type: Feed.Type.USER_CHAT_MESSAGE,
					body: [
							sender: currentUser,
							msgId: responseMessage.id
					]),
					User.load(request.JSON["recipient"]["id"] as Long))

			respond responseMessage
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def deleteMessage(Long messageId) {
		def currentUser = springSecurityService.currentUser as User

		def message = Message.get(messageId)

		if (message && message.sender.id == currentUser.id) {
			messageService.delete(message)
			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.FORBIDDEN
		}

	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def markMessageAsRead(Long messageId) {
		def currentUser = springSecurityService.currentUser as User

		def message = Message.get(messageId)

		if (message && message.recipient.id == currentUser.id) {
			messageService.markAsRead(message)
			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.FORBIDDEN
		}
	}

	@Secured('isAuthenticated()')
	def sendContactEmail() {
		def currentUser = springSecurityService.currentUser as User

		if (request.JSON["subject"] && request.JSON["body"]) {
			if (currentUser) {

				def institutions = UserInstitution.findAllByUser(currentUser).institution.name
				def groups = UserGroup.findAllByUser(currentUser).group.name

				def header = """
					name: $currentUser.name
					username: $currentUser.username
					email: $currentUser.email
					institutions: ${institutions.join(", ")}
					groups: ${groups.join(", ")}
					roles: ${currentUser.authorities.authority.join(", ")}
				""".replaceAll("\t", "").trim()

				try {
					mailService.sendMail {
						to "support@thehuxley.com"
						from currentUser.email
						subject request.JSON["subject"]
						body header + "\n\n" + request.JSON["body"]
					}

					render status: HttpStatus.NO_CONTENT
				} catch (Exception e) {
					e.printStackTrace()
					render status: HttpStatus.SERVICE_UNAVAILABLE
				}

			} else {
				render status: HttpStatus.NOT_FOUND
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}

	}

	@Secured('isAuthenticated()')
	def getUserFeed() {
		def currentUser = springSecurityService.currentUser as User ?: null
		respond feedService.findAllByUser(currentUser, params)
	}

}
