package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.http.HttpStatus

class LicensePackController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET", save: "POST", update: "PUT", delete: "DELETE"]

	def licensePackService

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
	def index() {

		if (params.sort && !licensePackService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender licensePackService.list(licensePackService.normalize(params))
	}

	@Secured(['ROLE_ADMIN'])
	def show(Long id) {
		respond licensePackService.get(LicensePack.load(id))
	}

	@Secured(['ROLE_ADMIN'])
	def save() {

		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()
		def licensePack = new LicensePack()
		licensePack.total = request.JSON["total"] as Integer
		licensePack.institution = Institution.get(request.JSON["institution"]["id"] as Long)
		licensePack.startDate = formatter.parseDateTime(request.JSON["startDate"] as String).toDate()
		licensePack.endDate = formatter.parseDateTime(request.JSON["endDate"] as String).toDate()
		customRender licensePackService.save(licensePack)
	}

	@Secured(['ROLE_ADMIN'])
	def update(Long id) {
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()
		def licensePack = LicensePack.load(id)
		if(request.JSON["total"]) {
			licensePack.total = request.JSON["total"] as Integer
		}
		if(request.JSON["institution"]) {
			licensePack.institution = Institution.get(request.JSON["institution"]["id"] as Long)
		}
		if(request.JSON["startDate"]){
			licensePack.startDate = formatter.parseDateTime(request.JSON["startDate"] as String).toDate()
		}
		if(request.JSON["endDate"]){
			licensePack.endDate = formatter.parseDateTime(request.JSON["endDate"] as String).toDate()
		}
		customRender licensePackService.save(licensePack)
	}

	@Secured(['ROLE_ADMIN'])
	def delete(Long id) {
		if (licensePackService.delete(LicensePack.load(id))) {
			render (status: HttpStatus.NO_CONTENT)
		}
	}

}
