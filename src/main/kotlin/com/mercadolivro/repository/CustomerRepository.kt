package com.mercadolivro.repository

import com.mercadolivro.model.CustomerModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<CustomerModel, Int> {

    fun findByName(pageable: Pageable, name: String): Page<CustomerModel>

    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): CustomerModel?

}