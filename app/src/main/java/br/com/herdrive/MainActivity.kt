package br.com.herdrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.herdrive.ui.ConsultaFipeScreen
import br.com.herdrive.ui.HerDriveTheme
import br.com.herdrive.ui.InicioScreen
import br.com.herdrive.ui.RosaHerDrive
import br.com.herdrive.ui.VeiculosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HerDriveTheme { AplicativoHerDrive() } }
    }
}

private data class Aba(val rota: String, val titulo: String, val icone: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AplicativoHerDrive() {
    val navController = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val abas = listOf(
        Aba("inicio", "Início", Icons.Default.Home),
        Aba("veiculos", "Meus carros", Icons.Default.DirectionsCar),
        Aba("fipe", "Tabela FIPE", Icons.Default.Search)
    )
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route ?: "inicio"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HerDrive", color = RosaHerDrive, fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NavigationBar {
                abas.forEach { aba ->
                    NavigationBarItem(
                        selected = rotaAtual == aba.rota,
                        onClick = {
                            if (rotaAtual != aba.rota) {
                                navController.navigate(aba.rota) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                        label = { Text(aba.titulo) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("inicio") {
                InicioScreen(
                    onVeiculosClick = { navController.navigate("veiculos") },
                    onFipeClick = { navController.navigate("fipe") }
                )
            }
            composable("veiculos") { VeiculosScreen(snackbar) }
            composable("fipe") { ConsultaFipeScreen(snackbar) }
        }
    }
}
