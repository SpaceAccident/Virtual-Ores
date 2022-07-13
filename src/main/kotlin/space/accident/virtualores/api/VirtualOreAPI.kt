package space.accident.virtualores.api

import net.minecraft.world.chunk.Chunk
import space.accident.virtualores.api.OreGenerator.createOreRegion

/**
 * Virtual Ore API
 */
object VirtualOreAPI {

    /**
     * Set of types Virtual Ores
     */
    @JvmStatic
    val VIRTUAL_ORES: HashSet<VirtualOreLayer> = HashSet(200)

    /**
     * Current generated Virtual Ores
     */
    @JvmStatic
    val REGIONS_VIRTUAL_ORES: HashMap<Int, RegionOre> = HashMap()

    /**
     * Max layers of Virtual ores
     */
    @JvmStatic
    val LAYERS_VIRTUAL_ORES = 2

    /**
     * Get Virtual Ore by layer and dimension
     *
     * @param layer layer of virtual ore
     * @param dim minecraft dimension
     */
    @JvmStatic
    fun getVirtualOre(layer: Int, dim: Int): VirtualOreVein? {
        return VIRTUAL_ORES.filter { it.ore.layer == layer && it.ore.dimensions.contains(dim) }.let {
            RandomItemChooser.chooseOnWeight(it)?.ore
        }
    }

    /**
     * Get registered Virtual Ores
     */
    @JvmStatic
    fun getRegisterOres(): List<VirtualOreLayer> {
        return VIRTUAL_ORES.toList()
    }

    /**
     * Register Virtual Ore
     *
     * @param ore virtual ore with layer
     */
    @JvmStatic
    fun registerOre(ore: VirtualOreLayer) {
        VIRTUAL_ORES += ore
    }

    /**
     * Generate Virtual Ore by Minecraft Chunk
     *
     * @param chunk minecraft chunk
     */
    fun generateRegion(chunk: Chunk) {
        chunk.createOreRegion()
    }
}