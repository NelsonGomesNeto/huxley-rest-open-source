package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.http.HttpStatus

class PendencyController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET", save:"POST", update: "PUT"]

	def pendencyService
	def mailService
	def institutionService
	def groupService

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

		if (params.sort && !pendencyService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender pendencyService.list(pendencyService.normalize(params))
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender pendencyService.get(Pendency.load(id))
	}

	@Secured('permitAll()')
	def save() {
		def pendencyJSON = request.JSON
		def pendency = new Pendency()

		if (pendencyJSON["institution"]) {
			pendency.institution = Institution.get(pendencyJSON["institution"]["id"] as Long)
		}

		if (pendencyJSON["group"]) {
			pendency.group = Group.get(pendencyJSON["group"]["id"] as Long)
			pendency.institution = pendency.group.institution
		}

		pendency.user = User.get(pendencyJSON["user"]["id"] as Long)
		pendency.kind = Pendency.PendencyKind.valueOf(pendencyJSON["kind"] as String)

		def response = pendencyService.save(pendency)

		if (response) {

			if (pendency.kind == Pendency.PendencyKind.INSTITUTION_APPROVAL) {

				def link = "https://www.thehuxley.com/admin/pendency/institution-approval"

				mailService.sendMail {
					to "support@thehuxley.com"
					cc "romero.malaquias@thehuxley.com", "rodrigo.paes@thehuxley.com", "marcio.guimaraes@thehuxley.com"
					subject message(code: "email.pendency.institutionApproval.subject", args: [
							pendency.institution.acronym,
							pendency.institution.name
					])
					html message(code: "email.pendency.institutionApproval.body", args:  [
							"The Huxley Team",
							pendency.institution.acronym,
							pendency.institution.name,
							pendency.user.name,
							pendency.user.email,
							link
					])
				}
			} else if (pendency.kind == Pendency.PendencyKind.TEACHER_APPROVAL) {

				def link = "https://www.thehuxley.com/institutions/$pendency.institution.id/pendency"

				UserInstitution.findAllByInstitutionAndRole(pendency.institution, UserInstitution.Role.ADMIN_INST).user.each { User user ->
					mailService.sendMail {
						to user.email
						bcc "support@thehuxley.com"
						subject message(code: "email.pendency.teacherApproval.subject", args: [pendency.user.name])
						html message(code: "email.pendency.teacherApproval.body", args: [
								user.name,
								pendency.user.name,
								pendency.user.email,
								pendency.institution.name,
								link
						])
					}
				}
			}

			customRender response
		} else {
			render status: HttpStatus.INTERNAL_SERVER_ERROR
		}


	}

	@Secured('permitAll()')
	def update(Long id) {
		def pendency = Pendency.get(id)

		def status = request.JSON["status"] ?: params.status

		if (status) {
			pendency.status = Pendency.Status.valueOf(status as String)
		}

		if (pendency.status != pendency.getPersistentValue("status")) {
			if (pendency.status == Pendency.Status.APPROVED) {
				switch (pendency.kind as String) {
					case "USER_GROUP_INVITATION":
						groupService.addToGroup(pendency.user, pendency.group)
						break
					case "INSTITUTION_APPROVAL":
						institutionService.changeStatus(pendency.institution, Institution.Status.APPROVED)
						break
					case "TEACHER_APPROVAL":
						institutionService.addToInstitution(pendency.user, pendency.institution, UserInstitution.Role.TEACHER)
						break
				}
			} else if (pendency.status == Pendency.Status.REJECTED) {
				switch (pendency.kind as String) {
					case "USER_GROUP_INVITATION":
						groupService.removeFromGroup(pendency.user, pendency.group)
						break
					case "INSTITUTION_APPROVAL":
						institutionService.changeStatus(pendency.institution, Institution.Status.REJECTED)
						break
					case "TEACHER_APPROVAL":
						institutionService.changeRole(pendency.user, pendency.institution, UserInstitution.Role.STUDENT)
						break
				}
			}

			customRender pendencyService.save(pendency)
		} else {
			customRender pendencyService.get(pendency)
		}
	}
}
