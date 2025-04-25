package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.repository.BookRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*

@ExtendWith(MockKExtension::class)
class BookServiceTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @SpyK
    @InjectMockKs
    private lateinit var bookService: BookService

    @Test
    fun `should create a book`() {

        val bookModel = buildBook()

        every { bookRepository.save(bookModel) } returns bookModel

        bookService.create(bookModel)

        verify(exactly = 1) { bookRepository.save(bookModel) }
    }

    @Test
    fun `should find all books`() {
        val book1 = buildBook()
        val book2 = buildBook()

        val pageable: Pageable = PageRequest.of(0, 10)
        val fakeBook = listOf(book1, book2)
        val fakePage = PageImpl(fakeBook, pageable, fakeBook.size.toLong())

        every { bookRepository.findAll(pageable) } returns fakePage

        val books = bookService.findAll(pageable)

        assertEquals(fakeBook, books.content)
        verify(exactly = 1) { bookRepository.findAll(any<Pageable>()) }
    }

    @Test
    fun `should find all books when actives`() {
        val book1 = buildBook(status = BookStatus.ATIVO)

        val pageable: Pageable = PageRequest.of(0, 10)
        val fakeBook = listOf(book1)
        val fakePage = PageImpl(fakeBook, pageable, fakeBook.size.toLong())

        every { bookRepository.findByStatus(BookStatus.ATIVO, pageable) } returns fakePage

        val books = bookService.findActives(pageable)

        assertEquals(fakeBook, books.content)
        verify(exactly = 1) { bookRepository.findByStatus(BookStatus.ATIVO, pageable) }
    }

    @Test
    fun `should find by Id one book`() {
        val book1 = buildBook()

        every { bookRepository.findById(123) } returns Optional.of(book1)

        val books = bookService.findById(123)

        assertEquals(book1, books)
        verify(exactly = 1) { bookRepository.findById(123) }
    }

    @Test
    fun `should launch the exception Not Found Exception when you don't find any book`() {
        val bookId = 999

        every { bookRepository.findById(bookId) } returns Optional.empty()

        val error = assertThrows<NotFoundException> { bookService.findById(bookId) }

        assertEquals("Book $bookId not exists.", error.message)
        assertEquals("ML-101", error.errorCode)
        verify(exactly = 1) { bookRepository.findById(bookId) }
    }

    @Test
    fun `should delete a book`() {
        val book = buildBook(id = 1)
        val bookId = book.id

        every { bookService.findById(bookId!!) } returns book
        every { bookService.update(book) } just runs

        bookService.delete(bookId!!)

        verify(exactly = 1) { bookService.findById(bookId) }
        verify(exactly = 1) { bookService.update(book) }
    }

    @Test
    fun `should update a book`() {
        val book = buildBook(id = 1)

        every { bookRepository.save(book) } returns book

        bookService.update(book)

        verify(exactly = 1) { bookService.update(book) }
    }

    @Test
    fun `should delete a book by customer`() {
        val customer = buildCustomer(id = 1)
        val book = buildBook(status = BookStatus.ATIVO)
        val bookDelete = book.copy()
        bookDelete.status = BookStatus.DELETADO

        every { bookRepository.findByCustomer(customer) } returns listOf(book)
        every { bookRepository.saveAll(listOf(bookDelete)) } returns listOf(bookDelete)

        bookService.deleteByCustomer(customer)

        verify(exactly = 1) { bookRepository.findByCustomer(customer) }
        verify(exactly = 1) { bookRepository.saveAll(listOf(bookDelete)) }
    }

    @Test
    fun `should find all by ids`() {
        val booksIds = setOf(1, 2, 3)
        val listBooks = listOf(buildBook(), buildBook())

        every { bookRepository.findAllById(booksIds) } returns listBooks

        val findAllByIds = bookService.findAllByIds(booksIds)

        assertEquals(2, findAllByIds.size)

        verify(exactly = 1) { bookRepository.findAllById(booksIds) }
    }

    @Test
    fun `should purchase`() {
        val listBooks = listOf(buildBook(), buildBook()).toMutableList()

        every { bookRepository.saveAll(listBooks) } returns listBooks

        bookService.purchase(listBooks)

        verify(exactly = 1) { bookRepository.saveAll(listBooks) }
    }

}