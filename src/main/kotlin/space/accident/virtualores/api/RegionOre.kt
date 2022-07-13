package space.accident.virtualores.api

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Region with Virtual Ore
 */
data class RegionOre @JvmOverloads constructor(
    @SerializedName("x") val x: Int,
    @SerializedName("z") val z: Int,
    @SerializedName("d") val dim: Int,
    @SerializedName("veins") val veins: HashMap<Int, ArrayList<VeinOre>> = HashMap()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (other.hashCode() == hashCode()) return true
        return (other as RegionOre).let {
            x == it.x && z == it.z && dim == it.dim
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(x, z, dim)
    }
}