package br.com.herdrive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.herdrive.data.Endereco
import br.com.herdrive.data.ViaCepClient
import kotlinx.coroutines.launch

@Composable
fun ConsultaCepScreen(snackbar: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    var cep by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf<Endereco?>(null) }
    var carregando by remember { mutableStateOf(false) }
    val cepValido = cep.length == 8 && cep.all(Char::isDigit)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 860.dp)) {
            Text("Consulta de CEP", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Consulte o endereço de uma região antes de procurar serviços automotivos.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(18.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { novo ->
                            cep = novo.filter(Char::isDigit).take(8)
                            endereco = null
                        },
                        label = { Text("CEP (8 números)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                carregando = true
                                endereco = null
                                try {
                                    val resultado = ViaCepClient.api.consultar(cep)
                                    if (resultado.erro == true) {
                                        snackbar.showSnackbar("CEP não encontrado")
                                    } else {
                                        endereco = resultado
                                    }
                                } catch (e: Exception) {
                                    snackbar.showSnackbar("Não foi possível consultar. Verifique a conexão.")
                                } finally {
                                    carregando = false
                                }
                            }
                        },
                        enabled = cepValido && !carregando
                    ) { Text("Consultar endereço") }
                    if (!cepValido) {
                        Text("Digite 8 números para consultar.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    if (carregando) { CircularProgressIndicator() }
                }
            }

            endereco?.let { info ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Endereço encontrado", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium)
                        Text("CEP: ${info.cep.orEmpty()}")
                        Text("Rua: ${info.logradouro?.ifBlank { "Não informada" } ?: "Não informada"}")
                        Text("Bairro: ${info.bairro?.ifBlank { "Não informado" } ?: "Não informado"}")
                        Text("Cidade: ${info.localidade.orEmpty()}")
                        Text("UF: ${info.uf.orEmpty()}")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Esta consulta utiliza a API ViaCEP e exibe endereços. Não pesquisa nem avalia oficinas.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
