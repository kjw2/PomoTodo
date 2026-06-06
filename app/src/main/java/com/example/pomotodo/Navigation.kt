package com.example.pomotodo

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pomotodo.ui.about.AboutScreen
import com.example.pomotodo.ui.about.OpenSourceLicensesScreen
import com.example.pomotodo.ui.main.MainScreen

@Composable
fun MainNavigation(onExitRequested: () -> Unit) {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = {
      if (backStack.size > 1) {
        backStack.removeLastOrNull()
      } else {
        onExitRequested()
      }
    },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) }, modifier = Modifier.safeDrawingPadding().padding(16.dp))
        }
        entry<About> {
          AboutScreen(
            onBackClick = { backStack.removeLastOrNull() },
            onOpenSourceClick = { backStack.add(OpenSourceLicenses) },
            modifier = Modifier.safeDrawingPadding().padding(16.dp),
          )
        }
        entry<OpenSourceLicenses> {
          OpenSourceLicensesScreen(
            onBackClick = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding().padding(16.dp),
          )
        }
      },
    transitionSpec = {
      EnterTransition.None togetherWith ExitTransition.None
    },
    popTransitionSpec = {
      EnterTransition.None togetherWith ExitTransition.None
    },
    predictivePopTransitionSpec = {
      EnterTransition.None togetherWith ExitTransition.None
    },
  )
}
