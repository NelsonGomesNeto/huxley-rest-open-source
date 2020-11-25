package com.thehuxley

import com.thehuxley.predictor.ClusteringPredictor
import com.thehuxley.predictor.FailingPredictable
import com.thehuxley.predictor.Parameter
import com.thehuxley.predictor.Student
import grails.converters.JSON
import grails.transaction.Transactional
import net.coobird.thumbnailator.Thumbnails
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.ObjectNotFoundException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.springframework.security.crypto.codec.Hex
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.security.MessageDigest

class UserService {

	def redisService
	def cacheService
	def dataService
	def grailsApplication

	def final EXPIRE_CACHE = 60 * 60 * 24 * 7


	def get(User user) {
		try {
			redisService.memoize(cacheService.generateKey(User, user), EXPIRE_CACHE) {
				(user as JSON) as String
			}
		} catch (ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def getData(User user) {
		dataService.getData([user: user])
	}

	def getFullUser(User user) {
		try {
			redisService.memoize(cacheService.generateKey(User, user, 'full'), EXPIRE_CACHE) {
				JSON.use("private") {
					(user as JSON) as String
				}
			}
		} catch (ObjectNotFoundException e) {
			e.finalize()
		}
	}

	def uploadAvatar(CommonsMultipartFile file) {
		String path = grailsApplication.config.huxleyFileSystem.profile.images.dir + System.getProperty("file.separator")

		File dir = new File(path)
		dir.mkdirs()

		def originalFilename = file.originalFilename
		def index = originalFilename.lastIndexOf('.')
		def extension = ""
		if ((index > 0) && (originalFilename.size() > index)) {
			extension = originalFilename.substring(index - 1)
		}

		def filename = new String(Hex.encode(MessageDigest.getInstance("SHA1").digest(file.bytes))) + extension
		def destFile = new File(dir, filename)

		file.transferTo(destFile)

		return destFile
	}

	def crop(User user, String filename, Integer x, Integer y, Integer width, Integer height) {
		String path = grailsApplication.config.huxleyFileSystem.profile.images.dir + System.getProperty("file.separator")

		def file = new File(path, filename)
		BufferedImage image = ImageIO.read(file).getSubimage(x, y, width, height)

		ByteArrayOutputStream baos = new ByteArrayOutputStream()
		ImageIO.write(image, "png", baos)
		baos.flush()

		def newFilename = new String(Hex.encode(MessageDigest.getInstance("SHA1").digest(baos.toByteArray()))) + ".png"

		ImageIO.write(image, "png", new File(path, newFilename))

		user.avatar = newFilename
		user.save(flush: true)
		cacheService.expireCache(User, user)
		get(user)
	}

	def getAvatar(String key, Integer width = 0, Integer height = 0) {
		try {

			String path = grailsApplication.config.huxleyFileSystem.profile.images.dir + System.getProperty("file.separator")
			String temp = path + System.getProperty("file.separator") + "tmp" + System.getProperty("file.separator")

			def originalFile = new File(path, key)

			BufferedImage avatar = ImageIO.read(originalFile)
			def resizedFile = new File(temp, originalFile.name)
			resizedFile.mkdirs()

			if ((width > 0) && !(height > 0)) {
				height = (3 / 4) * width
			}

			if ((height > 0) && !(width > 0)) {
				width = (4 / 3) * height
			}

			if (width > 0 && height > 0) {
				if (height == width) {
					def min = Math.min(avatar.width, avatar.height)
					avatar = avatar.getSubimage(
							(avatar.width > avatar.height ? ((avatar.width - avatar.height) / 2).abs() : 0) as Integer,
							0,
							min,
							min
					)
				}
				ImageIO.write(resizeImage(avatar, width, height), "png", resizedFile)

				return resizedFile
			}


			ImageIO.write(avatar, "png", resizedFile)

			return resizedFile
		} catch (Exception e) {
			e.finalize()
		}
	}

	def resizeImage(BufferedImage originalImage, int width, int height) {
		return Thumbnails.of(originalImage).forceSize(width, height).asBufferedImage()
	}

	def save(User user) {
		try {
			cacheService.expireCache(User, user)

			if (user.getPersistentValue("username")) {
				AccessToken.findAllByUsername(user.getPersistentValue("username")).each {
					it.username = user.username
					it.save()
				}
			}

			user.save(flush: true)

			if (user.getAuthorities().empty) {
				new UserRole(user: user, role: Role.findByAuthority("ROLE_STUDENT")).save(flush: true)
			}

			getFullUser(user)
		} catch (Exception e) {
			e.finalize()
		}
	}

	def generateRecoveryKey(User user) {
		new PendencyKey(type: PendencyKey.Type.CHANGE_PASSWORD, entity: user.id).save(flush: true)
	}

	def list(Map params) {
		redisService.memoizeHash(cacheService.generateKey(User, params), EXPIRE_CACHE) {
			def resultList = User.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

			["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String]
		}
	}

	def findAllInTopCoderWithFocus(User user, Integer max) {
		redisService.memoizeHash(cacheService.generateKey(User, user, "topcoder"), EXPIRE_CACHE) {
			def resultList = TopCoder.createCriteria().list() {
				order("points", "desc")
			}

			def index = resultList.indexOf(resultList.find { it.user.id == user.id })
			def size = resultList.size()

			if (max < size) {

				int start = index - (max / 2)
				int end = index + (max / 2)

				if (start < 0) {
					start = 0
					end = max
				} else if (end > (resultList.size() - 1)) {
					start = (resultList.size() - 1) - max
					end = resultList.size() - 1
				}

				resultList = resultList.subList(start, end)
			}


			def json = new JSONArray()

			resultList.each {
				def jsonElement = JSON.parse((it.user as JSON) as String)
				jsonElement.put("points", it.points)
				jsonElement.put("position", it.position)
				json.add(jsonElement)
			}

			resultList ? ["searchResults": (json as JSON) as String, "total": max as String] :
					["searchResults": [] as String, "total": "0"]

		}
	}


	def findAllInTopCoder(Map params) {
		redisService.memoizeHash(cacheService.generateKey(User, params, "topcoder"), EXPIRE_CACHE) {
			def resultList = TopCoder.createCriteria().list([max: params.max, offset: params.offset]) {
				user {
					or {
						if (params.q) {
							like("name", "%$params.q%")
							like("email", "%$params.q%")
							like("username", "%$params.q%")
						}
					}
				}
				order("points", "desc")
			}

			def json = new JSONArray()

			resultList.each {
				def jsonElement = JSON.parse((it.user as JSON) as String)
				jsonElement.put("points", it.points)
				jsonElement.put("position", it.position)
				json.add(jsonElement)
			}

			resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	def findInTopCoder(User user) {
		redisService.memoizeHash(cacheService.generateKey(User, user, "topcoder"), EXPIRE_CACHE) {
			TopCoder topCoder = TopCoder.findByUser(user)

			if (topCoder) {
				def json = JSON.parse((topCoder.user as JSON) as String)
				json.put("points", topCoder.points)

				(json as JSON) as String
			}
		}
	}



	def findByGroup(User user, Group group) {
		redisService.memoize(cacheService.generateKey(User, user, [group]), EXPIRE_CACHE) {

			UserGroup userGroup = UserGroup.findByUserAndGroup(user, group)

			if (userGroup) {
				def json = JSON.parse((userGroup.user as JSON) as String)
				json.put("role", userGroup.role)

				(json as JSON) as String
			}
		}
	}

	def findAllByGroup(Group group, Map params) {
		redisService.memoizeHash(cacheService.generateKey(User, params, [group]), EXPIRE_CACHE) {

			params.role = params.role ?: null

			def resultList = UserGroup.createCriteria().list([max: params.max, offset: params.offset]) {
				eq("group", group)

				if (params.role) {
					eq("role", UserGroup.Role.valueOf((params.role as String).toUpperCase()))
				}

				user getCriteria(params)
			}

			def json = new JSONArray()

			resultList.each {
				def jsonElement = JSON.parse((it.user as JSON) as String)
				jsonElement.put("role", it.role)
				json.add(jsonElement)
			}

			resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	@Transactional
	def refreshRoles(User user) {

		Role ADMIN_INST = Role.findByAuthority("ROLE_ADMIN_INST")
		Role TEACHER = Role.findByAuthority("ROLE_TEACHER")
		Role TEACHER_ASSISTANT = Role.findByAuthority("ROLE_TEACHER_ASSISTANT")

		try {

			def userInstitutions = UserInstitution.findAllByUserAndRole(user, UserInstitution.Role.ADMIN_INST)
			def hasApproved = false

			userInstitutions.each {
				if (it.institution.status == Institution.Status.APPROVED) {
					hasApproved = true
				}
			}

			if (hasApproved) {
				if (!UserRole.findByUserAndRole(user, ADMIN_INST)) {
					new UserRole(user: user, role: ADMIN_INST).save(flush: true)
				}
			} else  {
				UserRole.findAllByUserAndRole(user, ADMIN_INST)*.delete()
			}


			if (!UserInstitution.findAllByUserAndRole(user, UserInstitution.Role.TEACHER).empty) {
				if (!UserRole.findByUserAndRole(user, TEACHER)) {
					new UserRole(user: user, role: TEACHER).save(flush: true)
				}
			} else  {
				UserRole.findAllByUserAndRole(user, TEACHER)*.delete()
			}

			if (!UserInstitution.findAllByUserAndRole(user, UserInstitution.Role.TEACHER_ASSISTANT).empty) {
				if (!UserRole.findByUserAndRole(user, TEACHER_ASSISTANT)) {
					new UserRole(user: user, role: TEACHER_ASSISTANT).save(flush: true)
				}
			} else {
				UserRole.findAllByUserAndRole(user, TEACHER_ASSISTANT)*.delete()
			}

			cacheService.expireCache(cacheService.generateKey(User, user, 'full'))

			return true
		} catch (Exception e) {
			e.printStackTrace()
			return false
		}
	}

	def findAllByGroupAndRole(Group group, UserGroup.Role role, Map params) {
		redisService.memoizeHash(cacheService.generateKey(User, params, [group, role])) {
			def resultList = UserGroup.createCriteria().list(params) {
				eq("group", group)
				eq("role", role)
			}

			resultList ? ["searchResults": (resultList.user as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	def findByInstitution(User user, Institution institution) {
		redisService.memoize(cacheService.generateKey(User, user, [institution]), EXPIRE_CACHE) {
			UserInstitution userInstitution = UserInstitution.findByUserAndInstitution(user, institution)

			if (userInstitution) {
				def json = JSON.parse((userInstitution.user as JSON) as String)
				json.put("role", userInstitution.role)

				(json as JSON) as String
			}
		}
	}

	def findAllByInstitution(Institution institution, Map params) {

		params.role = params.role ?: null

		redisService.memoizeHash(cacheService.generateKey(User, params, [institution]), EXPIRE_CACHE) {
			def resultList = UserInstitution.createCriteria().list([max: params.max, offset: params.offset]) {
				eq("institution", institution)

				if (params.role) {

					def role = UserInstitution.Role.valueOf((params.role as String).toUpperCase())

					if (role == UserInstitution.Role.TEACHER) {
						or {
							eq("role", UserInstitution.Role.TEACHER)
							eq("role", UserInstitution.Role.ADMIN_INST)
						}
					} else {
						eq("role", role)
					}
				}

				user getCriteria(params)
			}

			def json = new JSONArray()

			resultList.each {
				def jsonElement = JSON.parse((it.user as JSON) as String)
				jsonElement.put("role", it.role)
				json.add(jsonElement)
			}

			resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": [] as String, "total": "0"]
		}
	}

	def findByQuestionnaire(User user, Questionnaire questionnaire) {
		def questionnaireUser = QuestionnaireUser.findByUserAndQuestionnaire(user, questionnaire)

		if (questionnaireUser) {
			def json = JSON.parse((questionnaireUser.user as JSON) as String)

			json.putAt("quiz", [score: questionnaireUser.score])

			(json as JSON) as String
		}
	}

	def findAllByQuestionnaire(Questionnaire questionnaire, Map params) {
		def resultList = QuestionnaireUser.createCriteria().list([max: params.max, offset: params.offset]) {
			eq("questionnaire", questionnaire)
			user getCriteria(params)

			order("score", params.order ?: "desc")
		}

		def json = new JSONArray()

		resultList.each {
			def jsonElement = JSON.parse((it.user as JSON) as String)

			jsonElement.putAt("quiz", [score: it.score])
			json.add(jsonElement)
		}

		resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
				["searchResults": [] as String, "total": "0"]
	}

	def failingStudents(Group group, Map params) {

		FailingPredictable predictor = new ClusteringPredictor()
		List<Student> students = new ArrayList<Student>()
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis()

		def users = UserGroup.findAllByGroupAndRole(group, UserGroup.Role.STUDENT).user

		Date endDate = params.endDate ? formatter.parseDateTime(params.endDate as String).toDate() : group.endDate
		Date startDate = params.startDate ? formatter.parseDateTime(params.startDate as String).toDate() : group.startDate

		if (users && !users.empty && (startDate < endDate)) {

			def submissions = Submission.createCriteria().list {
				inList('user', users)
				between('submissionDate', startDate, endDate)
			}

			def data = [:]

			users.each { User user ->
				data[user.id] = [
						submissionsCount: 0,
						correctSubmissionsCount: 0
				]
			}

			submissions.each { Submission submission ->
				data[submission.user.id]["submissionsCount"]++
				if (submission.evaluation == Submission.Evaluation.CORRECT) {
					data[submission.user.id]["correctSubmissionsCount"]++
				}
			}

			users.each { User user ->

				List<Parameter> parameters = []
				parameters.add(new Parameter("submissionsCount", data[user.id]["submissionsCount"] as Double))
				parameters.add(new Parameter("correctSubmissionsCount", data[user.id]["correctSubmissionsCount"] as Double))

				students.add(new Student(user.id, parameters))
			}

			params.inUsers = predictor.filterStudentsLikelyToFail(students).id

			def resultList = User.createCriteria().list([max: params.max, offset: params.offset], getCriteria(params))

			if (params.debug) {
				def json = new JSONArray()

				resultList.each { User user ->
					def jsonElement = JSON.parse((user as JSON) as String)
					jsonElement.put("debug", ["this": data[user.id], "others": data])
					json.add(jsonElement)
				}

				return resultList ? ["searchResults": (json as JSON) as String, "total": resultList.totalCount as String] :
						["searchResults": "[]", "total": "0"]
			}

			return resultList ? ["searchResults": (resultList as JSON) as String, "total": resultList.totalCount as String] :
					["searchResults": "[]", "total": "0"]
		}

		return ["searchResults": "[]", "total": "0"]
	}

	private Closure getCriteria(Map params) {
		return {
			and {
				or {
					if (params.q) {
						like("name", "%$params.q%")
						like("email", "%$params.q%")
						like("username", "%$params.q%")
					}
				}

				if (params.inUsers && !params.inUsers.empty)
					inList("id", params.inUsers)
			}


			if (params.sort != "score") {
				order(params.sort ?: "name", params.order ?: "asc")
			}
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
				"username",
				"accountLocked",
				"passwordExpired",
				"email",
				"name",
				"avatar",
				"dateCreated",
				"lastUpdated",
				"score"
		].contains(param)
	}

}
