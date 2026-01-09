package dev.ktekik.barcodescanner.exception

sealed class SahafException(message: String) : RuntimeException(message)
class EmptyBarcodeException(message: String) : SahafException(message)
class IncompleteBarcodeException(message: String) : SahafException(message)
class NotABookException(message: String) : SahafException(message)
class NotTurkishException(message: String) : SahafException(message)