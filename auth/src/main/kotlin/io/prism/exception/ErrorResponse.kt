package io.prism.exception

/**
 * Exception 에 따른 Response 를 생성하기 위한 클래스
 */
class ErrorResponse(
    val message: String,
    val status: Int
) {

    // Error Code 기반 생성자
    constructor(
        message: String? = null,
        code: ErrorCode = ErrorCode.UNDEFINED_ERROR
    ) : this(
        message = message ?: code.message,
        status = code.status
    )

    constructor(exception: BusinessException) : this(message = exception.message, code = exception.errorCode)
}

/**
 * ERROR CODE 분류
 */
enum class ErrorCode(
    val status: Int,
    val message: String
) {
    NOT_FOUND_ERROR(404, "존재하지 않습니다."),
    UNAUTHORIZED_ERROR(401, "허가되지 않았습니다."),
    UNDEFINED_ERROR(500, "정의하지 않은 예외")
}
