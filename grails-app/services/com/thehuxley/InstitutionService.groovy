package com.thehuxley

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.springframework.web.multipart.commons.CommonsMultipartFile

class InstitutionService {

	def redisService
	def cacheService
	def userService
	def grailsApplication
	def imageService

	def final EXPIRE_CACHE = 60 * 60 * 24


	def get(Institution institution, User currentUser = null, Institution.Status status = null) {
		try {
			def institutionJSON = redisService.memoize(cacheService.generateKey(Institution, institution, [status])) {
				status ? (institution.status == status ? (institution as JSON) as String : null) : (institution as JSON) as String
			}

			if (currentUser && institutionJSON) {
				def json = JSON.parse(institutionJSON as String)

				if (currentUser) {
					json.putAt("role", UserInstitution.findByUserAndInstitution(currentUser, institution)?.role)
				}

				return (json as JSON) as String
			}

			return institutionJSON
		} catch (ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def addToInstitution(User user, Institution institution, UserInstitution.Role role = UserInstitution.Role.STUDENT) {
		cacheService.expireCache(User, user, [institution])

		if (!UserInstitution.findByUserAndInstitution(user, institution)) {
			new UserInstitution(institution: institution, user: user, role: role).save(flush: true)

			if (!user.institution) {
				user.institution = institution
				user.save(flush: true)
			}


			userService.refreshRoles(user)
			return true
		}

		return changeRole(user, institution, role)
	}

	def removeFromInstitution(User user, Institution institution) {
		cacheService.expireCache(User, user, [institution])

		def userInstitution = UserInstitution.findByUserAndInstitution(user, institution)

		if (userInstitution) {
			userInstitution.delete(flush: true)
			userService.refreshRoles(user)

			Group.findAllByInstitution(institution).each {
				UserGroup.findAllByUserAndGroup(user, it)*.delete(flush: true)
			}

			if (user.institution == institution) {
				user.institution = null
				user.save(flush: true)
			}

			return true
		}

		return false
	}

	def changeRole(User user, Institution institution, UserInstitution.Role role = UserInstitution.Role.STUDENT) {
		cacheService.expireCache(User, user, [institution])

		def userInstitution = UserInstitution.findByUserAndInstitution(user, institution)

		if (userInstitution) {
			userInstitution.role = role
			userInstitution.save(flush: true)
			userService.refreshRoles(user)

			if (role == UserInstitution.Role.STUDENT) {
				Group.findAllByInstitution(institution).each {
					UserGroup.findAllByUserAndGroup(user, it).each {
						it.role == UserGroup.Role.STUDENT
						it.save()
					}
				}
			}

			if (role == UserInstitution.Role.TEACHER_ASSISTANT) {
				Group.findAllByInstitution(institution).each {
					UserGroup.findAllByUserAndGroupAndRole(user, it, UserGroup.Role.TEACHER).each {
						it.role == UserGroup.Role.TEACHER_ASSISTANT
						it.save()
					}
				}
			}

			return true
		}

		return false
	}

	def normalizeInInstitution(User user, Institution institution, UserInstitution.Role role = UserInstitution.Role.STUDENT) {
		cacheService.expireCache(User, user, [institution])

		if (!UserInstitution.findByUserAndInstitution(user, institution)) {

			new UserInstitution(institution: institution, user: user, role: role).save(flush: true)

			if (!user.institution) {
				user.institution = institution
				user.save(flush: true)
			}


			userService.refreshRoles(user)
			return true
		}

		return changeRoleKeepGreatest(user, institution, role)
	}

	def changeRoleKeepGreatest(User user, Institution institution, UserInstitution.Role role = UserInstitution.Role.STUDENT) {
		cacheService.expireCache(User, user, [institution])

		def userInstitution = UserInstitution.findByUserAndInstitution(user, institution)

		if (userInstitution) {

			def newRole = userInstitution.role

			if (role == UserInstitution.Role.ADMIN_INST) {
				newRole = UserInstitution.Role.ADMIN_INST
			} else if (role == UserInstitution.Role.TEACHER && userInstitution.role != UserInstitution.Role.ADMIN_INST) {
				newRole = UserInstitution.Role.TEACHER
			} else if (role == UserInstitution.Role.TEACHER_ASSISTANT &&
					(userInstitution.role == UserInstitution.Role.STUDENT ||
							userInstitution.role == UserInstitution.Role.TEACHER_ASSISTANT)) {
				newRole = UserInstitution.Role.TEACHER_ASSISTANT
			}

			if (newRole != userInstitution.role) {
				userInstitution.role = newRole
				userInstitution.save(flush: true)
				userService.refreshRoles(user)
			}

			return true
		}

		return false
	}

	def uploadImage(CommonsMultipartFile file) {
		String path = grailsApplication.config.huxleyFileSystem.institution.images.dir + System.getProperty("file.separator")

		return imageService.uploadImage(path, file)
	}

	def cropImage(Institution institution, String filename, Integer x, Integer y, Integer width, Integer height) {
		String path = grailsApplication.config.huxleyFileSystem.institution.images.dir + System.getProperty("file.separator")

		institution.logo = imageService.crop(path, filename, x, y, width, height)
		institution.save(flush: true)

		cacheService.expireCache(Institution, institution)
		return get(institution)
	}

	def getImage(String key, Integer width = 0, Integer height = 0) {
		String path = grailsApplication.config.huxleyFileSystem.institution.images.dir + System.getProperty("file.separator")

		return imageService.getImage(path, key, width, height)
	}

	def changeStatus(Institution institution, Institution.Status status) {

		try{
			institution.status = status
			institution.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}

		def adminInst = UserInstitution.findByInstitutionAndRole(institution, UserInstitution.Role.ADMIN_INST).user

		if (status == Institution.Status.APPROVED) {
			if (adminInst) {
				save(institution, adminInst)
				changeRole(adminInst, institution, UserInstitution.Role.ADMIN_INST)
			}
		} else {
			if (adminInst) {
				if (!UserInstitution.findByUserAndRoleAndInstitutionNotEqual(adminInst, UserInstitution.Role.ADMIN_INST, institution)) {
					UserRole.findByUserAndRole(adminInst, Role.findByAuthority("ROLE_ADMIN_INST")).delete(flush: true)
				}
			}
		}
	}

	def save(Institution institution, User user = null) {
		try {
			cacheService.expireCache(Institution, institution)

			institution.save(flush: true)

			if (user) {
				new UserInstitution(user: user, institution: institution, role: UserInstitution.Role.ADMIN_INST).save()
			}

			get(institution, user)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	def list(Map params, Institution.Status status = null) {
		redisService.memoizeHash(cacheService.generateKey(Institution, params, [status]), EXPIRE_CACHE) {
			def resultList = Institution.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params, status))

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findByUser(Institution institution, User user, Institution.Status status = null) {
		redisService.memoize(cacheService.generateKey(Institution, institution, [user, status]), EXPIRE_CACHE) {
			def userInstitution = UserInstitution.findByInstitutionAndUser(institution, user)
			status ? (userInstitution?.institution?.status == status ? (userInstitution.institution as JSON) as String : null) : (institution as JSON) as String
		}
	}

	def findAllByUser(User user, Map params,  Institution.Status status = null) {
		redisService.memoizeHash(cacheService.generateKey(Institution, params, [user, status]), EXPIRE_CACHE) {
			def resultList = UserInstitution.createCriteria().list([max: params.max, offset: params.offset]) {
				eq("user", user)

				institution getCriteria(params, status)
			}.unique()

			resultList ? ["searchResults": (resultList.institution as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	Closure getCriteria(Map params, Institution.Status status) {
		return {
			and {
				or {
					like("name", "%$params.q%")
					like("acronym", "%$params.q%")
				}
				if (status) {
					eq("status", status)
				}
			}

			order(params.sort ?: "name", params.order ?: "asc")
		}
	}

	GrailsParameterMap normalize(GrailsParameterMap params) {
		params.max = Math.min(params.int("max", 0) ?: 10, 100)
		params.offset = params.int("offset", 0)
		params.q = params.q ?: ""

		return params
	}

	boolean isSortable(param) {
		[
				"id",
				"name",
				"phone",
				"photo",
				"status",
				"acronym"
		].contains(param)
	}

}
