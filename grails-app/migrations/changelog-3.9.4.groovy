databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "addDateColumnsToQuestionnaireTable") {
		addColumn(tableName: "questionnaire") {
			column(name: "date_created", type: "DATETIME")
			column(name: "last_updated", type: "DATETIME")
		}
	}
}