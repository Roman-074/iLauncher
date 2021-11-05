package hedgehog.tech.ilauncher.data.model

import android.graphics.drawable.Drawable


data class GridItemModel(
    var appId: Int,
    var appName: String,
    var icon: Drawable,
    var pName: String,
    var initActivity: String
)
