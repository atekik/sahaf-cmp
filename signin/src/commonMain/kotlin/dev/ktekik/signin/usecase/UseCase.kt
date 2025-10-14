package dev.ktekik.signin.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase {
    suspend fun invoke()
}

interface FlowUseCase<out T> {
    operator fun invoke(): Flow<T>
}