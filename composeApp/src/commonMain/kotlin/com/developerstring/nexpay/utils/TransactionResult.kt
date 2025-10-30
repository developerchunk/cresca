package com.developerstring.nexpay.utils

open class TransactionResult<T> {
    data class Success<T>(val data: T) : TransactionResult<T>()
    data class Error<T>(val message: String) : TransactionResult<T>()
}