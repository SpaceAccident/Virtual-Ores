package space.accident.virtualores.common.items

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import space.accident.virtualores.ASSETS
import space.accident.virtualores.api.OreGenerator.getVeinAndChunk
import space.accident.virtualores.api.VirtualOreAPI
import space.accident.virtualores.network.FindVeinsPacket
import space.accident.virtualores.network.VirtualOresNetwork

class ScannerTool : Item() {

    @SideOnly(Side.CLIENT)
    lateinit var icon: IIcon

    override fun registerIcons(reg: IIconRegister) {
        icon = reg.registerIcon("$ASSETS:ore_scanner")
    }

    @SideOnly(Side.CLIENT)
    override fun getIconFromDamage(meta: Int): IIcon {
        return icon
    }

    override fun addInformation(
        stack: ItemStack,
        player: EntityPlayer,
        tooltip: MutableList<Any?>,
        f3: Boolean
    ) {
        tooltip += "TEST1"
    }

    init {
        setMaxStackSize(1)
        unlocalizedName = "virtual_ore_scanner"
        GameRegistry.registerItem(this, "virtual_ore_scanner")
    }

    override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack? {
        if (!world.isRemote) {

            val layer = 0
            val radius = 11

            val chX = player.posX.toInt() shr 4
            val chZ = player.posZ.toInt() shr 4

            val chunks: ArrayList<Chunk> = ArrayList()

            for (x in -radius..radius) {
                for (z in -radius..radius) {
                    if (x != -radius && x != radius && z != -radius && z != radius) {
                        chunks += world.getChunkFromChunkCoords(chX + x, chZ + z)
                    }
                }
            }
            val packet = FindVeinsPacket(chX, chZ, player.posX.toInt(), player.posZ.toInt(), radius - 1, 0)
            for (chunk in chunks) {
                VirtualOreAPI.generateRegion(chunk).also { region ->
                    region.getVeinAndChunk(chunk, layer)?.let { (veinOre, chunkOre) ->
                        VirtualOreAPI.getVirtualVeinInChunk(veinOre, layer, region.dim)?.also { ore ->
                            val size = chunkOre.size.toDouble() / ore.rangeSize.last.toDouble() * 100.0
                            for (xx in 0..15) {
                                for (zz in 0..15) {
                                    packet.addRenderComponent(
                                        chunk.xPosition * 16 + xx,
                                        chunk.zPosition * 16 + zz,
                                        ore.id,
                                        size.toInt()
                                    )
                                }
                            }
                        }
                    }
                }
            }
            packet.level = radius - 1
            VirtualOresNetwork.sendToPlayer(packet, player as EntityPlayerMP)
        }
        return super.onItemRightClick(stack, world, player)
    }
}