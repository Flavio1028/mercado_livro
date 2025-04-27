package com.mercadolivro.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mercadolivro.controller.request.PostBookRequest
import com.mercadolivro.controller.request.PutBookRequest
import com.mercadolivro.enums.BookStatus
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.repository.BookRepository
import com.mercadolivro.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
@WithMockUser
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        bookRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should creat a book`() {

        val customer = customerRepository.save(buildCustomer())

        val request = PostBookRequest("Book test", BigDecimal.TEN, customer.id!!)

        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        val book = bookRepository.findAll().toList()
        assertEquals(1, book.size)
        assertEquals(request.name, book[0].name)
        assertTrue(book[0].price.compareTo(request.price) == 0)
    }

    @Test
    fun `should find all books`() {

        val customer = customerRepository.save(buildCustomer())
        val book1 = bookRepository.save(buildBook(customer = customer))
        val book2 = bookRepository.save(buildBook(customer = customer))

        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("items[0].id").value(book1.id))
            .andExpect(jsonPath("items[0].name").value(book1.name))
            .andExpect(jsonPath("items[0].status").value(book1.status!!.name))
            .andExpect(jsonPath("items[1].id").value(book2.id))
            .andExpect(jsonPath("items[1].name").value(book2.name))
            .andExpect(jsonPath("items[1].status").value(book2.status!!.name))
            .andExpect(jsonPath("currentPage").value(0))
            .andExpect(jsonPath("totalItems").value(2))
            .andExpect(jsonPath("size").value(10))
            .andExpect(jsonPath("totalPages").value(1))
    }

    @Test
    fun `should find all books when is active`() {

        val customer = customerRepository.save(buildCustomer())
        val book1 = bookRepository.save(buildBook(customer = customer, status = BookStatus.ATIVO))
        bookRepository.save(buildBook(customer = customer, status = BookStatus.VENDIDO))

        mockMvc.perform(get("/books/active"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("items[0].id").value(book1.id))
            .andExpect(jsonPath("items[0].name").value(book1.name))
            .andExpect(jsonPath("items[0].status").value(book1.status!!.name))
            .andExpect(jsonPath("currentPage").value(0))
            .andExpect(jsonPath("totalItems").value(1))
            .andExpect(jsonPath("size").value(10))
            .andExpect(jsonPath("totalPages").value(1))
    }

    @Test
    fun `should find book by id`() {

        val customer = customerRepository.save(buildCustomer())
        val book1 = bookRepository.save(buildBook(customer = customer, status = BookStatus.ATIVO))

        mockMvc.perform(get("/books/${book1.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("id").value(book1.id))
            .andExpect(jsonPath("name").value(book1.name))
            .andExpect(jsonPath("status").value(book1.status!!.name))
    }

    @Test
    fun `should delete a book`() {

        val customer = customerRepository.save(buildCustomer())
        val book1 = bookRepository.save(buildBook(customer = customer, status = BookStatus.ATIVO))

        mockMvc.perform(delete("/books/${book1.id}"))
            .andExpect(status().isNoContent)

        val book = bookRepository.findById(book1.id!!).get()

        assertEquals(BookStatus.CANCELADO, book.status)
    }

    @Test
    fun `should update a book`() {

        val customer = customerRepository.save(buildCustomer())
        val book1 =
            bookRepository.save(buildBook(customer = customer, status = BookStatus.ATIVO, price = BigDecimal.ONE))

        val request = PutBookRequest("Update test", BigDecimal.TEN)

        mockMvc.perform(
            put("/books/${book1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNoContent)

        val book = bookRepository.findById(book1.id!!).get()

        assertEquals(request.name, book.name)
        assertTrue(request.price!!.compareTo(request.price) == 0)
        assertEquals(BookStatus.ATIVO, book.status)
    }

}