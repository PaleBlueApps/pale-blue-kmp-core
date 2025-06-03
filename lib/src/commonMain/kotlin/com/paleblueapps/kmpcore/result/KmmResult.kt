package com.paleblueapps.kmpcore.result

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A sealed class representing a result that can either be a success with a value of type [T] or a failure with an exception.
 *
 * @param T The type of the value in case of success.
 */
sealed class KmmResult<out T : Any> {
    class Success<T : Any>(val value: T) : KmmResult<T>() {
        override fun equals(other: Any?): Boolean = other is Success<*> && value == other.value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Success($value)"
    }

    class Failure(val cause: Throwable) : KmmResult<Nothing>() {
        override fun equals(other: Any?): Boolean = other is Failure && cause == other.cause
        override fun hashCode(): Int = cause.hashCode()
        override fun toString(): String = "Failure($cause)"
    }

    /**
     * Checks if the result is a success.
     * @return `true` if the result is a success, `false` otherwise.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Checks if the result is a failure.
     * @return `true` if the result is a failure, `false` otherwise.
     */
    val isFailure: Boolean get() = this is Failure

    companion object {
        fun <T : Any> success(value: T) = Success(value)

        fun failure(exception: Throwable) = Failure(exception)
    }

    @OptIn(ExperimentalContracts::class)
    inline fun onFailure(action: (exception: Throwable) -> Unit): KmmResult<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        if (this is Failure) action(cause)
        return this
    }

    @OptIn(ExperimentalContracts::class)
    inline fun onSuccess(action: (value: T) -> Unit): KmmResult<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        if (this is Success) action(value)
        return this
    }

    @OptIn(ExperimentalContracts::class)
    inline fun fold(onSuccess: (value: T) -> Unit, onFailure: (exception: Throwable) -> Unit) {
        contract {
            callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
            callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(cause)
        }
    }

    /**
     * Transforms the value of this [KmmResult] if it is a success, otherwise returns a failure with the same cause.
     */
    inline fun <R : Any> map(transform: (value: T) -> R): KmmResult<R> {
        return when (this) {
            is Success -> success(transform(value))
            is Failure -> failure(cause)
        }
    }

    /**
     * Transforms the value of this [KmmResult] if it is a success, otherwise returns a failure with the same cause.
     * This function catches any exceptions thrown by the transform function and returns a failure.
     */
    inline fun <R : Any> mapCatching(transform: (value: T) -> R): KmmResult<R> {
        return when (this) {
            is Success -> runCatching { transform(value) }
            is Failure -> failure(cause)
        }
    }

    /**
     * Returns the value if this [KmmResult] is a success, or `null` if it is a failure.
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> value
            is Failure -> null
        }
    }

    /**
     * Returns the exception if this [KmmResult] is a failure, or `null` if it is a success.
     */
    fun exceptionOrNull(): Throwable? {
        return when (this) {
            is Success -> null
            is Failure -> cause
        }
    }

    /**
     * Throws the exception if this [KmmResult] is a failure.
     * If this is a success, it does nothing.
     *
     * @throws Throwable The exception that caused the failure.
     */
    fun throwOnFailure() {
        if (this is Failure) throw cause
    }

    /**
     * Returns the value if this [KmmResult] is a success, or throws the exception if it is a failure.
     *
     * @throws Throwable The exception that caused the failure.
     */
    fun getOrThrow(): T {
        throwOnFailure()
        return getOrNull()!!
    }
}

/**
 * Runs the given block of code with the receiver as the context and returns a [KmmResult] containing the result.
 * If an exception is thrown, it returns a [KmmResult.Failure] with the exception.
 *
 * @param T The type of the receiver.
 * @param R The type of the result.
 * @param block The block of code to run.
 * @return A [KmmResult] containing either the result or a failure.
 */
inline fun <T : Any, R : Any> T.runCatching(block: T.() -> R): KmmResult<R> {
    return try {
        KmmResult.success(block())
    } catch (e: Throwable) {
        KmmResult.failure(e)
    }
}

/**
 * Converts a [Result] to a [KmmResult].
 *
 * @param T The type of the value in the result.
 * @return A [KmmResult] containing either the value or the exception.
 */
fun <T : Any> Result<T>.toKmmResult(): KmmResult<T> {
    return when {
        isSuccess -> KmmResult.success(getOrNull()!!)
        else -> KmmResult.failure(exceptionOrNull()!!)
    }
}

/**
 * Converts a [KmmResult] to a [Result].
 *
 * @param T The type of the value in the result.
 * @return A [Result] containing either the value or the exception.
 */
fun <T : Any> KmmResult<T>.toKotlinResult(): Result<T> {
    return when (this) {
        is KmmResult.Success -> Result.success(value)
        is KmmResult.Failure -> Result.failure(cause)
    }
}