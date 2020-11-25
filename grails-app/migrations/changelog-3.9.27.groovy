
databaseChangeLog = {

	changeSet(author: "Marcio Augusto Guimarães", id: "drop_language_compile_params_not_null_constraint") {
		dropNotNullConstraint(tableName: "language", columnName: "compile_params", columnDataType: "VARCHAR(255)")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "drop_language_exec_params_not_null_constraint") {
		dropNotNullConstraint(tableName: "language", columnName: "exec_params", columnDataType: "VARCHAR(255)")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "drop_language_plag_config_not_null_constraint") {
		dropNotNullConstraint(tableName: "language", columnName: "plag_config", columnDataType: "VARCHAR(255)")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "add_language_label_column") {
		addColumn(tableName: "language") {
			column(name: "label", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_approval_table") {
		createTable(tableName: "approval") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "client_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "scope", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "approved", type: "BIT") {
				constraints(nullable: "true")
			}

			column(name: "expiration", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_modified", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}
}