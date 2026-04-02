package com.rwazi.app.todo.util

import androidx.core.graphics.toColorInt
import kotlin.random.Random

object ColorUtils {
    private val NoteColors = listOf(
        "#FFF8E1", // Light Yellow
        "#E8F5E9", // Light Green
        "#E3F2FD", // Light Blue
        "#F3E5F5", // Light Purple
        "#FFF3E0", // Light Orange
        "#F1F8E9", // Light Lime
        "#E0F2F1", // Light Teal
        "#EFEBE9", // Light Brown
        "#ECEFF1", // Light Blue Grey
        "#FAFAFA"  // Light Grey
    )

    fun getRandomNoteColor(): Int {
        return NoteColors[Random.nextInt(NoteColors.size)].toColorInt()
    }

    fun getRandomAppBackgroundColor(): Int {
        val lightColors = listOf("#FDFCFB", "#F5F7FA", "#E3EEFF", "#F6F5F7", "#F9F9F9")
        return lightColors[Random.nextInt(lightColors.size)].toColorInt()
    }

    fun getRandomSoftColor(): Int {
        val softColors = listOf(
            "#FFEBEE",
            "#FCE4EC",
            "#F3E5F5",
            "#EDE7F6",
            "#E8EAF6",
            "#E3F2FD",
            "#E1F5FE",
            "#E0F7FA",
            "#E0F2F1",
            "#E8F5E9"
        )
        return softColors[Random.nextInt(softColors.size)].toColorInt()
    }
}
