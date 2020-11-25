package com.thehuxley

import grails.transaction.Transactional

@Transactional
class EmailService {


	def sendInviteToGroup(String email, Group group) {
		println "convidando $email para o groupo $group.name"
	}

}
