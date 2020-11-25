package com.thehuxley

class Institution {

	enum Status  {
		PENDING, APPROVED, REJECTED
	}

    String name
	String acronym
    String phone
    String logo = "default.png"
	Status status = Status.PENDING

	static hasMany = [groups: Group]

	static searchable = {
		except = ["groups"]
	}

	static mapping = {
		status enumType: "ordinal"
	}

    static constraints = {
		name blank: false, unique: true
		acronym blank: false, size: 1..20
        phone nullable: true
    }

}
