databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "dropColumnQuestionnaireEvaluationDetail") {
		dropColumn(tableName: "questionnaire",  columnName: "evaluation_detail")
	}
}