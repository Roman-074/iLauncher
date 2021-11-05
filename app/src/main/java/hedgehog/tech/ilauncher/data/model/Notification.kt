package hedgehog.tech.ilauncher.data.model

import android.graphics.drawable.Drawable

data class Notification(
    val id: Int,
    val title: String,
    val icon: Drawable?,
    val action: String,
    val mainInfo: String,
    val additionalInfo: String
)
