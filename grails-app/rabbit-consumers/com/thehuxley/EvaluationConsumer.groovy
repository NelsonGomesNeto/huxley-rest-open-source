package com.thehuxley

import grails.transaction.Transactional

import com.budjb.rabbitmq.consumer.MessageContext

class EvaluationConsumer {

    static rabbitConfig = [
        queue: "evaluation_queue",
        retry: true
    ]

    def submissionService

    def handleMessage(Map dataMap, MessageContext context) {
        Boolean isReevaluation = dataMap["isReevaluation"] as Boolean
        def submission = Submission.get(dataMap["submissionId"] as Long)
        if (submission == null) {
            log.warn('A submissao [id=' + dataMap["submissionId"] + '] não existe. Ignorando!')
            return
        }

        submission.evaluation = dataMap["evaluation"] ? Submission.Evaluation.valueOf(dataMap["evaluation"] as String) : Submission.Evaluation.HUXLEY_ERROR
        submission.time = dataMap["executionTime"] as Double ?: -1D
        submission.testCase = dataMap["testCaseId"] ? TestCase.load(dataMap["testCaseId"] as Long) : null
        submission.errorMsg = dataMap["errorMsg"] as String ?: null
        submission.diffFile = dataMap["diff"] as String ?: null

        println("Casos de testes corretos: "+ dataMap["testCasesCorrect"] + "/" + dataMap["testCasesTotal"])


        log.debug submission.id
        log.debug submission.evaluation
        log.debug submission.time
        log.debug submission.errorMsg

        submissionService.update(submission, isReevaluation)
    }

}