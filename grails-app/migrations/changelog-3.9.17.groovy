databaseChangeLog = {

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_access_token") {

		createTable(tableName: "access_token") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "authentication", type: "LONGBLOB") {
				constraints(nullable: "false")
			}

			column(name: "authentication_key", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "client_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "expiration", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "refresh_token", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "token_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "value", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}

		addUniqueConstraint(
				constraintName: "UK_access_token_authentication_key",
				columnNames: "authentication_key",
				tableName: "access_token"
		)

		addUniqueConstraint(
				constraintName: "UK_access_token_value",
				columnNames: "value",
				tableName: "access_token"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_access_token_additional_information") {

		createTable(tableName: "access_token_additional_information") {

			column(name: "additional_information", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "additional_information_idx", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "additional_information_elt", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_access_token_scope") {

		createTable(tableName: "access_token_scope") {

			column(name: "access_token_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "scope_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_access_token_scope_access_token_id", tableName: "access_token_scope") {
			column(name: "access_token_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_access_token_scope_access_token_id",
				baseTableName: "access_token_scope",
				baseColumnNames: "access_token_id",
				referencedTableName: "access_token",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_authorization_code") {

		createTable(tableName: "authorization_code") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "authentication", type: "LONGBLOB") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}

		createIndex(indexName: "IDX_authorization_code_code", tableName: "authorization_code") {
			column(name: "code")
		}

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client") {

		createTable(tableName: "client") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "access_token_validity_seconds", type: "INT") {
				constraints(nullable: "true")
			}

			column(name: "client_id", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "client_secret", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "refresh_token_validity_seconds", type: "INT") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_client_id", tableName: "client") {
			column(name: "client_id")
		}

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_additional_information") {

		createTable(tableName: "client_additional_information") {

			column(name: "additional_information", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "additional_information_idx", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

			column(name: "additional_information_elt", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_authorities") {

		createTable(tableName: "client_authorities") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "authorities_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_authorities_client_id", tableName: "client_authorities") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_authorities_client_id",
				baseTableName: "client_authorities",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_authorized_grant_types") {

		createTable(tableName: "client_authorized_grant_types") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "authorized_grant_types_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_authorized_grant_types_client_id", tableName: "client_authorized_grant_types") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_authorized_grant_types_client_id",
				baseTableName: "client_authorized_grant_types",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}


	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_auto_approve_scopes") {

		createTable(tableName: "client_auto_approve_scopes") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "auto_approve_scopes_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_auto_approve_scopes_client_id", tableName: "client_auto_approve_scopes") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_auto_approve_scopes_client_id",
				baseTableName: "client_auto_approve_scopes",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}


	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_redirect_uris") {

		createTable(tableName: "client_redirect_uris") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "redirect_uris_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_redirect_uris_client_id", tableName: "client_redirect_uris") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_redirect_uris_client_id",
				baseTableName: "client_redirect_uris",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}


	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_resource_ids") {

		createTable(tableName: "client_resource_ids") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "resource_ids_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}
		}

		createIndex(indexName: "IDX_client_resource_ids_client_id", tableName: "client_resource_ids") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_resource_ids_client_id",
				baseTableName: "client_resource_ids",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}


	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_client_scopes") {

		createTable(tableName: "client_scopes") {

			column(name: "client_id", type: "BIGINT") {
				constraints(nullable: "true")
			}

			column(name: "scopes_string", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}

		}

		createIndex(indexName: "IDX_client_scopes_client_id", tableName: "client_scopes") {
			column(name: "client_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_client_scopes_client_id",
				baseTableName: "client_scopes",
				baseColumnNames: "client_id",
				referencedTableName: "client",
				referencedColumnNames: "id"
		)

	}

	changeSet(author: "Marcio Augusto Guimarães", id: "create_table_refresh_token") {

		createTable(tableName: "refresh_token") {

			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "authentication", type: "LONGBLOB") {
				constraints(nullable: "false")
			}

			column(name: "expiration", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

		}

		addUniqueConstraint(
				constraintName: "UK_refresh_token_value",
				columnNames: "value",
				tableName: "refresh_token"
		)

	}


	changeSet(author: "Marcio Augusto Guimarães", id: "drop_table_authentication") {
		dropTable(tableName: "authentication")
	}

}
