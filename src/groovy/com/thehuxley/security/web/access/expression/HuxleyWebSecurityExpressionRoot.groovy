package com.thehuxley.security.web.access.expression

import com.thehuxley.Group
import com.thehuxley.Institution
import com.thehuxley.SecurityService
import com.thehuxley.User
import org.springframework.security.core.Authentication
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot


class HuxleyWebSecurityExpressionRoot extends WebSecurityExpressionRoot {

	HuxleyWebSecurityExpressionRoot(Authentication authentication, FilterInvocation filterInvocation) {
		super(authentication, filterInvocation)
	}

	SecurityService securityService

	final boolean isAdmin() {
		securityService.isAdmin()
	}

	final boolean isInstitutionAdmin() {
		securityService.isInstitutionAdmin()
	}

	final boolean isTeacher() {
		securityService.isTeacher()
	}

	final boolean isTeacherAssistant() {
		securityService.isTeacherAssistant()
	}

	final boolean isStudent() {
		securityService.isStudent()
	}

	final boolean isInstitutionAdmin(long institutionId) {
		securityService.isInstitutionAdmin(institutionId)
	}

	final boolean isInstitutionAdmin(Institution institution) {
		securityService.isInstitutionAdmin(institution)
	}

	final boolean isInstitutionTeacher(long institutionId) {
		securityService.isInstitutionTeacher(institutionId)
	}

	final boolean isInstitutionTeacher(Institution institution) {
		securityService.isInstitutionTeacher(institution)
	}

	final boolean isInstitutionTeacherAssistant(long institutionId) {
		securityService.isInstitutionTeacherAssistant(institutionId)
	}

	final boolean isInstitutionTeacherAssistant(Institution institution) {
		securityService.isInstitutionTeacherAssistant(institution)
	}

	final boolean isInstitutionStudent(long institutionId) {
		securityService.isInstitutionStudent(institutionId)
	}

	final boolean isInstitutionStudent(Institution institution) {
		securityService.isInstitutionStudent(institution)
	}

	final boolean isInstitutionMember(long institutionId) {
		securityService.isInstitutionMember(institutionId)
	}

	final boolean isInstitutionMember(Institution institution) {
		securityService.isInstitutionMember(institution)
	}

	final boolean isGroupTeacher(long groupId) {
		securityService.securityServic(groupId)
	}

	final boolean isGroupTeacher(Group group) {
		securityService.isGroupTeacher(group)
	}

	final boolean isGroupTeacherAssistant(long groupId) {
		securityService.isGroupTeacherAssistant(groupId)
	}

	final boolean isGroupTeacherAssistant(Group group) {
		securityService.isGroupTeacherAssistant(group)
	}

	final boolean isGroupStudent(long groupId) {
		securityService.isGroupStudent(groupId)
	}

	final boolean isGroupStudent(Group group) {
		securityService.isGroupStudent(group)
	}

	final boolean isGroupMember(long groupId) {
		securityService.isGroupMember(groupId)
	}

	final boolean isGroupMember(Group group) {
		securityService.isGroupMember(group)
	}

	final boolean own(Long userId) {
		securityService.own(userId)
	}

	final boolean own(User user) {
		securityService.own(user)
	}

}
