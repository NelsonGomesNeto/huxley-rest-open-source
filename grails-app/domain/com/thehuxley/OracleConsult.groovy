package com.thehuxley

import org.springframework.security.crypto.codec.Hex

import java.security.MessageDigest


//TODO tabela temporária, até arrumar o Atmosphere
class OracleConsult {

	enum Type  {
		CONSENSUS,
		MAJORITY,
		INCONCLUSIVE,
		NO_ANSWER
	}

	enum Status  {
		PENDING, DONE
	}

	String hash
	String input
	String output
	Integer favour
	Integer against

	Date dateCreated
	Date lastUpdated

	Type type
	Status status = Status.PENDING
	User user
	Problem problem

	static mapping = {
		version false
		type enumType: "ordinal"
		status enumType: "ordinal"
		input type: "text"
		output type: "text"
	}

	static constraints = {
		user nullable: true
		problem nullable: true
		hash nullable: true, unique: true
		input nullable: true
		output nullable: true
		favour  nullable: true
		against nullable: true
		type nullable: true
	}

	def beforeInsert() {
		hash = new String(Hex.encode(MessageDigest.getInstance("SHA1").digest((new Random().nextInt() + new Date().toString()).bytes)))
	}
}
