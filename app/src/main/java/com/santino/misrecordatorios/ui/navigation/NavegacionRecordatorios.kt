package com.santino.misrecordatorios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.santino.misrecordatorios.PantallaRecordatorios
import com.santino.misrecordatorios.ui.screens.PantallaFormularioRecordatorio
import com.santino.misrecordatorios.ui.screens.PantallaDetalleRecordatorio
import com.santino.misrecordatorios.ui.viewmodel.RecordatoriosViewModel

private object Rutas {

    const val LISTADO = "listado"

    const val NUEVO = "nuevo"

    const val DETALLE = "detalle/{id}"

    fun detalle(id: Int): String {
        return "detalle/$id"
    }
}

@Composable
fun NavegacionRecordatorios(
    viewModel: RecordatoriosViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTADO
    ) {

        // Pantalla principal
        composable(Rutas.LISTADO) {

            PantallaRecordatorios(
                viewModel = viewModel,

                onNuevo = {
                    viewModel.limpiarFormulario()
                    navController.navigate(Rutas.NUEVO)
                },

                onDetalle = { id ->
                    navController.navigate(Rutas.detalle(id))
                }
            )
        }

        // Pantalla para crear recordatorios
        composable(Rutas.NUEVO) {

            PantallaFormularioRecordatorio(
                viewModel = viewModel,

                onCancelar = {
                    viewModel.limpiarFormulario()
                    navController.popBackStack()
                },

                onGuardadoExitoso = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle
        composable(
            route = Rutas.DETALLE,

            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments
                ?.getInt("id") ?: -1

            PantallaDetalleRecordatorio(
                id = id,
                viewModel = viewModel,

                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}