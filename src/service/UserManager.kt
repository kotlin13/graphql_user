package com.graphql.service

import com.graphql.user.UserPaymentOrder

interface UserManager {

    /**
     *
     * Getting list of orders for Admin functionality
     */
    fun listUsers(): List<UserPaymentOrder>

    /**
     *
     * Getting order by ID
     */
    fun userById(orderId: Int): UserPaymentOrder?
}