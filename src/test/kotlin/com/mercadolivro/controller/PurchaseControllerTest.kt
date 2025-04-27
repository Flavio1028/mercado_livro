package com.mercadolivro.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mercadolivro.controller.request.PostPurchaseRequest
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.repository.BookRepository
import com.mercadolivro.repository.CustomerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
@WithMockUser
class PurchaseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

//    @Test
    fun `should create a purchase`() {

        val customer = customerRepository.save(buildCustomer())
        val book = bookRepository.save(buildBook(customer = customer))

        val request = PostPurchaseRequest(customer.id!!, setOf(book.id!!))

        print(request.customerId.toString())
        print(request.booksIds.isEmpty())

        mockMvc.perform(
            post("/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

    }

}