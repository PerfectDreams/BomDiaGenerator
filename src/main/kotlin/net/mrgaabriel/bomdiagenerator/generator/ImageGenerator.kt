package net.mrgaabriel.bomdiagenerator.generator

import com.github.kevinsawicki.http.HttpRequest
import net.mrgaabriel.bomdiagenerator.BomDiaGenerator
import net.mrgaabriel.bomdiagenerator.utils.drawCenteredString
import net.mrgaabriel.bomdiagenerator.utils.drawCenteredStringWrapOutline
import net.mrgaabriel.bomdiagenerator.utils.drawStringOutline
import net.mrgaabriel.bomdiagenerator.utils.graphics
import org.jsoup.Jsoup
import java.awt.Color
import java.awt.Font
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.imageio.ImageIO

class ImageGenerator {

    fun generateImage(): BufferedImage {
        val date = OffsetDateTime.now()
        val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"))

        val quote = fetchDayQuote()

        val image = fetchRandomImage(700, 700, "nature")
        val graphics = image.graphics()

        val charlotte = Font.createFont(Font.PLAIN, File("assets", "charlotte.ttf"))

        graphics.font = charlotte.deriveFont(96f)
        graphics.color = Color.WHITE

        graphics.drawCenteredStringWrapOutline("Bom dia!", 700, 160)

        val arial = Font.createFont(Font.PLAIN, File("assets", "arial.ttf"))

        graphics.font = arial.deriveFont(40f)
        graphics.drawCenteredStringWrapOutline("${quote.phrase} - ${quote.author}", 700, 400)

        graphics.font = arial.deriveFont(27f)

        graphics.drawCenteredStringWrapOutline("${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-br"))} • $dateFormatted", 700, 1200)

        graphics.drawStringOutline("@${BomDiaGenerator.twitterManager.twitter.screenName}", 540, 660)
        graphics.drawStringOutline("bomdia.lori.fun", 520, 690)

        println("Image generated successfully!")

        return image
    }

    fun fetchRandomImage(width: Int, height: Int, vararg queries: String): BufferedImage {
        val url = "https://source.unsplash.com/${width}x$height/?${queries.joinToString(",") { it.split(" ").joinToString("%20") }}"

        val request = HttpRequest.get(url)
            .followRedirects(true)
            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")

        if (!request.ok()) {
            throw RuntimeException("Request for \"$url\" is not OK! Code: ${request.code()}")
        }

        println("Random image fetched successfully! ${request.url()}")

        return ImageIO.read(request.stream())
    }

    // Scrapping
    fun fetchDayQuote(): Quote {
        val url = "https://pt.wikiquote.org/wiki/Wikiquote:Frase_do_dia"
        val request = HttpRequest.get(url)
            .followRedirects(true)
            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")

        if (!request.ok()) {
            throw RuntimeException("Request to \"$url\" is not OK! Code: ${request.code()}")
        }

        val jsoup = Jsoup.parse(request.body())

        val phrase = jsoup.selectFirst("#mw-content-text > div > div > div:nth-child(2) > table > tbody > tr:nth-child(2) > td:nth-child(1) > p > b").text()
        val author = jsoup.selectFirst("#mw-content-text > div > div > div:nth-child(2) > table > tbody > tr:nth-child(2) > td:nth-child(1) > p > a").text()

        val quote = Quote(phrase, author)
        println("Quote fetched successfully! $quote")

        return quote
    }

    data class Quote(val phrase: String, val author: String)
}