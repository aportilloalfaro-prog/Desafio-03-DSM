package com.example.worldexplorer.util

import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

enum class ErrorType {
    INVALID_CREDENTIALS,
    NO_CONNECTION,
    RATE_LIMIT,
    TIMEOUT,
    NOT_FOUND,
    SERVER,
    UNKNOWN
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val type: ErrorType, val message: String) : ApiResult<Nothing>()
}

/**
 * Ejecuta una llamada Retrofit y convierte cualquier fallo en un ApiResult.Failure
 * con un mensaje amigable. Así los controladores nunca revientan la app.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Failure(ErrorType.UNKNOWN, "El servidor respondió sin datos.")
            }
        } else {
            when (response.code()) {
                401, 403 -> ApiResult.Failure(
                    ErrorType.INVALID_CREDENTIALS,
                    "Credenciales inválidas o sin permiso. Revisa tu token o API key."
                )
                400, 404 -> ApiResult.Failure(
                    ErrorType.NOT_FOUND,
                    "No se encontró la información o la ubicación solicitada."
                )
                429 -> ApiResult.Failure(
                    ErrorType.RATE_LIMIT,
                    "Se alcanzó el límite de solicitudes. Intenta de nuevo más tarde."
                )
                in 500..599 -> ApiResult.Failure(
                    ErrorType.SERVER,
                    "El servicio tiene problemas en este momento."
                )
                else -> ApiResult.Failure(
                    ErrorType.UNKNOWN,
                    "Error inesperado (código ${response.code()})."
                )
            }
        }
    } catch (e: SocketTimeoutException) {
        ApiResult.Failure(ErrorType.TIMEOUT, "La solicitud tardó demasiado. Intenta de nuevo.")
    } catch (e: IOException) {
        ApiResult.Failure(ErrorType.NO_CONNECTION, "Sin conexión a internet.")
    } catch (e: JsonSyntaxException) {
        ApiResult.Failure(ErrorType.UNKNOWN, "No se pudo leer la respuesta del servidor.")
    }
}