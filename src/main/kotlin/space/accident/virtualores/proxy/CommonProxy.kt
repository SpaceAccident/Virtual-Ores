package space.accident.virtualores.proxy

import cpw.mods.fml.common.event.*
import space.accident.virtualores.JsonManager
import space.accident.virtualores.common.items.ScannerTool
import space.accident.virtualores.common.items.ScannerToolPrimitive
import space.accident.virtualores.config.Config

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        Config.createConfig(event.modConfigurationDirectory)
    }

    open fun init(event: FMLInitializationEvent) {
        ScannerTool()
        ScannerToolPrimitive()
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

    open fun openGui() {
    }
}