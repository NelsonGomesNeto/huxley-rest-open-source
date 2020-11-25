databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "createPendencyKeyTable2") {
		createTable(tableName: "pendency_key") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "hash_key", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "entity", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropNotNullConstraintDescriptionColumnProblem") {
		dropNotNullConstraint(columnDataType: "LONGTEXT", columnName: "description", tableName: "problem")
	}

}