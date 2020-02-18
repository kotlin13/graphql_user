package com.graphql.user

data class UserPaymentOrder(
    //PK, FK(userId) REFERENCES Payment(paymentId) and Order(orderId)
    val userId: Int,
    val username: String,
    val listOfPayment: List<Int> = emptyList(),
    val listOfOrders:List<Int> = emptyList()
)