databaseChangeLog = {

	changeSet(author: "Diogo Cabral de Almeida", id: "newColumnToProblemUpdate") {

		addColumn(tableName: "problem") {
			column(name: "last_user_update", type: "DATETIME") {
				constraints(nullable: "true")
			}
		}

	}

	changeSet(author: "Diogo Cabral de Almeida", id: "populateNewProblemUpdateColumn") {
		sql("UPDATE `problem` SET `last_user_update` = `last_updated`")
	}

}