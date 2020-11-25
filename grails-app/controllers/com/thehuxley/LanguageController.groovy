package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.http.HttpStatus

class LanguageController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def languageService

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

		if (params.sort && !languageService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender languageService.list(languageService.normalize(params))
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender languageService.get(Language.load(id))
	}

	@Secured(['ROLE_ADMIN'])
	def save() {
		try {
			Language language = deserialize(request.JSON, null)

			if (language.hasErrors()) {
				params["entity"] = language
				forward(controller: "Error", action: "invalidLanguage")
			} else {
				customRender languageService.save(language)
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN'])
	def update(Long id) {
		try {

			Language language = deserialize(request.JSON, id)

			if (language.hasErrors()) {
				params["entity"] = language
				forward(controller: "Error", action: "invalidLanguage")
			} else {
				customRender languageService.save(language)
			}
		} catch (NullPointerException e) {
			e.finalize()
			render status: HttpStatus.NOT_FOUND
		} catch (Exception e) {
			e.printStackTrace()
			render status: HttpStatus.BAD_REQUEST
		}
	}

	@Secured(['ROLE_ADMIN'])
	def delete(Long id) {

		Language language = Language.get(id)

		if (language && languageService.delete(language)) {
			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.NOT_ACCEPTABLE
		}
	}

	Language deserialize(json, id) {

		Language language

		if (json["id"]) {
			try {
				language = Language.get(json["id"] as Long)
			} catch (Exception e) {
				e.finalize()
				return null
			}
		} else {
			language = id ? Language.get(id as Long) : new Language()
		}

		language.name = json["name"] != null ? (json["name"] as String) : language.name
		language.label = json["label"] != null ? (json["label"] as String) : language.label
		language.plagConfig = json["plagConfig"] != null ? (json["plagConfig"] as String) : language.plagConfig
		language.execParams = json["execParams"] != null ? (json["execParams"] as String) : language.execParams
		language.compileParams = json["compileParams"] != null ? (json["compileParams"] as String) : language.compileParams
		language.compiler = json["compiler"] != null ? (json["compiler"] as String) : language.compiler
		language.script = json["script"] != null ? (json["script"] as String) : language.script
		language.extension = json["extension"] != null ? (json["extension"] as String) : language.extension

		language.validate()

		return language
	}

}
