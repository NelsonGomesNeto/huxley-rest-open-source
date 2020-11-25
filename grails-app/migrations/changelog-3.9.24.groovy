databaseChangeLog = {

	changeSet(author: "Diogo Cabral de Almeida", id: "fixingSubmissionConstraint") {
		dropForeignKeyConstraint(baseTableName: "submission", constraintName: "submission_ibfk_2")		

		addForeignKeyConstraint(
				constraintName: "fk_submission_user",
				baseTableName: "submission",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)
	}

}
