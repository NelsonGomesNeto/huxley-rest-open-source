databaseChangeLog = {

	changeSet(author: "Marcio Augusto Guimarães", id: "renameusergroup-3.9.9") {
		renameTable(newTableName:  "user_cluster", oldTableName: "user_group")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "cleanuserclustertables-3.9.9") {
		dropTable(tableName:  "cluster_cluster")
		dropTable(tableName:  "cluster_permissions")
		dropTable(tableName:  "cluster_users")
		dropTable(tableName:  "questionnaire_cluster")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropfkandidxusercluster-3.9.9") {

		dropForeignKeyConstraint(baseTableName: "user_cluster", constraintName: "FK_user_group_group_id")
		dropForeignKeyConstraint(baseTableName: "user_cluster", constraintName: "FK_user_group_user_id")
		dropIndex(indexName: "FK_user_group_user_id", tableName: "user_cluster")
		dropIndex(indexName: "IDX_user_group_group_id", tableName: "user_cluster")

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "renamecolumnclusterid-3.9.9") {
		renameColumn(
				tableName: "user_cluster",
				newColumnName: "cluster_id",
				oldColumnName: "group_id",
				columnDataType: "BIGINT"
		)

		createIndex(indexName: "IDX_user_cluster_cluster_id", tableName: "user_cluster") {
			column(name: "cluster_id")
		}

		createIndex(indexName: "IDX_user_cluster_user_id", tableName: "user_cluster") {
			column(name: "user_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_user_cluster_user_id",
				baseTableName: "user_cluster",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_user_cluster_cluster_id",
				baseTableName: "user_cluster",
				baseColumnNames: "cluster_id",
				referencedTableName: "cluster",
				referencedColumnNames: "id"
		)
	}
}