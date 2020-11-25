package com.thehuxley

import grails.converters.*
import org.codehaus.groovy.grails.web.json.*
import org.jxls.common.Context
import org.jxls.util.JxlsHelper

import java.text.SimpleDateFormat

/**
 * Created by rodrigo on 04/10/15.
 */
class FileExportService {

    def userService
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    def exportToExcel (Questionnaire quiz) {
        def params = [:]
        QuizReportExcelBean bean = new QuizReportExcelBean()

        bean.quizTitle = quiz.title
        bean.quizDescription = quiz.description
        bean.quizScore = quiz.score
        bean.groupName = quiz.group.name
        bean.startDate = simpleDateFormat.format(quiz.startDate)
        bean.endDate = simpleDateFormat.format(quiz.endDate)


        String jsonSTR = userService.findAllByQuestionnaire(quiz, params)
        def result = JSON.parse(jsonSTR)
        result.searchResults.each { line ->
            bean.students.add(new QuizReportStudentBean(name: line.name, score: line.quiz.score))
        }

        InputStream is = new FileInputStream("/home/huxley/data/reports/quiz.xls")
        OutputStream os = new ByteArrayOutputStream()

        Context context = new Context()

        context.putVar("bean", bean)

        JxlsHelper.getInstance().processTemplate(is, os, context)

        byte[] file = os.toByteArray()
        is.close()
        os.close()

        return file
    }
}

class QuizReportExcelBean {
    String quizTitle
    String quizDescription
    double quizScore
    String startDate
    String endDate
    String groupName

    List<QuizReportStudentBean> students = new ArrayList<QuizReportStudentBean>()

}

class QuizReportStudentBean {
    String name
    double score
}