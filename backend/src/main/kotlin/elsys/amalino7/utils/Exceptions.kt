package elsys.amalino7.utils

sealed class AppException : Exception() {
    class ValidationException(message: String) : AppException()
    class NotFoundException(message: String) : AppException()
    class ConflictException(message: String) : AppException()
    class UnauthorizedException(message: String) : AppException()
    class DatabaseException : AppException()
    class ExternalServiceException : AppException()
}