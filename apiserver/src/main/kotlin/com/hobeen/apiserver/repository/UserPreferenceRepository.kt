package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.UserPreference
import org.springframework.data.jpa.repository.JpaRepository

interface UserPreferenceRepository : JpaRepository<UserPreference, String>
