package dev.ktekik.sahaf.cloud

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String?) : ApiResult<Nothing> // Using Nothing for error case
}