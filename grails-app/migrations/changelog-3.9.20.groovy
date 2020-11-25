databaseChangeLog = {

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_message_3") {

		createTable(tableName: "message") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "subject", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "body", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "unread", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "read_date", type: "DATETIME") {
				constraints(nullable: "true")
			}

			column(name: "group_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "sender_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "recipient_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "type", type: "INT") {
				constraints(nullable: "false")
			}
		}


		createIndex(indexName: "IDX_message_group_id", tableName: "message") {
			column(name: "group_id")
		}

		createIndex(indexName: "IDX_message_sender_id", tableName: "message") {
			column(name: "sender_id")
		}

		createIndex(indexName: "IDX_message_recipient_id", tableName: "message") {
			column(name: "recipient_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_message_group_id",
				baseTableName: "message",
				baseColumnNames: "group_id",
				referencedTableName: "cluster",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_message_sender_id",
				baseTableName: "message",
				baseColumnNames: "sender_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_message_recipient_id",
				baseTableName: "message",
				baseColumnNames: "recipient_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_message_information_3") {

		createTable(tableName: "message_information") {

			column(name: "information", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "information_idx", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "information_elt", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_message_message_3") {

		createTable(tableName: "message_message") {

			column(name: "message_responses_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "message_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_message_message_message_responses_id", tableName: "message_message") {
			column(name: "message_responses_id")
		}

		createIndex(indexName: "IDX_message_message_message_id", tableName: "message_message") {
			column(name: "message_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_message_message_message_responses_id",
				baseTableName: "message_message",
				baseColumnNames: "message_responses_id",
				referencedTableName: "message",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_message_message_message_id",
				baseTableName: "message_message",
				baseColumnNames: "message_id",
				referencedTableName: "message",
				referencedColumnNames: "id"
		)

	}

}