package com.hobeen.apiserver.config.filter

import com.hobeen.apiserver.config.SecurityProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1)
class OriginFilter(
    private val securityProperties: SecurityProperties
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val canPass = isFromCloudFront(request)

        if(!canPass) {
            response.sendError(403)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun isFromCloudFront(request: HttpServletRequest): Boolean {
        val header = request.getHeader("X-From-CloudFront")
        return securityProperties.cloudfrontSecret == header

    }
}