package space.accident.virtualores.network

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import space.accident.virtualores.VirtualOres
import space.accident.virtualores.api.VirtualOreAPI
import space.accident.virtualores.client.gui.ScannerGui
import space.accident.virtualores.client.gui.widgets.RenderMapTexture

typealias BlockCoordinates = Pair<Int, Int>
typealias RenderMap = HashMap<BlockCoordinates, RenderComponent>

data class RenderComponent(
    val idComponent: Int,
    val amount: Int,
)

class FindVeinsPacket(
    val chunkX: Int,
    val chunkZ: Int,
    val centerX: Int,
    val centerZ: Int,
    val radius: Int,
    val type: Int,
) : IPacket {

    val map: RenderMap = RenderMap()

    val ores: HashMap<String, Int> = HashMap()
    val metaMap: HashMap<Short, String> = HashMap()
    var level = -1

    private fun addComponent(idComponent: Int) {
        VirtualOreAPI.getRegisterOres().find { it.id == idComponent }?.let {
            ores[it.name] = it.color
            metaMap[idComponent.toShort()] = it.name
        }
    }

    fun addRenderComponent(chX: Int, chZ: Int, idComponent: Int, amount: Int) {
        val chunk = BlockCoordinates(
            chX - (chunkX - radius) * 16,
            chZ - (chunkZ - radius) * 16,
        )
        val component = RenderComponent(idComponent, amount)
        map[chunk] = component
    }

    override fun getPacketID(): Int {
        return 0
    }

    @Suppress("UnstableApiUsage")
    override fun encode(): ByteArray {
        val out = ByteStreams.newDataOutput(1)
        out.writeInt(chunkX)
        out.writeInt(chunkZ)
        out.writeInt(centerX)
        out.writeInt(centerZ)
        out.writeInt(radius)
        out.writeInt(type)
        out.writeInt(level)

        val radius: Int = (radius * 2 + 1) * 16

        for (x in 0 until radius) {
            for (z in 0 until radius) {
                val coordinates = BlockCoordinates(x, z)
                if (map[coordinates] == null) {
                    out.writeInt(0) // idComponent
                    out.writeInt(0) // amount
                } else {
                    map[coordinates]?.apply {
                        out.writeInt(idComponent)
                        out.writeInt(amount)
                    }
                }
            }
        }
        return out.toByteArray()
    }

    override fun process() {
        ScannerGui.create(RenderMapTexture(this))
        VirtualOres.proxy.openGui()
    }

    companion object {
        fun decode(data: ByteArrayDataInput): Any {
            val packet = FindVeinsPacket(
                chunkX = data.readInt(),
                chunkZ = data.readInt(),
                centerX = data.readInt(),
                centerZ = data.readInt(),
                radius = data.readInt(),
                type = data.readInt()
            )
            packet.level = data.readInt()

            val radius = (packet.radius * 2 + 1) * 16

            for (x in 0 until radius) {
                for (z in 0 until radius) {
                    val coordinates = BlockCoordinates(x, z)
                    val idComponent = data.readInt()
                    val amount = data.readInt()
                    val component = RenderComponent(idComponent, amount)
                    packet.map[coordinates] = component
                    packet.addComponent(idComponent)
                }
            }
            return packet
        }
    }

    fun getSize(): Int {
        return (radius * 2 + 1) * 16
    }
}