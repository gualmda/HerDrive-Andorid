package br.com.herdrive.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// O JSON do ViaCEP corresponde a essas propriedades.
data class Endereco(
    val cep: String? = null,
    val logradouro: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val erro: Boolean? = null
)

interface ViaCepApi {
    @GET("ws/{cep}/json/")
    suspend fun consultar(@Path("cep") cep: String): Endereco
}

object ViaCepClient {
    // Atenção: a URL base do Retrofit precisa terminar com "/".
    val api: ViaCepApi = Retrofit.Builder()
        .baseUrl("https://viacep.com.br/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ViaCepApi::class.java)
}
