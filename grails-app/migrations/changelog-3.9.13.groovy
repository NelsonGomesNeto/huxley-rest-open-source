databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "createAuthenticationHistoryTable2-3.9.13") {
		createTable(tableName: "authentication_history") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "accessed_date", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}

		createIndex(indexName: "IDX_authentication_history_user_id", tableName: "authentication_history") {
			column(name: "user_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_authentication_history_user_id",
				baseTableName: "authentication_history",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "populateAuthentication_history-3.9.13") {
		sql("INSERT INTO `authentication_history` (`user_id`, `accessed_date`, `date_created`) SELECT `user_id`, DATE(`date`), TIMESTAMP(`date`) FROM `historic` WHERE `action` = 'SignIn' AND `user_id` IN (SELECT `id` from `user`) GROUP BY `user_id`, YEAR(`date`), MONTH(`date`), DAY(`date`)")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "addDateAndExampleColumnsTestCaseTable") {
		addColumn(tableName: "test_case") {
			column(name: "date_created", type: "DATETIME")
			column(name: "last_updated", type: "DATETIME")
			column(name: "example", type: "BIT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "typeToExampleTestCase-3.9.13") {
		update(tableName: "test_case") {
			column(name: "example", valueComputed: "type = 1")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropTypeColumn-3.9.13") {
		dropColumn(tableName: "test_case", columnName: "type")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropEvaluationDetailColumn-3.9.13") {
		dropColumn(tableName: "problem", columnName: "evaluation_detail")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropCodeColumn-3.9.13") {
		dropColumn(tableName: "problem", columnName: "code")
	}
}