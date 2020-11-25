package com.thehuxley

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.http.HttpStatus

class TopicController {

	static responseFormats = ['json']
	static allowedMethods = [show: "GET", index: "GET"]

	def topicService

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

		if (params.sort && !topicService.isSortable(params.sort)) {
			forward (controller: "Error", action: "wrongSortParam")
			return
		}

		customRender topicService.list(topicService.normalize(params))
	}

	@Secured('permitAll()')
	def show(Long id) {
		customRender topicService.get(Topic.load(id))
	}

	@Secured(['ROLE_ADMIN'])
	def save() {
		try {
			Topic topic = deserialize(request.JSON, null)

			if (topic.hasErrors()) {
				params["entity"] = topic
				forward(controller: "Error", action: "invalidTopic")
			} else {
				customRender topicService.save(topic)
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

			Topic topic = deserialize(request.JSON, id)

			if (topic.hasErrors()) {
				params["entity"] = topic
				forward(controller: "Error", action: "invalidTopic")
			} else {
				customRender topicService.save(topic)
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

		Topic topic = Topic.get(id)

		if (topic && topicService.delete(topic)) {
			render status: HttpStatus.NO_CONTENT
		} else {
			render status: HttpStatus.NOT_ACCEPTABLE
		}
	}

	Topic deserialize(json, id) {

		Topic topic

		if (json["id"]) {
			try {
				topic = Topic.get(json["id"] as Long)
			} catch (Exception e) {
				e.finalize()
				return null
			}
		} else {
			topic = id ? Topic.get(id as Long) : new Topic()
		}

		topic.name = (json["name"] as String) ?: topic.name

		topic.validate()

		return topic
	}

}
