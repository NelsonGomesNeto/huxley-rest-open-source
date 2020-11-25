databaseChangeLog = {

	changeSet(author: "marcio (generated)", id: "1415817073075-1") {
		createTable(tableName: "cluster") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "institution_id", type: "BIGINT")

			column(name: "hash", type: "VARCHAR(255)")

			column(name: "date_created", type: "DATETIME")

			column(name: "last_updated", type: "DATETIME")

			column(name: "description", type: "VARCHAR(255)")

			column(name: "access_key", type: "VARCHAR(255)")

			column(name: "end_date", type: "DATETIME")

			column(name: "start_date", type: "DATETIME")

			column(name: "url", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-2") {
		createTable(tableName: "cluster_cluster") {
			column(name: "cluster_groups_id", type: "BIGINT")

			column(name: "cluster_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-3") {
		createTable(tableName: "cluster_permissions") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "permission", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "status_user", type: "SMALLINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-4") {
		createTable(tableName: "cluster_users") {
			column(name: "shiro_user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "cluster_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-5") {
		createTable(tableName: "common_errors") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comment", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "error_msg", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-6") {
		createTable(tableName: "content") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "embedded", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-7") {
		createTable(tableName: "content_topics") {
			column(name: "topic_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "content_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-8") {
		createTable(tableName: "content_user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "content_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-9") {
		createTable(tableName: "course_plan") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-10") {
		createTable(tableName: "course_plan_questionnaire") {
			column(name: "course_plan_questionnaire_id", type: "BIGINT")

			column(name: "questionnaire_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-11") {
		createTable(tableName: "cpdqueue") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "language", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT")

			column(name: "institution_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-12") {
		createTable(tableName: "email") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "port", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "smtp", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-13") {
		createTable(tableName: "email_to_send") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "message", type: "MEDIUMTEXT") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "subject", type: "VARCHAR(255)")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-14") {
		createTable(tableName: "forum_submission") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "submission_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "changed", type: "DATETIME")

			column(name: "date", type: "DATETIME")

			column(name: "message", type: "LONGTEXT")

			column(name: "title", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-15") {
		createTable(tableName: "forum_submission_submission_comment") {
			column(name: "forum_submission_comment_id", type: "BIGINT")

			column(name: "submission_comment_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-16") {
		createTable(tableName: "fragment") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "number_of_lines", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "percentage", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "plagium_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "start_line1", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "start_line2", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "fragments_idx", type: "INT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-17") {
		createTable(tableName: "historic") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "controller", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-18") {
		createTable(tableName: "history_license") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "DATETIME")

			column(name: "institution_id", type: "BIGINT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "license_id", type: "BIGINT")

			column(name: "start_date", type: "DATETIME")

			column(name: "user_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-19") {
		createTable(tableName: "huxley_system_fail") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "logged_user_id", type: "BIGINT")

			column(name: "message", type: "LONGTEXT")

			column(name: "stack_trace", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "time_of_error", type: "DATETIME") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-20") {
		createTable(tableName: "institution") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "address_id", type: "BIGINT")

			column(name: "phone", type: "VARCHAR(255)")

			column(name: "photo", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-21") {
		createTable(tableName: "institution_shiro_user") {
			column(name: "institution_users_id", type: "BIGINT")

			column(name: "shiro_user_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-22") {
		createTable(tableName: "language") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "exec_params", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "plag_config", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "compile_params", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "compiler", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "script", type: "VARCHAR(255)")

			column(name: "extension", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-23") {
		createTable(tableName: "lesson") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-24") {
		createTable(tableName: "lesson_plan") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "owner_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-25") {
		createTable(tableName: "lesson_plan_lessons") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "lesson_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "lesson_plan_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "position", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-26") {
		createTable(tableName: "lesson_topic") {
			column(name: "lesson_topics_id", type: "BIGINT")

			column(name: "topic_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-27") {
		createTable(tableName: "license") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "active", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "hash", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "indefinite_validity", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "institution_id", type: "BIGINT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "type_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-28") {
		createTable(tableName: "license_pack") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "institution_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "total", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-29") {
		createTable(tableName: "license_type") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "descriptor", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "kind", type: "VARCHAR(17)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-30") {
		createTable(tableName: "payment") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "DOUBLE")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "frequency", type: "INT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "payment_coupon_id", type: "BIGINT")

			column(name: "profile_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "INT")

			column(name: "response", type: "LONGTEXT")

			column(name: "status", type: "VARCHAR(255)")

			column(name: "total", type: "DOUBLE")

			column(name: "url", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-31") {
		createTable(tableName: "payment_coupon") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "discount", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "hash", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "VARCHAR(8)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-32") {
		createTable(tableName: "permissions") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "role", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "controller", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-33") {
		createTable(tableName: "plagium") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "percentage", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "submission1_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "submission2_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(defaultValueNumeric: "0", name: "status", type: "TINYINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-34") {
		createTable(tableName: "plagium_fragment") {
			column(name: "plagium_fragments_id", type: "BIGINT")

			column(name: "fragment_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-35") {
		createTable(tableName: "problem") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "time_limit", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "evaluation_detail", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "level", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "nd", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "TINYINT")

			column(name: "user_approved_id", type: "BIGINT")

			column(name: "user_suggest_id", type: "BIGINT")

			column(name: "fastest_submision_id", type: "BIGINT")

			column(name: "input_format", type: "LONGTEXT")

			column(name: "output_format", type: "LONGTEXT")

			column(name: "date_created", type: "DATETIME")

			column(name: "last_updated", type: "DATETIME")

			column(name: "source", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-36") {
		createTable(tableName: "problem_lesson") {
			column(name: "problem_lessons_id", type: "BIGINT")

			column(name: "lesson_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-37") {
		createTable(tableName: "problem_topics") {
			column(name: "topic_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-38") {
		createTable(tableName: "profile") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "hash", type: "VARCHAR(255)")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "photo", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "problems_correct", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "problems_tryed", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "small_photo", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME")

			column(name: "last_updated", type: "DATETIME")

			column(name: "institution_id", type: "BIGINT")

			column(name: "submission_correct_count", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "submission_count", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-39") {
		createTable(tableName: "questionnaire") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "evaluation_detail", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "score", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-40") {
		createTable(tableName: "questionnaire_cluster") {
			column(name: "questionnaire_groups_id", type: "BIGINT")

			column(name: "cluster_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-41") {
		createTable(tableName: "questionnaire_problem") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "score", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-42") {
		createTable(tableName: "questionnaire_shiro_user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "score", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comment", type: "LONGTEXT")

			column(name: "status", type: "INT")

			column(name: "plagium_status", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-43") {
		createTable(tableName: "questionnaire_statistics") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "average_note", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "greater_then_equals_seven", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "less_seven", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "standart_deviaton", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "try_percentage", type: "DOUBLE") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-44") {
		createTable(tableName: "questionnaire_user_penalty") {
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

			column(name: "penalty", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-45") {
		createTable(tableName: "reference_solution") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "SMALLINT")

			column(name: "user_approved_id", type: "BIGINT")

			column(name: "reference_solution", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "language_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_suggest_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "comment", type: "VARCHAR(255)")

			column(name: "reply", type: "VARCHAR(255)")

			column(name: "submission_date", type: "DATETIME")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-46") {
		createTable(tableName: "shiro_role") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-47") {
		createTable(tableName: "shiro_role_permissions") {
			column(name: "shiro_role_id", type: "BIGINT")

			column(name: "permissions_string", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-48") {
		createTable(tableName: "shiro_user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "password_hash", type: "VARCHAR(255)")

			column(name: "username", type: "VARCHAR(255)")

			column(name: "email", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "VARCHAR(255)")

			column(name: "last_login", type: "DATETIME")

			column(name: "top_coder_position", type: "INT")

			column(name: "top_coder_score", type: "DOUBLE")

			column(name: "current_license_id", type: "BIGINT")

			column(name: "settings_id", type: "BIGINT")

			column(name: "cpf", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-49") {
		createTable(tableName: "shiro_user_permissions") {
			column(name: "shiro_user_id", type: "BIGINT")

			column(name: "permissions_string", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-50") {
		createTable(tableName: "shiro_user_questionnaire") {
			column(name: "shiro_user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "questionnaire_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-51") {
		createTable(tableName: "shiro_user_roles") {
			column(name: "shiro_user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "shiro_role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-52") {
		createTable(tableName: "submission") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "submission", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "evaluation", type: "TINYINT")

			column(name: "submission_date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "detailed_log", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "diff_file", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "language_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "tries", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "output", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(defaultValueNumeric: "-1", name: "time", type: "DOUBLE")

			column(defaultValueNumeric: "1", name: "plagium_status", type: "INT")

			column(name: "input_test_case", type: "LONGTEXT")

			column(name: "cache_user_name", type: "VARCHAR(255)")

			column(name: "cache_user_email", type: "VARCHAR(255)")

			column(name: "cache_user_username", type: "VARCHAR(255)")

			column(name: "cache_problem_name", type: "VARCHAR(255)")

			column(name: "error_msg", type: "LONGTEXT")

			column(name: "test_case_id", type: "BIGINT")

			column(name: "comment", type: "LONGTEXT")

			column(defaultValue: "WAITING", name: "plagiarism_status", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-53") {
		createTable(tableName: "submission_comment") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "comment", type: "MEDIUMTEXT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "forum_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-54") {
		createTable(tableName: "teaching_resources") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "content_id", type: "BIGINT")

			column(name: "lesson_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "order_in_list", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-55") {
		createTable(tableName: "test_case") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "input", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "output", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "max_output_size", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "tip", type: "LONGTEXT")

			column(name: "rank", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "unrank", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-56") {
		createTable(tableName: "top_coder") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "points", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-57") {
		createTable(tableName: "topic") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-58") {
		createTable(tableName: "topic_problems") {
			column(name: "topic_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-59") {
		createTable(tableName: "user_evolution") {
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

			column(name: "problem_correct", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "problems_tried", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "top_coder_position", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "top_coder_score", type: "DOUBLE") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-60") {
		createTable(tableName: "user_link") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "link", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-61") {
		createTable(tableName: "user_problem") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "problem_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "INT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "similarity", type: "INT")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-62") {
		createTable(tableName: "user_setting") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "email_notify", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-63") {
		addPrimaryKey(columnNames: "cluster_id, shiro_user_id", tableName: "cluster_users")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-64") {
		addPrimaryKey(columnNames: "content_id, topic_id", tableName: "content_topics")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-65") {
		addPrimaryKey(columnNames: "shiro_user_id, questionnaire_id", tableName: "shiro_user_questionnaire")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-66") {
		addPrimaryKey(columnNames: "shiro_user_id, shiro_role_id", tableName: "shiro_user_roles")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-67") {
		addPrimaryKey(columnNames: "topic_id, problem_id", tableName: "topic_problems")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-101") {
		createIndex(indexName: "hash", tableName: "cluster", unique: "true") {
			column(name: "hash")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-102") {
		createIndex(indexName: "url", tableName: "cluster", unique: "true") {
			column(name: "url")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-103") {
		createIndex(indexName: "FKD15D3DBF1B247856", tableName: "cluster_permissions", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-104") {
		createIndex(indexName: "FKD15D3DBF3A487DD2", tableName: "cluster_permissions", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-105") {
		createIndex(indexName: "FKD15D3DBF4E718FC", tableName: "cluster_permissions", unique: "false") {
			column(name: "group_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-106") {
		createIndex(indexName: "FKD15D3DBFDAC7E480", tableName: "cluster_permissions", unique: "false") {
			column(name: "group_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-107") {
		createIndex(indexName: "FK38B73479870B286E", tableName: "content", unique: "false") {
			column(name: "owner_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-108") {
		createIndex(indexName: "FK1FA757AA8ACEF9C5", tableName: "content_topics", unique: "false") {
			column(name: "topic_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-109") {
		createIndex(indexName: "FK1FA757AAD11A4EC5", tableName: "content_topics", unique: "false") {
			column(name: "content_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-110") {
		createIndex(indexName: "FK319553D11B247856", tableName: "content_user", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-111") {
		createIndex(indexName: "FK319553D1D11A4EC5", tableName: "content_user", unique: "false") {
			column(name: "content_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-112") {
		createIndex(indexName: "FK789F352A1B247856", tableName: "forum_submission", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-113") {
		createIndex(indexName: "FK789F352A3A487DD2", tableName: "forum_submission", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-114") {
		createIndex(indexName: "FK789F352A60E048B3", tableName: "forum_submission", unique: "false") {
			column(name: "submission_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-115") {
		createIndex(indexName: "FK789F352A9B839EAF", tableName: "forum_submission", unique: "false") {
			column(name: "submission_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-116") {
		createIndex(indexName: "FKFE7BE1A150FBFDA4", tableName: "forum_submission_submission_comment", unique: "false") {
			column(name: "forum_submission_comment_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-117") {
		createIndex(indexName: "FKFE7BE1A1D7544EA8", tableName: "forum_submission_submission_comment", unique: "false") {
			column(name: "submission_comment_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-118") {
		createIndex(indexName: "FKFE7BE1A1F4DF852C", tableName: "forum_submission_submission_comment", unique: "false") {
			column(name: "submission_comment_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-119") {
		createIndex(indexName: "FKFE7BE1A1FE2B0528", tableName: "forum_submission_submission_comment", unique: "false") {
			column(name: "forum_submission_comment_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-120") {
		createIndex(indexName: "FK9DA2E25036EFBD05", tableName: "fragment", unique: "false") {
			column(name: "plagium_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-121") {
		createIndex(indexName: "FKB0BCAC5F1B247856", tableName: "historic", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-122") {
		createIndex(indexName: "FKC92C3B961B247856", tableName: "history_license", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-123") {
		createIndex(indexName: "FKC92C3B963EBED9C5", tableName: "history_license", unique: "false") {
			column(name: "license_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-124") {
		createIndex(indexName: "FKC92C3B96E41984E5", tableName: "history_license", unique: "false") {
			column(name: "institution_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-125") {
		createIndex(indexName: "FKB5AB4724A8477299", tableName: "huxley_system_fail", unique: "false") {
			column(name: "logged_user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-126") {
		createIndex(indexName: "FK3529A5B89424D8E5", tableName: "institution", unique: "false") {
			column(name: "address_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-127") {
		createIndex(indexName: "FK54E2B24010E90C08", tableName: "institution_shiro_user", unique: "false") {
			column(name: "shiro_user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-128") {
		createIndex(indexName: "FK54E2B240300D1184", tableName: "institution_shiro_user", unique: "false") {
			column(name: "shiro_user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-129") {
		createIndex(indexName: "FK54E2B24099FF0BB8", tableName: "institution_shiro_user", unique: "false") {
			column(name: "institution_users_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-130") {
		createIndex(indexName: "FK54E2B240B3C6753C", tableName: "institution_shiro_user", unique: "false") {
			column(name: "institution_users_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-131") {
		createIndex(indexName: "name", tableName: "language", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-132") {
		createIndex(indexName: "FKBE10AD38870B286E", tableName: "lesson", unique: "false") {
			column(name: "owner_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-133") {
		createIndex(indexName: "FK275219D0870B286E", tableName: "lesson_plan", unique: "false") {
			column(name: "owner_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-134") {
		createIndex(indexName: "FK6C57EACCC061C9AF", tableName: "lesson_plan_lessons", unique: "false") {
			column(name: "lesson_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-135") {
		createIndex(indexName: "FK6C57EACCFEA616E0", tableName: "lesson_plan_lessons", unique: "false") {
			column(name: "lesson_plan_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-136") {
		createIndex(indexName: "FKC32B13688ACEF9C5", tableName: "lesson_topic", unique: "false") {
			column(name: "topic_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-137") {
		createIndex(indexName: "FKC32B1368F10B87FC", tableName: "lesson_topic", unique: "false") {
			column(name: "lesson_topics_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-138") {
		createIndex(indexName: "FK9F084411B247856", tableName: "license", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-139") {
		createIndex(indexName: "FK9F08441BC53ED86", tableName: "license", unique: "false") {
			column(name: "type_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-140") {
		createIndex(indexName: "FK9F08441E41984E5", tableName: "license", unique: "false") {
			column(name: "institution_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-141") {
		createIndex(indexName: "hash", tableName: "license", unique: "true") {
			column(name: "hash")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-142") {
		createIndex(indexName: "hash", tableName: "payment_coupon", unique: "true") {
			column(name: "hash")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-143") {
		createIndex(indexName: "idx_pro_code", tableName: "problem", unique: "true") {
			column(name: "code")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-144") {
		createIndex(indexName: "idx_pro_name", tableName: "problem", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-145") {
		createIndex(indexName: "FK855086D83FE23189", tableName: "problem_lesson", unique: "false") {
			column(name: "problem_lessons_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-146") {
		createIndex(indexName: "FK855086D8C061C9AF", tableName: "problem_lesson", unique: "false") {
			column(name: "lesson_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-147") {
		createIndex(indexName: "FK9382B2C46598D505", tableName: "problem_topics", unique: "false") {
			column(name: "problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-148") {
		createIndex(indexName: "FK9382B2C48ACEF9C5", tableName: "problem_topics", unique: "false") {
			column(name: "topic_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-149") {
		createIndex(indexName: "FKED8E89A91B247856", tableName: "profile", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-150") {
		createIndex(indexName: "FKED8E89A9E41984E5", tableName: "profile", unique: "false") {
			column(name: "institution_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-151") {
		createIndex(indexName: "hash", tableName: "profile", unique: "true") {
			column(name: "hash")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-152") {
		createIndex(indexName: "user_id", tableName: "profile", unique: "true") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-153") {
		createIndex(indexName: "FK2CBC253F858C1A45", tableName: "questionnaire_statistics", unique: "false") {
			column(name: "questionnaire_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-154") {
		createIndex(indexName: "FK2CBC253FDAC7E480", tableName: "questionnaire_statistics", unique: "false") {
			column(name: "group_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-155") {
		createIndex(indexName: "FK4A91AD3181AB9C88", tableName: "questionnaire_user_penalty", unique: "false") {
			column(name: "questionnaire_problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-156") {
		createIndex(indexName: "FK4A91AD31A576EF61", tableName: "questionnaire_user_penalty", unique: "false") {
			column(name: "questionnaire_user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-157") {
		createIndex(indexName: "FK3243ED6598D505", tableName: "reference_solution", unique: "false") {
			column(name: "problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-158") {
		createIndex(indexName: "FK3243ED8FB80981", tableName: "reference_solution", unique: "false") {
			column(name: "problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-159") {
		createIndex(indexName: "FK3243EDBAE1DAF1", tableName: "reference_solution", unique: "false") {
			column(name: "user_suggest_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-160") {
		createIndex(indexName: "FK3243EDDA05E06D", tableName: "reference_solution", unique: "false") {
			column(name: "user_suggest_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-161") {
		createIndex(indexName: "FK3243EDE4E9AC6F", tableName: "reference_solution", unique: "false") {
			column(name: "language_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-162") {
		createIndex(indexName: "FK3243EDF0D7F776", tableName: "reference_solution", unique: "false") {
			column(name: "user_approved_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-163") {
		createIndex(indexName: "FK3243EDFEB10773", tableName: "reference_solution", unique: "false") {
			column(name: "language_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-164") {
		createIndex(indexName: "FK3243EDFFBFCF2", tableName: "reference_solution", unique: "false") {
			column(name: "user_approved_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-165") {
		createIndex(indexName: "name", tableName: "shiro_role", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-166") {
		createIndex(indexName: "FK389B46C96BBE4828", tableName: "shiro_role_permissions", unique: "false") {
			column(name: "shiro_role_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-167") {
		createIndex(indexName: "FK389B46C98AE24DA4", tableName: "shiro_role_permissions", unique: "false") {
			column(name: "shiro_role_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-168") {
		createIndex(indexName: "cpf", tableName: "shiro_user", unique: "true") {
			column(name: "cpf")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-169") {
		createIndex(indexName: "username", tableName: "shiro_user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-170") {
		createIndex(indexName: "FKBA2210578AE24DA4", tableName: "shiro_user_roles", unique: "false") {
			column(name: "shiro_role_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-171") {
		createIndex(indexName: "idx_submission_cache_problem_name", tableName: "submission", unique: "false") {
			column(name: "cache_problem_name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-172") {
		createIndex(indexName: "idx_submission_cache_user_email", tableName: "submission", unique: "false") {
			column(name: "cache_user_email")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-173") {
		createIndex(indexName: "idx_submission_cache_user_name", tableName: "submission", unique: "false") {
			column(name: "cache_user_name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-174") {
		createIndex(indexName: "idx_submission_cache_user_username", tableName: "submission", unique: "false") {
			column(name: "cache_user_username")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-175") {
		createIndex(indexName: "idx_submission_date", tableName: "submission", unique: "false") {
			column(name: "submission_date")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-176") {
		createIndex(indexName: "FK1A7C7BB36598D505", tableName: "teaching_resources", unique: "false") {
			column(name: "problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-177") {
		createIndex(indexName: "FK1A7C7BB3C061C9AF", tableName: "teaching_resources", unique: "false") {
			column(name: "lesson_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-178") {
		createIndex(indexName: "FK1A7C7BB3D11A4EC5", tableName: "teaching_resources", unique: "false") {
			column(name: "content_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-179") {
		createIndex(indexName: "FKB9A0FABD6598D505", tableName: "test_case", unique: "false") {
			column(name: "problem_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-180") {
		createIndex(indexName: "FK62220BB1B247856", tableName: "top_coder", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-181") {
		createIndex(indexName: "FK62220BB3A487DD2", tableName: "top_coder", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-182") {
		createIndex(indexName: "name", tableName: "topic", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-183") {
		createIndex(indexName: "FK143923EE1B247856", tableName: "user_link", unique: "false") {
			column(name: "user_id")
		}
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-68") {
		addForeignKeyConstraint(baseColumnNames: "cluster_groups_id", baseTableName: "cluster_cluster", baseTableSchemaName: "huxley-dev", constraintName: "FK1FB930B558F42106", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "cluster", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-69") {
		addForeignKeyConstraint(baseColumnNames: "cluster_id", baseTableName: "cluster_cluster", baseTableSchemaName: "huxley-dev", constraintName: "FK1FB930B5CAEBD325", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "cluster", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-70") {
		addForeignKeyConstraint(baseColumnNames: "cluster_id", baseTableName: "cluster_users", baseTableSchemaName: "huxley-dev", constraintName: "FKA7431F83CAEBD325", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "cluster", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-71") {
		addForeignKeyConstraint(baseColumnNames: "shiro_user_id", baseTableName: "cluster_users", baseTableSchemaName: "huxley-dev", constraintName: "FKA7431F8310E90C08", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-72") {
		addForeignKeyConstraint(baseColumnNames: "course_plan_questionnaire_id", baseTableName: "course_plan_questionnaire", baseTableSchemaName: "huxley-dev", constraintName: "FK421531B12925F0C2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "course_plan", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-73") {
		addForeignKeyConstraint(baseColumnNames: "questionnaire_id", baseTableName: "course_plan_questionnaire", baseTableSchemaName: "huxley-dev", constraintName: "FK421531B1858C1A45", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "questionnaire", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-74") {
		addForeignKeyConstraint(baseColumnNames: "payment_coupon_id", baseTableName: "payment", baseTableSchemaName: "huxley-dev", constraintName: "FKD11C3206C2174612", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "payment_coupon", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-75") {
		addForeignKeyConstraint(baseColumnNames: "submission1_id", baseTableName: "plagium", baseTableSchemaName: "huxley-dev", constraintName: "plagium_ibfk_1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "submission", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-76") {
		addForeignKeyConstraint(baseColumnNames: "submission2_id", baseTableName: "plagium", baseTableSchemaName: "huxley-dev", constraintName: "plagium_ibfk_2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "submission", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-77") {
		addForeignKeyConstraint(baseColumnNames: "fragment_id", baseTableName: "plagium_fragment", baseTableSchemaName: "huxley-dev", constraintName: "FKFED2AC3090970473", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "fragment", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-78") {
		addForeignKeyConstraint(baseColumnNames: "plagium_fragments_id", baseTableName: "plagium_fragment", baseTableSchemaName: "huxley-dev", constraintName: "FKFED2AC3055C0FF1D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "plagium", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-79") {
		addForeignKeyConstraint(baseColumnNames: "fastest_submision_id", baseTableName: "problem", baseTableSchemaName: "huxley-dev", constraintName: "problem_ibfk_3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "submission", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-80") {
		addForeignKeyConstraint(baseColumnNames: "user_approved_id", baseTableName: "problem", baseTableSchemaName: "huxley-dev", constraintName: "problem_ibfk_2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-81") {
		addForeignKeyConstraint(baseColumnNames: "user_suggest_id", baseTableName: "problem", baseTableSchemaName: "huxley-dev", constraintName: "problem_ibfk_1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-82") {
		addForeignKeyConstraint(baseColumnNames: "cluster_id", baseTableName: "questionnaire_cluster", baseTableSchemaName: "huxley-dev", constraintName: "FK7421735ECAEBD325", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "cluster", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-83") {
		addForeignKeyConstraint(baseColumnNames: "questionnaire_groups_id", baseTableName: "questionnaire_cluster", baseTableSchemaName: "huxley-dev", constraintName: "FK7421735E8215ACD8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "questionnaire", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-84") {
		addForeignKeyConstraint(baseColumnNames: "problem_id", baseTableName: "questionnaire_problem", baseTableSchemaName: "huxley-dev", constraintName: "FK2DB324036598D505", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "problem", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-85") {
		addForeignKeyConstraint(baseColumnNames: "questionnaire_id", baseTableName: "questionnaire_problem", baseTableSchemaName: "huxley-dev", constraintName: "FK2DB32403858C1A45", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "questionnaire", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-86") {
		addForeignKeyConstraint(baseColumnNames: "questionnaire_id", baseTableName: "questionnaire_shiro_user", baseTableSchemaName: "huxley-dev", constraintName: "FK17E7B975858C1A45", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "questionnaire", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-87") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "questionnaire_shiro_user", baseTableSchemaName: "huxley-dev", constraintName: "FK17E7B9751B247856", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-88") {
		addForeignKeyConstraint(baseColumnNames: "shiro_user_id", baseTableName: "shiro_user_permissions", baseTableSchemaName: "huxley-dev", constraintName: "FK34555A9E10E90C08", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-89") {
		addForeignKeyConstraint(baseColumnNames: "questionnaire_id", baseTableName: "shiro_user_questionnaire", baseTableSchemaName: "huxley-dev", constraintName: "FK8D22553D858C1A45", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "questionnaire", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-90") {
		addForeignKeyConstraint(baseColumnNames: "shiro_user_id", baseTableName: "shiro_user_questionnaire", baseTableSchemaName: "huxley-dev", constraintName: "FK8D22553D10E90C08", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-91") {
		addForeignKeyConstraint(baseColumnNames: "shiro_user_id", baseTableName: "shiro_user_roles", baseTableSchemaName: "huxley-dev", constraintName: "FKBA22105710E90C08", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-92") {
		addForeignKeyConstraint(baseColumnNames: "language_id", baseTableName: "submission", baseTableSchemaName: "huxley-dev", constraintName: "submission_ibfk_3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "language", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-93") {
		addForeignKeyConstraint(baseColumnNames: "problem_id", baseTableName: "submission", baseTableSchemaName: "huxley-dev", constraintName: "submission_ibfk_1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "problem", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-94") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "submission", baseTableSchemaName: "huxley-dev", constraintName: "submission_ibfk_2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-95") {
		addForeignKeyConstraint(baseColumnNames: "forum_id", baseTableName: "submission_comment", baseTableSchemaName: "huxley-dev", constraintName: "submission_comment_ibfk_2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "forum_submission", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-96") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "submission_comment", baseTableSchemaName: "huxley-dev", constraintName: "submission_comment_ibfk_1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-97") {
		addForeignKeyConstraint(baseColumnNames: "problem_id", baseTableName: "topic_problems", baseTableSchemaName: "huxley-dev", constraintName: "FK74AC5AC46598D505", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "problem", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-98") {
		addForeignKeyConstraint(baseColumnNames: "topic_id", baseTableName: "topic_problems", baseTableSchemaName: "huxley-dev", constraintName: "FK74AC5AC452E91D41", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "topic", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-99") {
		addForeignKeyConstraint(baseColumnNames: "problem_id", baseTableName: "user_problem", baseTableSchemaName: "huxley-dev", constraintName: "user_problem_ibfk_1", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "problem", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}

	changeSet(author: "marcio (generated)", id: "1415817073075-100") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_problem", baseTableSchemaName: "huxley-dev", constraintName: "user_problem_ibfk_2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "shiro_user", referencedTableSchemaName: "huxley-dev", referencesUniqueColumn: "false")
	}
}
