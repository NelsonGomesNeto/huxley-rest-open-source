package com.thehuxley

import grails.transaction.Transactional

import com.budjb.rabbitmq.publisher.RabbitMessagePublisher

@Transactional
class QueueService {

    //FIXME not good
    def grailsApplication

    RabbitMessagePublisher rabbitMessagePublisher


    def sendSubmissionToJudge(Submission submission, sourceCode, allTestCases, boolean isReevaluation=false) {
        def testCases = []

        allTestCases.each {
            def testCase = [
                    id: it.id,
                    input: it.input,
                    output: it.output,
                    example : it.example

            ]
            testCases.add(testCase)
        }

        def language = [
                name: submission.language.name,
                script: submission.language.script
        ]

        log.debug(submission.problem.timeLimit)

        def dataMap = [submissionId: submission.id, filename: submission.submission, language: language,
                       sourceCode: sourceCode, testCases: testCases, time : submission.time,
                       problem : [ id: submission.problem.id, timeLimit : submission.problem.timeLimit],
                       isReevaluation: isReevaluation]

//		log.debug "Sending submission to submission_queue. " + dataMap

        rabbitMessagePublisher.send {
            routingKey = "submission_queue"
            contentType = "application/json"
            contentEncoding = "UTF-8"
            deliveryMode = 2 //persistent
            body = dataMap
            headers = [
                    "__KeyTypeId__": "java.lang.Object",
                    "__TypeId__": "java.util.HashMap",
                    "__ContentTypeId__": "java.lang.Object"
            ]
        }
    }

    //FIXME not good
    def String mountSubmissionPath(Submission submission){
        grailsApplication.config.huxleyFileSystem.base + System.getProperty("file.separator") +
                submission.problem.id + System.getProperty("file.separator") +
                submission.user.id + System.getProperty("file.separator") +
                submission.language.name + System.getProperty("file.separator") +
                submission.tries + System.getProperty("file.separator");
    }

    //FIXME not good
    def getSubmissionFile(Submission submission) {
        new File(mountSubmissionPath(submission) + submission.submission)
    }

    public boolean fileExists(Submission submission){
        try {
            File f = getSubmissionFile(submission)
            return f.exists()
        }catch (Exception e){
			e.finalize()
            return false
        }
    }

    def sendSubmissionsToOracle(User user, String input, List<Submission> chosenSubmissions, String id) {
        def submissions = []

        chosenSubmissions.each {
            def sourceCode = getSubmissionFile(it).getText("UTF-8")

            def language = [
                    name: it.language.name,
                    script: it.language.script
            ]

            def problem = [
                    id: it.problem.id,
                    timeLimit : it.problem.timeLimit
            ]

            def submission = [
                    submissionId: it.id,
                    filename: it.submission,
                    language: language,
                    sourceCode: sourceCode,
                    time: it.time,
                    problem: problem
            ]
            submissions.add(submission)
        }

        def dataMap = [input: input, submissions: submissions, userId: user.id, id: id]

        log.debug "Sending data to oracle_queue. " + dataMap

        rabbitMessagePublisher.send {
            routingKey = "oracle_queue"
            contentType = "application/json"
            contentEncoding = "UTF-8"
            deliveryMode = 2 //persistent
            body = dataMap
            headers = [
                    "__KeyTypeId__": "java.lang.Object",
                    "__TypeId__": "java.util.HashMap",
                    "__ContentTypeId__": "java.lang.Object"
            ]
        }
    }

   def sendToStatisticsQueue(Submission submission, Boolean isReevaluation) {
        def dataMap = [submissionId: submission.id, isReevaluation: isReevaluation]

        log.debug "Sending data to statistics_queue. " + dataMap

        rabbitMessagePublisher.send {
            routingKey = "statistics_queue"
            contentType = "application/json"
            contentEncoding = "UTF-8"
            deliveryMode = 2 //persistent
            body = dataMap
            headers = [
                    "__KeyTypeId__": "java.lang.Object",
                    "__TypeId__": "java.util.HashMap",
                    "__ContentTypeId__": "java.lang.Object"
            ]
        }
    }

}