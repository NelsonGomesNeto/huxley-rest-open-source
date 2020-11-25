import com.thehuxley.*
import grails.converters.JSON

class SubmissionControllerTest extends GroovyTestCase {

	def submissionService

	void testUpdateSubmission() {
		int subId = 400
		def userName
		def newEvaluation

		def submissionController = new SubmissionController()
		submissionController.submissionService = submissionService


		// recupera uma submissão qualquer
		Submission submission = Submission.get(subId);
		assertNotNull(submission.evaluation)
		userName = submission.user.name
		assertNotNull(userName) // só pra garantir que todos os campos estão vindo


		// simula a atualizacao do status da submissao
		Submission updatedSub = Submission.load(subId)
		if (submission.evaluation.equals(Submission.Evaluation.CORRECT)) {
			newEvaluation = Submission.Evaluation.WRONG_ANSWER
		} else {
			newEvaluation = Submission.Evaluation.CORRECT

		}
		updatedSub.evaluation = newEvaluation
		submissionController.request.contentType = "application/json"
		submissionController.request.content = (updatedSub as JSON) as String

		submissionController.update(subId)

		// agora recupera de novo e verifica se o status realmente mudou
		submission = Submission.get(subId);
		assertEquals(newEvaluation, submission.evaluation)
		assertEquals(userName, submission.user.name) // verifica se manteve uma propriedade que existia antes
	}

	/* Testa as atualizações que ocorrem após o recebimento de uma submissão */
	void testTriggerAfterSubmission(){
		Long userId = 1693 //Carla Nicole Calheiros Pimentel
		Long quizId = 236 // 3. Tomando decisões
		Long submissionIdCorrect = 132090; // 3 numeros em ordem crescente (id=2), CORRETA
		//Long submissionIdWrong = 130696; // 3 numeros em ordem crescente (id=2), WRONG

		// Testando primeiro uma submissao correta
		Submission submission = Submission.get(submissionIdCorrect)
		assertEquals(Submission.Evaluation.CORRECT, submission.evaluation)

		//Limpando os dados de userproblem
		def userProblem = UserProblem.findByUserAndProblem(submission.user, submission.problem)
		userProblem.status = Submission.Evaluation.WAITING
		userProblem.save()

		userProblem = UserProblem.findByUserAndProblem(submission.user, submission.problem)
		assertEquals(Submission.Evaluation.WAITING, userProblem.status)

		//limpando dados de profile
		def userProfile = Profile.findByUser(submission.user)
		userProfile.problemsCorrect = 0
		userProfile.submissionCorrectCount = 0
		userProfile.problemsTried = 0
		userProfile.submissionCount = 0
		userProfile.save()

		userProfile = Profile.findByUser(submission.user)
		assertEquals(0, userProfile.problemsCorrect)
		assertEquals(0, userProfile.submissionCorrectCount)
		assertEquals(0, userProfile.problemsTried)
		assertEquals(0, userProfile.submissionCount)

		def quiz = Questionnaire.get(quizId)

		def quizUser = QuestionnaireUser.findByQuestionnaireAndUser(quiz,submission.user) //id 8533

		quizUser.problemsCorrect = 0
		quizUser.problemsTried = 0
		quizUser.score = 0
		quizUser.submissionsCount = 0;

		quizUser.save()

		def submissionController = new SubmissionController()
		submissionController.submissionService = submissionService
		submissionController.request.contentType = "application/json"
		submissionController.request.content = (submission as JSON) as String
		log.info("Chamando o controller")
		submissionController.update(submission.id)
		log.info("Terminando")

		// Testa se a atalizacao de user problem funcionou
		userProblem = UserProblem.findByUserAndProblem(submission.user, submission.problem)
		assertEquals(Submission.Evaluation.CORRECT, userProblem.status)

		// testa a atualização do profile
		userProfile = Profile.findByUser(submission.user)
		assertTrue(0 < userProfile.problemsCorrect)
		assertTrue(0 < userProfile.submissionCorrectCount)
		assertTrue(0 < userProfile.problemsTried)
		assertTrue(0 < userProfile.submissionCount)

		// testa a atualizacao do quizUser
		quizUser = QuestionnaireUser.findByQuestionnaireAndUser(quiz,submission.user) //id 8533

		assertEquals( 6, quizUser.problemsCorrect)
		assertEquals( 6, quizUser.problemsTried )
		assertEquals( 10, quizUser.score)
		assertTrue( quizUser.submissionsCount >  0)
	}


}
