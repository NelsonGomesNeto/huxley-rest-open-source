databaseChangeLog = {

	changeSet(author: "Diogo Cabral de Almeida", id: "removingUnusedShiroUserConstraints") {

		dropForeignKeyConstraint(baseTableName: "questionnaire_user", constraintName: "FK17E7B975858C1A45")
		dropForeignKeyConstraint(baseTableName: "questionnaire_user", constraintName: "FK17E7B975BFF901C1")

		addForeignKeyConstraint(
				constraintName: "fk_questionnaire_user_ibfk_1",
				baseTableName: "questionnaire_user",
				baseColumnNames: "questionnaire_id",
				referencedTableName: "questionnaire",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "fk_questionnaire_user_ibfk_2",
				baseTableName: "questionnaire_user",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		dropForeignKeyConstraint(baseTableName: "user_problem", constraintName: "user_problem_ibfk_2")

		addForeignKeyConstraint(
				constraintName: "fk_user_problem_ibfk_2",
				baseTableName: "user_problem",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		dropForeignKeyConstraint(baseTableName: "problem", constraintName: "problem_ibfk_1")

		addForeignKeyConstraint(
				constraintName: "fk_problem_ibfk_1",
				baseTableName: "problem",
				baseColumnNames: "user_approved_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		dropForeignKeyConstraint(baseTableName: "problem", constraintName: "problem_ibfk_2")

		addForeignKeyConstraint(
				constraintName: "fk_problem_ibfk_2",
				baseTableName: "problem",
				baseColumnNames: "user_suggest_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		dropForeignKeyConstraint(baseTableName: "submission_comment", constraintName: "submission_comment_ibfk_1")

		addForeignKeyConstraint(
				constraintName: "fk_submission_comment_ibfk_1",
				baseTableName: "submission_comment",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)
	}

}
