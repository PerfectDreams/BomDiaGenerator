package net.mrgaabriel.bomdiagenerator.utils

import java.awt.*
import java.awt.image.BufferedImage

fun Graphics.drawCenteredStringWrap(text: String, width: Int, height: Int) {
    // Para escrever uma imagem centralizada, nós precisamos primeiro saber algumas coisas sobre o texto

    // Lista contendo (texto, posição)
    val lines = mutableListOf<String>()

    // Se está centralizado verticalmente ou não, por enquanto não importa para a gente
    val split = text.split(" ")

    var x = 0
    var currentLine = StringBuilder()

    for (string in split) {
        val stringWidth = fontMetrics.stringWidth("$string ")
        val newX = x + stringWidth

        if (newX >= width) {
            var endResult = currentLine.toString().trim()
            if (endResult.isEmpty()) { // okay wtf
                // Se o texto é grande demais e o conteúdo atual está vazio... bem... substitua o endResult pela string atual
                endResult = string
                lines.add(endResult)
                x = 0
                continue
            }
            lines.add(endResult)
            currentLine = StringBuilder()
            currentLine.append(' ')
            currentLine.append(string)
            x = fontMetrics.stringWidth("$string ")
        } else {
            currentLine.append(' ')
            currentLine.append(string)
            x = newX
        }
    }
    lines.add(currentLine.toString().trim())

    // got it!!!
    // bem, supondo que cada fonte tem 22f de altura...

    // para centralizar é mais complicado
    val skipHeight = fontMetrics.ascent + 5
    var y = (height / 2) - ((skipHeight - 10) * (lines.size - 1))
    for (line in lines) {
        this.drawCenteredString(line, Rectangle(0, y, width, 24))
        y += skipHeight
    }
}

fun Graphics.drawCenteredStringWrapOutline(text: String, width: Int, height: Int) {
    val lastColor = this.color

    // Fazer o outline
    this.color = Color.BLACK

    for (diffX in -2..2) {
        for (diffY in -2..2) {
            drawCenteredStringWrap(text, width + diffX, height + diffY)
        }
    }

    this.color = lastColor
    this.drawCenteredStringWrap(text, width, height)
}

fun Graphics.drawStringOutline(text: String, x: Int, y: Int) {
    val lastColor = this.color

    // Fazer o outline
    this.color = Color.BLACK

    this.drawString(text, x - 1, y)
    this.drawString(text, x + 1, y)
    this.drawString(text, x, y - 1)
    this.drawString(text, x, y + 1)

    this.color = lastColor
    this.drawString(text, x, y)
}

fun Graphics.drawCenteredString(text: String, rect: Rectangle) {
    // Get the FontMetrics
    val metrics = this.getFontMetrics(this.font)
    // Determine the X coordinate for the text
    val x = rect.x + (rect.width - metrics.stringWidth(text)) / 2
    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
    val y = rect.y + (rect.height - metrics.height) / 2 + metrics.ascent
    // Draw the String
    this.drawString(text, x, y)
}

fun BufferedImage.graphics(): Graphics2D {
    val graphics = this.createGraphics()

    graphics.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)

    return graphics
}