databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "createAuthenticationTable") {
		createTable(tableName: "authentication") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "token", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "access_count", type: "INT") {
				constraints(nullable: "true")
			}

			column(name: "last_access", type: "DATETIME") {
				constraints(nullable: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "true")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "true")
			}
		}
	}
}