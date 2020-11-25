databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "cleanUserInstitution-3.9.12") {
		sql("DELETE FROM `user_institution` WHERE `user_id` IN (SELECT * FROM(SELECT `user_id` FROM `user_institution` GROUP BY `user_id` HAVING count(`user_id`) > 1) AS ui) AND `role` = 0")
		sql("DELETE FROM `user_institution` WHERE `user_id` IN (SELECT * FROM(SELECT `user_id` FROM `user_institution` GROUP BY `user_id` HAVING count(`user_id`) > 1) AS ui) AND `role` = 2")
	}
}