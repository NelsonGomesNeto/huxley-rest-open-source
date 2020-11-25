databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "addGroupIdQuestionnaireTable") {
		addColumn(tableName: "questionnaire") {
			column(name: "group_id", type: "BIGINT")
		}
	}


	changeSet(author: "Marcio Augusto Guimarães", id: "populateGroupIdQuestionnaireTable") {

		grailsChange {
			change {
				sql.execute("UPDATE questionnaire q SET group_id = (SELECT cluster_id FROM questionnaire_cluster c WHERE q.id = c.questionnaire_groups_id GROUP BY  questionnaire_groups_id);")
			}
		}


		createIndex(indexName: "IDX_questionnaire_group_id", tableName: "questionnaire") {
			column(name: "group_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_questionnaire_group_id",
				baseTableName: "questionnaire",
				baseColumnNames: "group_id",
				referencedTableName: "cluster",
				referencedColumnNames: "id"
		)
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeNullGroupsInQuestionnaire") {
		update(tableName: "questionnaire") {
			column(name: "group_id", value: "4")
			where("group_id IS NULL")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeValueEvaluationSubmission") {
		update(tableName: "submission") {
			column(name: "evaluation", value: "10")
			where("evaluation = -1")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "alterEvaluationDataTypeInSubmission") {
		modifyDataType(tableName: "submission", columnName: "evaluation", newDataType: "INT")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "dropForeignKeysAndIndexUserGroup") {
		dropForeignKeyConstraint(baseTableName: "user_group", constraintName: "user_group_ibfk_1")
		dropForeignKeyConstraint(baseTableName: "user_group", constraintName: "user_group_ibfk_2")
		dropIndex(tableName: "user_group", indexName: "FK_user_group_group_id")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "renameUserGroupToUserGroup2") {
		renameTable(oldTableName: "user_group", newTableName: "user_group_2")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "reCreteUserGroup") {
		createTable(tableName: "user_group") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "BIT") {
				constraints(nullable: "false")
			}
		}

		createIndex(indexName: "IDX_user_group_group_id", tableName: "user_group") {
			column(name: "group_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_user_group_user_id",
				baseTableName: "user_group",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_user_group_group_id",
				baseTableName: "user_group",
				baseColumnNames: "group_id",
				referencedTableName: "cluster",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "rePopulateUserGroupAndDropUserGroup2") {
		sql("INSERT INTO `user_group` (`version`, `user_id`, `group_id`, `role`, `enabled`) (SELECT `version`, `user_id`, `group_id`, `role`, `enabled` FROM `user_group_2`)")

		dropTable(tableName: "user_group_2")

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "createUserInstitutionTable") {
		createTable(tableName: "user_institution") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "institution_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "BIT") {
				constraints(nullable: "false")
			}
		}

		createIndex(indexName: "IDX_user_institution_institution_id", tableName: "user_institution") {
			column(name: "institution_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_user_institution_user_id",
				baseTableName: "user_institution",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_user_institution_institution_id",
				baseTableName: "user_institution",
				baseColumnNames: "institution_id",
				referencedTableName: "institution",
				referencedColumnNames: "id"
		)

		sql("INSERT INTO `user_institution` (`version`, `user_id`, `institution_id`, `role`, `enabled`) (SELECT `version`, `user_id`, `institution_id`, 0, 1 FROM `license` WHERE (`type_id` = 2 OR `type_id` = 6) AND `institution_id` IS NOT NULL)")
		sql("INSERT INTO `user_institution` (`version`, `user_id`, `institution_id`, `role`, `enabled`) (SELECT `version`, `user_id`, `institution_id`, 1, 1 FROM `license` WHERE `type_id` = 4 AND `institution_id` IS NOT NULL)")
		sql("INSERT INTO `user_institution` (`version`, `user_id`, `institution_id`, `role`, `enabled`) (SELECT `version`, `user_id`, `institution_id`, 2, 1 FROM `license` WHERE `type_id` = 3 AND `institution_id` IS NOT NULL)")
		sql("INSERT INTO `user_institution` (`version`, `user_id`, `institution_id`, `role`, `enabled`) (SELECT `version`, `user_id`, `institution_id`, 3, 1 FROM `license` WHERE `type_id` = 5 AND `institution_id` IS NOT NULL)")

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeStatusUserProblem") {

		update(tableName: "user_problem") {
			column(name: "status", value: "0")
			where("status = 1")
		}

		update(tableName: "user_problem") {
			column(name: "status", value: "1")
			where("status = 2")
		}

	}
}