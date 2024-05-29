package view.Theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val ComponentColorPurple = Color(153, 51, 153)
val ComponentColorNavy = Color(0, 102, 255)
val ComponentColorBurdundy = Color(204, 0, 0)
val ComponentColorOrange = Color(255, 153, 0)
val ComponentColorBlue = Color(0, 51, 153)
val ComponentColorLavender = Color(204, 153, 255)
val ComponentColorPink = Color(255, 51, 204)
val ComponentColorSmoke = Color(102, 102, 153)
val ComponentColorWater = Color(0, 255, 255)
val ComponentColorRed = Color(230, 46, 0)

val Pink10 = Color(51, 0, 26)
val Pink20 = Color(102, 0, 53)
val Pink30 = Color(153, 0, 79)
val Pink40 = Color(204, 0, 105)
val Pink80 = Color(255, 153, 204)
val Pink90 = Color(255, 204, 230)

val DarkPink10 = Color(51, 0, 20)
val DarkPink20 = Color(102, 0, 41)
val DarkPink30 = Color(153, 0, 61)
val DarkPink40 = Color(204, 0, 82)
val DarkPink80 = Color(255, 153, 194)
val DarkPink90 = Color(255, 204, 224)

val Red10 = Color(51, 0, 0)
val Red20 = Color(102, 0, 0)
val Red30 = Color(153, 0, 0)
val Red40 = Color(204, 0, 0)
val Red80 = Color(255, 153, 153)
val Red90 = Color(255, 204, 204)

/* still pink more grey */
val PinkBgColor90 = Color(236, 223, 230)
val PinkBgColor80 = Color(217, 191, 205)
val PinkBgColor60 = Color(179, 128, 155)
val PinkBgColor30 = Color(96, 57, 78)
val PinkBgColor20 = Color(64, 38, 52)
val PinkBgColor10 = Color(32, 19, 26)

/* still pink not grey */
val PinkSurfaceColor90 = Color(249, 236, 242)
val PinkSurfaceColor70 = Color(219, 138, 176)
val PinkSurfaceColor80 = Color(231, 177, 202)
val PinkSurfaceColor60 = Color(207, 99, 149)
val PinkSurfaceColor30 = Color(117, 36, 74)
val PinkSurfaceColor20 = Color(78, 24, 49)
val PinkSurfaceColor10 = Color(20, 6, 13)

val Vintage10 = Color(51, 36, 0)
val Vintage20 = Color(102, 71, 0)
val Vintage30 = Color(153, 107, 0)
val Vintage40 = Color(204, 143, 0)
val Vintage80 = Color(255, 224, 153)
val Vintage90 = Color(255, 240, 204)

val DarkVintage10 = Color(36, 26, 15)
val DarkVintage20 = Color(72, 52, 30)
val DarkVintage30 = Color(108, 78, 45)
val DarkVintage40 = Color(144, 103, 60)
val DarkVintage80 = Color(225, 205, 183)
val DarkVintage90 = Color(240, 230, 219)

/* still vintage more grey */
val VintageBgColor95 = Color(245, 242, 240)
val VintageBgColor90 = Color(234, 230, 225)
val VintageBgColor80 = Color(213, 205, 195)
val VintageBgColor60 = Color(171, 155, 135)
val VintageBgColor30 = Color(90, 78, 63)
val VintageBgColor20 = Color(60, 52, 42)
val VintageBgColor10 = Color(30, 26, 21)

/* still vintage not grey */
val VintageSurfaceColor90 = Color(241, 233, 218)
val VintageSurfaceColor80 = Color(227, 211, 181)
val VintageSurfaceColor60 = Color(148, 116, 56)
val VintageSurfaceColor30 = Color(111, 87, 42)
val VintageSurfaceColor20 = Color(74, 58, 28)
val VintageSurfaceColor10 = Color(37, 29, 14)

val BlackAndWhite90 = Color(230, 230, 230)
val BlackAndWhite80 = Color(204, 204, 204)
val BlackAndWhite70 = Color(179, 179, 179)
val BlackAndWhite65 = Color(166, 166, 166)
val BlackAndWhite60 = Color(153, 153, 153)
val BlackAndWhite50 = Color(128, 128, 128)
val BlackAndWhite40 = Color(102, 102, 102)
val BlackAndWhite35 = Color(89, 89, 89)
val BlackAndWhite30 = Color(77, 77, 77)
val BlackAndWhite20 = Color(51, 51, 51)
val BlackAndWhite10 = Color(26, 26, 26)



val DarkColorPalette = darkColorScheme(
    primary = Pink80,
    onPrimary = Pink20,
    primaryContainer = Pink30,
    onPrimaryContainer = Pink90,
    inversePrimary = Pink40,
    secondary = DarkPink80,
    onSecondary = DarkPink20,
    secondaryContainer = DarkPink30,
    onSecondaryContainer = DarkPink90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = PinkBgColor90,
    onBackground = PinkBgColor10,
    surface = PinkSurfaceColor90,
    onSurface = PinkSurfaceColor10,
    inverseSurface = PinkBgColor90,
    inverseOnSurface = PinkBgColor10,
    surfaceVariant = PinkSurfaceColor90,
    onSurfaceVariant = PinkSurfaceColor10,
    outline = PinkSurfaceColor70
)

val LightColorPalette = lightColorScheme(
    primary = Vintage80,
    onPrimary = Vintage20,
    primaryContainer = Vintage30,
    onPrimaryContainer = Vintage90,
    inversePrimary = Vintage40,
    secondary = DarkVintage80,
    onSecondary = DarkVintage20,
    secondaryContainer = DarkVintage30,
    onSecondaryContainer = DarkVintage90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = VintageBgColor90,
    onBackground = VintageBgColor10,
    surface = VintageSurfaceColor90,
    onSurface = VintageSurfaceColor10,
    inverseSurface = VintageBgColor90,
    inverseOnSurface = VintageBgColor10,
    surfaceVariant = VintageSurfaceColor90,
    onSurfaceVariant = VintageSurfaceColor10,
    outline = VintageSurfaceColor60
)
