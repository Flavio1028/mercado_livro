package com.mercadolivro.security

import com.mercadolivro.enums.Errors
import com.mercadolivro.exception.AuthenticationException
import com.mercadolivro.service.UserDetailsCustomerService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val userDetails: UserDetailsCustomerService,
    private val jwtUtil: JwtUtil
) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorization = request.getHeader("Authorization")

        if (authorization != null && authorization.startsWith("Bearer ")) {
            val auth = getAuthentication(authorization.split(" ")[1])
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        if (!jwtUtil.isValidToken(token)) {
            throw AuthenticationException(Errors.ML002.message, Errors.ML002.code)
        }
        val subject = jwtUtil.getSubject(token)
        val customer = userDetails.loadUserByUsername(subject)
        return UsernamePasswordAuthenticationToken(customer, null, customer.authorities)
    }

}