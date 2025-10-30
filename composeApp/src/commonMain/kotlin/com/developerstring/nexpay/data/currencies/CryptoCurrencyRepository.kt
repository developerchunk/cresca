package com.developerstring.nexpay.data.currencies

import com.developerstring.nexpay.utils.error_handlers.CryptoResult
import com.developerstring.nexpay.utils.error_handlers.NetworkError
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

class CryptoCurrencyRepository(private val client: HttpClient) {

    suspend fun getCryptoCurrencies(
        perPage: Int = 50,
        page: Int = 1
    ): CryptoResult<List<CryptoCurrency>, NetworkError> {
        val response = try {
            client.get(
                urlString = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=$perPage&page=$page&sparkline=false"
            )
        } catch (e: UnresolvedAddressException) {
            return CryptoResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return CryptoResult.Error(NetworkError.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<List<CryptoCurrencyModel>>()
                CryptoResult.Success(data.map { it.toCryptoCurrency() })
            }

            401 -> CryptoResult.Error(NetworkError.UNAUTHORIZED)
            409 -> CryptoResult.Error(NetworkError.CONFLICT)
            408 -> CryptoResult.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> CryptoResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> CryptoResult.Error(NetworkError.SERVER_ERROR)
            else -> CryptoResult.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getMarketChart(cryptoId: String, currency: String, days: Int): CryptoResult<List<Pair<Long, Double>>, NetworkError> {

        val response = try {
            client.get(
                urlString = "https://api.coingecko.com/api/v3/coins/$cryptoId/market_chart?vs_currency=$currency&days=$days"
            )
        } catch (e: UnresolvedAddressException) {
            return CryptoResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return CryptoResult.Error(NetworkError.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<MarketChartResponseModel>()
                CryptoResult.Success(data.prices.map { Pair(it[0].toLong(), it[1]) })
            }

            401 -> CryptoResult.Error(NetworkError.UNAUTHORIZED)
            409 -> CryptoResult.Error(NetworkError.CONFLICT)
            408 -> CryptoResult.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> CryptoResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> CryptoResult.Error(NetworkError.SERVER_ERROR)
            else -> CryptoResult.Error(NetworkError.UNKNOWN)
        }
    }
}