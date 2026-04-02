package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.UserPreferenceService
import com.hobeen.apiserver.service.dto.UserPreferenceRequest
import com.hobeen.apiserver.service.dto.UserPreferenceResponse
import com.hobeen.apiserver.util.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me")
class UserPreferenceController(
    private val userPreferenceService: UserPreferenceService,
) {

    @GetMapping("/preferences")
    fun getPreference(
        @AuthenticationPrincipal jwt: Jwt,
    ): ApiResponse<UserPreferenceResponse> {
        val response = userPreferenceService.getPreference(jwt.subject)
        return ApiResponse.of(response)
    }

    @PutMapping("/preferences")
    fun updatePreference(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: UserPreferenceRequest,
    ): ApiResponse<Boolean> {
        userPreferenceService.updatePreference(jwt.subject, request.emailSubscription)
        return ApiResponse.of(true)
    }
}
