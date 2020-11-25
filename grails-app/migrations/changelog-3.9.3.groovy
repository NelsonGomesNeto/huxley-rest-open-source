databaseChangeLog = {
    changeSet(author: "Marcio Augusto Guimarães", id: "createUserGroupTable") {
        createTable(tableName: "user_group") {
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

        addPrimaryKey(
                tableName: "user_group",
                constraintName: "PK_user_group",
                columnNames: "user_id, group_id"
        )

        createIndex(indexName: "FK_user_group_group_id", tableName: "user_group") {
            column(name: "group_id")
        }

        addForeignKeyConstraint(
                constraintName: "user_group_ibfk_2",
                baseTableName: "user_group",
                baseColumnNames: "user_id",
                referencedTableName: "user",
                referencedColumnNames: "id"
        )

        addForeignKeyConstraint(
                constraintName: "user_group_ibfk_1",
                baseTableName: "user_group",
                baseColumnNames: "group_id",
                referencedTableName: "cluster",
                referencedColumnNames: "id"
        )
    }

    changeSet(author: "Marcio Augusto Guimarães", id: "populateUserGroupTable") {
        sql("INSERT INTO `user_group` (`version`, `user_id`, `group_id`, `role`, `enabled`) (SELECT 0, `user_id`, `group_id`, 0, CASE WHEN `status_user` = 0 THEN 1 ELSE 0 END FROM `cluster_permissions` WHERE `permission` = 0) ON DUPLICATE KEY UPDATE `role` = 0;")
        sql("INSERT INTO `user_group` (`version`, `user_id`, `group_id`, `role`, `enabled`) (SELECT 0, `user_id`, `group_id`, 2, CASE WHEN `status_user` = 0 THEN 1 ELSE 0 END FROM `cluster_permissions` WHERE `permission` = 30) ON DUPLICATE KEY UPDATE `role` = 2;")
    }
}