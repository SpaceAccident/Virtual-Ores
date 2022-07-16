package space.accident.virtualores

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.minecraftforge.common.DimensionManager
import space.accident.virtualores.api.TypeFluidVein
import space.accident.virtualores.api.VirtualAPI
import space.accident.virtualores.api.VirtualAPI.GENERATED_REGIONS_VIRTUAL_FLUIDS
import space.accident.virtualores.api.VirtualAPI.GENERATED_REGIONS_VIRTUAL_ORES
import space.accident.virtualores.api.fluids.RegionFluid
import space.accident.virtualores.api.ores.RegionOre
import space.accident.virtualores.extras.TypeFluidVeinSerialized
import java.io.File
import java.io.FileWriter
import java.util.*

object JsonManager {

    private var WORLD_DIRECTORY: File? = null
    private const val ROOT_FOLDER = "SpaceAccident"
    private const val VIRTUAL_ORE_FOLDER = "VirtualOres"
    private const val VIRTUAL_FLUID_FOLDER = "VirtualFluids"

    private lateinit var rootDirectory: File
    private lateinit var oresDirectory: File
    private lateinit var fluidsDirectory: File

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(TypeFluidVein::class.java, TypeFluidVeinSerialized())
        .create()

    private fun initData() {
        WORLD_DIRECTORY = DimensionManager.getCurrentSaveRootDirectory()
        if (WORLD_DIRECTORY == null) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] ERROR NOT SET WORLD DIRECTORY"))
        }
        rootDirectory = File(WORLD_DIRECTORY, ROOT_FOLDER)
        oresDirectory = File(rootDirectory, VIRTUAL_ORE_FOLDER)
        fluidsDirectory = File(rootDirectory, VIRTUAL_FLUID_FOLDER)

        if (!rootDirectory.isDirectory && !rootDirectory.mkdirs()) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] Failed to create ${rootDirectory.absolutePath}"))
        }
        if (!oresDirectory.isDirectory && !oresDirectory.mkdirs()) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] Failed to create ${oresDirectory.absolutePath}"))
        }
        if (!fluidsDirectory.isDirectory && !fluidsDirectory.mkdirs()) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] Failed to create ${fluidsDirectory.absolutePath}"))
        }
    }

    private fun clearData() {
        GENERATED_REGIONS_VIRTUAL_ORES.clear()
        GENERATED_REGIONS_VIRTUAL_FLUIDS.clear()
    }

    fun save() {
        runBlocking(Dispatchers.IO) {
            saveOres()
            saveFluids()
        }

        clearData()
        WORLD_DIRECTORY = null
    }

    fun load() {
        initData()
        clearData()

        runBlocking {
            loadOres()
            loadFluids()
        }
    }

    private fun loadOres() {
        if (!oresDirectory.isDirectory) return
        oresDirectory.listFiles()?.forEach { folderDim ->
            if (folderDim.isDirectory) {
                var currentDim = 0
                val dimRegions = HashMap<Int, RegionOre>()
                folderDim.listFiles()?.forEach { fileRegion ->
                    fileRegion.bufferedReader().use {
                        val reg = gson.fromJson(it, RegionOre::class.java)
                        val hash = Objects.hash(reg.xRegion, reg.zRegion, reg.dim)
                        currentDim = reg.dim
                        dimRegions[hash] = reg
                    }
                }
                GENERATED_REGIONS_VIRTUAL_ORES[currentDim] = dimRegions
            }
        }
        VirtualAPI.resizeOreVeins()
    }

    private fun saveOres() {
        if (GENERATED_REGIONS_VIRTUAL_ORES.isEmpty()) return
        for ((dim, regions) in GENERATED_REGIONS_VIRTUAL_ORES) {
            val folderDim = File(oresDirectory, "DIM$dim")
            if (folderDim.mkdirs()) {
                for ((_, region) in regions) {
                    val fileRegion = File(folderDim, "r.${region.xRegion}.${region.zRegion}.json")
                    FileWriter(fileRegion).buffered().use {
                        gson.toJson(region, it)
                    }
                }
            }
        }
    }

    private fun loadFluids() {
        if (!fluidsDirectory.isDirectory) return
        fluidsDirectory.listFiles()?.forEach { folderDim ->
            if (folderDim.isDirectory) {
                var currentDim = 0
                val dimRegions = HashMap<Int, RegionFluid>()
                folderDim.listFiles()?.forEach { fileRegion ->
                    fileRegion.bufferedReader().use {
                        val reg = gson.fromJson(it, RegionFluid::class.java)
                        val hash = Objects.hash(reg.xRegion, reg.zRegion, reg.dim)
                        currentDim = reg.dim
                        dimRegions[hash] = reg
                    }
                }
                GENERATED_REGIONS_VIRTUAL_FLUIDS[currentDim] = dimRegions
            }
        }
        VirtualAPI.resizeFluidVeins()
    }

    private fun saveFluids() {
        if (GENERATED_REGIONS_VIRTUAL_FLUIDS.isEmpty()) return
        for ((dim, regions) in GENERATED_REGIONS_VIRTUAL_FLUIDS) {
            val folderDim = File(fluidsDirectory, "DIM$dim")
            if (folderDim.mkdirs()) {
                for ((_, region) in regions) {
                    val fileRegion = File(folderDim, "r.${region.xRegion}.${region.zRegion}.json")
                    FileWriter(fileRegion).buffered().use {
                        gson.toJson(region, it)
                    }
                }
            }
        }
    }
}