databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "addTopCoderColumnsToProfile") {
		addColumn(tableName: "profile") {
			column(name: "top_coder_score", type: "DOUBLE")
		}

		addColumn(tableName: "profile") {
			column(name: "top_coder_position", type: "BIGINT")
		}

		addColumn(tableName: "profile") {
			column(name: "last_login", type: "DATETIME")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "copyTopCoderDataFromUserToProfile") {
		sql("""
				UPDATE
					`profile` p
				LEFT JOIN
					`shiro_user` s
				ON
					p.`user_id` = s.`id`
				SET
					p.`top_coder_score` = s.`top_coder_score`,
					p.`top_coder_position` = s.`top_coder_position`,
					p.`last_login` = s.`last_login`
			""")
	}
}