package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.http.HttpStatus

class TopCoderController {


	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]


	def topCoderService
	def userService
	def groupService
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

		def currentUser = springSecurityService.currentUser as User

		if (params.sort && !userService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		if (params.group) {
			customRender groupService.findAllInTopCoder(Group.load(params.long('group')), userService.normalize(params))
		} else if (params.focused && currentUser) {
			customRender userService.findAllInTopCoderWithFocus(currentUser, params.max as Integer)
		} else {
			customRender userService.findAllInTopCoder(userService.normalize(params))
		}
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender userService.findInTopCoder(User.load(id))
	}

	@Secured(['ROLE_ADMIN'])
	def updateNds() {
		render topCoderService.updateNd(params.boolean("check"))
	}


	@Secured(['ROLE_ADMIN'])
	def refreshTopCoder(Long userId) {
		if (userId) {
			def user = User.load(userId)
			topCoderService.refreshTopCoder(user)
			render status: HttpStatus.OK
		} else {
			topCoderService.refreshTopCoder()
			render status: HttpStatus.OK
		}
	}
}
