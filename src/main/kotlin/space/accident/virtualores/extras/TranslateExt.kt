package space.accident.virtualores.extras

import net.minecraft.util.StatCollector

fun String.toTranslate(): String {
    return StatCollector.translateToLocal(this)
}