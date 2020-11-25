databaseChangeLog = {

	changeSet(author: "Diogo Cabral de Almeida", id: "fixingQuestionnaireUserConstraint") {
		dropForeignKeyConstraint(baseTableName: "questionnaire_user", constraintName: "FK17E7B9751B247856")		
	}

}
