package br.com.herdrive.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.herdrive.data.FipeClient
import br.com.herdrive.data.OpcaoFipe
import br.com.herdrive.data.PrecoFipe
import kotlinx.coroutines.launch

@Composable
fun ConsultaFipeScreen(snackbar: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    var marcas by remember { mutableStateOf(emptyList<OpcaoFipe>()) }
    var modelos by remember { mutableStateOf(emptyList<OpcaoFipe>()) }
    var anos by remember { mutableStateOf(emptyList<OpcaoFipe>()) }
    var marca by remember { mutableStateOf<OpcaoFipe?>(null) }
    var modelo by remember { mutableStateOf<OpcaoFipe?>(null) }
    var ano by remember { mutableStateOf<OpcaoFipe?>(null) }
    var resultado by remember { mutableStateOf<PrecoFipe?>(null) }
    var carregando by remember { mutableStateOf(false) }

    // Carrega as marcas uma vez ao abrir esta tela.
    LaunchedEffect(Unit) {
        carregando = true
        try {
            marcas = FipeClient.api.listarMarcas()
        } catch (e: Exception) {
            snackbar.showSnackbar("Não foi possível carregar as marcas. Verifique sua conexão.")
        } finally {
            carregando = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 860.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Consulta FIPE",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Escolha a marca, o modelo e o ano para consultar o preço médio do carro.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SeletorFipe(
                        titulo = "1. Marca",
                        selecionado = marca,
                        opcoes = marcas,
                        habilitado = !carregando && marcas.isNotEmpty()
                    ) { escolha ->
                        marca = escolha
                        modelo = null
                        ano = null
                        modelos = emptyList()
                        anos = emptyList()
                        resultado = null
                        scope.launch {
                            carregando = true
                            try {
                                modelos = FipeClient.api.listarModelos(escolha.code)
                            } catch (e: Exception) {
                                snackbar.showSnackbar("Erro ao buscar modelos. Tente outra vez.")
                            } finally {
                                carregando = false
                            }
                        }
                    }

                    SeletorFipe(
                        titulo = "2. Modelo",
                        selecionado = modelo,
                        opcoes = modelos,
                        habilitado = !carregando && marca != null && modelos.isNotEmpty()
                    ) { escolha ->
                        modelo = escolha
                        ano = null
                        anos = emptyList()
                        resultado = null
                        scope.launch {
                            carregando = true
                            try {
                                anos = FipeClient.api.listarAnos(marca!!.code, escolha.code)
                            } catch (e: Exception) {
                                snackbar.showSnackbar("Erro ao buscar anos. Tente outra vez.")
                            } finally {
                                carregando = false
                            }
                        }
                    }

                    SeletorFipe(
                        titulo = "3. Ano e combustível",
                        selecionado = ano,
                        opcoes = anos,
                        habilitado = !carregando && modelo != null && anos.isNotEmpty()
                    ) { escolha ->
                        ano = escolha
                        resultado = null
                    }

                    Button(
                        onClick = {
                            val m = marca ?: return@Button
                            val mo = modelo ?: return@Button
                            val a = ano ?: return@Button
                            scope.launch {
                                carregando = true
                                resultado = null
                                try {
                                    resultado = FipeClient.api.consultarPreco(m.code, mo.code, a.code)
                                } catch (e: Exception) {
                                    snackbar.showSnackbar("Não foi possível consultar o preço. Tente novamente.")
                                } finally {
                                    carregando = false
                                }
                            }
                        },
                        enabled = !carregando && marca != null && modelo != null && ano != null,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Consultar preço") }

                    if (carregando) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    if (!carregando && marcas.isEmpty()) {
                        Text("As marcas não foram carregadas. Saia desta tela e volte para tentar novamente.")
                    }
                }
            }

            resultado?.let { info ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Preço médio de referência", style = MaterialTheme.typography.titleMedium)
                        Text(
                            info.price,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = RosaHerDrive
                        )
                        Text("Marca: ${info.brand}")
                        Text("Modelo: ${info.model}")
                        Text("Ano: ${info.modelYear}")
                        Text("Combustível: ${info.fuel}")
                        Text("Código FIPE: ${info.codeFipe}")
                        Text("Referência: ${info.referenceMonth}")
                    }
                }
            }

            Text(
                "Fonte: FIPE API. O valor é uma referência média de mercado e pode variar conforme o estado do veículo e a região. A API possui limite de consultas gratuitas.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SeletorFipe(
    titulo: String,
    selecionado: OpcaoFipe?,
    opcoes: List<OpcaoFipe>,
    habilitado: Boolean,
    onSelecionar: (OpcaoFipe) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var pesquisa by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(titulo, fontWeight = FontWeight.SemiBold)
        OutlinedButton(
            onClick = { pesquisa = ""; mostrarDialogo = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selecionado?.name ?: "Selecionar", maxLines = 2)
        }
    }

    if (mostrarDialogo) {
        val filtradas = opcoes.filter { it.name.contains(pesquisa, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Selecionar ${titulo.substringAfter(". ")}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pesquisa,
                        onValueChange = { pesquisa = it },
                        label = { Text("Pesquisar") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                        items(filtradas, key = { it.code }) { item ->
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        mostrarDialogo = false
                                        onSelecionar(item)
                                    }
                                    .padding(vertical = 13.dp, horizontal = 5.dp)
                            )
                        }
                    }
                    if (filtradas.isEmpty()) Text("Nenhuma opção encontrada")
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Fechar") }
            }
        )
    }
}
