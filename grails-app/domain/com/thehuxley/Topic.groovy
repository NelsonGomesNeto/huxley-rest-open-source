package com.thehuxley

class Topic {

	String name

	static hasMany = [problems: Problem]

	static searchable = {
		except = ["problems"]
	}

	static mapping = {
		cache true
	}

	static constraints = {
		name blank: false, unique: true
	}
}