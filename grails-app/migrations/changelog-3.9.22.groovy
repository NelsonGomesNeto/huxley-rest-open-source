databaseChangeLog = {

	changeSet(author: "Romero B. de S. Malaquias", id: "create_table_feed") {

		createTable(tableName: "feed") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "recipient_id", type: "BIGINT") {
				constraints(nullable: "true")
			}
		}

		createIndex(indexName: "IDX_feed_recipient_id", tableName: "feed") {
			column(name: "recipient_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_feed_recipient_id",
				baseTableName: "feed",
				baseColumnNames: "recipient_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Romero B. de S. Malaquias", id: "create_table_feed_body") {

		createTable(tableName: "feed_body") {

			column(name: "body", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "body_idx", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "body_elt", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}

	}
}