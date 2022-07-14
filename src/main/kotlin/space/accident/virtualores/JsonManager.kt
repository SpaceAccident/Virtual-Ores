package space.accident.virtualores

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.minecraftforge.common.DimensionManager
import space.accident.virtualores.api.RegionOre
import space.accident.virtualores.api.VirtualOreAPI
import space.accident.virtualores.api.VirtualOreAPI.REGIONS_VIRTUAL_ORES
import java.io.File
import java.io.FileWriter
import java.util.*

object JsonManager {

    private var WORLD_DIRECTORY: File? = null
    private const val ROOT_FOLDER = "SpaceAccident"
    private const val VIRTUAL_ORE_FOLDER = "VirtualOres"

    private lateinit var rootDirectory: File
    private lateinit var oresDirectory: File

    private fun initData() {
        WORLD_DIRECTORY = DimensionManager.getCurrentSaveRootDirectory()
        if (WORLD_DIRECTORY == null) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] ERROR NOT SET WORLD DIRECTORY"))
        }
        rootDirectory = File(WORLD_DIRECTORY, ROOT_FOLDER)
        oresDirectory = File(rootDirectory, VIRTUAL_ORE_FOLDER)

        if (!rootDirectory.isDirectory && !rootDirectory.mkdirs()) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] Failed to create ${rootDirectory.absolutePath}"))
        }
        if (!oresDirectory.isDirectory && !oresDirectory.mkdirs()) {
            println(IllegalStateException("[SpaceAccident|VirtualOres] Failed to create ${oresDirectory.absolutePath}"))
        }
    }

    private fun clearData() {
        REGIONS_VIRTUAL_ORES.clear()
    }

    fun save() {
        runBlocking(Dispatchers.IO) {
            saveOres()
        }

        clearData()
        WORLD_DIRECTORY = null
    }

    fun load() {
        initData()
        clearData()

        runBlocking {
            loadOres()
        }
    }

    private fun loadOres() {
        GsonBuilder().setPrettyPrinting().create().also { gson ->
            if (!oresDirectory.isDirectory) return
            oresDirectory.listFiles()?.forEach { file ->
                file.bufferedReader().use {
                    val type = object : TypeToken<List<RegionOre>>() {}.type
                    val regions = gson.fromJson<List<RegionOre>>(it, type)
                    regions.forEach { reg ->
                        val hash = Objects.hash(reg.xRegion, reg.zRegion, reg.dim)
                        REGIONS_VIRTUAL_ORES[hash] = reg
                    }
                }
            }
        }
        VirtualOreAPI.resizeVeins()
    }

    private fun saveOres() {
        if (REGIONS_VIRTUAL_ORES.isEmpty()) return
        GsonBuilder().setPrettyPrinting().create().also { gson ->

            val map = HashMap<Int, ArrayList<RegionOre>>()

            REGIONS_VIRTUAL_ORES.forEach { (_, reg) ->
                if (!map.contains(reg.dim)) {
                    map[reg.dim] = arrayListOf(reg)
                } else {
                    map[reg.dim]?.add(reg)
                }
            }
            for ((dim, regions) in map) {
                FileWriter(File(oresDirectory, "DIM${dim}.json")).buffered().use {
                    gson.toJson(regions, it)
                }
            }
        }
    }
}