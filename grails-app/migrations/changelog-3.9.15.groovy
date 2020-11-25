databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "newColumnAcronymFromInstitutionTable2") {
		addColumn (tableName: "institution") {
			column(name: "acronym", type: "VARCHAR(20)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "renameColumnPhotoFromInstituionTable") {
		renameColumn(columnDataType: "VARCHAR(255)", oldColumnName: "photo", newColumnName: "logo", tableName: "institution")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "cleanInstitutionTable") {
		dropColumn(tableName: "institution",  columnName: "address_id")
	}

}