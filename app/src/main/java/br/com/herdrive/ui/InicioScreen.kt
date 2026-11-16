package br.com.herdrive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InicioScreen(onVeiculosClick: () -> Unit, onFipeClick: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val telaLarga = maxWidth >= 700.dp

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.widthIn(max = 900.dp)) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "SUA JORNADA AUTOMOTIVA",
                            style = MaterialTheme.typography.labelLarge,
                            color = RosaHerDrive,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Cuidar do seu carro pode ser simples.",
                            fontSize = if (telaLarga) 34.sp else 28.sp,
                            lineHeight = if (telaLarga) 40.sp else 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoHerDrive
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Organize seus veículos e consulte preços de referência pela Tabela FIPE.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(18.dp))
                        Button(onClick = onVeiculosClick) {
                            Text("Cadastrar meu carro")
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "Acessos rápidos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                if (telaLarga) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CardAcesso(
                            "Meus carros", "Cadastre, busque e edite veículos.",
                            Icons.Default.DirectionsCar, onVeiculosClick, Modifier.weight(1f)
                        )
                        CardAcesso(
                            "Tabela FIPE", "Consulte preços de carros por marca, modelo e ano.",
                            Icons.Default.Search, onFipeClick, Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CardAcesso(
                            "Meus carros", "Cadastre, busque e edite veículos.",
                            Icons.Default.DirectionsCar, onVeiculosClick, Modifier.fillMaxWidth()
                        )
                        CardAcesso(
                            "Tabela FIPE", "Consulte preços de carros por marca, modelo e ano.",
                            Icons.Default.Search, onFipeClick, Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardAcesso(
    titulo: String,
    descricao: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icone, contentDescription = null, tint = RosaHerDrive)
            Spacer(Modifier.height(12.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(descricao, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
