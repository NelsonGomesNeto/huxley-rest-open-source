databaseChangeLog = {

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_oracle_consult_3") {

		createTable(tableName: "oracle_consult") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "INT") {
				constraints(nullable: "true")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "true")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "hash", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "input", type: "LONGTEXT") {
				constraints(nullable: "true")
			}

			column(name: "output", type: "LONGTEXT") {
				constraints(nullable: "true")
			}

			column(name: "favour", type: "INT") {
				constraints(nullable: "true")
			}

			column(name: "against", type: "INT") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_oracle_consult_user_id", tableName: "oracle_consult") {
			column(name: "user_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_oracle_consult_user_id",
				baseTableName: "oracle_consult",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		createIndex(indexName: "IDX_oracle_consult_problem_id", tableName: "oracle_consult") {
			column(name: "problem_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_oracle_consult_problem_id",
				baseTableName: "oracle_consult",
				baseColumnNames: "problem_id",
				referencedTableName: "problem",
				referencedColumnNames: "id"
		)

	}
}