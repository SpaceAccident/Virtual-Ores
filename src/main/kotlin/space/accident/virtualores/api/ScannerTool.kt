package space.accident.virtualores.api

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ScannerTool : Item() {

    init {
        setMaxStackSize(1)
        unlocalizedName = "virtual_ore_scanner"
        GameRegistry.registerItem(this, "virtual_ore_scanner")
    }

    override fun onItemUseFirst(
        stack: ItemStack,
        p: EntityPlayer,
        world: World,
        x: Int,
        y: Int,
        z: Int,
        side: Int,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (p is EntityPlayerMP) {
            VirtualOreAPI.generateRegion(world.getChunkFromBlockCoords(x, y))
        }
        return super.onItemUseFirst(stack, p, world, x, y, z, side, hitX, hitY, hitZ)
    }
}