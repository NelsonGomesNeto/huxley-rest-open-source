databaseChangeLog = {
	changeSet(author: "Luiz Paulo Barroca", id: "new_position_column_in_message") {
		addColumn(tableName: "message") {
			column(name: "first_message", type: "BIT") {
				constraints(nullable: "false")
			}
		}
	}
}