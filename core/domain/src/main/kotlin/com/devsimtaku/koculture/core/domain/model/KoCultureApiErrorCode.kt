package com.devsimtaku.koculture.core.domain.model

enum class KoCultureApiErrorCode(
    val code: String,
    val defaultMessage: String,
) {
    Normal(
        code = "INFO-000",
        defaultMessage = "정상 처리되었습니다",
    ),
    MissingRequiredValue(
        code = "ERROR-300",
        defaultMessage = "필수 값이 누락되어 있습니다.",
    ),
    InvalidAuthenticationKey(
        code = "INFO-100",
        defaultMessage = "인증키가 유효하지 않습니다.",
    ),
    InvalidFileType(
        code = "ERROR-301",
        defaultMessage = "파일타입 값이 누락 혹은 유효하지 않습니다.",
    ),
    ServiceNotFound(
        code = "ERROR-310",
        defaultMessage = "해당하는 서비스를 찾을 수 없습니다.",
    ),
    InvalidStartIndex(
        code = "ERROR-331",
        defaultMessage = "요청시작위치 값을 확인하십시오.",
    ),
    InvalidEndIndex(
        code = "ERROR-332",
        defaultMessage = "요청종료위치 값을 확인하십시오.",
    ),
    InvalidIndexType(
        code = "ERROR-333",
        defaultMessage = "요청위치 값의 타입이 유효하지 않습니다.",
    ),
    StartIndexGreaterThanEndIndex(
        code = "ERROR-334",
        defaultMessage = "요청종료위치 보다 요청시작위치가 더 큽니다.",
    ),
    SampleDataLimitExceeded(
        code = "ERROR-335",
        defaultMessage = "샘플데이터(샘플키:sample) 는 한번에 최대 5건을 넘을 수 없습니다.",
    ),
    RequestLimitExceeded(
        code = "ERROR-336",
        defaultMessage = "데이터요청은 한번에 최대 1000건을 넘을 수 없습니다.",
    ),
    ServerError(
        code = "ERROR-500",
        defaultMessage = "서버 오류입니다.",
    ),
    DatabaseConnectionError(
        code = "ERROR-600",
        defaultMessage = "데이터베이스 연결 오류입니다.",
    ),
    SqlError(
        code = "ERROR-601",
        defaultMessage = "SQL 문장 오류 입니다.",
    ),
    NoData(
        code = "INFO-200",
        defaultMessage = "해당하는 데이터가 없습니다.",
    ),
    Unknown(
        code = "UNKNOWN",
        defaultMessage = "알 수 없는 API 응답 코드입니다.",
    );

    companion object {
        fun fromCode(code: String): KoCultureApiErrorCode {
            return entries.firstOrNull { it.code == code } ?: Unknown
        }
    }
}
