package dev.ktekik.sahaf.usecases

import kotlinx.coroutines.flow.Flow

interface UseCase<T, R> {
    suspend fun execute(param: T): Flow<R>
}