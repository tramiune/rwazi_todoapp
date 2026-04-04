package com.rwazi.app.todo.util

import androidx.annotation.StyleRes
import androidx.core.graphics.toColorInt
import com.rwazi.app.todo.R
import javax.inject.Inject
import javax.inject.Singleton

data class ThemePalette(
    @StyleRes val themeResId: Int,
    val backgroundColor: Int,
    val isLight: Boolean,
    val isSelected: Boolean = false
)

@Singleton
class DynamicBackgroundUtils @Inject constructor() {

    // Curated "Premium" Palettes that match the themes in themes.xml
    private val THEME_PALETTES = listOf(
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_IndigoNight,
            backgroundColor = "#1A237E".toColorInt(), // Deep Indigo
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_OceanDeep,
            backgroundColor = "#002171".toColorInt(), // Very Deep Blue
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_ForestWhisper,
            backgroundColor = "#003308".toColorInt(), // Very Deep Green
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_SoftCloud,
            backgroundColor = "#ECEFF1".toColorInt(), // Light Blue Grey
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_PeachSunset,
            backgroundColor = "#FFE0B2".toColorInt(), // Light Peach
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_MidnightBlue,
            backgroundColor = "#0D47A1".toColorInt(), // Deep Blue
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_Rosewood,
            backgroundColor = "#4A001F".toColorInt(), // Deep Rose
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_TealDepth,
            backgroundColor = "#002E2C".toColorInt(), // Deep Teal
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_RoyalPurple,
            backgroundColor = "#2E003E".toColorInt(), // Deep Purple
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_Charcoal,
            backgroundColor = "#102027".toColorInt(), // Deep Grey
            isLight = false
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_MintBreeze,
            backgroundColor = "#E0F2F1".toColorInt(), // Light Mint
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_LavenderMist,
            backgroundColor = "#F3E5F5".toColorInt(), // Light Lavender
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_GoldenSand,
            backgroundColor = "#FFF8E1".toColorInt(), // Light Sand
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_SakuraPink,
            backgroundColor = "#FCE4EC".toColorInt(), // Light Pink
            isLight = true
        ),
        ThemePalette(
            themeResId = R.style.Theme_ToDoApp_Palette_SkySerenity,
            backgroundColor = "#E1F5FE".toColorInt(), // Light Sky Blue
            isLight = true
        )
    )

    /**
     * Returns the full list of curated theme palettes.
     */
    fun getPalettes(): List<ThemePalette> {
        return THEME_PALETTES
    }

    /**
     * Gets a specific palette by its theme resource ID.
     */
    fun getPalette(themeResId: Int): ThemePalette? {
        return THEME_PALETTES.find { it.themeResId == themeResId }
    }

    /**
     * Randomly picks one of the curated theme palettes.
     */
    fun getRandomPalette(): ThemePalette {
        return THEME_PALETTES.random()
    }
}
