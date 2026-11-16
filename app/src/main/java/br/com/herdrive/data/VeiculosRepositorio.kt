package br.com.herdrive.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Exatamente as quatro operações CRUD cobradas na aula de Firebase.
class VeiculosRepositorio {
    private val colecao = FirebaseFirestore.getInstance().collection("veiculos")

    // READ: recuperar todos os veículos
    suspend fun listar(): List<Veiculo> = colecao.get().await().documents.map { documento ->
        Veiculo(
            id = documento.id,
            marca = documento.getString("marca").orEmpty(),
            modelo = documento.getString("modelo").orEmpty(),
            ano = documento.getString("ano").orEmpty()
        )
    }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.marca })

    // CREATE (novo) ou UPDATE (existente)
    suspend fun salvar(veiculo: Veiculo) {
        val dados = mapOf(
            "marca" to veiculo.marca,
            "modelo" to veiculo.modelo,
            "ano" to veiculo.ano
        )
        if (veiculo.id.isBlank()) {
            colecao.add(dados).await()
        } else {
            colecao.document(veiculo.id).update(dados).await()
        }
    }

    // DELETE
    suspend fun excluir(id: String) {
        colecao.document(id).delete().await()
    }
}
