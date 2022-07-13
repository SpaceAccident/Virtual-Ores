package space.accident.virtualores.proxy

import cpw.mods.fml.common.event.*

class ClientProxy : CommonProxy() {

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
    }

    override fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        super.serverAboutToStart(event)
    }

    override fun serverStarting(event: FMLServerStartingEvent) {
        super.serverStarting(event)
    }

    override fun serverStarted(event: FMLServerStartedEvent) {
        super.serverStarted(event)
    }

    override fun serverStopping(event: FMLServerStoppingEvent) {
        super.serverStopping(event)
    }

    override fun serverStopped(event: FMLServerStoppedEvent) {
        super.serverStopped(event)
    }
}