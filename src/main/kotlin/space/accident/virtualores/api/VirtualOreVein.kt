package space.accident.virtualores.api

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import kotlin.random.Random.Default.nextDouble

/**
 * Type for Virtual Ore
 */
typealias VirtualOreTypeComponent = ItemStack

/**
 * Type for Special Drilling Fluid (for mining Virtual Ore)
 */
typealias VirtualSpecialTypeComponent = FluidStack

/**
 * Virtual Ore Instance
 *
 * @param id ID of ore
 * @param layer layer of mining
 * @param name name of ore
 * @param maxWeight max weight by generation on the world
 * @param rangeSize ore quantity range
 * @param color color of vein
 * @param dimensions dimensions in which there is ore
 * @param ores components of ore vein
 * @param special special drilling component
 */
data class VirtualOreVein(
    val id: Int,
    val layer: Int,
    val name: String,
    var maxWeight: Double,
    val rangeSize: IntRange,
    val color: Int,
    val dimensions: List<Int>,
    val ores: List<VirtualOreComponent>,
    val special: VirtualSpecialTypeComponent? = null,
)

/**
 * Wrapper for Virtual ore with implement Random Chooser
 *
 * @param ore virtual ore
 */
data class VirtualOreLayer(
    val ore: VirtualOreVein,
    override val maxWeight: Double = ore.maxWeight,
    override var weight: Double = nextDouble(0.0, maxWeight),
    override val reduceCoefficient: Double = 2.5,
    override val increaseCoefficient: Double = 0.5,
) : RandomItemChooser.ItemChooser

/**
 * Vein Component for Virtual Ore
 *
 * @param ore type for virtual ore
 * @param chance chance for mining
 */
data class VirtualOreComponent(
    val ore: VirtualOreTypeComponent,
    val chance: Int,
)