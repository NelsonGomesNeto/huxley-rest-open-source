databaseChangeLog = {

	changeSet(author: "Rodrigo Paes", id: "renamequestionnairestatistics-3.9.8") {
		renameColumn(tableName: "questionnaire_statistics",  newColumnName:  "average_score", oldColumnName: "average_note", columnDataType : "double")

	}

	changeSet(author: "Rodrigo Paes", id: "questionnaire_user-3.9.8") {

		renameTable(newTableName: "questionnaire_user", oldTableName: "questionnaire_shiro_user")

		addColumn(tableName: "questionnaire_user") {
			column(name: "submissions_count", type: "int", defaultValue="0") {
				constraints(nullable : "false")
			}
			column(name: "problems_tried", type: "int", defaultValue="0"){
				constraints(nullable : "false")
			}
			column(name: "problems_correct", type: "int", defaultValue="0"){
				constraints(nullable : "false")
			}
		}

		dropTable(tableName:"questionnaire_statistics")
	}

	changeSet(author: "Rodrigo Paes", id: "change_compiler_name-3.9.8") {
		dropUniqueConstraint(tableName: "language", constraintName: "name")
		update(tableName: "language") {
			column(name: "compiler", value: "gcc 4.8.2")
			column(name: "compile_params", value: "-lm")
			where("id = 1")
		}

		update(tableName: "language") {
			column(name: "compiler", value: "python 2.7")
			where("id = 2")
		}

		update(tableName: "language") {
			column(name: "compiler", value: "fpc 2.6.4")
			where("id = 3")
		}

		update(tableName: "language") {
			column(name: "name", value: "C++")
			column(name: "compiler", value: "g++ 4.8.2")
			column(name: "compile_params", value: "-lm")
			where("id = 4")
		}

		update(tableName: "language") {
			column(name: "name", value: "Python")
			column(name: "compiler", value: "python 3.4.2")
			where("id = 5")
		}

		update(tableName: "language") {
			column(name: "compiler", value: "javac 1.8.0_31")
			where("id = 6")
		}

		update(tableName: "language") {
			column(name: "compiler", value: "octave 3.8.2")
			where("id = 7")
		}

	}

	changeSet(author: "Rodrigo Paes", id: "submission-changes-3.9.8") {
		dropColumn(tableName:"submission",  columnName : "detailed_log")

		dropNotNullConstraint(tableName : "submission", columnName:"diff_file", columnDataType:"varchar(255)")
		dropNotNullConstraint(tableName : "submission", columnName:"output", columnDataType:"varchar(255)")
		dropNotNullConstraint(tableName : "submission", columnName:"error_msg", columnDataType:"longtext")
		dropNotNullConstraint(tableName : "submission", columnName:"comment", columnDataType:"longtext")

		modifyDataType(tableName : "submission",columnName:"diff_file", newDataType: "text" )
		modifyDataType(tableName : "submission",columnName:"output", newDataType: "text" )
		modifyDataType(tableName : "submission",columnName:"error_msg", newDataType: "text" )
		modifyDataType(tableName : "submission",columnName:"comment", newDataType: "text" )

	}

}