package otus.homework.coroutines.ui

// im too lazy for updating kotlin version to 1.5 and using sealed interfaces instead :D
sealed class Error {
    object TimeoutError : Error()
    data class UnknownError(val message: String) : Error()
}