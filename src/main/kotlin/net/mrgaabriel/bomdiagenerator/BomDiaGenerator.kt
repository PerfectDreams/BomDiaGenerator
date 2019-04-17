package net.mrgaabriel.bomdiagenerator

import com.google.gson.Gson
import net.mrgaabriel.bomdiagenerator.config.BomDiaConfig
import net.mrgaabriel.bomdiagenerator.generator.ImageGenerator
import net.mrgaabriel.bomdiagenerator.manager.TwitterManager
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.thread

object BomDiaGenerator {

    lateinit var config: BomDiaConfig

    lateinit var imageGenerator: ImageGenerator
    lateinit var twitterManager: TwitterManager

    @JvmStatic
    fun main(args: Array<String>) {
        val configFile = File("config.json")

        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(Gson().toJson(BomDiaConfig("Consumer Key", "Consumer Secret", "Access Token", "Access Secret")))
        }

        config = Gson().fromJson(configFile.readText(Charsets.UTF_8), BomDiaConfig::class.java)

        imageGenerator = ImageGenerator()
        twitterManager = TwitterManager(config)

        twitterManager.startThread()

        thread(name = "Console Commands Handler") {
            while (true) {
                val next = readLine()!!.toLowerCase()

                when (next) {
                    "force_tweet" -> {
                        twitterManager.tweetBomDiaImage()
                    }
                }
            }
        }

        println("Alright! \"BomDiaGenerator\" started successfully!!!")
    }

}