package com.mercadolivro.service

import com.mercadolivro.enums.Errors
import com.mercadolivro.exception.AuthenticationException
import com.mercadolivro.repository.CustomerRepository
import com.mercadolivro.security.UserCustomerDetails
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsCustomerService(
    private val customerRepository: CustomerRepository
) : UserDetailsService {

    override fun loadUserByUsername(id: String): UserDetails {
        val customer = customerRepository.findById(id.toInt()).orElseThrow {
            AuthenticationException(Errors.ML201.message.format(id), Errors.ML201.code)
        }
        return UserCustomerDetails(customer)
    }

}