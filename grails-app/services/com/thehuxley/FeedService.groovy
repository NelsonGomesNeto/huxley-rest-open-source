package com.thehuxley

class FeedService {

	def findAllByGroup(Group group, Map params) {

		Feed.createCriteria().list(params) {
			order(params.order ?: "dateCreated", params.sort ?: "desc")
		}
	}

	def findAllByUser(User user, Map params) {
		Feed.createCriteria().list(params) {
			and {
				eq("recipient", user)
			}
			order(params.sort?: 'lastUpdated', params.order?: 'desc')
		}
	}

	def save(Feed feed) {
		try {
			feed.save(flush: true)
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

	def delete(Feed feed) {

		try {
			feed.save(flush: true)
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

}
