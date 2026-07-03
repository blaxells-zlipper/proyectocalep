package com.example.incidencias.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.incidencias.screens.AuthScreen
import com.example.incidencias.screens.FormScreen
import com.example.incidencias.screens.HomeScreen
import com.example.incidencias.screens.ListScreen
import com.example.incidencias.viewmodel.AuthViewModel
import com.example.incidencias.viewmodel.IncidenciaViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    incidenciaViewModel: IncidenciaViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val incidenciaState by incidenciaViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(authState.user?.uid, currentRoute) {
        val user = authState.user
        if (user == null && currentRoute != null && currentRoute != NavRoutes.Auth) {
            navController.navigate(NavRoutes.Auth) {
                popUpTo(NavRoutes.Auth) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

        if (user != null && currentRoute == NavRoutes.Auth) {
            incidenciaViewModel.observarIncidencias(user.uid)
            navController.navigate(NavRoutes.Home) {
                popUpTo(NavRoutes.Auth) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Auth
        ) {
            composable(NavRoutes.Auth) {
                AuthScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onLogin = authViewModel::iniciarSesion,
                    onRegister = authViewModel::registrar,
                    onMessageShown = authViewModel::limpiarMensaje
                )
            }

            composable(NavRoutes.Home) {
                val user = authState.user ?: return@composable

                HomeScreen(
                    correoUsuario = user.email.orEmpty(),
                    onNuevaIncidencia = { navController.navigate(NavRoutes.Form) },
                    onListarIncidencias = { navController.navigate(NavRoutes.List) },
                    onCerrarSesion = { authViewModel.cerrarSesion() }
                )
            }

            composable(NavRoutes.Form) {
                val user = authState.user ?: return@composable

                FormScreen(
                    uiState = incidenciaState,
                    uidUsuario = user.uid,
                    correoUsuario = user.email.orEmpty(),
                    snackbarHostState = snackbarHostState,
                    onBack = { navController.popBackStack() },
                    onGuardar = incidenciaViewModel::guardar,
                    onMessageShown = incidenciaViewModel::limpiarMensaje
                )
            }

            composable(NavRoutes.List) {
                if (authState.user == null) return@composable

                ListScreen(
                    uiState = incidenciaState,
                    snackbarHostState = snackbarHostState,
                    onBack = { navController.popBackStack() },
                    onEstadoChange = incidenciaViewModel::actualizarEstado,
                    onEliminar = incidenciaViewModel::eliminar,
                    onMessageShown = incidenciaViewModel::limpiarMensaje
                )
            }
        }
    }
}
