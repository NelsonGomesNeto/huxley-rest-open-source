package com.thehuxley

import com.thehuxley.error.ErrorReason
import com.thehuxley.error.ErrorResponse
import org.springframework.http.HttpStatus

import java.text.MessageFormat

class ErrorController {

	static responseFormats = ['json']
	static allowedMethods = []

	def index() {}

	def wrongOrderParam () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.WRONG_ORDER_PARAM])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def wrongSortParam () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.WRONG_SORT_PARAM])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def entityNotFound () {
		def errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, [ErrorReason.ENTITY_NOT_FOUND])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidUser () {

		def user = params.entity as User
		def errors = []

		user.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "email") {
					if (it.code == "email.invalid") {
						errors.add(ErrorReason.USER_EMAIL_IS_NOT_VALID.setParams(it.arguments[2]))
					}

					if (it.code == "unique") {
						errors.add(ErrorReason.USER_EMAIL_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.USER_EMAIL_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "username") {
					if (it.code == "unique") {
						errors.add(ErrorReason.USER_USERNAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "matches.invalid") {
						errors.add(ErrorReason.USER_USERNAME_NOT_MATCH.setParams(it.arguments[2]))
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.USER_USERNAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.USER_USERNAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.USER_USERNAME_CANNOT_BE_BLANK)
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.USER_USERNAME_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "password") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.USER_PASSWORD_CANNOT_BE_NULL)
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def passwordWrong() {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.USER_PASSWORD_WRONG])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def passwordInvalid() {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.USER_PASSWORD_INVALID])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def passwordNotMatch() {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.USER_PASSWORD_NOT_MATCH])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidGroup() {
		def group = params.entity as Group
		def errors = []

		group.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "name") {

					if (it.code == "unique") {
						errors.add(ErrorReason.GROUP_NAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.GROUP_NAME_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.GROUP_NAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.GROUP_NAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.GROUP_NAME_CANNOT_BE_NULL)
					}

				} else if (it.arguments[0] == "url") {

					if (it.code == "unique") {
						errors.add(ErrorReason.GROUP_URL_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.GROUP_URL_CANNOT_BE_BLANK)
					}

					if (it.code == "matches.invalid") {
						errors.add(ErrorReason.GROUP_URL_NOT_MATCH.setParams(it.arguments[2]))
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.GROUP_URL_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.GROUP_URL_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.GROUP_URL_CANNOT_BE_NULL)
					}

				} else if (it.arguments[0] == "institution") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.GROUP_INSTITUTION_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "endDate") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.GROUP_END_DATE_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "startDate") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.GROUP_START_DATE_CANNOT_BE_NULL)
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		if (group.endDate < group.startDate) {
			errors.add(ErrorReason.GROUP_END_DATE_CANNOT_BE_EARLIER_THAN_START_DATA)
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}


	def invalidProblem() {

		def problem = params.entity as Problem
		def errors = []

		problem.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "name") {

					if (it.code == "unique") {
						errors.add(ErrorReason.PROBLEM_NAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.PROBLEM_NAME_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.PROBLEM_NAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.PROBLEM_NAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.PROBLEM_NAME_CANNOT_BE_NULL)
					}

				} else if (it.arguments[0] == "level") {
					if (it.code == "range") {
						errors.add(ErrorReason.PROBLEM_LEVEL_OUT_OF_RANGE.setParams(it.arguments[2]))
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidInstitution() {

		def institution = params.entity as Institution
		def errors = []

		institution.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "name") {

					if (it.code == "unique") {
						errors.add(ErrorReason.INSTITUTION_NAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.INSTITUTION_NAME_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.INSTITUTION_NAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.INSTITUTION_NAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.INSTITUTION_NAME_CANNOT_BE_NULL)
					}

				} else if (it.arguments[0] == "acronym") {
					if (it.code == "unique") {
						errors.add(ErrorReason.INSTITUTION_ACRONYM_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

					if (it.code == "blank") {
						errors.add(ErrorReason.INSTITUTION_ACRONYM_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.INSTITUTION_ACRONYM_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.INSTITUTION_ACRONYM_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.INSTITUTION_ACRONYM_CANNOT_BE_NULL)
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidTopic() {

		def topic = params.entity as Topic
		def errors = []

		topic.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "name") {

					if (it.code == "blank") {
						errors.add(ErrorReason.TOPIC_NAME_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.TOPIC_NAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.TOPIC_NAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.TOPIC_NAME_CANNOT_BE_NULL)
					}

					if (it.code == "unique") {
						errors.add(ErrorReason.TOPIC_NAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidLanguage() {

		def language = params.entity as Language
		def errors = []

		language.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "name") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_NAME_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_NAME_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_NAME_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_NAME_CANNOT_BE_NULL)
					}

					if (it.code == "unique") {
						errors.add(ErrorReason.LANGUAGE_NAME_MUST_BE_UNIQUE.setParams(it.arguments[2]))
					}

				} else if (it.arguments[0] == "plagConfig") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_PLAG_CONFIG_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_PLAG_CONFIG_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_PLAG_CONFIG_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_PLAG_CONFIG_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "compileParams") {

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_COMPILE_PARAMS_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_COMPILE_PARAMS_TOO_SMALL.setParams(it.arguments[2]))
					}
				} else if (it.arguments[0] == "compiler") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_COMPILER_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_COMPILER_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_COMPILER_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_COMPILER_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "execParams") {

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_EXEC_PARAMS_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_EXEC_PARAMS_TOO_SMALL.setParams(it.arguments[2]))
					}
				} else if (it.arguments[0] == "script") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_SCRIPT_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_SCRIPT_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_SCRIPT_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_SCRIPT_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "extension") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_EXTENSION_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_EXTENSION_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_EXTENSION_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_EXTENSION_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "label") {

					if (it.code == "blank") {
						errors.add(ErrorReason.LANGUAGE_LABEL_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.LANGUAGE_LABEL_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.LANGUAGE_LABEL_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.LANGUAGE_LABEL_CANNOT_BE_NULL)
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}


	def invalidQuestionnaire() {
		def questionnaire = params.entity as Questionnaire
		def errors = []

		questionnaire.errors.each {
			it.getAllErrors().each {
				if (it.arguments[0] == "title") {

					if (it.code == "blank") {
						errors.add(ErrorReason.QUESTIONNAIRE_TITLE_CANNOT_BE_BLANK)
					}

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.QUESTIONNAIRE_TITLE_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.QUESTIONNAIRE_TITLE_TOO_SMALL.setParams(it.arguments[2]))
					}

					if (it.code == "nullable") {
						errors.add(ErrorReason.QUESTIONNAIRE_TITLE_CANNOT_BE_NULL)
					}

				} else if (it.arguments[0] == "description") {

					if (it.code == "size.toobig") {
						errors.add(ErrorReason.QUESTIONNAIRE_DESCRIPTION_TOO_BIG.setParams(it.arguments[2]))
					}

					if (it.code == "size.toosmall") {
						errors.add(ErrorReason.QUESTIONNAIRE_DESCRIPTION_TOO_SMALL.setParams(it.arguments[2]))
					}

				} else if (it.arguments[0] == "group") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.QUESTIONNAIRE_GROUP_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "endDate") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.QUESTIONNAIRE_END_DATE_CANNOT_BE_NULL)
					}
				} else if (it.arguments[0] == "startDate") {
					if (it.code == "nullable") {
						errors.add(ErrorReason.QUESTIONNAIRE_START_DATE_CANNOT_BE_NULL)
					}
				} else {
					errors.add(ErrorReason.GENERIC.setParams(it.code + " - " + MessageFormat.format(it.defaultMessage, it.arguments)))
				}
			}
		}

		if (questionnaire.endDate < questionnaire.startDate) {
			errors.add(ErrorReason.QUESTIONNAIRE_END_DATE_CANNOT_BE_EARLIER_THAN_START_DATA)
		}

		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors)

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidAvatarSize () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.AVATAR_INVALID_SIZE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidAvatarMimeType () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.AVATAR_INVALID_MIME_TYPE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidLogoSize () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.LOGO_INVALID_SIZE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidLogoMimeType () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.LOGO_INVALID_MIME_TYPE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidProblemImageSize () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.PROBLEM_IMAGE_INVALID_SIZE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}

	def invalidProblemImageMimeType () {
		def errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, [ErrorReason.PROBLEM_IMAGE_INVALID_MIME_TYPE])

		respond errorResponse, [status: errorResponse.httpStatus]
	}
}
