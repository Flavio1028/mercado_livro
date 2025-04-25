package com.mercadolivro.service

import com.mercadolivro.exception.AuthenticationException
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class UserDetailsCustomerServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    private lateinit var userDetailsCustomerService: UserDetailsCustomerService

    @Test
    fun `should load user by username`() {
        val customer = buildCustomer(id = 1)

        every { customerRepository.findById(customer.id!!) } returns Optional.of(customer)

        val loadUserByUsername = userDetailsCustomerService.loadUserByUsername(customer.id.toString())

        assertEquals(customer.id.toString(), loadUserByUsername.username)
        assertEquals(customer.password, loadUserByUsername.password)
    }

    @Test
    fun `should throw Authentication Exception`() {
        val customer = buildCustomer(id = 1)

        every { customerRepository.findById(customer.id!!) } returns Optional.empty()

        val error =
            assertThrows<AuthenticationException> { userDetailsCustomerService.loadUserByUsername(customer.id.toString()) }

        assertEquals("Customer ${customer.id} not exists.", error.message)
        assertEquals("ML-201", error.errorCode)
    }

}