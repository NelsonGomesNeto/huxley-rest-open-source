package com.thehuxley.marshaller

import com.thehuxley.AccessToken
import com.thehuxley.Group
import com.thehuxley.Institution
import com.thehuxley.Language
import com.thehuxley.LicensePack
import com.thehuxley.Message
import com.thehuxley.OracleConsult
import com.thehuxley.Pendency
import com.thehuxley.PendencyKey
import com.thehuxley.Plagiarism
import com.thehuxley.Problem
import com.thehuxley.Questionnaire
import com.thehuxley.Role
import com.thehuxley.Submission
import com.thehuxley.TestCase
import com.thehuxley.Topic
import com.thehuxley.User
import com.thehuxley.UserGroup
import com.thehuxley.UserInstitution
import com.thehuxley.atmosphere.Feed
import com.thehuxley.error.ErrorReason
import com.thehuxley.error.ErrorResponse
import grails.converters.JSON
import grails.util.Environment
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

import javax.annotation.PostConstruct
import java.text.MessageFormat

class CustomMarshallerRegistrar {

	def grailsLinkGenerator
	def final static avatarUrl = Environment.current != Environment.PRODUCTION ?
			"http://dev.thehuxley.com/api/v1/users/avatar" :
			"https://www.thehuxley.com/api/v1/users/avatar"

	def final static institutionLogoUrl = Environment.current != Environment.PRODUCTION ?
			"http://dev.thehuxley.com/api/v1/institutions/logo" :
			"https://www.thehuxley.com/api/v1/institutions/logo"

	@PostConstruct
	def static registerMarshallers() {

		JSON.registerObjectMarshaller(Date) {
			DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()
			DateTime dt = new DateTime((it as Date).getTime())
			formatter.print( dt )
		}

		JSON.registerObjectMarshaller(ErrorResponse) {
			[
					status: it.status,
					reason: it.reason,
					errors: it.errors
			]
		}

		JSON.registerObjectMarshaller(AccessToken) {
			[
					id: it.id,
					clientId: it.clientId,
					expiration: it.expiration,
					refreshToken: it.refreshToken,
					tokenType: it.tokenType,
					username: it.username,
					value:  it.value
			]
		}

		JSON.registerObjectMarshaller(Feed.Type) {
			it as String
		}

		JSON.registerObjectMarshaller(ErrorReason) {

			def message

			if (it.params) {
				message = MessageFormat.format(it.reason as String, (it.params as List).toArray())
			} else {
				message = it.reason
			}

			[
			        code: it.value,
					message: message
			]
		}

		JSON.registerObjectMarshaller(com.thehuxley.Feed) {
			def body = it.body
			if (it.type == com.thehuxley.Feed.Type.USER_SUBMISSION_STATUS) {
				body.submission = Submission.get(body.submissionId as Long)
			}

			[
					id                : it.id,
					date       		  : it.dateCreated,
					body              : body,
					type			  : it.type
			]
		}

		JSON.registerObjectMarshaller(com.thehuxley.Feed.Type) {
			it as String
		}

		JSON.registerObjectMarshaller(Message) {
			[
					id                : it.id,
					recipient         : it.recipient,
					sender            : it.sender,
					dateCreated       : it.dateCreated,
					lastUpdated       : it.lastUpdated,
					body              : it.body,
					responses         : it.responses,
					readDate          : it.readDate,
					unread            : it.unread,
					firstMessage	  : it.firstMessage,
					type			  : it.type
			]
		}

		JSON.registerObjectMarshaller(Group) {
			[
					id                : it.id,
					name              : it.name,
					url               : it.url,
					description       : it.description,
					startDate         : it.startDate,
					endDate           : it.endDate,
					dateCreated       : it.dateCreated,
					lastUpdated       : it.lastUpdated,
					institution       : it.institution,
			]
		}

		JSON.registerObjectMarshaller(Institution.Status) {
			it as String
		}


		JSON.registerObjectMarshaller(Institution) {
			[
					id     : it.id,
					name   : it.name,
					acronym: it.acronym,
					logo   : "$institutionLogoUrl/$it.logo",
					status : it.status
			]
		}


		JSON.registerObjectMarshaller(UserInstitution.Role) {
			it as String
		}

		JSON.registerObjectMarshaller(Language) {
			[
					id           : it.id,
					name         : it.name,
					compiler     : it.compiler,
					extension	 : it.extension,
					script       : it.script,
					execParams   : it.execParams,
					compileParams: it.compileParams,
					plagConfig   : it.plagConfig,
					label        : it.label
			]
		}

		JSON.registerObjectMarshaller(Problem.Status) {
			it as String
		}

		JSON.registerObjectMarshaller(Problem) {

			def testCases = [
				examples:	TestCase.countByProblemAndExample(it as Problem, true, [sort: "id", order: "asc"]),
				total: TestCase.countByProblem(it as Problem)
			]

			[
					id               : it.id,
					name             : it.name,
					description      : it.description,
					inputFormat      : it.inputFormat,
					outputFormat     : it.outputFormat,
					source           : it.source,
					level            : it.level,
					timeLimit        : it.timeLimit,
					nd               : it.nd,
					dateCreated      : it.dateCreated,
					lastUpdated      : it.lastUserUpdate,
					approvedBy  	 : it.userApproved,
					suggestedBy 	 : it.userSuggest,
					topics           : it.topics,
					status           : it.status,
					testCases		 : testCases
			]
		}

		JSON.registerObjectMarshaller(Submission.Evaluation) {
			it as String
		}

		JSON.registerObjectMarshaller(Submission) {

			Map testCase = null

			try {
				if (it.testCase &&
						((it.evaluation == Submission.Evaluation.WRONG_ANSWER) ||
						(it.evaluation == Submission.Evaluation.PRESENTATION_ERROR) ||
						(it.evaluation == Submission.Evaluation.RUNTIME_ERROR) ||
						(it.evaluation == Submission.Evaluation.TIME_LIMIT_EXCEEDED))
				) {

					TestCase tc = it.testCase as TestCase

					Set<TestCase> testCasesExample = TestCase.findAllByProblemAndExample(it.problem as Problem, true, [sort: "id", order: "asc"])
					Set<TestCase> testCasesNoExample = TestCase.findAllByProblemAndExample(it.problem as Problem, false, [sort: "id", order: "asc"])

					def order = 1

					if (it.testCase.example) {
						order += testCasesExample.findIndexOf {
							it.id == tc.id
						}
					} else {
						order += testCasesNoExample.findIndexOf {
							it.id == tc.id
						}

						order += testCasesExample.size()
					}

					testCase = [id: it.testCase.id, order: order, total: (testCasesExample.size() + testCasesNoExample.size())]
				}
			} catch (Exception e) {
				e.finalize()
				testCase = null
			}


			[
					id            : it.id,
					time          : it.time,
					tries         : it.tries,
					comment       : it.comment,
					submissionDate: it.submissionDate,
					evaluation    : it.evaluation,
					filename      : it.submission,
					testCase      : testCase,
					user          : it.user,
					problem       : it.problem,
					language      : it.language,
					errorMsg      : it.errorMsg
			]
		}

		JSON.registerObjectMarshaller(Questionnaire) {
			[
					id         : it.id,
					title      : it.title,
					description: it.description,
					score      : it.score,
					startDate  : it.startDate,
					endDate    : it.endDate,
					serverTime : new Date(),
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated,
					group      : it.group
			]
		}

		JSON.registerObjectMarshaller(Topic) {
			[
					id  : it.id,
					name: it.name
			]
		}

		JSON.registerObjectMarshaller(TestCase) {
			[
					id		   : it.id,
					input	   : it.input,
					output     : it.output,
					example    : it.example,
					tip        : it.tip,
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated,
			]
		}

		JSON.registerObjectMarshaller(Role) {
			[
					id       : it.id,
					authority: it.authority
			]
		}


		JSON.registerObjectMarshaller(User) {
			[
					id                    : it.id,
					name                  : it.name,
					avatar 				  : "$avatarUrl/$it.avatar",
					institution           : it.institution
			]
		}

		JSON.registerObjectMarshaller(UserGroup.Role) {
			it as String
		}


		JSON.registerObjectMarshaller(Pendency.PendencyKind) {
			it as String
		}

		JSON.registerObjectMarshaller(Pendency.Status) {
			it as String
		}

		JSON.registerObjectMarshaller(Pendency) {
			[
					id:          it.id,
					kind:        it.kind,
					status:      it.status,
					user:        it.user,
					institution: it.institution,
					group:       it.group,
					params:      it.params,
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated
			]
		}

		JSON.registerObjectMarshaller(OracleConsult.Type) {
			it as String
		}

		JSON.registerObjectMarshaller(OracleConsult.Status) {
			it as String
		}

		JSON.registerObjectMarshaller(OracleConsult) {
			[
					id: it.hash,
					input: it.input,
					output: it.output,
					favour: it.favour,
					against: it.against,
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated,
					type: it.type,
					status: it.status
			]
		}



		JSON.registerObjectMarshaller(PendencyKey.Type) {
			it as String
		}

		JSON.registerObjectMarshaller(PendencyKey) {
			[
					key: it.hashKey,
					type: it.type,
					id: it.entity,
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated,
			]
		}

		JSON.registerObjectMarshaller(LicensePack) {
			[
					id:          it.id,
					total:       it.total,
					startDate:   it.startDate,
					endDate:     it.endDate,
					dateCreated: it.dateCreated,
					lastUpdated: it.lastUpdated,
					institution: it.institution
			]
		}

		JSON.registerObjectMarshaller(Plagiarism.Status) {
			it as String
		}

		JSON.registerObjectMarshaller(Plagiarism) {
			[
					id                   : it.id,
					plagiarizedSubmission: it.submission1.submissionDate.before(it.submission2.submissionDate) ? it.submission1 : it.submission2,
					suspiciousSubmission : it.submission2.submissionDate.after(it.submission1.submissionDate) ? it.submission2 : it.submission1,
					similarity           : it.percentage,
					status               : it.status
			]
		}

		JSON.createNamedConfig('public') { }

		JSON.createNamedConfig('private') {
			it.registerObjectMarshaller(User) {
				[
						id                    : it.id,
						name                  : it.name,
						username		      : it.username,
						email                 : it.email,
						avatar 				  : "$avatarUrl/$it.avatar",
						institution           : it.institution,
						roles 			      : it.authorities
				]
			}
		}
	}
}
