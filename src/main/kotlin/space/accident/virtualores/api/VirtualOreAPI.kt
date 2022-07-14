package space.accident.virtualores.api

import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.DimensionManager
import space.accident.virtualores.api.OreGenerator.createOreRegion
import space.accident.virtualores.api.OreGenerator.getVeinAndChunk
import java.util.*

/**
 * Virtual Ore API
 */
object VirtualOreAPI {

    /**
     * Set of types Virtual Ores
     */
    @JvmStatic
    val VIRTUAL_ORES: HashSet<VirtualOreVein> = HashSet(200)

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

    @JvmStatic
    var RESIZE_VEINS: HashMap<Int, Map<Int, List<VirtualOreVein>>> = HashMap()

    /**
     * Get Virtual Ore by layer and dimension
     *
     * @param layer layer of virtual ore
     * @param dim minecraft dimension
     */
    @JvmStatic
    fun getRandomVirtualOre(layer: Int, dim: Int, seed: Long): VirtualOreVein? {
        if (!RESIZE_VEINS.contains(dim)) return null
        if (!RESIZE_VEINS[dim]!!.containsKey(layer)) return null
        var total = 0.0
        RESIZE_VEINS[dim]!![layer]!!.forEach { candidate ->
            total += candidate.maxWeight
            candidate.weight = total
        }
        val rand = Random() //todo create general random with seed by world
        val medium = rand.nextDouble() * total

        RESIZE_VEINS[dim]!![layer]!!.forEach { candidate ->
            if (candidate.weight > medium) {
                candidate.reduceWeight()
                return candidate
            } else {
                candidate.increaseWeight()
            }
        }
        return null
    }

    /**
     * Resize registered ores
     */
    fun resizeVeins() {
        RESIZE_VEINS.clear()
        for (dim in DimensionManager.getIDs()) {
            val resizedLayersVeins = HashMap<Int, List<VirtualOreVein>>()
            for (layer in 0 until LAYERS_VIRTUAL_ORES) {
                val list = ArrayList<VirtualOreVein>()
                for (virtualOre in VIRTUAL_ORES) {
                    if (virtualOre.layer == layer) {
                        for (dimension in virtualOre.dimensions) {
                            if (dimension == dim) {
                                list += virtualOre
                            }
                        }
                    }
                }
                resizedLayersVeins[layer] = list
            }
            RESIZE_VEINS[dim] = resizedLayersVeins
        }
    }

    /**
     * Get virtual vein from chunk
     *
     * @param vein generated ore vein
     * @param layer layer
     * @param dim dimension world
     */
    @JvmStatic
    fun getVirtualVeinInChunk(vein: VeinOre, layer: Int, dim: Int): VirtualOreVein? {
        return VIRTUAL_ORES.filter { it.layer == layer && it.dimensions.contains(dim) }.let { veins ->
            veins.find { it.id == vein.oreId }
        }
    }

    /**
     * Get virtual vein by id
     *
     * @param oreId ID virtual vein
     */
    @JvmStatic
    fun getVirtualVeinById(oreId: Int): VirtualOreVein {
        return VIRTUAL_ORES.first { it.id == oreId }
    }

    /**
     * Get registered Virtual Ores
     */
    @JvmStatic
    fun getRegisterOres(): List<VirtualOreVein> {
        return VIRTUAL_ORES.toList()
    }

    /**
     * Register Virtual Ore
     *
     * @param ore virtual ore with layer
     */
    @JvmStatic
    fun registerOre(ore: VirtualOreVein) {
        if (VIRTUAL_ORES.any { it.id == ore.id }) {
            throw ConcurrentModificationException(
                "Ore vein must not use the identifier of the other ore vein: ${ore.name}"
            )
        }
        VIRTUAL_ORES += ore
    }

    /**
     * Generate Virtual Ore by Minecraft Chunk
     *
     * @param chunk minecraft chunk
     */
    @JvmStatic
    fun generateRegion(chunk: Chunk): RegionOre {
        return chunk.createOreRegion()
    }

    /**
     * Extract component from chunk
     *
     * @param chunk current chunk
     * @param layer layer
     */
    @[JvmStatic JvmOverloads]
    fun extractFromChunk(chunk: Chunk, layer: Int, reduceCoefficient: Int = 1): Pair<VirtualOreVein?, Int> {
        return chunk.getVeinAndChunk(layer)?.let { (veinOre, chunkOre) ->
            getVirtualVeinById(veinOre.oreId) to if (!chunkOre.hasExtract(reduceCoefficient)) 0 else reduceCoefficient
        } ?: (null to 0)
    }
}