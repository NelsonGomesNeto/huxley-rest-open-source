package com.thehuxley

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONException
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartFile

class InstitutionController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def institutionService
	def groupService
	def userService
	def springSecurityService
	def grailsLinkGenerator

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

		if (params.sort && !institutionService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		def currentUser = springSecurityService.currentUser as User ?: null
		Institution.Status status = Institution.Status.APPROVED

		if (currentUser) {
			def authorities = currentUser.getAuthorities().authority
			if (authorities.contains("ROLE_ADMIN")) {
				try {
					status = params.status ? Institution.Status.valueOf(params.status as String) : null
				} catch (Exception e) {
					e.finalize()
				}
			}
		}

		customRender institutionService.list(institutionService.normalize(params), status)
	}

	@Secured('permitAll()')
	def show(Long id) {

		def currentUser = springSecurityService.currentUser as User ?: null
		Institution.Status status = Institution.Status.APPROVED
		Institution institution = Institution.load(id)

		if (currentUser) {
			if (UserInstitution.findByUserAndInstitutionAndRole(currentUser, institution, UserInstitution.Role.ADMIN_INST)
					|| currentUser.authorities.authority.contains("ROLE_ADMIN")) {
					status = null
			}
		}

		customRender institutionService.get(institution, currentUser, status)
	}

	@Secured('permitAll()')
	def getGroups(Long institutionId, Long groupId) {

		def institution = Institution.load(institutionId)

		if (groupId) {
			customRender groupService.findByInstitution(Group.load(groupId), institution)
		} else {

			if (params.sort && !groupService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender  groupService.findAllByInstitution(institution, params)
		}
	}

	@Secured('permitAll()')
	def getUsers(Long institutionId, Long userId) {

		def institution = Institution.load(institutionId)

		if (userId) {
			customRender userService.findByInstitution(User.load(userId), institution)
		} else {

			if (params.sort && !userService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender userService.findAllByInstitution(institution, userService.normalize(params))
		}
	}

	@Secured(['ROLE_ADMIN'])
	def changeStatus(Long institutionId) {
		def status = null

		try {
			status = params.status ? Institution.Status.valueOf(params.status as String) : null
		} catch (Exception e) {
			e.finalize()
		}

		try {
			if (!status) {
				status = request.JSON["status"] ? Institution.Status.valueOf(request.JSON["status"] as String) : null
			}
		} catch (Exception e) {
			e.finalize()
			render status: HttpStatus.BAD_REQUEST
			return
		}

		if (status) {
			Institution institution = Institution.load(institutionId)
			onValid institution, {
				render institutionService.changeStatus(institution, status)
			}

		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('permitAll()')
	def save() {
		User currentUser = springSecurityService.currentUser as User
		def institution = deserialize(false, null) as Institution

		onValid institution, {
			customRender institutionService.save(institution, currentUser)
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_INST'])
	def update(Long id) {
		User currentUser = springSecurityService.currentUser as User
		Institution institution = Institution.get(id)

		if (institution) {
			if (currentUser &&
					(currentUser.authorities.authority.contains("ROLE_ADMIN") ||
							UserInstitution.findByUserAndInstitutionAndRole(currentUser, institution, UserInstitution.Role.ADMIN_INST))) {
				institution = deserialize(true, institution.id) as Institution
				onValid institution, {
					customRender institutionService.save(institution, currentUser)
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Secured('permitAll()')
	def validate() {
		onValid deserialize(false, null) as Institution, {
			render status: HttpStatus.ACCEPTED
		}
	}

	@Secured('permitAll()')
	def getLogo(Long institutionId) {
		Institution institution = Institution.get(institutionId)

		if (institution) {
			getLogoByKey(institution.logo)
		} else {
			render status: HttpStatus.NOT_FOUND
		}
	}

	@Transactional(readOnly = false)
	@Secured('isAuthenticated()')
	def uploadLogo(Long institutionId) {

		User currentUser = springSecurityService.currentUser as User
		Institution institution = Institution.get(institutionId)

		if (params.file) {

			def kb = 1024
			def MIN_SIZE = 1 * kb
			def MAX_SIZE = 5 * (kb * kb)
			def ALLOWED_MIME_TYPE = ["image/jpg", "image/jpeg", "image/png"]

			def fileSize = (params.file as CommonsMultipartFile).size

			if (ALLOWED_MIME_TYPE.contains((params.file as CommonsMultipartFile).contentType)) {
				if ((fileSize >= MIN_SIZE) && (fileSize <= MAX_SIZE)) {
					if (institution) {
						if (currentUser.authorities.authority.contains("ROLE_ADMIN") ||
								UserInstitution.findByInstitutionAndUser(institution, currentUser)?.role
									== UserInstitution.Role.ADMIN_INST) {
							def file = institutionService.uploadImage(params.file as CommonsMultipartFile)
							customRender(
									([
											_links: [
													self: grailsLinkGenerator.link(
															controller: "institutions",
															action: "logo",
															absolute: true
													) + "/" + file.name
											],
											name  : file.name
									] as JSON) as String
							)
						} else {
							render status: HttpStatus.FORBIDDEN
						}
					} else {
						render status: HttpStatus.BAD_REQUEST
					}
				} else {
					forward(controller: "Error", action: "invalidLogoSize")
				}
			} else {
				forward(controller: "Error", action: "invalidLogoMimeType")
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('isAuthenticated()')
	def cropImage(Long institutionId) {

		def institution = Institution.get(institutionId)
		def currentUser = springSecurityService.currentUser as User

		if (institution) {
			if (currentUser.authorities.authority.contains("ROLE_ADMIN") ||
					UserInstitution.findByInstitutionAndUser(institution, currentUser)?.role
						== UserInstitution.Role.ADMIN_INST) {
				def json = request.JSON

				if (json["filename"]) {
					customRender institutionService.cropImage(
							institution,
							json["filename"] as String,
							json["x"] as Integer ?: 0,
							json["y"] as Integer ?: 0,
							json["width"] as Integer ?: 400,
							json["height"] as Integer ?: 300
					)
				} else {
					render status: HttpStatus.BAD_REQUEST
				}
			} else {
				render status: HttpStatus.FORBIDDEN
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}


	@Secured('permitAll()')
	def getLogoByKey(String key) {

		def width = params["width"] as Integer ?: 0
		def height = params["height"] as Integer ?: 0

		File file = institutionService.getImage(key, width, height)

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

	@Secured(['ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def addUser(Long institutionId, Long userId) {
		Institution institution = Institution.load(institutionId)
		User user = User.load(userId)

		if (!canUpdate(institution)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		try {
			UserInstitution.Role role = params.role ? UserInstitution.Role.valueOf(params.role as String) : UserInstitution.Role.STUDENT
			if (institutionService.addToInstitution(user, institution, role)) {
				if (!params.skipResponse){
					customRender userService.findAllByInstitution(institution, userService.normalize(params))
				}
				render HttpStatus.OK
			} else {
				render status: HttpStatus.BAD_REQUEST
			}
		} catch(Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def addUsers(Long institutionId) {
		def users = request.JSON["users"] ? request.JSON["users"] : []
		Institution institution = Institution.load(institutionId)
		if (!canUpdate(institution)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		users.each {
			UserInstitution.Role role = it["role"] ? UserInstitution.Role.valueOf(it["role"] as String) : UserInstitution.Role.STUDENT
			if (it["id"]) {
				def user = User.get(it["id"] as Long)
				if (user) {
					institutionService.addToInstitution(user, institution, role)
				}
			} else if (it["email"]) {
				def user = User.findByEmail(it["email"] as String)
				if (user) {
					institutionService.addToInstitution(user, institution, role)
				}
			} else if (it["username"]) {
				def user = User.findByUsername(it["username"] as String)
				if (user) {
					institutionService.addToInstitution(user, institution, role)
				}
			}

		}

		customRender userService.findAllByInstitution(institution, userService.normalize(params))

	}


	@Secured(['ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def removeUser(Long institutionId, Long userId) {
		Institution institution = Institution.load(institutionId)
		User user = User.load(userId)

		if (!canUpdate(institution)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		try {
			institutionService.removeFromInstitution(user, institution)
			render status: HttpStatus.NO_CONTENT
		} catch(Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN'])
	def normalizeRoles(Long institutionId) {
		try {
			def institution = Institution.read(institutionId)

			if (institution) {

				def instAdmins = UserInstitution.findAllByInstitutionAndRole(institution, UserInstitution.Role.ADMIN_INST).user

				UserInstitution.withNewTransaction {
					UserInstitution.findAllByInstitution(institution)*.delete()
				}

				instAdmins.each {
					institutionService.addToInstitution(it, institution, UserInstitution.Role.ADMIN_INST)
				}

				Group.findAllByInstitution(institution).each { Group group ->
					UserGroup.findAllByGroup(group).each { UserGroup userGroup ->

						def roleInInstitution = UserInstitution.Role.STUDENT

						if (userGroup.role == UserGroup.Role.TEACHER) {
							roleInInstitution = UserInstitution.Role.TEACHER
						} else if (userGroup.role == UserGroup.Role.TEACHER_ASSISTANT) {
							roleInInstitution = UserInstitution.Role.TEACHER_ASSISTANT
						}


						institutionService.normalizeInInstitution(userGroup.user, institution, roleInInstitution)
					}
				}

				render status: HttpStatus.NO_CONTENT
				return
			}

			render status: HttpStatus.NOT_FOUND
		} catch (Exception e) {
			log.error(e.message, e)

			render status: HttpStatus.INTERNAL_SERVER_ERROR
		}
	}

	def canUpdate(Institution institution) {

		User user = springSecurityService.currentUser as User

		if(UserInstitution.findByUserAndInstitutionAndRole(user, institution, UserInstitution.Role.ADMIN_INST) ||
				user.getAuthorities().authority.contains("ROLE_ADMIN")) {
			return true
		}

		return false
	}


	def onValid(Institution institution, c) {
		try {
			if (institution.hasErrors()) {
				params["entity"] = institution
				forward(controller: "Error", action: "invalidInstitution")
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


	def deserialize(update, id) {
		def institution = update ? Institution.get(id as Long) : new Institution()

		def json = request.JSON

		institution.name = json["name"] as String ?: institution.name
		institution.acronym = json["acronym"] as String ?: institution.acronym
		institution.logo = json["logo"] as String ?: institution.logo

		institution.validate()

		return institution
	}

}
