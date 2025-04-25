package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.event.PurchaseEvent
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.exception.PurchaseException
import com.mercadolivro.model.PurchaseModel
import com.mercadolivro.repository.PurchaseRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val bookService: BookService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun create(purchaseModel: PurchaseModel) {

        val books = bookService.findAllByIds(purchaseModel.books.map { it.id }.toSet() as Set<Int>)

        if (books.isEmpty()) {
            throw NotFoundException(
                Errors.ML101.message
                .format(purchaseModel.books.map { it.id }.toSet()), Errors.ML101.code
            )
        }

        books.forEach { book ->
            if (book.status != BookStatus.ATIVO) {
                throw PurchaseException(Errors.ML301.message.format(book.id), Errors.ML301.code)
            }
        }

        purchaseRepository.save(purchaseModel)
        applicationEventPublisher.publishEvent(PurchaseEvent(this, purchaseModel))
    }

    fun update(purchaseModel: PurchaseModel) {
        purchaseRepository.save(purchaseModel)
    }

}
