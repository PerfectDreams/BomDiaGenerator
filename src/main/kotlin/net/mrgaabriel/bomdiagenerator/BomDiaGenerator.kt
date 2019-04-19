package net.mrgaabriel.bomdiagenerator

import com.google.gson.Gson
import mu.KotlinLogging
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

    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        val configFile = File("config.json")

        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(Gson().toJson(BomDiaConfig(
                BomDiaConfig.TwitterConfig(
                    false,
                    "Consumer Key",
                    "Consumer Secret",
                    "Access Token",
                    "Access Secret"
                )
            )))

            logger.warn { "Looks like it's the first time you're running BomDiaGenerator!" }
            logger.warn { "Please configure the app in the file \"config.json\"" }
            return
        }

        config = Gson().fromJson(configFile.readText(Charsets.UTF_8), BomDiaConfig::class.java)

        imageGenerator = ImageGenerator()

        if (config.twitter.enabled) {
            twitterManager = TwitterManager(config)
            twitterManager.startThread()
        }

        thread(name = "Console Commands Handler") {
            while (true) {
                val next = readLine()!!.toLowerCase()

                when (next) {
                    "force_tweet" -> {
                        twitterManager.tweetBomDiaImage()
                    }
                    "generate" -> {
                        val output = imageGenerator.generateImage()
                        ImageIO.write(output, "png", File("bom-dia.png"))
                        logger.info { "Done!" }
                    }
                }
            }
        }

        logger.info { "Alright! \"BomDiaGenerator\" started successfully!!!" }
    }

}