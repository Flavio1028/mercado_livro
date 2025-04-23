package com.mercadolivro.event.listener

import com.mercadolivro.event.PurchaseEvent
import com.mercadolivro.helper.buildPurchase
import com.mercadolivro.service.PurchaseService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class GeneratedNfeListenerTest {

    @MockK
    private lateinit var purchaseService: PurchaseService

    @InjectMockKs
    private lateinit var generatedNfeListener: GeneratedNfeListener

    @Test
    fun `should generated NFE`() {

        val purchase = buildPurchase()
        val fakeNfe = UUID.randomUUID()
        val purchaseExpected = purchase.copy(nfe = fakeNfe.toString())

        mockkStatic(UUID::class)

        every { UUID.randomUUID() } returns fakeNfe
        every { purchaseService.update(purchaseExpected) } just runs

        generatedNfeListener.listener(PurchaseEvent(this, purchase))

        verify(exactly = 1) { purchaseService.update(purchaseExpected) }
    }

}