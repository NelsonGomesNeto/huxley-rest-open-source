package com.thehuxley

import grails.transaction.Transactional

import com.budjb.rabbitmq.consumer.MessageContext

@Transactional
class StatisticsConsumer {

    static rabbitConfig = [
		queue: "statistics_queue",
		retry: true
    ]

    def submissionService

    def handleMessage(Map dataMap, MessageContext context) {
		Boolean isReevaluation = dataMap["isReevaluation"] as Boolean
		def submission = Submission.get(dataMap["submissionId"] as Long)

		submissionService.triggersAfterUpdate(submission, isReevaluation)
    }

}