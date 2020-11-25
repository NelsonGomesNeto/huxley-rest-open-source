databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "addColumnsUserTable-3.9.11") {
		addColumn(tableName: "user") {
			column(name: "date_created", type: "DATETIME")
			column(name: "last_updated", type: "DATETIME")
			column(name: "avatar", type: "VARCHAR(255)")
			column(name: "institution_id", type: "BIGINT")
		}

		createIndex(indexName: "IDX_user_institution_id", tableName: "user") {
			column(name: "institution_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_user_institution_id",
				baseTableName: "user",
				baseColumnNames: "institution_id",
				referencedTableName: "institution",
				referencedColumnNames: "id"
		)
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "populateNewColumns-3.9.11") {
		sql("UPDATE `profile` SET `institution_id` = NULL WHERE `institution_id` = 7 OR `institution_id` = 14 OR `institution_id` = 28")
		sql("UPDATE `user` AS u LEFT JOIN `profile` as p ON u.`id` = p.`user_id` SET u.`date_created` = p.`date_created`, u.`last_updated` = p.`last_updated`, u.`avatar` = p.`photo`, u.`institution_id` = p.`institution_id`")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "setNullAvatarTODefault.jpg-3.9.11.2") {
		sql("UPDATE `user` SET `avatar` = 'default.png' WHERE `avatar` IS NULL")
	}
}

