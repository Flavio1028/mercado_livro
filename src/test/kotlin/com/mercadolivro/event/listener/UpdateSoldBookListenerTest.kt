package com.mercadolivro.event.listener

import com.mercadolivro.event.PurchaseEvent
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.model.PurchaseModel
import com.mercadolivro.service.BookService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class UpdateSoldBookListenerTest {

    @MockK
    private lateinit var bookService: BookService

    @InjectMockKs
    private lateinit var updateSoldBookListener: UpdateSoldBookListener

    @Test
    fun `should generated purchase`() {
        val purchaseModel = PurchaseModel(
            customer = buildCustomer(),
            books = listOf(buildBook()).toMutableList(),
            price = BigDecimal.TEN
        )

        val purchaseEvent = PurchaseEvent(this, purchaseModel)

        every { bookService.purchase(purchaseEvent.purchaseModel.books) } just runs

        updateSoldBookListener.listener(purchaseEvent)

        verify(exactly = 1) { bookService.purchase(purchaseEvent.purchaseModel.books) }
    }

}