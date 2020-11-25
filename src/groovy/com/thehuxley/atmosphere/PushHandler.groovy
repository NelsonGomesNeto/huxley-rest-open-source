package com.thehuxley.atmosphere

import com.thehuxley.PushService
import com.thehuxley.User
import grails.converters.JSON
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.atmosphere.cpr.AtmosphereHandler
import org.atmosphere.cpr.AtmosphereRequest
import org.atmosphere.cpr.AtmosphereResource
import org.atmosphere.cpr.AtmosphereResourceEvent
import org.atmosphere.cpr.AtmosphereResponse

import java.text.MessageFormat

import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.*

class PushHandler implements AtmosphereHandler {

	Log log = LogFactory.getLog(getClass())

	@Override
	void onRequest(AtmosphereResource resource) throws IOException {
		AtmosphereRequest request = resource.request

		if (request.method.equalsIgnoreCase("GET")) {

			User user = request.getAttribute("user") as User

			resource.suspend()

			if (user) {
				resource.removeFromAllBroadcasters()
				resource.getAtmosphereConfig().broadcasterFactory
						.lookup(MessageFormat.format(PushService.mapping.USER_CHANNEL, user.id), true)
						.addAtmosphereResource(resource)
			}

			resource.getAtmosphereConfig().broadcasterFactory
					.lookup(PushService.mapping.PUBLIC_CHANNEL, true)
					.addAtmosphereResource(resource)
		}

	}

	@Override
	void onStateChange(AtmosphereResourceEvent event) throws IOException {
		AtmosphereResource resource = event.resource
		AtmosphereResponse response = resource.response

		if (log.debugEnabled) {
			log.debug("##########--------------------------------------------------------------------------------------------------------------")
			log.debug("atmosphere - resource is suspended: ${resource.isSuspended()}, sending message ${(event.getMessage() as JSON) as String}")
			log.debug event.resource.request.servletPath
			log.debug event.resource.request.requestURI
			log.debug event.resource.request.contextPath
			log.debug("------------------------------------------------------------------------------------------------------------------------")
		}

		if (resource.isSuspended()) {
			def body = event.getMessage()

			response.contentType = "application/json"
			response.getWriter()?.write((body as JSON) as String)

			if (resource.transport() == JSONP || resource.transport() == LONG_POLLING) {
				event.resource.resume()
			} else {
				response.getWriter()?.flush()
			}

		} else if (!event.isResuming()) {
			event.broadcaster().broadcast([resuming: event.isResuming()])
		}

	}

	@Override
	void destroy() { }
}
