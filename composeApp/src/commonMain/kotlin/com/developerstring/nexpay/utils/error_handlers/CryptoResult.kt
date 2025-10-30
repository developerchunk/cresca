package com.developerstring.nexpay.utils.error_handlers

sealed interface CryptoResult<out D, out E: Error> {
    data class Success<out D>(val data: D): CryptoResult<D, Nothing>
    data class Error<out E: com.developerstring.nexpay.utils.error_handlers.Error>(val error: E):
        CryptoResult<Nothing, E>
}

inline fun <T, E: Error, R> CryptoResult<T, E>.map(map: (T) -> R): CryptoResult<R, E> {
    return when(this) {
        is CryptoResult.Error -> CryptoResult.Error(error)
        is CryptoResult.Success -> CryptoResult.Success(map(data))
    }
}

fun <T, E: Error> CryptoResult<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: Error> CryptoResult<T, E>.onSuccess(action: (T) -> Unit): CryptoResult<T, E> {
    return when(this) {
        is CryptoResult.Error -> this
        is CryptoResult.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: Error> CryptoResult<T, E>.onError(action: (E) -> Unit): CryptoResult<T, E> {
    return when(this) {
        is CryptoResult.Error -> {
            action(error)
            this
        }
        is CryptoResult.Success -> this
    }
}

typealias EmptyResult<E> = CryptoResult<Unit, E>
