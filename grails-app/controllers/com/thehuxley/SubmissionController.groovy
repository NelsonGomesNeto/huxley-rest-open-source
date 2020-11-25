package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.hibernate.ObjectNotFoundException
import org.springframework.http.HttpStatus

class SubmissionController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET", getSubmissionFile: "GET"]

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

		if (params.sort && !submissionService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender submissionService.list(submissionService.normalize(params))
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender submissionService.get(Submission.load(id))
	}

	@Secured('permitAll()')
	def getSubmissionFile(Long submissionId) {

		def submission = Submission.load(submissionId)
		def user = springSecurityService.currentUser as User

		try {
			if (user) {
				def roles = user.getAuthorities().collect { it.authority }
				if (roles.contains('ROLE_ADMIN') ||
						roles.contains('ROLE_ADMIN_INST') ||
						roles.contains('ROLE_TEACHER') ||
						roles.contains('ROLE_TEACHER_ASSISTANT') ||
						(submission?.user?.id == user.id)
				) {
					File file = submissionService.getSubmissionFile(submission)
					if (file.exists()) {
						response.setContentType("application/octet-stream")
						response.setHeader("Content-disposition", "filename=${file.name}")
						response.outputStream << file.bytes
					} else {
						render status: HttpStatus.NOT_FOUND
					}
				} else {
					render status: HttpStatus.FORBIDDEN
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured('permitAll()')
	def getDiffFile(Long submissionId) {

		def submission = Submission.get(submissionId)
		def user = springSecurityService.currentUser as User

		try {
			if (user) {
				def roles = user.getAuthorities().collect { it.authority }
				if (roles.contains('ROLE_ADMIN') ||
						roles.contains('ROLE_ADMIN_INST') ||
						roles.contains('ROLE_TEACHER') ||
						roles.contains('ROLE_TEACHER_ASSISTANT') ||
						submission.testCase?.example
				) {
					if (submission.diffFile && !submission.diffFile.endsWith(".diff")) {
						render contentType: "application/json", text: submission.diffFile
					} else {
						File file = submissionService.getDiffFile(submission)
						if (file.exists()) {
							response.setContentType("application/octet-stream")
							response.setHeader("Content-disposition", "filename=${file.name}")
							response.outputStream << file.bytes
						} else {
							render status: HttpStatus.NOT_FOUND
						}
					}


				} else {
					render status: HttpStatus.FORBIDDEN
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} catch(ObjectNotFoundException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_TEACHER_ASSISTANT', 'ROLE_ADMIN', 'ROLE_ADMIN_INST'])
	def reevaluate(Long submissionId) {
		params.submission = submissionId
		customRender submissionService.reevaluate(submissionService.normalize(params))
	}

	@Secured(['ROLE_ADMIN'])
	def reevaluateByProblem(Long problemId) {

		if (request.JSON) {
			extractJSON()
		}

		params.problem = problemId
		customRender submissionService.reevaluate(submissionService.normalize(params))
	}

	@Secured(['ROLE_ADMIN'])
	def reevaluateAll() {

		if (request.JSON) {
			extractJSON()
		}

		customRender submissionService.reevaluate(submissionService.normalize(params))
	}

	@Secured(['ROLE_ADMIN'])
	def update(Long id) {
		def submission = Submission.get(id)

		def json = request.JSON

		if (submission) {

			submission.evaluation = json["evaluation"]  ? Submission.Evaluation.valueOf(json["evaluation"] as String) : Submission.Evaluation.HUXLEY_ERROR
			submission.time = json["time"] as Double ?: -1D
			submission.errorMsg = json["errorMsg"] as String ?: ""

			customRender submissionService.update(submission)
		} else {
			render status: HttpStatus.NOT_FOUND
		}

	}

	def extractJSON() {
		params.submission = request.JSON["submission"]
		params.problem = request.JSON["problem"]
		params.language = request.JSON["language"]
		params.user = request.JSON["user"]

		params.tries = request.JSON["tries"]
		params.triesGt = request.JSON["triesGt"]
		params.triesGe = request.JSON["triesGe"]
		params.triesLt = request.JSON["triesLt"]
		params.triesLe = request.JSON["triesLe"]
		params.triesNe = request.JSON["triesNe"]

		params.time = request.JSON["time"]
		params.timeGt = request.JSON["timeGt"]
		params.timeGe = request.JSON["timeGe"]
		params.timeLt = request.JSON["timeLt"]
		params.timeLe= request.JSON["timeLe"]
		params.timeNe = request.JSON["timeNe"]

		params.submissionDate = request.JSON["submissionDate"]
		params.submissionDateGt = request.JSON["submissionDateGt"]
		params.submissionDateGe = request.JSON["submissionDateGe"]
		params.submissionDateLt = request.JSON["submissionDateLt"]
		params.submissionDateLe = request.JSON["submissionDateLe"]
		params.submissionDateNe = request.JSON["submissionDateNe"]


		params.evaluations = request.JSON["evaluations"]

		params.excludeEvaluations = request.JSON["excludeEvaluations"]
	}


}
