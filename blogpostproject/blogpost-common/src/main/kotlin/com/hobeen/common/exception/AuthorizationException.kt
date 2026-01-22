package com.hobeen.common.exception

class AuthorizationException(
    resource: String,
    id: String,
): BusinessException(
    "not permitted to $resource $id",
    401
) {
}