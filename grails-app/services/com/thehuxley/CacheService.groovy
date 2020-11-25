package com.thehuxley

import org.hibernate.proxy.HibernateProxyHelper
import org.springframework.security.crypto.codec.Hex
import org.springframework.security.crypto.codec.Utf8

import java.security.MessageDigest

class CacheService {

	def redisService

	def expireCache(Class clazz, Object entity, List belongTo) {
		expireCache(generateKey(clazz, null, belongTo))

		if (entity) {
			expireCache(generateKey(clazz, entity, belongTo))
		}
	}

	def expireCache(Class clazz, Object entity = null) {
		expireCache(generateKey(clazz))

		if (entity) {
			expireCache(generateKey(clazz, entity))
		}
	}

	def expireCache(String keyPattern, exact = false) {
		try {
			redisService.deleteKeysWithPattern(keyPattern + (exact ? "" : "*"))
		} catch (Exception e) {
			e.finalize()
		}
	}

	def generateKey(Class clazz, Object entity, String suffix = null) {
		generateKey(clazz, entity, [], suffix)
	}

	def generateKey(Class clazz, List belongTo, String suffix = null) {
		generateKey(clazz, null, belongTo, suffix)
	}

	def generateKey(Class clazz, String suffix = null) {
		generateKey(clazz, null, [], suffix)
	}

	def generateKey(Class clazz, Object entity, List belongTo, String suffix = null) {

		def key = clazz.simpleName
		def end = null

		if (entity) {
			if (entity instanceof Map) {
				Map params = entity as Map
				def hash = ""

				MessageDigest messageDigest = MessageDigest.getInstance("SHA1")

				params.keySet().sort().each {
					hash += it + ":" + params.get(it) + ";"
				}
				byte[] digest = messageDigest.digest(Utf8.encode(hash));
				end = new String(Hex.encode(digest));

				key += ":list"
			} else if (entity instanceof Enum) {
				key += ":${entity.name()}"
			} else if (entity.hasProperty("id")) {
				key += ":${entity.id}"
			}
		} else {
			key += ":list"
		}

		belongTo?.each {  key += ":" + (it ? generateKey(HibernateProxyHelper.getClassWithoutInitializingProxy(it), it, []) : "null")  }

		key += (suffix ? ":$suffix" : "")
		key += (end ? ":$end" : "")

		return key.toLowerCase().replace("_", "-")
	}
}
