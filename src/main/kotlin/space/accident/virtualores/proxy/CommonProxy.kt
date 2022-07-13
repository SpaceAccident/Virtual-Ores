package space.accident.virtualores.proxy

import cpw.mods.fml.common.event.*

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun postInit(event: FMLPostInitializationEvent) {
    }

    open fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
    }

    open fun serverStarting(event: FMLServerStartingEvent) {
    }

    open fun serverStarted(event: FMLServerStartedEvent) {
    }

    open fun serverStopping(event: FMLServerStoppingEvent) {
    }

    open fun serverStopped(event: FMLServerStoppedEvent) {
    }
}