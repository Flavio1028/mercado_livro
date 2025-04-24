package com.mercadolivro.repository

import com.mercadolivro.helper.buildCustomer
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerRepositoryTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setUp() = customerRepository.deleteAll()

    @Test
    fun `should return name containing`() {

        val marcos = buildCustomer(name = "Marcos")
        val matheus = buildCustomer(name = "Matheus")
        val alex = buildCustomer(name = "Alex")

        val customerList = listOf(marcos, matheus, alex)
        val pageable: Pageable = PageRequest.of(0, 10)

        customerRepository.saveAll(customerList)

        val customer = customerRepository.findByName(pageable, "Marcos")

        assertEquals(listOf(marcos), customer.content)
    }

    @Nested
    inner class `exists by email` {
        @Test
        fun `should return true when email exists`() {

            val email = "email@test.com"
            customerRepository.save(buildCustomer(email = email))

            val exist = customerRepository.existsByEmail(email)

            assertTrue(exist)
        }

        @Test
        fun `should return true when do not email exists`() {

            val email = "email@test.com"

            val exist = customerRepository.existsByEmail(email)

            assertFalse(exist)
        }
    }

    @Nested
    inner class `find by email` {
        @Test
        fun `should return customer when email exists`() {

            val email = "email@test.com"
            val customer = customerRepository.save(buildCustomer(email = email))

            val result = customerRepository.findByEmail(email)

            assertNotNull(result)
            assertEquals(customer, result)
        }

        @Test
        fun `should return customer when do not email exists`() {

            val email = "email@test.com"

            val result = customerRepository.findByEmail(email)

            assertNull(result)
        }
    }

}