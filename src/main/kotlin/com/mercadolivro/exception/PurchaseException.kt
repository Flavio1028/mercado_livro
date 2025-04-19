package com.mercadolivro.exception

class PurchaseException(override val message: String, val errorCode: String) : Exception()