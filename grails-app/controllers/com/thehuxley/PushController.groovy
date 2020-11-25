package com.thehuxley

import com.thehuxley.atmosphere.Feed
import grails.plugin.springsecurity.annotation.Secured

class PushController {

	def springSecurityService
	def pushService
	def submissionService

	@Secured("permitAll()")
	def pull() {
		User user = springSecurityService.currentUser as User

		if (user) {
			request.setAttribute("user", user)
		}

		pushService.subscribe(request, response)
	}

	@Secured("permitAll()")
	def publish() {
		if (params.user) {
			def user = User.get(params.user as Long)
			pushService.publish(new Feed(type: Feed.Type.USER_SUBMISSION_STATUS, body: [scope: "user", submission: submissionService.findByUser(Submission.findAllByUser(user).last() as Submission, user)]), user)
		} else if (params.group) {
			def group = Group.get(params.group as Long)
			pushService.publish(new Feed(type: Feed.Type.GROUP_MEMBER_SOLVED_PROBLEM, body: [scope: "group", group: group, submission: submissionService.findByUser(Submission.findAllByUser(group.users.last()).last() as Submission, group.users.last())]), group)
		} else {
			pushService.publish(new Feed(type: Feed.Type.valueOf(params.type as String), body: [message: params.message, scope: "public"]))
		}
	}

}
