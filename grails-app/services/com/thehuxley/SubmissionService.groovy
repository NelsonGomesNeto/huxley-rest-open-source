package com.thehuxley

import com.thehuxley.atmosphere.Feed
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.commons.lang.time.StopWatch
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.web.multipart.commons.CommonsMultipartFile

class SubmissionService {

    def redisService
    def grailsApplication
    def userService
    def cacheService
    def queueService
    def dataService
    def pushService
    def topCoderService
	def mailService

    def final EXPIRE_CACHE = 60 * 60 * 24 * 7
	final int UPDATE_NDS_EVERY = 200


    private static final String FILE_SEPARATOR = System.getProperty("file.separator")

    def get(Submission submission) {
        try {
            redisService.memoize(cacheService.generateKey(Submission, submission), EXPIRE_CACHE) {
                (submission as JSON) as String
            }
        } catch (ObjectNotFoundException e) {
            e.finalize()
        }
    }

    def list(Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params)) {
            def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                    getCriteria(params))

            ["searchResults": (resultList as JSON) as String, total: resultList.totalCount as String]
        }
    }

    def findByGroup(Submission submission, Group group) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [group]), EXPIRE_CACHE) {
            try {
                UserGroup.findAllByGroupAndUser(group, submission?.user) ? (submission as JSON) as String : null
            } catch (ObjectNotFoundException e) {
                e.finalize()
            }
        }
    }

    def findAllByGroup(Group group, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [group]), EXPIRE_CACHE) {

			DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

            def users = UserGroup.findAllByGroup(group).user

            if (!users.empty) {
                params.users = users

				if (params.submissionDateGe) {
					def date = formatter.parseDateTime(params.submissionDateGe as String).toDate()

					if (date > group.endDate) {
						params.submissionDateGe = new DateTime(group.endDate).toString(formatter)
					}

					if (date < group.startDate) {
						params.submissionDateGe = new DateTime(group.startDate).toString(formatter)
					}

				} else {
					params.submissionDateGe = new DateTime(group.startDate).toString(formatter)
				}

				if (params.submissionDateLe) {
					def date = formatter.parseDateTime(params.submissionDateLe as String).toDate()

					if (date < group.startDate) {
						params.submissionDateLe = new DateTime(group.startDate).toString(formatter)
					}

					if (date > group.endDate) {
						params.submissionDateLe = new DateTime(group.endDate).toString(formatter)
					}

				} else {
					params.submissionDateLe = new DateTime(group.endDate).toString(formatter)
				}


                def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                        getCriteria(params))

                ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
            } else {
                ["searchResults": [] as JSON, "total": "0"]

            }
        }
    }

    def findByProblem(Submission submission, Problem problem) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [problem])) {
            try {
                return submission.problem?.id == problem.id ? (submission as JSON) as String : null
            } catch (ObjectNotFoundException e) {
                e.finalize()
            }
        }
    }

    def findAllByProblem(Problem problem, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [problem]), EXPIRE_CACHE) {

            params.problem = problem.id

            def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                    getCriteria(params))

            ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
        }
    }

    def findByUser(Submission submission, User user) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [user]), EXPIRE_CACHE) {
            try {
                return submission.user?.id == user.id ? (submission as JSON) as String : null
            } catch (ObjectNotFoundException e) {
                e.finalize()
            }
        }
    }

    def findAllByUser(User user, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [user]), EXPIRE_CACHE) {
            params.user = user.id

            def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                    getCriteria(params))

            ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
        }
    }

    def findByUserAndProblem(Submission submission, Problem problem, User user) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [problem, user]), EXPIRE_CACHE) {
            try {
                return (submission.user?.id == user.id && submission.problem?.id == problem.id) ?
                        (submission as JSON) as String : null
            } catch (ObjectNotFoundException e) {
                e.finalize()
            }
        }
    }

    def findAllByUserAndProblem(Problem problem, User user, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [problem, user])) {

            params.user = user.id
            params.problem = problem.id

            def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                    getCriteria(params))

            ["searchResults": (resultList as JSON) as String, total: resultList.totalCount as String]
        }
    }

	def findAllByQuestionnaire(Questionnaire questionnaire, Map params) {
		redisService.memoizeHash(cacheService.generateKey(Submission, params, [questionnaire]), EXPIRE_CACHE) {
			DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

			def users = QuestionnaireUser.findAllByQuestionnaire(questionnaire).user
			def problems = QuestionnaireProblem.findAllByQuestionnaire(questionnaire).problem
			if (!users.empty && !problems.empty) {
				params.users = users
				params.problems = problems


				if (params.submissionDateGe) { //inicial
					def date = formatter.parseDateTime(params.submissionDateGe as String).toDate()

					if (date > questionnaire.endDate) { //maior que o max
						params.submissionDateGe = new DateTime(questionnaire.endDate).toString(formatter) //retornar vazio
					} else {
						params.submissionDateGe = date.toString(formatter)
					}

				}

				if (params.submissionDateLe) { //final
					def date = formatter.parseDateTime(questionnaire.submissionDateLe as String).toDate()

					if (date > questionnaire.endDate) {
						params.submissionDateLe = new DateTime(questionnaire.endDate).toString(formatter)
					} else {
						params.submissionDateLe = date.toString(formatter)
					}

				} else {
					params.submissionDateLe = new DateTime(questionnaire.endDate).toString(formatter)
				}

				def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
						getCriteria(params))

				["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
			} else {
				["searchResults": [] as JSON, "total": "0"]

			}
		}
	}

    def findByQuestionnaireAndProblem(Submission submission, Questionnaire questionnaire, Problem problem) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [questionnaire, problem]), EXPIRE_CACHE) {
            try {
                if (QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)) {
                    return (submission.problem?.id == problem.id
                            && submission.submissionDate <= questionnaire.endDate
                    ) ? SubmissionDTO.asDTO(submission) : null
                } else {
                    return null
                }
            } catch (ObjectNotFoundException e) {
                e.finalize()
                return null
            }
        }
    }

    def findAllByQuestionnaireAndProblem(Questionnaire questionnaire, Problem problem, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [questionnaire, problem]), EXPIRE_CACHE) {

            def users = QuestionnaireUser.findAllByQuestionnaire(questionnaire).user

            if (QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem) && !users.empty) {

                DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

                params.users = users
                params.problem = problem.id
                params.submissionDateLe = new DateTime(questionnaire.endDate).toString(formatter)

                def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                        getCriteria(params))

                ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
            } else {
                [searchResults: [], total: 0]
            }
        }
    }

    def findByUserAndQuestionnaireAndProblem(
            Submission submission,
            User user,
            Questionnaire questionnaire,
            Problem problem) {
        redisService.memoize(cacheService.generateKey(Submission, submission, [user, questionnaire, problem]), EXPIRE_CACHE) {
            try {
                if (QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)) {
                    return (submission.problem?.id == problem.id
                            && submission.user?.id == user.id
                            && submission.submissionDate <= questionnaire.endDate
                    ) ? (submission as JSON) as String : null
                } else {
                    return null
                }
            } catch (ObjectNotFoundException e) {
                e.finalize()
            }
        }
    }

    def findAllByUserAndQuestionnaireAndProblem(User user, Questionnaire questionnaire, Problem problem, Map params) {
        redisService.memoizeHash(cacheService.generateKey(Submission, params, [user, questionnaire, problem]), EXPIRE_CACHE) {

            if (QuestionnaireProblem.findByQuestionnaireAndProblem(questionnaire, problem)) {

                DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

                params.user = user.id
                params.problem = problem.id
                params.submissionDateLe = new DateTime(questionnaire.endDate).toString(formatter)

                def resultList = Submission.createCriteria().list([max: params.max, offset: params.offset],
                        getCriteria(params))

                ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount]
            } else {
                ["searchResults": [] as String, "total": "0"]
            }
        }
    }

    def getSubmissionFile(Submission submission) {
        new File(mountSubmissionPath(submission) + submission.submission)
    }

    def getDiffFile(Submission submission) {
        def filename = submission.submission.substring(0, submission.submission.lastIndexOf('.')) + ".diff"
        new File(mountSubmissionPath(submission) + filename)
    }

    def String mountSubmissionPath(Submission submission) {
        grailsApplication.config.huxleyFileSystem.base + FILE_SEPARATOR +
                submission.problem.id + FILE_SEPARATOR +
                submission.user.id + FILE_SEPARATOR +
                submission.language.name + FILE_SEPARATOR +
                submission.tries + FILE_SEPARATOR;
    }

	def String mountSubmissionPath(Long problemId, Long userId, String language, Integer tries) {
		grailsApplication.config.huxleyFileSystem.base + FILE_SEPARATOR +
				problemId + FILE_SEPARATOR +
				userId + FILE_SEPARATOR +
				language + FILE_SEPARATOR +
				tries + FILE_SEPARATOR;
	}

    @Transactional
    def createSubmission(User user, Problem problem, Language language, String filename, Integer tries) {
        try {

            def submission = new Submission(
                    language: language,
                    user: user,
                    problem: problem,
                    submission: filename,
                    tries: tries
            ).save(flush: true)

            cacheService.expireCache(Submission, submission)
            cacheService.expireCache(Submission, null)

            return submission
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        }
    }

    @Transactional
    def save(Submission submission) {
        try {
            cacheService.expireCache(Submission, submission)
            cacheService.expireCache(Submission, null)
            submission.save(flush: true)

            get(submission)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    @Transactional
    def reevaluate(Map params) {
        ArrayList<Map> queue = new ArrayList<Map>();
        def errorCount = 0
        def errors = []
        def submissions = []

        /**
         * Esse método estava levantando o monte de exceções de StaleObject do Hibernate.
         *
         * Como esse é um método de serviço, o flush da sessão e o posterior
         * commit no banco de dados só é feito depois que o método encerra.
         * Entretanto, como colocamos várias mensagens na fila do rabbit, pode ocorrer
         * da resposta chegar antes do flush da sessão. Quando isso ocorre, o hibernate
         * levanta uma exceção.
         * Para evitar isso, estamos usando o withNewSession. Essa closure vai
         * criar uma nova seção do hibernate e realizará o flush após o fim da closure
         * e não do método.
         *
         * Então depois só depois da closure é que chamamos a fila. Assim, nesse ponto
         * o hibernate já estará com o estado sincronizado, mesmo que as mensagens de
         * resposta comecem a chegar.
         *
         * http://stackoverflow.com/questions/9542129/grails-what-is-the-difference-between-an-unflushed-session-and-a-rolled-back-tr/9543507#9543507
         */
        Submission.withNewSession{
            submissions = Submission.createCriteria().list([readOnly: true], getCriteria(params) )
            errorCount = 0
            errors = []

            submissions.each() { Submission submission ->
                try {
                    def testCases = TestCase.findAllByProblem(submission.problem)
                    def sourceCode = getSubmissionFile(submission).getText("UTF-8")

                    submission.time = -1
                    submission.diffFile = ""
                    submission.output = ""
                    submission.errorMsg = ""
                    submission.evaluation = Submission.Evaluation.WAITING
                    submission.testCase = null

                    submission.save()
                    def theMap = [:]
                    theMap.submission = submission
                    theMap.sourceCode = sourceCode
                    theMap.testCases = testCases

                    queue.add(theMap)

                } catch (Exception e) {
                    log.error(e.getMessage())
                    e.finalize()
                    errorCount++
                    errors.add(["id": submission.id, "error": e.class.name, "message": e.message])
                }
            }
        } // end transaction

        // Coloca na fila do avaliador
        for (Map theMap : queue) {
            queueService.sendSubmissionToJudge(theMap.submission, theMap.sourceCode, theMap.testCases, true)
        }

        ["searchResults": (["total": submissions.size(), "errorCount": errorCount, "errors": errors] as JSON) as String, "total": submissions.size()]
    }


    def update(Submission submission, boolean isReevaluation=false) {
        try {
			cacheService.expireCache(Submission, submission)
            cacheService.expireCache(Submission, null)

			Submission.withTransaction {
				submission.save(flush: true)
			}

            //queueService.sendToStatisticsQueue(submission, isReevaluation)
            triggersAfterUpdate(submission, isReevaluation)

            get(submission)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def triggersAfterUpdate(Submission submission, boolean isReevaluation=false) {

        try {

			StopWatch stopWatch = new StopWatch()

			if (log.infoEnabled) {
				log.info("Iniciando as alterações provocadas pela submissão $submission.id em $submission.language.name" +
						" do usuário $submission.user.username")
				stopWatch.start()
			}

			if (submission.id % 10 == 0L) {
				dataService.expireCache(submission)
				if (log.infoEnabled) {
					log.info("Expirado o cache dos dados. TEMPO: $stopWatch")
				}
			}

            if (submission.evaluation != Submission.Evaluation.WAITING) {
                if (log.infoEnabled)
                    log.info("Enviando a mensagem via atmosphere para notificar ao usuário. TEMPO: $stopWatch")
                pushService.publish(new Feed(type: Feed.Type.USER_SUBMISSION_STATUS, body: [
                        submission: submission,
                        judged    : true,
                        evaluation: submission.evaluation,
                ]), submission.user)
            } else {
                log.error("A submissão #$submission.id estava em WAITING depois do retorno do avaliador TEMPO: $stopWatch")
            }

            if (submission.evaluation == Submission.Evaluation.CORRECT) {
                if (log.infoEnabled)
                    log.info("A submissão estava correta, atualizando a pontuação do usuário TEMPO: $stopWatch")
                topCoderService.refreshTopCoder(submission.user)
                if (log.infoEnabled)
                    log.info("Notificando todos os membros de todos os grupos que o usuário pertence a respeito da " +
                            "submissão correta. TEMPO: $stopWatch")
                UserGroup.findAllByUser(submission.user).group.each { Group group ->
                    group.users.each { User user ->
                        pushService.publish(new Feed(type: Feed.Type.GROUP_MEMBER_SOLVED_PROBLEM, body: [
                                submission: submission
                        ]), user)
                    }
                }
                if (log.infoEnabled)
                    log.info("Terminou de notificar os mebros do grupo. TEMPO: $stopWatch")
            }

            if (submission.evaluation == Submission.Evaluation.CORRECT) {

                if (log.infoEnabled)
                    log.info("Agora os questionários serão atualizados... TEMPO: $stopWatch")


                def questionnaires = QuestionnaireUser.findAllByUser(submission.user).questionnaire.findAll {
                    it.problems.id.contains(submission.problem.id)
                }

                if (log.infoEnabled)
                    log.info("Os questionário para $submission.user.username quem possuem o problema $submission.problem.name " +
                            "são $questionnaires.id. TEMPO: $stopWatch")

                questionnaires.each { Questionnaire questionnaire ->
                    if (questionnaire.endDate >= submission.submissionDate) {
                        if (log.infoEnabled)
                            log.info("\t\t\t$questionnaire.id - $questionnaire.title ESTAVA ABERTO " +
                                    "EM $submission.submissionDate!!! (Finaliza em: $questionnaire.endDate)")

                        QuestionnaireService.updateScores(questionnaire, submission.user)
						cacheService.expireCache(Problem, submission.problem)
                    }
                }

                if (log.infoEnabled) {
                    log.info("Terminou de atualizar os questionários. TIME: $stopWatch")
                    stopWatch.stop()
                }
            }
        } catch (Exception e) {
            log.error e.printStackTrace()
        }

    }

    Closure getCriteria(Map params) {

        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

        return {
            and {
                !params.submission ?: eq("id", params.submission as Long)
                !params.problem ?: eq("problem", Problem.load(params.problem as Long))
				!params.problems ?: inList("problem", params.problems)
                !params.language ?: eq("language", Language.load(params.language as Long))
                !params.user ?: eq("user", User.load(params.user as Long))
				!params.users ?: inList("user", params.users)
                ''

                !params.tries ?: eq("tries", params.tries as Integer)
                !params.triesGt ?: gt("tries", params.triesGt as Integer)
                !params.triesGe ?: ge("tries", params.triesGe as Integer)
                !params.triesLt ?: lt("tries", params.triesLt as Integer)
                !params.triesLe ?: le("tries", params.triesLe as Integer)
                !params.triesNe ?: ne("tries", params.triesNe as Integer)

                !params.time ?: eq("time", params.time as Double)
                !params.timeGt ?: gt("time", params.timeGt as Double)
                !params.timeGe ?: ge("time", params.timeGe as Double)
                !params.timeLt ?: lt("time", params.timeLt as Double)
                !params.timeLe ?: le("time", params.timeLe as Double)
                !params.timeNe ?: ne("time", params.timeNe as Double)

                !params.submissionDate ?: eq("submissionDate",
                        formatter.parseDateTime(params.submissionDate as String).toDate())
                !params.submissionDateGt ?: gt("submissionDate",
                        formatter.parseDateTime(params.submissionDateGt as String).toDate())
                !params.submissionDateGe ?: ge("submissionDate",
                        formatter.parseDateTime(params.submissionDateGe as String).toDate())
                !params.submissionDateLt ?: lt("submissionDate",
                        formatter.parseDateTime(params.submissionDateLt as String).toDate())
                !params.submissionDateLe ?: le("submissionDate",
                        formatter.parseDateTime(params.submissionDateLe as String).toDate())
                !params.submissionDateNe ?: ne("submissionDate",
                        formatter.parseDateTime(params.submissionDateNe as String).toDate())


                !params.inEvaluations ?: inList("evaluation", params.inEvaluations)

                if (params.isNotEvaluations && !params.isNotEvaluations.empty) {
                    not {
                        inList("evaluation", params.isNotEvaluations)
                    }
                }
            }



            order(params.sort ?: "submissionDate", params.order ?: "desc")
        }
    }

    GrailsParameterMap normalize(GrailsParameterMap params) {
        params.max = Math.min(params.int("max", 0) ?: 10, 100)
        params.offset = params.int("offset", 0)
        params.order = params.order ?: "desc"
        params.sort = params.sort ?: "submissionDate"
        params.inEvaluations = params.list("evaluations").collect { Submission.Evaluation.valueOf(it as String) }
        params.isNotEvaluations = params.list("excludeEvaluations").collect {
            Submission.Evaluation.valueOf(it as String)
        }
        params.problem = params.problem as Long
        params.language = params.language as Long
        params.user = params.user as Long
        params.tries = params.tries as Integer
        params.time = params.time as Double

        return params
    }

    boolean isSortable(param) {
        [
                "id",
                "time",
                "tries",
                "comment",
                "submissionDate",
                "evaluation",
                "user",
                "problem",
                "language"
        ].contains(param)
    }


}