package com.mercadolivro.exception

import com.mercadolivro.controller.response.ErrorResponse
import com.mercadolivro.controller.response.FieldErrorResponse
import com.mercadolivro.enums.Errors
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.NOT_FOUND.value(), ex.message, ex.errorCode, null
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), ex.message, ex.errorCode, null
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PurchaseException::class)
    fun handlePurchaseException(ex: PurchaseException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), ex.message, ex.errorCode, null
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), Errors.ML010.message, Errors.ML010.code,
            ex.bindingResult.fieldErrors.map { FieldErrorResponse(it.defaultMessage ?: "invalid", it.field) }
        )
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleNotFoundException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.FORBIDDEN.value(), Errors.ML000.message, Errors.ML000.message, null
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

}