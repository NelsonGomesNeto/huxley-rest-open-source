package com.thehuxley

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class AdminController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET", save: "POST", update: "PUT", delete: "DELETE"]

	def grailsApplication

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

	@Secured(['ROLE_ADMIN'])
	def getAccessTokens() {

		def resultList = AccessToken.createCriteria().list([max: params.max, offset: params.offset]) {
			or {
				like("username", "%$params.q%")
				like("clientId", "%$params.clientId%")
			}

			order(params.sort ?: "expiration", params.order ?: "desc")
		}

		customRender(["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String])
	}

	@Secured('permitAll()')
	def getVersion() {
		respond([version: grailsApplication.metadata['app.version']])
	}


}
