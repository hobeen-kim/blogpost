package com.hobeen.apiserver.util.exception

class AuthorizationException(
    resource: String,
    id: String,
): BusinessException(
    "not permitted to $resource $id",
    401
) {
}