databaseChangeLog = {

	changeSet(author: "Diogo Cabral", id: "submission-changes-3.9.18") {
		dropColumn(tableName:"questionnaire_user",  columnName : "plagium_status")
	}

}