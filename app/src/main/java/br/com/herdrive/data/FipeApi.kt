package br.com.herdrive.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class OpcaoFipe(
    val code: String = "",
    val name: String = ""
)

data class PrecoFipe(
    val brand: String = "",
    val model: String = "",
    val modelYear: Int = 0,
    val fuel: String = "",
    val price: String = "",
    val codeFipe: String = "",
    val referenceMonth: String = ""
)

interface FipeApi {
    @GET("cars/brands")
    suspend fun listarMarcas(): List<OpcaoFipe>

    @GET("cars/brands/{marca}/models")
    suspend fun listarModelos(@Path("marca") marca: String): List<OpcaoFipe>

    @GET("cars/brands/{marca}/models/{modelo}/years")
    suspend fun listarAnos(
        @Path("marca") marca: String,
        @Path("modelo") modelo: String
    ): List<OpcaoFipe>

    @GET("cars/brands/{marca}/models/{modelo}/years/{ano}")
    suspend fun consultarPreco(
        @Path("marca") marca: String,
        @Path("modelo") modelo: String,
        @Path("ano") ano: String
    ): PrecoFipe
}

object FipeClient {
    val api: FipeApi = Retrofit.Builder()
        .baseUrl("https://fipe.parallelum.com.br/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FipeApi::class.java)
}
