databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "newPositionColumnInTopCoder") {
		addColumn(tableName: "top_coder") {
			column(name: "position", type: "BIGINT") {
				constraints(nullable: "true")
			}
		}
	}
}