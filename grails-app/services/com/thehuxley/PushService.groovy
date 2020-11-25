package com.thehuxley

import com.thehuxley.atmosphere.Feed
import grails.transaction.Transactional
import org.atmosphere.client.TrackMessageSizeInterceptor
import org.atmosphere.cpr.AtmosphereFramework
import org.atmosphere.cpr.AtmosphereRequest
import org.atmosphere.cpr.AtmosphereResponse
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor
import org.atmosphere.interceptor.HeartbeatInterceptor

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.MessageFormat

@Transactional
class PushService {

	def atmosphere
	def pushHandler
	def feedService

	def static mapping = [
			"PUSH_URL": "/grails/push/pull.dispatch",
			"USER_CHANNEL": "/channel/user/{0}",
			"PUBLIC_CHANNEL": "/channel/*"
	]

	@PostConstruct
	void init() {
		atmosphere = new AtmosphereFramework()
		atmosphere.addAtmosphereHandler(mapping.PUSH_URL, pushHandler)
		atmosphere.addAtmosphereHandler("/api/grails/push/pull.dispatch", pushHandler)
		atmosphere.addAtmosphereHandler("/api/pull", pushHandler)
		atmosphere.broadcasterCacheClassName = "org.atmosphere.cache.UUIDBroadcasterCache"
		atmosphere.interceptor(new AtmosphereResourceLifecycleInterceptor())
		atmosphere.interceptor(new BroadcastOnPostAtmosphereInterceptor())
		atmosphere.interceptor(new HeartbeatInterceptor())
		atmosphere.interceptor(new TrackMessageSizeInterceptor())

		atmosphere.init()
	}

	def subscribe(HttpServletRequest request, HttpServletResponse response) {
		log.debug("atmosphere - subscribe in channel. ${request.getAttribute("user")}")
		atmosphere.doCometSupport(AtmosphereRequest.wrap(request), AtmosphereResponse.wrap(response))
	}

	def publish(Feed feed, User user) {


		if (feed.type as String == com.thehuxley.Feed.Type.USER_SUBMISSION_STATUS as String) {
			def feedPersist = new com.thehuxley.Feed(recipient: user)
			feedPersist.type = com.thehuxley.Feed.Type.USER_SUBMISSION_STATUS
			feedPersist.body = [
							scope: "user",
							submissionId: feed.body.submission.id as String
			]
			feedService.save(feedPersist)
		}


		def channel = MessageFormat.format(mapping.USER_CHANNEL, user.id)

		log.debug("atmosphere - publishing a new feed in $channel.")
		atmosphere.getBroadcasterFactory().lookup(channel, true)
				.broadcast(feed)
	}

	def publish(Feed feed, Group group) {
		UserGroup.findAllByGroup(group).user.each {
			publish(feed, it)
		}
	}

	def publish(Feed feed) {
		log.debug("atmosphere - publishing a new feed in ${mapping.PUBLIC_CHANNEL}.")
		atmosphere.getBroadcasterFactory().lookup(mapping.PUBLIC_CHANNEL, true)
				.broadcast(feed)
	}

}
