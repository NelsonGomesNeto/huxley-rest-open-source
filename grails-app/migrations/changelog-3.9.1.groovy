databaseChangeLog = {
    changeSet(author: "Marcio Augusto Guimarães", id: "createPendencyTable") {
        createTable(tableName: "pendency") {
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

            column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "BIGINT")

			column(name: "institution_id", type: "BIGINT")

            column(name: "user_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
			column(name: "kind", type: "INT") {
				constraints(nullable: "false")
			}

        }

		createIndex(indexName: "IDX_pendency_user_id", tableName: "pendency") {
			column(name: "user_id")
		}

		createIndex(indexName: "IDX_pendency_institution_id", tableName: "pendency") {
			column(name: "institution_id")
		}

		createIndex(indexName: "IDX_pendency_group_id", tableName: "pendency") {
			column(name: "group_id")
		}

        addForeignKeyConstraint(
                constraintName: "FK_pendency_user_id",
                baseTableName: "pendency",
                baseColumnNames: "user_id",
                referencedTableName: "user",
                referencedColumnNames: "id"
        )

        addForeignKeyConstraint(
                constraintName: "FK_pendency_institution_id",
                baseTableName: "pendency",
                baseColumnNames: "institution_id",
                referencedTableName: "institution",
                referencedColumnNames: "id"
        )

        addForeignKeyConstraint(
                constraintName: "FK_pendency_group_id",
                baseTableName: "pendency",
                baseColumnNames: "group_id",
                referencedTableName: "cluster",
                referencedColumnNames: "id"
        )

    }

    changeSet(author: "Marcio Augusto", id: "createPendencyParamsTable") {
        createTable(tableName: "pendency_params") {
            column(name: "params", type: "BIGINT")

            column(name: "params_idx", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

            column(name: "params_elt", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}
        }
    }
}
