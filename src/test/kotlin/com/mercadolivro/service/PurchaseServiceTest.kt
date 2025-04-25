package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.event.PurchaseEvent
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.exception.PurchaseException
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildPurchase
import com.mercadolivro.repository.PurchaseRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class PurchaseServiceTest {

    @MockK
    private lateinit var purchaseRepository: PurchaseRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var purchaseService: PurchaseService

    val purchaseEventSlot = slot<PurchaseEvent>()

    @Test
    fun `should create purchase and publish event`() {
        val purchase = buildPurchase()

        every { bookService.findAllByIds(any()) } returns listOf(buildBook())
        every { purchaseRepository.save(purchase) } returns purchase
        every { applicationEventPublisher.publishEvent(capture(purchaseEventSlot)) } just runs

        purchaseService.create(purchase)

        verify(exactly = 1) { bookService.findAllByIds(any()) }
        verify(exactly = 1) { purchaseRepository.save(purchase) }
        verify(exactly = 1) { applicationEventPublisher.publishEvent(any()) }

        assertEquals(purchase, purchaseEventSlot.captured.purchaseModel)
    }

    @Test
    fun `should not create purchase and publish event when book is not ATIVO`() {
        val purchase = buildPurchase()
        val book = buildBook(status = BookStatus.VENDIDO)

        every { bookService.findAllByIds(any()) } returns listOf(book)

        val error = assertThrows<PurchaseException> { purchaseService.create(purchase) }

        assertEquals("Book ${book.id} cannot be sold.", error.message)
        assertEquals("ML-203", error.errorCode)

        verify(exactly = 1) { bookService.findAllByIds(any()) }
        verify(exactly = 0) { purchaseRepository.save(purchase) }
        verify(exactly = 0) { applicationEventPublisher.publishEvent(any()) }
    }

    @Test
    fun `should throw Not Found Exception when book is not found`() {
        val purchase = buildPurchase(books = listOf(buildBook(id = 1)).toMutableList())

        every { bookService.findAllByIds(any()) } returns listOf()

        val error = assertThrows<NotFoundException> { purchaseService.create(purchase) }

        assertEquals("Book ${purchase.books.map { it.id }.toSet()} not exists.", error.message)
        assertEquals("ML-101", error.errorCode)

        verify(exactly = 1) { bookService.findAllByIds(any()) }
        verify(exactly = 0) { purchaseRepository.save(purchase) }
        verify(exactly = 0) { applicationEventPublisher.publishEvent(any()) }
    }

    @Test
    fun `should update purchase`() {
        val purchase = buildPurchase()

        every { purchaseRepository.save(purchase) } returns purchase

        purchaseService.update(purchase)

        verify(exactly = 1) { purchaseRepository.save(purchase) }
    }

}