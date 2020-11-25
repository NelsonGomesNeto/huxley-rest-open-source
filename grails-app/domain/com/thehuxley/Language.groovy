package com.thehuxley

class Language {

	String name
	String plagConfig
	String execParams
	String compileParams
	String compiler
	String script
	String extension
	String label

	static searchable = {
		only = ["name"]
	}

	static mapping = {
		cache: true
	}

	static constraints = {
		name blank: false, unique: true
		plagConfig blank: false
		compileParams blank: true, nullable: true
		compiler black: false
		execParams blank: true, nullable: true
		label blank: false, nullable: false
	}

}
