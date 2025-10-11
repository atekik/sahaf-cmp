package dev.ktekik.signin.usecase

fun interface UseCaseExecutor<T : UseCase> {
    suspend fun execute(useCase: T)
}
