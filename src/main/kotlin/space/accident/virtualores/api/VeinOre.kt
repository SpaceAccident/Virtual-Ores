package space.accident.virtualores.api

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Vein with Virtual Ore
 */
data class VeinOre @JvmOverloads constructor(
    @SerializedName("x") val x: Int,
    @SerializedName("z") val z: Int,
    @SerializedName("id") val oreId: Int,
    @SerializedName("ch") val oreChunks: ArrayList<ChunkOre> = ArrayList(),
) {
    override fun hashCode(): Int {
        return Objects.hash(x, z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (other.hashCode() == hashCode()) return true
        return (other as VeinOre).let { x == it.x && z == it.z }
    }
}