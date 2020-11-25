import com.thehuxley.Submission
import com.thehuxley.dto.SubmissionDTO

class SubmissionServiceTest extends GroovyTestCase {

	def submissionService

	void testGetSubmissionFile() {
		Submission submission = Submission.get(201); //uma submissao com status de correta
		SubmissionDTO dto = new SubmissionDTO(submission)
		def submissionFile = submissionService.getSubmissionFile(dto);
		assertNotNull(submissionFile)

	}
}
