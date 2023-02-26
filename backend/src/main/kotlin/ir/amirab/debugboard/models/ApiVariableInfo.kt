package ir.amirab.debugboard.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiVariableInfo(
    val name: String,
    val type: String,
    val value: String,
    val children: List<ApiVariableInfo>?
)

@Serializable
internal data class ApiNetworkData(
    val tag: String,
    val request: ApiRequest,
    val response: ApiNetworkResponse?,
)

@Serializable
internal data class ApiRequest(
    val url: String,
    val method: String,
    val headers: Map<String, List<String>>,
    val body: String? = null
)

@Serializable
internal sealed interface ApiNetworkResponse

@Serializable
@SerialName("fail")
internal data class ApiFailResponse(
    val cause: String
) : ApiNetworkResponse

@Serializable
@SerialName("success")
internal data class ApiSuccessResponse(
    val code: Int,
    val description: String,
    val headers: Map<String, List<String>>,
    val body: String?
) : ApiNetworkResponse

@Serializable
internal data class ApiLogData(
    val tag: String,
    val level: String,
    val timestamp: Long,
    val message: String,
)