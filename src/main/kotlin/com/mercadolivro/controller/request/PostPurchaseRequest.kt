package com.mercadolivro.controller.request

import org.jetbrains.annotations.NotNull
import javax.validation.constraints.Positive

data class PostPurchaseRequest(

    @field:NotNull
    @field:Positive
    val customerId: Int,

    @field:NotNull
    val booksIds: Set<Int>

)