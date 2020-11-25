package com.thehuxley

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder
import spock.lang.Specification

@Domain([
		User,
		Group,
		Institution,
		Role, UserRole,
		Profile,
		Questionnaire,
		Problem,
		Topic,
		Submission,
		Language,
		TestCase
])
@TestMixin(HibernateTestMixin)
class UserSpec extends Specification {

	void "save a valid user"() {
		given:
		def user = new User(
				name: "User Name 1",
				email: "username+1@thehuxley.com",
				username: "username1",
				password: "password"
		)

		when:
			user.save(failOnError: true)

		then:
			User.get(1L).username == "username1"
			User.count == 1
	}

	void "when we try to save a user with duplicate username, an exception must be throw"() {
		given:
		def user2 = new User(
				name: "User Name 2",
				email: "username+2@thehuxley.com",
				username: "username2",
				password: "password"
		)

		def user3 = new User(
				name: "User Name 3",
				email: "username+3@thehuxley.com",
				username: "username2",
				password: "password"
		)

		when:
		user2.save(failOnError: true)
		user3.save()

		then:
		thrown(DuplicateKeyException)
		User.count() == 2

	}

	void "when we try to save a user with duplicate email, an exception must be throw"() {
		given:
		def user4 = new User(
				name: "User Name 4",
				email: "username+4@thehuxley.com",
				username: "username4",
				password: "password"
		)

		def user5 = new User(
				name: "User Name 5",
				email: "username+4@thehuxley.com",
				username: "username5",
				password: "password"
		)

		when:
		user4.save(failOnError: true)
		user5.save()

		then:
		thrown(DuplicateKeyException)
		User.count() == 3

	}

	void "a user with blank name must fail in validation"() {
		given:
		def user = new User(
				name: " ",
				email: "username@thehuxley.com",
				username: "username",
				password: "password"
		)

		when:
		user.validate()

		then:
		user.hasErrors()
		user.errors.getFieldError("name").code == "nullable"
	}

	void "a user with blank username must fail in validation"() {
		given:
		def user = new User(
				name: "User Name",
				email: "username@thehuxley.com",
				username: " ",
				password: "password"
		)

		when:
		user.validate()

		then:
		user.hasErrors()
		user.errors.getFieldError("username").code == "nullable"
	}

	void "a user with blank password must fail in validation"() {
		given:
		def user = new User(
				name: "User Name",
				email: "username@thehuxley.com",
				username: "username",
				password: " "
		)

		when:
		user.validate()

		then:
		user.hasErrors()
		user.errors.getFieldError("password").code == "nullable"
	}

	void "a user with invalid email must fail in validation"() {
		given:
		def user = new User(
				name: "username",
				email: "usernamethehuxley.com",
				username: "username",
				password: "password"
		)

		when:
		user.validate()

		then:
		user.hasErrors()
		user.errors.getFieldError("email").code == "email.invalid"
	}

	void "beforeInsert must be called to encode password before save"() {
		given:
		def user6 = new User(
				name: "username6",
				email: "username+6@thehuxley.com",
				username: "username6",
				password: "password"
		)

		user6.springSecurityService = new SpringSecurityService()
		user6.springSecurityService.passwordEncoder = new MessageDigestPasswordEncoder("SHA-512")

		when:
		user6.save()

		then:
		user6.password == "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f" +
				"1326af5a2ea6d103fd07c95385ffab0cacbc86"
	}

	void "beforeUpdate must be called and if password is dirty, it must be encoded"() {
		given:
		def user6 = User.findByUsername("username6")
		user6.password = "new_password"

		user6.springSecurityService = new SpringSecurityService()
		user6.springSecurityService.passwordEncoder = new MessageDigestPasswordEncoder("SHA-512")

		when:
		user6.save(flush: true)

		then:
		user6.password != "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df" +
				"5f1326af5a2ea6d103fd07c95385ffab0cacbc86"
		user6.password == "8f9afb58880507ec875fd077ed76abd4eeeff81e615648d8b8b00376d076b6e5eac0f98420341f61b07cb13f" +
				"7540ae52c906014fd4412dcf56aeae8cfea2c005"
	}

	void "call method getAuthorities must returns an array of Role"() {
		given:
		def user7 = new User(
				name: "User Name 7",
				email: "username+7@thehuxley.com",
				username: "username7",
				password: "password"
		).save(failOnError: true, flush: true)

		new UserRole(
				user: user7,
				role: new Role(authority: "ADMIN").save(flush: true)).save(failOnError: true)
		new UserRole(
				user: user7,
				role: new Role(authority: "STUDENT").save(flush: true)).save(failOnError: true)
		new UserRole(
				user: user7,
				role: new Role(authority: "TEACHER").save(flush: true)).save(failOnError: true, flush: true)

		when:
		def authorities = user7.getAuthorities()

		then:
		authorities.size() == 3
		authorities.contains(Role.findByAuthority("ADMIN"))
		authorities.contains(Role.findByAuthority("STUDENT"))
		authorities.contains(Role.findByAuthority("TEACHER"))
	}

}