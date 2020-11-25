import com.thehuxley.Submission
import com.thehuxley.User
import com.thehuxley.UserProblem

class ProblemServiceTest extends GroovyTestCase {

	def problemService
	void testGetRecommendation() {
		log.info("Testando")
		def rodrigoUser = User.load(53) //Rodrigo Paes, aluno
		assertNotNull(rodrigoUser)
		def suggestedProblem = problemService.getSuggestion(rodrigoUser)
		// deve sempre retornar um problema
		assertNotNull(suggestedProblem)

		def userProblem = UserProblem.createCriteria().list() {
			user {
				eq("id", rodrigoUser.id)
			}
			problem {
				eq("id", suggestedProblem.id)
			}
			ne("status", Submission.Evaluation.CORRECT)
		}
		// e o usuário não pode ter resolvido esse problema
		assertEquals(0, userProblem.size())

	}

}
