package br.com.herdrive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import br.com.herdrive.data.Veiculo
import br.com.herdrive.data.VeiculosRepositorio
import kotlinx.coroutines.launch

@Composable
fun VeiculosScreen(snackbar: SnackbarHostState) {
    val repositorio = remember { VeiculosRepositorio() }
    val scope = rememberCoroutineScope()
    var veiculos by remember { mutableStateOf<List<Veiculo>>(emptyList()) }
    var carregando by remember { mutableStateOf(false) }
    var processando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }
    var busca by remember { mutableStateOf("") }

    var formularioAberto by remember { mutableStateOf(false) }
    var idEmEdicao by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var exclusaoPendente by remember { mutableStateOf<Veiculo?>(null) }

    suspend fun carregarVeiculos() {
        carregando = true
        erro = null
        try {
            veiculos = repositorio.listar()
        } catch (e: Exception) {
            erro = "Não foi possível carregar os veículos. Verifique a internet e o Firebase."
        } finally {
            carregando = false
        }
    }

    fun abrirFormulario(veiculo: Veiculo? = null) {
        idEmEdicao = veiculo?.id.orEmpty()
        marca = veiculo?.marca.orEmpty()
        modelo = veiculo?.modelo.orEmpty()
        ano = veiculo?.ano.orEmpty()
        formularioAberto = true
    }

    LaunchedEffect(Unit) { carregarVeiculos() }

    val filtrados = veiculos.filter {
        it.marca.contains(busca, ignoreCase = true) ||
            it.modelo.contains(busca, ignoreCase = true) ||
            it.ano.contains(busca, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.widthIn(max = 860.dp).fillMaxWidth()) {
                Text("Meus carros", style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Guarde e encontre os dados básicos dos seus veículos.",
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { abrirFormulario() }, enabled = !processando) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Adicionar veículo", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    label = { Text("Buscar por marca, modelo ou ano") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        if (carregando) {
            item { CircularProgressIndicator() }
        }
        if (erro != null) {
            item {
                Card(modifier = Modifier.widthIn(max = 860.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(erro.orEmpty())
                        TextButton(onClick = { scope.launch { carregarVeiculos() } }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        } else if (!carregando && filtrados.isEmpty()) {
            item {
                Card(modifier = Modifier.widthIn(max = 860.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            if (busca.isBlank()) "Nenhum veículo cadastrado ainda."
                            else "Nenhum veículo encontrado para essa busca.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else if (!carregando) {
            items(filtrados, key = { it.id }) { veiculo ->
                CardVeiculo(
                    veiculo = veiculo,
                    modifier = Modifier.widthIn(max = 860.dp).fillMaxWidth(),
                    onEditar = { abrirFormulario(veiculo) },
                    onExcluir = { exclusaoPendente = veiculo }
                )
            }
        }
    }

    if (formularioAberto) {
        val valido = marca.isNotBlank() && modelo.isNotBlank() &&
            ano.length == 4 && ano.all(Char::isDigit)
        AlertDialog(
            onDismissRequest = { if (!processando) formularioAberto = false },
            title = { Text(if (idEmEdicao.isEmpty()) "Adicionar veículo" else "Editar veículo") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = marca, onValueChange = { marca = it },
                        label = { Text("Marca") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = modelo, onValueChange = { modelo = it },
                        label = { Text("Modelo") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ano,
                        onValueChange = { ano = it.filter(Char::isDigit).take(4) },
                        label = { Text("Ano (4 dígitos)") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!valido) {
                        Text("Preencha marca, modelo e ano com 4 dígitos.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = valido && !processando, onClick = {
                    scope.launch {
                        processando = true
                        try {
                            repositorio.salvar(
                                Veiculo(idEmEdicao, marca.trim(), modelo.trim(), ano)
                            )
                            formularioAberto = false
                            carregarVeiculos()
                            snackbar.showSnackbar("Veículo salvo com sucesso")
                        } catch (e: Exception) {
                            snackbar.showSnackbar("Falha ao salvar. Tente novamente.")
                        } finally {
                            processando = false
                        }
                    }
                }) { Text(if (processando) "Salvando..." else "Salvar") }
            },
            dismissButton = {
                TextButton(enabled = !processando, onClick = { formularioAberto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    exclusaoPendente?.let { veiculo ->
        AlertDialog(
            onDismissRequest = { if (!processando) exclusaoPendente = null },
            title = { Text("Excluir veículo?") },
            text = { Text("Remover ${veiculo.marca} ${veiculo.modelo}? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(enabled = !processando, onClick = {
                    scope.launch {
                        processando = true
                        try {
                            repositorio.excluir(veiculo.id)
                            exclusaoPendente = null
                            carregarVeiculos()
                            snackbar.showSnackbar("Veículo excluído")
                        } catch (e: Exception) {
                            snackbar.showSnackbar("Não foi possível excluir o veículo")
                        } finally {
                            processando = false
                        }
                    }
                }) { Text(if (processando) "Excluindo..." else "Excluir") }
            },
            dismissButton = {
                TextButton(enabled = !processando, onClick = { exclusaoPendente = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CardVeiculo(
    veiculo: Veiculo,
    modifier: Modifier = Modifier,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = RosaHerDrive)
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("${veiculo.marca} ${veiculo.modelo}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium)
                    Text("Ano: ${veiculo.ano}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar ${veiculo.modelo}")
                }
                IconButton(onClick = onExcluir) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir ${veiculo.modelo}")
                }
            }
        }
    }
}
