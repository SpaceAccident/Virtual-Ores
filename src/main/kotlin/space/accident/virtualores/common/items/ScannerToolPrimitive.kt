package space.accident.virtualores.common.items

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World
import space.accident.virtualores.api.OreGenerator.createOreRegion
import space.accident.virtualores.api.OreGenerator.getVeinAndChunk
import space.accident.virtualores.api.VirtualOreAPI

class ScannerToolPrimitive : Item() {

    init {
        setMaxStackSize(1)
        unlocalizedName = "virtual_ore_scanner_primitive"
        GameRegistry.registerItem(this, "virtual_ore_scanner_primitive")
    }

    override fun onItemUseFirst(
        stack: ItemStack,
        player: EntityPlayer,
        world: World,
        x: Int,
        y: Int,
        z: Int,
        side: Int,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {

        fun EntityPlayer.send(msg: String) {
            addChatComponentMessage(ChatComponentText(msg))
        }

        if (!world.isRemote) {
            val chunk = world.getChunkFromBlockCoords(x, z)
            chunk.createOreRegion().apply {
                getVeinAndChunk(chunk, 0)?.let { (veinOre, chunkOre) ->
                    VirtualOreAPI.getVirtualOreVeinInChunk(veinOre, 0, dim)?.also { ore ->
                        player.send("${ore.name}, size: ${chunkOre.size.toDouble() / ore.rangeSize.last.toDouble() * 100.0}")
                    }
                }
            }
        }

        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
    }
}