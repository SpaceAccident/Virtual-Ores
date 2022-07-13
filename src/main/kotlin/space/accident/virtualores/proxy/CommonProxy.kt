package space.accident.virtualores.proxy

import cpw.mods.fml.common.event.*
import space.accident.virtualores.JsonManager
import space.accident.virtualores.api.ScannerTool

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
    }

    open fun init(event: FMLInitializationEvent) {
        ScannerTool()
    }

    open fun postInit(event: FMLPostInitializationEvent) {
    }

    open fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
    }

    open fun serverStarting(event: FMLServerStartingEvent) {
    }

    open fun serverStarted(event: FMLServerStartedEvent) {
        JsonManager.load()
    }

    open fun serverStopping(event: FMLServerStoppingEvent) {
        JsonManager.save()
    }

    open fun serverStopped(event: FMLServerStoppedEvent) {
    }
}