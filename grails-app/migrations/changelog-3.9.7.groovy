databaseChangeLog = {

	changeSet(author: "Diogo Cabral de Almeida", id: "removeSubmissionColumns") {
		dropColumn(columnName: "input_test_case", tableName: "submission")
		dropColumn(columnName: "cache_user_name", tableName: "submission")
		dropColumn(columnName: "cache_user_email", tableName: "submission")
		dropColumn(columnName: "cache_user_username", tableName: "submission")
		dropColumn(columnName: "cache_problem_name", tableName: "submission")

		createIndex(indexName: "idx_submission_problem_id_user_id", tableName: "submission") {
			column(name: "problem_id")
			column(name: "user_id")
		}
	}

}