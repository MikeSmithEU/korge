package com.soywiz.korge.view

import com.soywiz.korge.bitmapfont.BitmapFont
import com.soywiz.korge.bitmapfont.convert
import com.soywiz.korge.html.Html
import com.soywiz.korim.font.BitmapFontGenerator
import com.soywiz.korio.error.invalidOp
import com.soywiz.korma.geom.Rectangle

class FontRepository(val views: Views) : Html.MetricsProvider {
	val fonts = hashMapOf<String, BitmapFont>()

	fun registerFont(name: String, bmp: BitmapFont) {
		fonts[name.toLowerCase()] = bmp
	}

	fun getBitmapFont(name: String): BitmapFont {
		val nameLC = name.toLowerCase()
		if (nameLC !in fonts) {
			registerFont(name, BitmapFontGenerator.generate(name, 32, BitmapFontGenerator.LATIN_ALL).convert(views.ag))
		}
		return fonts[nameLC] ?: views.defaultFont
	}

	fun getBitmapFont(face: Html.FontFace): BitmapFont = when (face) {
		is Html.FontFace.Named -> getBitmapFont(face.name)
		is Html.FontFace.Bitmap -> face.font
		else -> invalidOp("Unsupported font face: $face")
	}

	override fun getBounds(text: String, format: Html.Format, out: Rectangle) {
		val font = getBitmapFont(format.face)
		val ratio = font.fontSize.toDouble() / format.size.toDouble()
		var width = 0.0
		for (c in text) {
			val glyph = font[c]
			val xadvance = glyph.xadvance * ratio
			width += xadvance
		}
		out.setTo(0.0, 0.0, width, font.fontSize.toDouble())
	}
}