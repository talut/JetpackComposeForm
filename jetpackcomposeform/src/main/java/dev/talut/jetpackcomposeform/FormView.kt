package dev.talut.jetpackcomposeform

import androidx.compose.runtime.Composable


@Composable
inline fun <T> FormView(
    values: T,
    content: @Composable () -> Unit,
) {


    content()
}
