package com.primeme.app.ui.localization

import androidx.compose.runtime.compositionLocalOf

val LocalIsZulu = compositionLocalOf { false }

fun translate(isZulu: Boolean, english: String, isiZulu: String): String =
    if (isZulu) isiZulu else english
