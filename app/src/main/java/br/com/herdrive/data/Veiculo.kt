package br.com.herdrive.data

// Um documento da coleção "veiculos" no Firestore.
data class Veiculo(
    val id: String = "",
    val marca: String = "",
    val modelo: String = "",
    val ano: String = ""
)
