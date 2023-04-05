package io.prism.exception


open class BusinessException(
    override val message: String? = null,
    val errorCode: ErrorCode = ErrorCode.UNDEFINED_ERROR
) : RuntimeException()

class NotFoundError: BusinessException(message = "존재하지 않습니다.", errorCode = ErrorCode.NOT_FOUND_ERROR)
class UnauthorizedError: BusinessException(message = "허가되지 않았습니다.", errorCode = ErrorCode.UNAUTHORIZED_ERROR)
