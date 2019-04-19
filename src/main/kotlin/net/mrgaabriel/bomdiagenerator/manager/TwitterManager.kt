package net.mrgaabriel.bomdiagenerator.manager

import mu.KotlinLogging
import net.mrgaabriel.bomdiagenerator.BomDiaGenerator
import net.mrgaabriel.bomdiagenerator.config.BomDiaConfig
import net.mrgaabriel.bomdiagenerator.listener.TwitterListeners
import twitter4j.*
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import javax.imageio.ImageIO
import kotlin.concurrent.thread

class TwitterManager(val config: BomDiaConfig) {
    val logger = KotlinLogging.logger {}

    val randomEmotes = listOf(
        "\uD83D\uDE0A",
        "\uD83C\uDF1E",
        "\uD83D\uDE42",
        "\uD83D\uDC4D",
        "\uD83D\uDC96"
    )

    var twitter: Twitter
    var twitterStream: TwitterStream

    init {
        val configuration = ConfigurationBuilder()
            .setOAuthConsumerKey(config.twitter.consumerKey)
            .setOAuthConsumerSecret(config.twitter.consumerSecret)
            .setOAuthAccessToken(config.twitter.accessToken)
            .setOAuthAccessTokenSecret(config.twitter.accessSecret)
            .build()

        twitter = TwitterFactory(configuration).instance

        twitterStream = TwitterStreamFactory(configuration).instance
        twitterStream.oAuthAccessToken = AccessToken(config.twitter.accessToken, config.twitter.accessSecret);
        twitterStream.addListener(TwitterListeners(this))

        val tweetFilterQuery = FilterQuery()
        tweetFilterQuery.track("bomdiazap")
        twitterStream.filter(tweetFilterQuery)
    }

    fun tweetBomDiaImage(): Status {
        logger.info { "Tweeting image..." }

        val image = BomDiaGenerator.imageGenerator.generateImage()
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        val bais = ByteArrayInputStream(baos.toByteArray())

        val status = twitter.updateStatus(StatusUpdate("Bom dia, grupo do zap! ${randomEmotes.random()}\n\n#GoodMorningWorld #GoodMorning #BomDia").media("bom-dia.png", bais))

        logger.info { "Tweeted successfully! https://twitter.com/${twitter.screenName}/status/${twitter.id}" }
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