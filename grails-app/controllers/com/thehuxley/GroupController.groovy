package com.thehuxley

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.http.HttpStatus

import java.nio.charset.StandardCharsets

class GroupController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET", save: "POST", update: "PUT"]

	def groupService
	def userService
	def questionnaireService
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
		if (params.sort && !groupService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender groupService.list(params)
	}

	@Secured('permitAll()')
	def show(String id) {

		def currentUser = springSecurityService.currentUser as User ?: null

		Group group = null

		try {
			 group = Group.get(id as Long)
		} catch (Exception e) {
			e.finalize()
		}

		if (!group) {
			try {
				group = Group.findByUrl(id)
			} catch (Exception e) {
				e.finalize()
			}
		}


		customRender groupService.get(group, currentUser)
	}

	@Secured('permitAll()')
	def getByGroup(String key) {
		def currentUser = springSecurityService.currentUser as User ?: null
		Group group = Group.findByAccessKey(key)

		customRender groupService.get(group, currentUser)
	}

	@Secured('permitAll()')
	def getUsers(Long groupId, Long userId) {

		def group = Group.load(groupId)

		if (userId) {
			customRender userService.findByGroup(User.load(userId), group)
		} else {

			if (params.sort && !userService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender userService.findAllByGroup(group, userService.normalize(params))
		}
	}

	@Secured('permitAll()')
	def getQuestionnaires(Long groupId, Long questionnaireId) {

		def group = Group.load(groupId)

		if (questionnaireId) {
			customRender questionnaireService.findByGroup(Questionnaire.load(questionnaireId), group)
		} else {

			if (params.sort && !questionnaireService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender questionnaireService.findAllByGroup(group, questionnaireService.normalize(params))
		}
	}

	@Secured('permitAll()')
	def getSubmissions(Long groupId, Long submissionId) {

		def group = Group.load(groupId)

		if (submissionId) {
			customRender submissionService.findByGroup(Submission.load(submissionId), group)
		} else {

			if (params.sort && !submissionService.isSortable(params.sort)) {
				forward (controller: "Error", action: "wrongSortParam")
				return
			}

			customRender submissionService.findAllByGroup(group, submissionService.normalize(params))
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def save() {
		try {
			Group group = deserialize(request.JSON, false, null)
			User user = springSecurityService.currentUser as User

			if (group.hasErrors()) {
				params["entity"] = group
				forward(controller: "Error", action: "invalidGroup")
			} else {
				customRender groupService.save(group, user)
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

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def update(Long id) {
		try {

			User user = springSecurityService.currentUser as User

			Group group = deserialize(request.JSON, true, id)

			UserGroup userGroup = UserGroup.findByUserAndGroup(user, group)
			UserInstitution userInstitution = UserInstitution.findByUserAndInstitution(user, group.institution)

			if (!userGroup && !userInstitution) {
				render status: HttpStatus.FORBIDDEN
				return
			}

			if (group.hasErrors()) {
				params["entity"] = group
				forward(controller: "Error", action: "invalidGroup")
			} else {
				customRender groupService.save(group)
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

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def addUsers(Long groupId) {

		def users = request.JSON["users"] ? request.JSON["users"] : []

		Group group = Group.load(groupId)

		if (!canUpdate(group)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		users.each {
			if (it["id"]) {
				def user = User.get(it["id"] as Long)
				if (user) {
					groupService.addToGroup(user, group, it["role"] as UserGroup.Role ?: UserGroup.Role.STUDENT)
				}
			} else if (it["email"]) {
				def user = User.findByEmail(it["email"] as String)
				if (user) {
					groupService.addToGroup(user, group, it["role"] as UserGroup.Role ?: UserGroup.Role.STUDENT)
				} else {
					groupService.inviteToGroup(it["email"] as String, group)
				}
			} else if (it["username"]) {
				def user = User.findByUsername(it["username"] as String)
				if (user) {
					groupService.addToGroup(user, group, it["role"] as UserGroup.Role ?: UserGroup.Role.STUDENT)
				}
			}
		}

		customRender userService.findAllByGroup(group, userService.normalize(params))
	}


	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def addUser(Long groupId, Long userId) {
		Group group = Group.load(groupId)
		User user = User.load(userId)


		if (!canUpdate(group)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		try {
			UserGroup.Role role = params.role ? UserGroup.Role.valueOf(params.role as String) : UserGroup.Role.STUDENT
			if (groupService.addToGroup(user, group, role)) {
				if(!params.skipResponse){
					customRender userService.findAllByGroup(group, userService.normalize(params))
				}
				render status: HttpStatus.OK
			} else {
				render status: HttpStatus.BAD_REQUEST
			}
		} catch(Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('isAuthenticated()')
	def getByKey() {
		def key = params.key as String

		if (!key) {
			key = request.JSON["key"]
		}

		if (key) {
			def group = Group.findByAccessKey(key as String)

			if (group) {
				customRender groupService.get(group, springSecurityService.currentUser as User)
			} else {
				render status: HttpStatus.NOT_FOUND
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured('isAuthenticated()')
	def addByKey() {
		def key = params.key as String
		def currentUser = springSecurityService.currentUser as User

		if (!key) {
			key = request.JSON["key"]
		}

		if (key) {
			def group = Group.findByAccessKey(key as String)

			if (group) {
				groupService.addToGroup(currentUser, group)
				render status: HttpStatus.NO_CONTENT
			} else {
				render status: HttpStatus.NOT_FOUND
			}
		} else {
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def getKey(Long groupId) {
		Group group = Group.load(groupId)

		if (canUpdate(group)) {

			if (!group.accessKey) {
				group = groupService.refreshAccessKey(group)
			}

			customRender(([key: group.accessKey] as JSON) as String)
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def refreshKey(Long groupId) {
		Group group = Group.load(groupId)

		if (canUpdate(group)) {

			groupService.refreshAccessKey(group)

			if (group.hasErrors()) {
				validate(groupId)
			} else {
				customRender(([key: group.accessKey] as JSON) as String)
			}
		}
	}

	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def removeUser(Long groupId, Long userId) {
		Group group = Group.load(groupId)
		User user = User.load(userId)

		if (!canUpdate(group)) {
			render status: HttpStatus.FORBIDDEN
			return
		}

		try {
			groupService.removeFromGroup(user, group)
			customRender userService.findAllByGroup(group, userService.normalize(params))
		} catch(Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}


	def canUpdate(Group group) {

		User user = springSecurityService.currentUser as User

		UserGroup userGroup = UserGroup.findByUserAndGroup(user, group)
		UserInstitution userInstitution = UserInstitution.findByUserAndInstitution(user, group.institution)

		if (user.getAuthorities().authority.contains("ROLE_ADMIN")) {
			return true
		}

		if ((!userGroup && !userInstitution)) {
			return false
		}

		return true
	}


	@Secured(['ROLE_TEACHER', 'ROLE_ADMIN_INST', 'ROLE_ADMIN'])
	def validate(Long groupId) {
		try {

			def group

			if (groupId) {
				group = deserialize(request.JSON, true, groupId)
			} else {
				group = deserialize(request.JSON, false, null)
			}

			if (group.hasErrors()) {
				params["entity"] = group
				forward(controller: "Error", action: "invalidGroup")
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

	@Secured('permitAll()')
	def getFailingStudents(Long groupId) {

		if (params.sort && !userService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender userService.failingStudents(Group.get(groupId), userService.normalize(params))
	}

	@Secured('permitAll()')
	def getData(Long groupId) {
		customRender groupService.getData(Group.load(groupId))
	}


	Group deserialize(json, update, groupId) {

		User user = springSecurityService.currentUser as User

		UserInstitution userInstitution = null
		Institution institution = null

		try {
			if (user && json["institution"] && json["institution"]["id"]) {
				userInstitution = UserInstitution.findByUserAndInstitution(user, Institution.load(json["institution"]["id"] as Long))
			}
		} catch (MissingPropertyException e) {
			e.finalize()
		}

		if (userInstitution?.role == UserInstitution.Role.TEACHER ||
				userInstitution?.role == UserInstitution.Role.ADMIN_INST) {
			institution = userInstitution.institution
		}


		def group

		if (json["id"]) {
			try {
				group = Group.get(json["id"] as Long)
			} catch (Exception e) {
				e.finalize()
				return null
			}
		} else {
			group = update ? Group.get(groupId as Long) : new Group()
		}


		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		group.name = (json["name"] as String) ?: group.name
		group.url = json["url"] as String ?: group.url
		group.description = json["description"] as String ?: group.description
		group.institution = institution ?: group.institution
		try {
			group.startDate = json["startDate"] ? formatter.parseDateTime(json["startDate"] as String).toDate() : group.startDate
			group.endDate = json["endDate"] ? formatter.parseDateTime(json["endDate"] as String).toDate() : group.endDate
		} catch (IllegalArgumentException e) {
			e.finalize()
		}

		group.validate()

		group
	}
    //TODO Deletar depois
	@Secured(['ROLE_ADMIN'])
	def normalizeUrlGroups() {

		def count = 0
		def urls = []
		def groups = []
		def dirt = false

		Group.list().each { Group group ->

			if (!group.startDate) {
				if (group.dateCreated) {
					group.startDate = group.dateCreated
				} else {
					def users = UserGroup.findAllByGroup(group).user

					if (users && !users.empty) {
						def submission = Submission.findAllByUserInList(users).first()
						if (submission) {
							group.startDate = submission.submissionDate
						} else {
							group.startDate = new Date()
						}
					} else {
						group.startDate = new Date()
					}
				}

				group.endDate = group.startDate.plus(180)
				groups << group
				dirt = true
			}


			if (!group.endDate) {
				group.endDate = group.startDate.plus(180)
			}

			if (!group.url) {

				def url = group.name
				url = url.replaceAll("ç", "c")
				url = url.replaceAll("ã", "a")
				url = url.replaceAll("ê", "e")
				url = url.replaceAll("/", "-")
				url = url.replaceAll("\\(", "")
				url = url.replaceAll("\\)", "")
				url = url.replaceAll("\\.", "-")
				url = url.replaceAll(" ", "-")
				url = url.replaceAll("_", "-")
				url = url.replaceAll("---", "-")
				url = url.replaceAll("--", "-")
				url = url.toLowerCase()

				if (url == "professor")
					url = "professor-2"

				if (url == "lp1-2011-1")
					url = "lp1-2011-1-$count"

				if (group.name.toLowerCase() == "professor")
					group.name = "Professor 2"

				group.url = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
				urls << group.url
				count++
				dirt = true
			}

			if (group.name == "PROFESSOR") {
				group.name = "PROFESSOR 1"
				group.url = "professor-1"
				dirt = true
			}


			if (dirt) {
				group.save(flush: true)
			}
		}

		respond([count: count, urls: urls, groups: groups, groupsCount: groups.size()])
	}

	@Secured(['ROLE_ADMIN'])
	def forceQuizzesUpdate(Long groupId) {
		QuestionnaireService.updateScores(Group.get(groupId), false)
	}


}
