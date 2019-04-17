package com.hedvig.android.owldroid.service.remotevectordrawable

import android.graphics.Color
import android.util.Xml
import org.xmlpull.v1.XmlPullParser

object VectorDrawableParser {
    private const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
    fun parseVectorDrawable(xml: String): VectorDrawableCreator.VectorDrawableRepr {
        val parser = Xml.newPullParser()

        parser.setInput(xml.reader())

        parser.nextTag()

        parser.require(XmlPullParser.START_TAG, null, "vector")
        val width = parser.getAttributeValue(ANDROID_NAMESPACE, "width").replace("dp", "").toInt()
        val height = parser.getAttributeValue(ANDROID_NAMESPACE, "height").replace("dp", "").toInt()
        val viewportWidth = parser.getAttributeValue(ANDROID_NAMESPACE, "viewportWidth").toFloat()
        val viewportHeight = parser.getAttributeValue(ANDROID_NAMESPACE, "viewportHeight").toFloat()

        val paths = parseNodes(parser)

        return VectorDrawableCreator.VectorDrawableRepr(width, height, viewportWidth, viewportHeight, paths)
    }

    private fun parseNodes(parser: XmlPullParser): List<VectorDrawableCreator.Path> {
        val paths = mutableListOf<VectorDrawableCreator.Path>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.TEXT) {
                continue
            }

            if (parser.name == "group") {
                paths += parseNodes(parser)
                continue
            }

            if (parser.name == "path") {
                paths += parsePath(parser)
                parser.next()
                continue
            }
        }

        return paths
    }

    private fun parsePath(parser: XmlPullParser): VectorDrawableCreator.Path {
        val pathData = parser.getAttributeValue(ANDROID_NAMESPACE, "pathData")
        val path = VectorDrawableCreator.Path.newInstance(pathData)

        parser.getAttributeValue(ANDROID_NAMESPACE, "fillColor")?.let { path.fillColor = Color.parseColor(it) }
        parser.getAttributeValue(ANDROID_NAMESPACE, "fillType")
            ?.let { path.fillType = parseFillType(it) }
        parser.getAttributeValue(ANDROID_NAMESPACE, "strokeColor")?.let { path.fillColor = Color.parseColor(it) }
        parser.getAttributeValue(ANDROID_NAMESPACE, "strokeWidth")?.let { path.strokeWidth = it.toFloat() }

        return path
    }

    private fun parseFillType(fillType: String) = when (fillType) {
        "nonZero" -> 0
        "evenOdd" -> 1
        else -> 0
    }
}
