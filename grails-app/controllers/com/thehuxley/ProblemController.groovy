package com.thehuxley

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartFile

class ProblemController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def problemService
	def submissionService
	def testCaseService
	def oracleService
	def springSecurityService
	def grailsLinkGenerator

	def beforeInterceptor = {
		params.q = params.q ?: ""
		params.max = Math.min(params.max as Integer ?: 50, 100)

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

		if (params.sort && !problemService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		User user = springSecurityService.currentUser as User
		Problem.Status status = Problem.Status.ACCEPTED

		if (user) {
			params.user = user.id
			def authorities = user.getAuthorities().authority
			if ((authorities.contains("ROLE_TEACHER")
					|| authorities.contains("ROLE_ADMIN_INST")
					|| authorities.contains("ROLE_ADMIN"))) {
				try {
					status = params.status ? Problem.Status.valueOf(params.status as String) : Problem.Status.ACCEPTED
				} catch (Exception e) {
					e.finalize()
				}
			}
		}

		customRender problemService.list(problemService.normalize(params), status)
	}

	@Secured('permitAll()')
	def show(Long id) {
		User user = springSecurityService.currentUser as User
		Problem.Status status = Problem.Status.ACCEPTED
		if (user) {
			def authorities = user.getAuthorities().authority

			if ((authorities.contains("ROLE_TEACHER")
					|| authorities.contains("ROLE_ADMIN_INST")
					|| authorities.contains("ROLE_ADMIN"))) {
				status = null
			}
		}

		customRender problemService.get(Problem.load(id), status)
	}

	@Secured('permitAll()')
	def getData(Long problemId) {
		customRender problemService.getData(Problem.load(problemId))
	}

	@Transactional(readOnly = true)
	@Secured('permitAll()')
	def getOracleConsult(String hash) {
		if (hash) {
			respond OracleConsult.findByHash(hash)
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}


	@Transactional
	@Secured('permitAll()')
	def sendToOracle(Long problemId) {
		def currentUser = springSecurityService.currentUser as User

		def JSON = request.JSON
		def input = JSON["input"] as String

		if (input) {

			def oracleConsult = new OracleConsult(
					input: input,
					user: currentUser,
					problem: Problem.read(problemId)
			).save(flush: true)

			/*
            Usei o read() aqui porque ele desativa o dirtyChecking.
             O problema será usado somente para leitura no serviço.
             Não usei o load() porque precisamos de atributos do problema.
             */
			oracleService.sendToOracle(currentUser, input, oracleConsult.problem, oracleConsult.hash)
			render(contentType: "application/json") {
				[id: oracleConsult.hash]
			}

		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def save() {
		try {
			def problem = deserialize(request.JSON, false, null)
			User currentUser = springSecurityService.currentUser as User
			problem.userSuggest = currentUser

			if (!problem.hasErrors()) {
				customRender problemService.save(problem)
			} else {
				params.entity = problem
				forward(controller: "Error", action: "invalidProblem")
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

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def update(Long id) {

		User currentUser = springSecurityService.currentUser as User
		def roles = currentUser.authorities.authority

		try {			
			def problem = deserialize(request.JSON, true, id)
						
			if (!problem.hasErrors()) {
				if (roles.contains("ROLE_ADMIN")
						|| (problem.userSuggest.id == currentUser.id)
						|| UserInstitution.findByUserAndRoleAndInstitutionInList(
								currentUser,
								UserInstitution.Role.ADMIN_INST,
								(problem.userSuggest.groups.institution).toList()
				)) {
					customRender problemService.save(problem)
				} else {
					render status: HttpStatus.FORBIDDEN
				}
			} else {
				params.entity = problem
				forward(controller: "Error", action: "invalidProblem")
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


	@Secured('permitAll()')
	def getSubmissions(Long problemId, Long submissionId) {

		def problem = Problem.load(problemId)

		if (submissionId) {
			customRender submissionService.findByProblem(Submission.load(submissionId), problem)
		} else {

			if (params.sort && !submissionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender submissionService.findAllByProblem(problem, submissionService.normalize(params))
		}
	}

	@Secured('permitAll()')
	def getExampleTestCases(Long problemId, Long testCaseId) {
		def problem = Problem.load(problemId)

		if (testCaseId) {
			customRender testCaseService.findByProblem(TestCase.load(testCaseId), problem, true)
		} else {

			if (params.sort && !testCaseService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender testCaseService.findAllByProblem(problem, testCaseService.normalize(params), true)
		}
	}

	@Secured('isAuthenticated()')
	def getTestCases(Long problemId, Long testCaseId){
		def problem = Problem.load(problemId)
		def currentUser = springSecurityService.currentUser as User
		def testCase = TestCase.get(testCaseId)
		def roles = currentUser.getAuthorities().collect { it.authority }

		if (roles.contains('ROLE_ADMIN') ||
				roles.contains('ROLE_ADMIN_INST') ||
				roles.contains('ROLE_TEACHER') ||
				roles.contains('ROLE_TEACHER_ASSISTANT') ||
				testCase.example
		) {
			if (testCaseId) {
				customRender testCaseService.findByProblem(testCase, problem)
			} else {
				if (params.sort && !testCaseService.isSortable(params.sort)) {
					forward (controller: "Error", action: "wrongSortParam")
					return
				}

				customRender testCaseService.findAllByProblem(problem, testCaseService.normalize(params))
			}
		} else {
			if (testCaseId) {
				testCase.input = null
				testCase.output = null

				customRender testCaseService.findByProblem(testCase, problem)
			}
		}
	}

	@Secured(['ROLE_ADMIN'])
	def getTestCaseList(Long problemId) {
		Problem problem = Problem.get(problemId)

		customRender testCaseService.getTestCaseList(problem)
	}

	@Secured(['ROLE_ADMIN'])
	def getInputTestCasePlainText(Long problemId, Long testCaseId) {
		Problem problem = Problem.get(problemId)
		TestCase testCase = TestCase.get(testCaseId)

		String input = testCaseService.findByProblemInputPlainText(testCase, problem)

		render input
	}

	@Secured(['ROLE_ADMIN'])
	def getOutputTestCasePlainText(Long problemId, Long testCaseId) {
		Problem problem = Problem.get(problemId)
		TestCase testCase = TestCase.get(testCaseId)

		String output = testCaseService.findByProblemOutputPlainText(testCase, problem)

		render output
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def saveTestCase(Long problemId) {
		def problem = Problem.load(problemId)

		def testCase = new TestCase()
		def JSON = request.JSON

		testCase.problem = problem
		testCase.output = JSON["output"] as String
		testCase.input = JSON["input"] as String
		testCase.example = JSON["example"] as Boolean
		testCase.tip = JSON["tip"] as String

		customRender testCaseService.save(testCase)
	}



	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def updateTestCase(Long problemId, Long testCaseId){


		def testCase = TestCase.load(testCaseId)
		def JSON = request.JSON

		testCase.output = JSON["output"] as String ?: testCase.output
		testCase.input = JSON["input"] as String ?: testCase.input
		testCase.example = JSON["example"] != null ? JSON["example"] as Boolean : testCase.example
		testCase.tip = JSON["tip"] as String ?: testCase.tip

		if (testCase.problem.id == problemId) {
			customRender testCaseService.update(testCase)
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST', 'ROLE_TEACHER'])
	def deleteTestCase(Long problemId, Long testCaseId){

		def testCase = TestCase.load(testCaseId)

		if (testCase.problem.id == problemId && testCaseService.delete(testCase)) {
			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.NOT_ACCEPTABLE
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def validate() {
		try {
			def problem = deserialize(request.JSON, false, null)

			if (problem.hasErrors()) {
				params["entity"] = problem
				forward(controller: "Error", action: "invalidProblem")
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


	Problem deserialize(json, update, problemId) {
		User user = springSecurityService.currentUser as User
		def problem = update ? Problem.get(problemId as Long) : new Problem()

		problem.name = json["name"] as String ?: problem.name
		problem.description = json["description"] as String ?: problem.description
		problem.inputFormat = json["inputFormat"] as String ?: problem.inputFormat
		problem.outputFormat = json["outputFormat"] as String ?: problem.outputFormat
		problem.source = json["source"] as String ?: problem.source
		problem.level = json["level"] as Integer ?: problem.level
		problem.timeLimit = json["timeLimit"] as Integer ?: problem.timeLimit
		problem.status = json["status"] ? Problem.Status.valueOf(json["status"] as String) : problem.status
		problem.nd = null

		if (problem.getPersistentValue("status") && (problem.getPersistentValue("status") != problem.status)) {
			problem.userApproved = user
		}

		def topics = json["topics"] ? json["topics"] as List<Topic> : null
		if (topics && !topics.empty) {
			problem.setTopics(new HashSet<Topic>())

			json["topics"].each {
				problem.addToTopics(Topic.load(it["id"] as Long))
			}
		}

		problem.validate()
		return problem
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def uploadImage() {
		if (params.file) {

			def kb = 1024
			def MIN_SIZE = 1 * kb
			def MAX_SIZE = 5 * (kb * kb)
			def ALLOWED_MIME_TYPE = ["image/jpg", "image/jpeg", "image/png"]

			def fileSize = (params.file as CommonsMultipartFile).size

			if (ALLOWED_MIME_TYPE.contains((params.file as CommonsMultipartFile).contentType)) {
				if ((fileSize >= MIN_SIZE) && (fileSize <= MAX_SIZE)) {
					def file = problemService.uploadImage(params.file as CommonsMultipartFile)
					customRender(
							([
									_links: [
											self: grailsLinkGenerator.link(
													controller: "problems",
													action: "image",
													absolute: true
											) + "/" + file.name
									],
									name  : file.name
							] as JSON) as String
					)
				} else {
					forward(controller: "Error", action: "invalidProblemImageSize")
				}
			} else {
				forward(controller: "Error", action: "invalidProblemImageMimeType")
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('permitAll()')
	def getImageByKey(String key) {

		File file = problemService.getImage(key)

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

}
