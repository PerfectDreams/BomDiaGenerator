package net.mrgaabriel.bomdiagenerator.listener

import mu.KotlinLogging
import net.mrgaabriel.bomdiagenerator.BomDiaGenerator
import net.mrgaabriel.bomdiagenerator.manager.TwitterManager
import twitter4j.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import javax.imageio.ImageIO

class TwitterListeners(val m: TwitterManager) : StatusListener {

    val logger = KotlinLogging.logger {}

    override fun onTrackLimitationNotice(p0: Int) {}
    override fun onStallWarning(p0: StallWarning) {}
    override fun onException(p0: Exception?) {}
    override fun onDeletionNotice(p0: StatusDeletionNotice?) {}
    override fun onScrubGeo(p0: Long, p1: Long) {}

    override fun onStatus(status: Status) {
        val start = System.currentTimeMillis()

        val content = status.text.removePrefix("@${m.twitter.screenName} ")

        when (content) {
            "generate" -> {
                logger.info { "@${status.user.screenName}: ${status.text}" }

                val image = BomDiaGenerator.imageGenerator.generateImage()
                val baos = ByteArrayOutputStream()
                ImageIO.write(image, "png", baos)
                val bais = ByteArrayInputStream(baos.toByteArray())

                val statusUpdate = m.twitter.updateStatus(StatusUpdate("Bom dia, @${status.user.screenName}!")
                    .media("bom-dia.png", bais)
                    .inReplyToStatusId(status.id))

                logger.info { "@${status.user.screenName}: ${status.text} - Done! https://twitter.com/${m.twitter.screenName}/status/${statusUpdate.id} - Took ${System.currentTimeMillis() - start}ms" }
            }
        }
    }
}