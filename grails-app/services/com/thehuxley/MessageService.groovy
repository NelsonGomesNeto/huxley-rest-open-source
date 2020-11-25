package com.thehuxley

import grails.gorm.*
import org.apache.tools.ant.types.resources.Restrict
import org.hibernate.criterion.Restrictions

class MessageService {

	def findAllByGroup(Group group, Map params) {
		Message.findAllByGroupAndDeleted(group, false, params)
	}

	def findAllByRecipient(User user, Map params) {
		Message.createCriteria().list(params) {
			and {

				or {
					eq("sender", user)
					eq("recipient", user)
				}

				if (params.q) {
					or {
						and {
							eq("sender", user)
							recipient {
								like("name", "%$params.q%")
							}
						}

						and {
							eq("recipient", user)
							sender {
								like("name", "%$params.q%")
							}
						}
					}
				}

				eq("deleted", false)
				!params.firstMessage ?: eq("firstMessage", true)
				order(params.sort?: 'lastUpdated', params.order?: 'desc')
			}
		}
	}

	def countUnread(User user) {
		Message.countByRecipientAndUnreadAndDeleted(user, true, false)
	}

	def save(Message message) {
		try {
			message.type = Message.Type.USER_CHAT
			message.save(flush: true)
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

	def delete(Message message) {
		message.deleted = true

		try {
			message.save(flush: true)
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

	def markAsRead(Message message) {
		message.unread = false
		message.readDate = new Date()

		try {
			message.save(flush: true)
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

}
