databaseChangeLog = {
	changeSet(author: "Marcio Augusto Guimarães", id: "createUserTable") {
		createTable(tableName: "user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "BIT") {
				constraints(nullable: "false")
			}
		}

		addUniqueConstraint(
				constraintName: "UK_user_username",
				columnNames: "username",
				tableName: "user"
		)

		addUniqueConstraint(
				constraintName: "UK_user_email",
				columnNames: "email",
				tableName: "user"
		)

		modifySql() {
			replace(replace: "tinyint", with: "bit")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "createRoleTable") {

		createTable(tableName: "role") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}

		addUniqueConstraint(
				constraintName: "UK_role_authority",
				columnNames: "authority",
				tableName: "role"
		)
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "createUserRoleTable") {
		createTable(tableName: "user_role") {
			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}

		addPrimaryKey(
				constraintName: "PK_user_role_id",
				columnNames: "user_id, role_id",
				tableName: "user_role"
		)

		createIndex(indexName: "IDX_user_role_user_id", tableName: "user_role") {
			column(name: "user_id")
		}

		addForeignKeyConstraint(
				constraintName: "FK_user_role_user_id",
				baseTableName: "user_role",
				baseColumnNames: "user_id",
				referencedTableName: "user",
				referencedColumnNames: "id"
		)

		addForeignKeyConstraint(
				constraintName: "FK_user_role_role_id",
				baseTableName: "user_role",
				baseColumnNames: "role_id",
				referencedTableName: "role",
				referencedColumnNames: "id"
		)
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias@gmail.com")
			where("id = 1")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+1@gmail.com")
			where("id = 2")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "thiegodsb+1@gmail.com")
			where("id = 6")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "brunogarcia016+1@gmail.com")
			where("id = 8")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "laysa.s.paula+1@gmail.com")
			where("id = 10")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "dudu.cedrim+1@gmail.com")
			where("id = 17")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "danielarcanjo1+1@gmail.com")
			where("id = 18")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "Matheus.ial91+1@gmail.com")
			where("id = 23")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "Thiagoemanuel.1991+1@gmail.com")
			where("id = 24")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "kyokeneth+1@gmail.com")
			where("id = 26")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "andersongustavo.ss+1@gmail.com")
			where("id = 27")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rafaelrocha.ufal+1@gmail.com")
			where("id = 50")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "marcioaugustosg@gmail.com")
			where("id = 51")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rodrigobpaes@gmail.com")
			where("id = 53")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "marcioaugustosg+1@gmail.com")
			where("id = 54")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diogofelipec+1@gmail.com")
			where("id = 57")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rafaelrocha.ufal@gmail.com")
			where("id = 61")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ailtoncruzs@gmail.com")
			where("id = 65")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+2@gmail.com")
			where("id = 69")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rodrigobpaes+1@gmail.com")
			where("id = 83")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+3@gmail.com")
			where("id = 86")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "hozano@gmail.com")
			where("id = 87")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diego.cedrim@gmail.com")
			where("id = 89")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero@gmail.com")
			where("id = 92")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero+1@gmail.com")
			where("id = 93")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rodrigopex@gmail.com")
			where("id = 94")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+4@gmail.com")
			where("id = 95")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diego.cedrim+1@gmail.com")
			where("id = 96")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+5@gmail.com")
			where("id = 98")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ailtoncruzs+1@gmail.com")
			where("id = 99")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "hozano+1@gmail.com")
			where("id = 100")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+6@gmail.com")
			where("id = 101")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+7@gmail.com")
			where("id = 102")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+8@gmail.com")
			where("id = 103")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+9@gmail.com")
			where("id = 104")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+10@gmail.com")
			where("id = 105")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+11@gmail.com")
			where("id = 106")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+12@gmail.com")
			where("id = 107")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+13@gmail.com")
			where("id = 108")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+14@gmail.com")
			where("id = 110")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+15@gmail.com")
			where("id = 111")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "hozano+2@gmail.com")
			where("id = 112")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "denysonsoares+1@gmail.com")
			where("id = 137")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "denysonsoares@gmail.com")
			where("id = 140")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "christhofe_lins+1@hotmail.com")
			where("id = 157")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ricardo.ufal2011+1@gmail.com")
			where("id = 168")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diogof.barros+1@hotmail.com")
			where("id = 183")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "mendoncasistemas+1@hotmail.com")
			where("id = 232")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "saaulol.b+1@gmail.com")
			where("id = 251")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "mendoncasistemas@hotmail.com")
			where("id = 260")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "tiagocedrim+1@gmail.com")
			where("id = 261")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diego.cedrim+2@gmail.com")
			where("id = 262")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "arthur.luiz+1@gmail.com")
			where("id = 263")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "arthur.luiz@gmail.com")
			where("id = 265")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "christhofe_lins@hotmail.com")
			where("id = 269")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ricardo.ufal2011@gmail.com")
			where("id = 272")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diego.cedrim+3@gmail.com")
			where("id = 284")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diego.cedrim+4@gmail.com")
			where("id = 289")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "andersongustavo.ss@gmail.com")
			where("id = 308")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "brunogarcia016@gmail.com")
			where("id = 309")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "dudu.cedrim@gmail.com")
			where("id = 312")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "kyokeneth@gmail.com")
			where("id = 316")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "laysa.s.paula@gmail.com")
			where("id = 317")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "matheus.ial91@gmail.com")
			where("id = 318")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "thiagoemanuel.1991@gmail.com")
			where("id = 323")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "thiegodsb@gmail.com")
			where("id = 324")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "danielarcanjo1@gmail.com")
			where("id = 325")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+16@gmail.com")
			where("id = 327")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+17@gmail.com")
			where("id = 328")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+18@gmail.com")
			where("id = 329")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "rodrigopex+1@gmail.com")
			where("id = 357")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "carlisson_couto+1@hotmail.com")
			where("id = 361")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "carlisson_couto@hotmail.com")
			where("id = 362")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "pedrohenrique-passos+1@hotmail.com")
			where("id = 400")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "dayseferreiradeamorim+1@gmail.com")
			where("id = 420")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "wanderson.sd6+1@gmail.com")
			where("id = 439")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ygorgf+1@gmail.com")
			where("id = 441")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "levii.lp+1@gmail.com")
			where("id = 443")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "ygorgf@gmail.com")
			where("id = 447")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "gleidenicacio13+1@hotmail.com")
			where("id = 451")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "guimaraes_thaina+1@hotmail.com")
			where("id = 452")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jfelipe.monteiro+1@gmail.com")
			where("id = 453")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "levii.lp@gmail.com")
			where("id = 457")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "dayseferreiradeamorim@gmail.com")
			where("id = 458")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "diogofelipec@gmail.com")
			where("id = 461")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "pedrohenrique-passos@hotmail.com")
			where("id = 469")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "clarissaborges_al+1@hotmail.com")
			where("id = 490")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "bsb+1@ic.ufal.br")
			where("id = 513")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "bsb@ic.ufal.br")
			where("id = 547")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jfelipe.monteiro@gmail.com")
			where("id = 564")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "saaulol.b+2@gmail.com")
			where("id = 565")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "gleidenicacio13@hotmail.com")
			where("id = 566")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "guimaraes_thaina@hotmail.com")
			where("id = 567")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jayane.vsf+1@gmail.com")
			where("id = 586")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "misael.0_0+1@hotmail.com")
			where("id = 610")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jayane.vsf@gmail.com")
			where("id = 612")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "misael.0_0@hotmail.com")
			where("id = 615")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "tiagocedrim@gmail.com")
			where("id = 616")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "wagnerviana00+1@hotmail.com")
			where("id = 625")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "cleidison.j.c+1@gmail.com")
			where("id = 628")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "wagnerviana00@hotmail.com")
			where("id = 629")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "cleidison.j.c@gmail.com")
			where("id = 632")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "clarissaborges_al@hotmail.com")
			where("id = 643")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "wanderson.sd6@gmail.com")
			where("id = 645")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jpvalerio+1@hotmail.com.br")
			where("id = 648")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "edubarros+1@communicatti.com")
			where("id = 654")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "gutobarros+1@communicatti.com")
			where("id = 655")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "edubarros@communicatti.com")
			where("id = 657")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "gutobarros@communicatti.com")
			where("id = 658")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "marcioasguimaraes@gmail.com")
			where("id = 681")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+19@gmail.com")
			where("id = 682")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "marcioasguimaraes+1@gmail.com")
			where("id = 683")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "marcioaugustosg+2@gmail.com")
			where("id = 688")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "sandeison_san+1@hotmail.com")
			where("id = 731")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "sandeison_san+2@hotmail.com")
			where("id = 740")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "sandeison_san@hotmail.com")
			where("id = 741")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "thallessmoreira23+1@hotmail.com")
			where("id = 745")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "thallessmoreira23@hotmail.com")
			where("id = 749")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "vitorbaandeira+1@hotmail.com")
			where("id = 803")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "vitorbaandeira@hotmail.com")
			where("id = 805")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "kessler.sccp+1@gmail.com")
			where("id = 811")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jessmartins+1@hotmail.com")
			where("id = 827")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "matheusmonteiro07+1@hotmail.com")
			where("id = 849")
		}

		update(tableName: "shiro_user", ) {
			column(name: "email", value: "julia_aa95+1@hotmail.com")
			where("id = 911")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "arnobiojroliveira+1@gmail.com")
			where("id = 1007")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "kessler.sccp@gmail.com")
			where("id = 1085")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jpvalerio@hotmail.com.br")
			where("id = 1218")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "matheusmonteiro07@hotmail.com")
			where("id = 1219")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jessmartins@hotmail.com")
			where("id = 1224")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "denner.ricardo+1@gmail.com")
			where("id = 1469")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "denner.ricardo@gmail.com")
			where("id = 1470")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "julia_aa95@hotmail.com")
			where("id = 1591")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jeisielisvi+1@hotmail.com")
			where("id = 1670")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jeisielisvi@hotmail.com")
			where("id = 1757")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "eleandro_campos.26+1@hotmail.com")
			where("id = 1840")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jair.legiao+1@gmail.com")
			where("id = 1869")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "laiscarolinie+1@hotmail.com")
			where("id = 1906")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "arnobiojroliveira@gmail.com")
			where("id = 1921")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "eleandro_campos.26@hotmail.com")
			where("id = 1946")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "tigerthought1980+1@hotmail.com")
			where("id = 1954")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "tigerthought1980@hotmail.com")
			where("id = 1955")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "laiscarolinie@hotmail.com")
			where("id = 2065")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "jair.legiao@gmail.com")
			where("id = 2073")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate2") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "edso+1@cin.ufpe.br")
			where("id = 4118")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "edso+2@cin.ufpe.br")
			where("id = 4119")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "edso+3@cin.ufpe.br")
			where("id = 4120")
		}

		update(tableName: "shiro_user") {
			column(name: "email", value: "edso+4@cin.ufpe.br")
			where("id = 4121")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate3") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "demetrios-.-+1@hotmail.com")
			where("id = 1037")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate4") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "romero.malaquias+20@hotmail.com")
			where("id = 3072")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate5") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "ant.ulisses+1@hotmail.com")
			where("id = 3266")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate6.1") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "oliviajeronimo91+1@gmail.com")
			where("id = 3626")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate7") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "Athaysantos+1@gmail.com")
			where("id = 3691")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeEmailDuplicate8") {
		update(tableName: "shiro_user") {
			column(name: "email", value: "lclc+1@cin.ufpe.br")
			where("id = 4099")
		}
	}


	changeSet(author: "Marcio Augusto Guimarães", id: "changeNullUsernames") {
		update(tableName: "shiro_user") {
			column(name: "username", valueComputed: "email")
			where("username IS NULL")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "changeNullPasswords") {
		update(tableName: "shiro_user") {
			column(name: "password_hash", value: "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413")
			where("password_hash IS NULL")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "copyFromShiroUserToUser") {
		sql("INSERT INTO user (id, version, account_expired, account_locked, enabled, password, password_expired, username, email, name) SELECT s.id, s.version, 0, 0, 1, s.password_hash, 0, s.username, s.email, s.name FROM shiro_user AS s")
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "populateRoleTable") {
		insert(tableName: "role") {
			column(name: "id", value: "1")
			column(name: "version", value: "1")
			column(name: "authority", value: "ROLE_ADMIN")
		}

		insert(tableName: "role") {
			column(name: "id", value: "2")
			column(name: "version", value: "1")
			column(name: "authority", value: "ROLE_STUDENT")
		}

		insert(tableName: "role") {
			column(name: "id", value: "3")
			column(name: "version", value: "1")
			column(name: "authority", value: "ROLE_TEACHER")
		}

		insert(tableName: "role") {
			column(name: "id", value: "4")
			column(name: "version", value: "1")
			column(name: "authority", value: "ROLE_TEACHER_ASSISTANT")
		}

		insert(tableName: "role") {
			column(name: "id", value: "5")
			column(name: "version", value: "1")
			column(name: "authority", value: "ROLE_ADMIN_INST")
		}
	}

	changeSet(author: "Marcio Augusto Guimarães", id: "populateUserRoleTable") {
		sql("INSERT INTO `user_role` (`user_id`, `role_id`) (SELECT `user_id`, `type_id` FROM `license` AS l) ON DUPLICATE KEY UPDATE `role_id` = l.`type_id`;")
	}

}
