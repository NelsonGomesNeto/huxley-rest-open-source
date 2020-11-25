package com.thehuxley

import com.thehuxley.atmosphere.Feed
import grails.orm.PagedResultList

class OracleService {

	def queueService
	def pushService

	void sendToOracle(User user, String input, Problem problem, String id) {
        ArrayList<Submission> submissions = new ArrayList<>()
        /*
         * max = número máximo de submissões que o orákulo irá utilizar
         *
         * counter = conta quantas submissões colocamos na lista de submissões a enviar
         * ao orákulo
         */
        int max = 5, counter = 0

        // Pega a submissão do criador do problema
        PagedResultList result = Submission.createCriteria().list(max:1){
            setReadOnly(true)
            eq('problem', problem)
            eq('evaluation', Submission.Evaluation.CORRECT)
            eq('user', problem.userSuggest)
            order('submissionDate', 'desc')
        }
        if (result.size() >0) {
            Submission submission = result.first() as Submission
            if (queueService.fileExists(submission)) {
                ++counter
                submissions.add(submission)
            }else {
                log.warn('Arquivo da submissão '+submission+ ' não foi encontrado.')
            }
        }

        /*
            Todos os usuários que acertaram o problema
         */
        List<User> correctUsers = Submission.createCriteria().list([max:5]) {
            setReadOnly(true)
            eq('problem', problem)
            eq('evaluation', Submission.Evaluation.CORRECT)
            ne('user', problem.userSuggest)
            projections {
                distinct('user')
            }
        }

        // Ordena os usuários pelo topcoder, decrescente
        Collections.sort(correctUsers, new Comparator<User>() {
            @Override
            int compare(User u1, User u2) {
                if (u1.profile!= null && u2.profile==null) return 1
                if (u1.profile== null && u2.profile!=null) return -1
                if (u1.profile== null && u2.profile==null) return 0
                return u1.profile.topCoderScore - u2.profile.topCoderScore
            }
        })

        /*
            Recupera a submissão correta mais atual de cada usuário,
            ordenado pelo topcoder, verifica se o arquivo de submissão
            ainda existe, e se existir adiciona a submissão na
            lista de submissões a enviar ao orákulo.
            Lembrando que existe um limite máximo de submissões a enviar.
         */

        for (int i=0; i < correctUsers.size() && counter < max; ++i){
            user = correctUsers.get(i)

            // máximo de 1 por usuário, evitando vários arquivos de um mesmo usuário
            Submission.createCriteria().list(max:1) {
                setReadOnly(true)
                eq('problem', problem)
                eq('evaluation', Submission.Evaluation.CORRECT)
                eq('user', user)
                order('submissionDate', 'desc')
            }.each { submission ->
                if (queueService.fileExists(submission)){
                    submissions.add(submission)
                    counter++
                } else {
                    log.warn('Arquivo da submissão '+submission+ ' não foi encontrado.')
                }
            }
        }

        if (counter == 0){
            // sem oráculo
            log.warn('Problema '+problem + ' não possui submissões suficientes para o orákulo')
        }

        pushService.publish(new Feed(type: Feed.Type.USER_ORACLE_REQUEST, body: [
                input: input,
				id: id,
                executing: true
        ]), user)

        /*
        pode ser que a lista de submissões tenha tamanho zero. Mas não tem problema,
        porque o oraculo já está preparado para reponder nesses casos.
         */

		queueService.sendSubmissionsToOracle(user, input, submissions, id)


	}

}