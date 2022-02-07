package com.xardev.userapp.core.utils

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Failure<T>(val error: Throwable) : Result<T>()
    class Loading<T>(val b: Boolean) : Result<T>()
}

inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
    onLoading: (b: Boolean) -> R
): R = when (this) {
    is Result.Success -> onSuccess(value)
    is Result.Failure -> onFailure(error)
    is Result.Loading -> onLoading(b)
}

val <T> Result<T>.isSuccess: Boolean
    get() = when (this) {
        is Result.Success -> true
        else -> false
    }

val <T> Result<T>.isFailure: Boolean
    get() = when (this) {
        is Result.Failure -> true
        else -> false
    }

val <T> Result<T>.isLoading: Boolean
    get() = when (this) {
        is Result.Loading -> b
        else -> false
    }

fun <T> Result<T>.getOrNull(): T? =
    when (this) {
        is Result.Success -> this.value
        else -> null
    }

 fun <T> Result<T>.exceptionOrNull(): Throwable? =
    when (this) {
        is Result.Failure -> this.error
        else -> null
    }