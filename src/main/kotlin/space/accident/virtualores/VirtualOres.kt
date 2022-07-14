package space.accident.virtualores

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.*
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import space.accident.virtualores.api.VirtualOreComponent
import space.accident.virtualores.api.VirtualOreVein
import space.accident.virtualores.client.GuiHandler
import space.accident.virtualores.network.VirtualOresNetwork
import space.accident.virtualores.proxy.CommonProxy
import java.awt.Color

@Mod(
    modid = MODID,
    version = VERSION,
    name = MODNAME,
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:forgelin;"
)
object VirtualOres {

    @SidedProxy(clientSide = "$GROUPNAME.proxy.ClientProxy", serverSide = "$GROUPNAME.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    @JvmStatic
    @Mod.InstanceFactory
    fun instance() = VirtualOres

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        VirtualOresNetwork
        GuiHandler()
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {

        repeat(100) {
            VirtualOreVein(
                it + 1, 0, "TEST #$it", 40.0, 5000..100000,
                Color(it, it, it).rgb,
                listOf(1, 0, -1), listOf(VirtualOreComponent(ItemStack(Items.leather, 1), 50))
            )
        }
        proxy.postInit(event)
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        proxy.serverAboutToStart(event)
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        proxy.serverStarting(event)
    }

    @Mod.EventHandler
    fun serverStarted(event: FMLServerStartedEvent) {
        proxy.serverStarted(event)
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        proxy.serverStopping(event)
    }

    @Mod.EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        proxy.serverStopped(event)
    }
}