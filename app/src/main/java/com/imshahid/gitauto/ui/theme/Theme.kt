package com.imshahid.gitauto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val Brand = Color(0xFF07965F)
val Income = Color(0xFF078A55)
val Expense = Color(0xFFD92D3A)

private val LightColors = lightColorScheme(primary=Brand,secondary=Color(0xFF386A58),background=Color(0xFFF8FAF8),surface=Color.White,error=Expense)
private val DarkColors = darkColorScheme(primary=Color(0xFF55DB9C),secondary=Color(0xFF9BD1B7),background=Color(0xFF0D1511),surface=Color(0xFF141D18),error=Color(0xFFFFB3B7))

@Composable fun XpnseTrackTheme(darkTheme:Boolean=isSystemInDarkTheme(),content:@Composable()->Unit){
 MaterialTheme(colorScheme=if(darkTheme) DarkColors else LightColors,typography=Typography(),shapes=Shapes(extraSmall=RoundedCornerShape(8.dp),small=RoundedCornerShape(12.dp),medium=RoundedCornerShape(18.dp),large=RoundedCornerShape(24.dp)),content=content)
}
