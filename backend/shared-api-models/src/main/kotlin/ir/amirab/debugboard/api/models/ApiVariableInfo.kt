package ir.amirab.debugboard.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiVariableInfo(
    val name: String,
    val type: String,
    val value: String,
    val children: List<ApiVariableInfo>?
)

@Serializable
data class ApiNetworkData(
    val tag: String,
    val request: ApiRequest,
    val response: ApiNetworkResponse?,
)

@Serializable
data class ApiRequest(
    val url: String,
    val method: String,
    val headers: Map<String, List<String>>,
    val body: String? = null
)

@Serializable
sealed interface ApiNetworkResponse

@Serializable
@SerialName("fail")
data class ApiFailResponse(
    val cause: String
) : ApiNetworkResponse

@Serializable
@SerialName("success")
data class ApiSuccessResponse(
    val code: Int,
    val description: String,
    val headers: Map<String, List<String>>,
    val body: String?
) : ApiNetworkResponse

@Serializable
data class ApiLogData(
    val tag: String,
    val level: String,
    val timestamp: Long,
    val message: String,
)