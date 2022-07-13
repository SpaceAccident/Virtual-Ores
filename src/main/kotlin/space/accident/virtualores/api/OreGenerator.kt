package space.accident.virtualores.api

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.world.chunk.Chunk
import space.accident.virtualores.api.VirtualOreAPI.LAYERS_VIRTUAL_ORES
import space.accident.virtualores.api.VirtualOreAPI.REGIONS_VIRTUAL_ORES
import space.accident.virtualores.api.VirtualOreAPI.getVirtualOre
import java.awt.Color
import java.util.*
import kotlin.random.nextInt

/**
 * Singleton Virtual Ore Generator
 */
object OreGenerator {

    private const val SHIFT_REGION_FROM_CHUNK = 5
    private const val SHIFT_VEIN_FROM_REGION = 3
    private const val SHIFT_CHUNK_FROM_VEIN = 2
    private const val CHUNK_COUNT_IN_VEIN_COORDINATE = 4
    private const val VEIN_COUNT_IN_REGIN_COORDINATE = 4

    init {
        VirtualOreAPI.registerOre(
            VirtualOreLayer(
                VirtualOreVein(
                    0,
                    0, "test",
                    40.0,
                    1000..5000,
                    Color.CYAN.hashCode(),
                    listOf(0, 1),
                    listOf(VirtualOreComponent(ItemStack(Items.leather, 1), 100)),
                )
            )
        )
        VirtualOreAPI.registerOre(
            VirtualOreLayer(
                VirtualOreVein(
                    1,
                    0, "test2",
                    20.0,
                    4000..7000,
                    Color.WHITE.hashCode(),
                    listOf(0, 1),
                    listOf(VirtualOreComponent(ItemStack(Items.emerald, 1), 100)),
                )
            )
        )
    }

    /**
     * Generate Region Ore by Minecraft Chunk
     */
    fun Chunk.createOreRegion() {
        val dim = worldObj.provider.dimensionId
        getChunkVein().let { ch ->
            RegionOre(ch.x shr SHIFT_REGION_FROM_CHUNK, ch.z shr SHIFT_REGION_FROM_CHUNK, dim).let { reg ->
                val hash = Objects.hash(reg.x, reg.z, dim)
                if (!REGIONS_VIRTUAL_ORES.contains(hash)) {
                    reg.generate()
                    REGIONS_VIRTUAL_ORES[hash] = reg
                } else {
                    REGIONS_VIRTUAL_ORES[hash]
                }
            }
        }
    }

    /**
     * Set size of Virtual Ore
     *
     * @param ore virtual ore
     */
    private fun ChunkOre.setSize(ore: VirtualOreVein) {
        size = kotlin.random.Random.nextInt(ore.rangeSize)
    }

    /**
     * Get Ore Chunk by Minecraft Chunk
     */
    private fun Chunk.getChunkVein(): ChunkOre {
        return chunkCoordIntPair.let {
            print("ORE CHUNK ($xPosition $zPosition)")
            ChunkOre(it.chunkXPos, it.chunkZPos)
        }
    }

    /**
     * Generate Ore Vein by Virtual Ore
     *
     * @param ore virtual ore
     */
    private fun VeinOre.generate(ore: VirtualOreVein) {
        for (x in 0 until CHUNK_COUNT_IN_VEIN_COORDINATE) {
            for (z in 0 until CHUNK_COUNT_IN_VEIN_COORDINATE) {
                oreChunks += ChunkOre(
                    x = (x shl SHIFT_CHUNK_FROM_VEIN) + x,
                    z = (z shl SHIFT_CHUNK_FROM_VEIN) + z,
                ).apply { setSize(ore) }
            }
        }
    }

    /**
     * Generate Ore Region with all layers
     */
    private fun RegionOre.generate() {
        for (layer in 0 until LAYERS_VIRTUAL_ORES) {
            val rawVeins = ArrayList<VeinOre>()
            for (xx in 0 until VEIN_COUNT_IN_REGIN_COORDINATE) {
                for (zz in 0 until VEIN_COUNT_IN_REGIN_COORDINATE) {
                    getVirtualOre(layer, dim)?.also { ore ->
                        VeinOre(
                            x = (x shl SHIFT_VEIN_FROM_REGION) + xx,
                            z = (z shl SHIFT_VEIN_FROM_REGION) + zz,
                            oreId = ore.id,
                        ).also { vein ->
                            vein.generate(ore)
                            rawVeins += vein
                        }
                    }
                }
            }
            this.veins[layer] = rawVeins
        }
    }
}