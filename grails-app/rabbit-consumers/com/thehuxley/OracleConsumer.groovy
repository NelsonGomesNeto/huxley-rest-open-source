package com.thehuxley

import com.budjb.rabbitmq.consumer.MessageContext
import com.thehuxley.atmosphere.Feed
import grails.converters.JSON

class OracleConsumer {

	static rabbitConfig = [
        queue: "oracle_result_queue",
        retry: true
    ]

	def pushService
	def userService

	def handleMessage(Map dataMap, MessageContext context) {
		log.debug "Receiving result from oracle_result_queue. " + dataMap

		OracleConsult.withNewTransaction {

			def oracleConsult = OracleConsult.findByHash(dataMap.id as String)

			def json = JSON.parse(dataMap.result as String)

			if (oracleConsult) {
				oracleConsult.output = json["output"] as String
				oracleConsult.type = OracleConsult.Type.valueOf(json["type"] as String)
				oracleConsult.status = OracleConsult.Status.DONE
				oracleConsult.favour = json["favour"] as Integer
				oracleConsult.against = json["against"] as Integer

				oracleConsult.save(flush: true)
			}
		}

	}

}