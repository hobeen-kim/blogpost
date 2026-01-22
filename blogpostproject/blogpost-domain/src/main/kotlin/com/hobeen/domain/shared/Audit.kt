package com.hobeen.domain.shared

import java.time.Instant

class Audit (
    val createdAt: Instant,
    val modifiedAt: Instant,
)