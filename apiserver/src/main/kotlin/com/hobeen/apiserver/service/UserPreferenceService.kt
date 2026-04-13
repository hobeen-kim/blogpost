package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.UserPreference
import com.hobeen.apiserver.repository.UserPreferenceRepository
import com.hobeen.apiserver.service.dto.UserPreferenceResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserPreferenceService(
    private val userPreferenceRepository: UserPreferenceRepository,
) {

    fun getPreference(userId: String): UserPreferenceResponse {
        val preference = userPreferenceRepository.findById(userId).orElseGet {
            userPreferenceRepository.save(UserPreference(userId = userId, emailSubscription = true))
        }
        return UserPreferenceResponse(emailSubscription = preference.emailSubscription)
    }

    fun updatePreference(userId: String, emailSubscription: Boolean) {
        val preference = userPreferenceRepository.findById(userId).orElse(null)
        if (preference != null) {
            preference.emailSubscription = emailSubscription
        } else {
            userPreferenceRepository.save(UserPreference(userId = userId, emailSubscription = emailSubscription))
        }
    }
}
