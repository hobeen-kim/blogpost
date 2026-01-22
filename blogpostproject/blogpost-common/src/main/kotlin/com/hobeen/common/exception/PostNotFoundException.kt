package com.hobeen.common.exception

class PostNotFoundException(): BusinessException(
    "POST NOT FOUND",
    404
)