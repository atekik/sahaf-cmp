package dev.ktekik.barcodescanner.validator

import dev.ktekik.barcodescanner.exception.EmptyBarcodeException
import dev.ktekik.barcodescanner.exception.IncompleteBarcodeException
import dev.ktekik.barcodescanner.exception.NotABookException
import dev.ktekik.barcodescanner.exception.NotTurkishException

object IsbnValidator {
    val isbnRegex = Regex("^(978|979)[0-9]{10}\$")
    val turkishIsbnRegex = Regex("^[0-9]{3}(605|975)[0-9]{7}\$")
    val turkishIsbnRegex2 = Regex("^[0-9]{3}(9944)[0-9]{6}\$")

    fun validateISBN(isbn: String?): String {
        if (isbn.isNullOrEmpty()) throw EmptyBarcodeException("ISBN cannot be empty")
        if (isbn.length != 13) throw IncompleteBarcodeException("ISBN must be 13 digits long")
        if (!isbn.matches(isbnRegex)) throw NotABookException("ISBN must start with 978 or 979")
        if (!isbn.matches(turkishIsbnRegex) && !isbn.matches(turkishIsbnRegex2)) throw NotTurkishException(
            "ISBN does not belong to a Turkish book"
        )

        return isbn
    }
}