databaseChangeLog = {
    changeSet(author: "Marcio Augusto Guimarães", id: "alterStatusDataType") {
        modifyDataType(tableName: "problem", columnName: "status", newDataType: "INT")

        update(tableName: "problem") {
            column(name: "status", value: "0")
            where("status = 3")
        }

        update(tableName: "problem") {
            column(name: "status", value: "3")
            where("status = 2")
        }

        update(tableName: "problem") {
            column(name: "status", value: "2")
            where("status = 1")
        }

        update(tableName: "problem") {
            column(name: "status", value: "1")
            where("status = 3")
        }
    }
}