package net.mrgaabriel.bomdiagenerator.manager

import net.mrgaabriel.bomdiagenerator.BomDiaGenerator
import net.mrgaabriel.bomdiagenerator.config.BomDiaConfig
import twitter4j.Status
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.io.File
import java.time.OffsetDateTime
import javax.imageio.ImageIO
import kotlin.concurrent.thread

class TwitterManager(val config: BomDiaConfig) {

    var twitter: Twitter

    init {
        val configuration = ConfigurationBuilder()
            .setOAuthConsumerKey(config.consumerKey)
            .setOAuthConsumerSecret(config.consumerSecret)
            .setOAuthAccessToken(config.accessToken)
            .setOAuthAccessTokenSecret(config.accessSecret)
            .build()

        twitter = TwitterFactory(configuration).instance
    }

    fun tweetBomDiaImage(): Status {
        val image = BomDiaGenerator.imageGenerator.generateImage()
        val file = File("tmp-${System.currentTimeMillis()}.png")

        ImageIO.write(image, "png", file)

        val status = twitter.updateStatus(StatusUpdate("Bom dia, grupo do zap! #GoodMorningWorld #GoodMorning #BomDia").media(file))

        file.delete()

        println("Tweeted successfully! https://twitter.com/${twitter.screenName}/status/${twitter.id}")
        return status
    }

    fun startThread(): Thread = thread(name = "Tweet Thread") {
        while (true) {
            val now = OffsetDateTime.now()

            if (now.hour == 9 && now.minute == 0) {
                tweetBomDiaImage()
            }

            Thread.sleep(1000 * 60)
        }
    }
}