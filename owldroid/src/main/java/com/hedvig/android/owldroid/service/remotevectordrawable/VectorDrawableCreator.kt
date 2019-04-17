package com.hedvig.android.owldroid.service.remotevectordrawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/**
 * Credit to Nicolas@StackOverflow, https://stackoverflow.com/a/49920860
 */
object VectorDrawableCreator {

    // Starter data for the string pool.
    // The first N elements of this array should match up with the first N elements of BIN_XML_STRINGS
    private val BIN_XML_STRINGS = arrayOf(
        "width".toByteArray(),
        "height".toByteArray(),
        "viewportWidth".toByteArray(),
        "viewportHeight".toByteArray(),
        "fillColor".toByteArray(),
        "fillType".toByteArray(),
        "strokeColor".toByteArray(),
        "strokeWidth".toByteArray(),
        "pathData".toByteArray(),
        "path".toByteArray(),
        "vector".toByteArray(),
        "http://schemas.android.com/apk/res/android".toByteArray()
    )

    private const val WIDTH = 0
    private const val HEIGHT = 1
    private const val VIEWPORT_WIDTH = 2
    private const val VIEWPORT_HEIGHT = 3
    private const val FILL_COLOR = 4
    private const val FILL_TYPE = 5
    private const val STROKE_COLOR = 6
    private const val STROKE_WIDTH = 7
    private const val PATH_DATA = 8
    private const val PATH = 9
    private const val VECTOR = 10
    private const val ANDROID_NAMESPACE = 11

    private const val MAX_BUFFER_SIZE = 8192
    private const val NO_RAW_VALUE = -1

    private const val NON_MAPPED_STRING = -1

    // attribute resource ids. Indices must match up with BIN_XML_STRINGS
    private val BIN_XML_ATTRS = intArrayOf(
        android.R.attr.height,
        android.R.attr.width,
        android.R.attr.viewportWidth,
        android.R.attr.viewportHeight,
        android.R.attr.fillColor,
        android.R.attr.fillType,
        android.R.attr.strokeColor,
        android.R.attr.strokeWidth,
        android.R.attr.pathData
    )

    private const val CHUNK_TYPE_XML: Short = 0x0003
    private const val CHUNK_TYPE_STR_POOL: Short = 0x0001
    private const val CHUNK_TYPE_START_TAG: Short = 0x0102
    private const val CHUNK_TYPE_END_TAG: Short = 0x0103
    private const val CHUNK_TYPE_RES_MAP: Short = 0x0180

    private const val VALUE_TYPE_DIMENSION: Short = 0x0500
    private const val VALUE_TYPE_STRING: Short = 0x0300
    private const val VALUE_TYPE_COLOR: Short = 0x1D00
    private const val VALUE_TYPE_FLOAT: Short = 0x0400
    private const val VALUE_TYPE_INT: Short = 0x1000

    /**
     * Create a vector drawable from a list of paths and colors
     *
     * @param width          drawable width
     * @param height         drawable height
     * @param viewportWidth  vector image width
     * @param viewportHeight vector image height
     * @param paths          list of path data and colors
     * @return the vector drawable or null it couldn't be created.
     */
    fun getVectorDrawable(
        context: Context,
        repr: VectorDrawableRepr
    ): Drawable? {
        val binXml = createBinaryDrawableXml(repr)

        try {
            // Get the binary XML parser (XmlBlock.Parser) and use it to create the drawable
            // This is the equivalent of what AssetManager#getXml() does
            @SuppressLint("PrivateApi")
            val xmlBlock = Class.forName("android.content.res.XmlBlock")
            val xmlBlockConstr = xmlBlock.getConstructor(ByteArray::class.java)
            val xmlParserNew = xmlBlock.getDeclaredMethod("newParser")
            xmlBlockConstr.isAccessible = true
            xmlParserNew.isAccessible = true
            val parser = xmlParserNew.invoke(
                xmlBlockConstr.newInstance(binXml as Any)
            ) as XmlPullParser

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Drawable.createFromXml(context.resources, parser)
            } else {
                // Before API 24, vector drawables aren't rendered correctly without compat lib
                val attrs = Xml.asAttributeSet(parser)
                var type = parser.next()
                while (type != XmlPullParser.START_TAG) {
                    type = parser.next()
                }
                return VectorDrawableCompat.createFromXmlInner(context.resources, parser, attrs, null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Vector creation failed")
        }

        return null
    }

    fun createBinaryDrawableXml(repr: VectorDrawableRepr): ByteArray {
        val paths = repr.paths
        val stringPool = makeStringPool(paths)

        val bb = ByteBuffer.allocate(MAX_BUFFER_SIZE) // Capacity might have to be greater.
        bb.order(ByteOrder.LITTLE_ENDIAN)

        val xmlSizePos = makeXmlChunk(bb)
        makeStringPoolChunk(bb, stringPool)

        makeResourceMapChunk(bb)
        makeVectorStartTag(bb, repr.width, repr.height, repr.viewportWidth, repr.viewportHeight)

        paths.forEach { makePath(bb, it) }

        putEndTag(bb, VECTOR)

        // Write XML chunk size
        writeSizeToXmlChunk(bb, xmlSizePos)

        // Return binary XML byte array
        val binXml = ByteArray(bb.position())
        bb.rewind()
        bb.get(binXml)

        return binXml
    }

    /**
     * Creates a string pool filled with the static XML strings needed to create this vector drawable, and all String
     * Attributes found inside the vector drawable.
     *
     * Note that this function will update the provided @param paths with the String Pool index assigned to their
     * data.
     */
    private fun makeStringPool(paths: List<Path>): MutableList<ByteArray> {
        val stringPool = BIN_XML_STRINGS.toMutableList()
        for (path in paths) {
            val index = addToStringPool(stringPool, path.pathData.second)
            path.pathData = path.pathData.copy(first = index)
        }

        return stringPool
    }

    /**
     * Adds this value to the string pool, deduplicated. Returns the index of the value in the string pool.
     */
    private fun addToStringPool(stringPool: MutableList<ByteArray>, value: ByteArray): Int {
        if (!stringPool.contains(value)) {
            stringPool.add(value)
        }

        return stringPool.indexOf(value)
    }

    private const val XML_CHUNK_HEADER_SIZE = 8.toShort()
    private const val CHUNK_SIZE_FIELD_SIZE = 4
    /**
     * Writes an XML chunk to @param bb.
     * See https://justanapplication.wordpress.com/2011/09/22/android-internals-binary-xml-part-two-the-xml-chunk/ for more information
     * Note that you must call writeSizeToXmlChunk to finalize the XML Chunk when the XML Chunk has been completed.
     * @return The index in which to place the final XML chunk size
     */
    private fun makeXmlChunk(bb: ByteBuffer): Int {
        bb.putShort(CHUNK_TYPE_XML)  // Type
        bb.putShort(XML_CHUNK_HEADER_SIZE)  // Header size
        val xmlSizePos = bb.position() // We store this position so that we may write the chunk size later on
        bb.position(bb.position() + CHUNK_SIZE_FIELD_SIZE) // Skip ahead to make room for XML chunk size

        return xmlSizePos
    }

    /**
     * Writes the size of the XML chunk to the XML Chunk header.
     */
    private fun writeSizeToXmlChunk(bb: ByteBuffer, position: Int) {
        val posBefore = bb.position()
        bb.putInt(position, bb.position())
        bb.position(posBefore)
    }

    private const val STRING_POOL_HEADER_SIZE = 0x1c.toShort()
    private const val UTF8_ENCODING_FLAG = 1 shl 8
    private const val EIGHT_BIT_SIZE = 127
    private const val STRING_DATA_ADDITIONAL_SPACE_HIGH = 5
    private const val STRING_DATA_ADDITIONAL_SPACE_LOW = 3
    /**
     * Writes the String Pool chunk to @param bb
     * See https://justanapplication.wordpress.com/2011/09/15/android-internals-resources-part-four-the-stringpool-chunk/ for more info
     */
    private fun makeStringPoolChunk(bb: ByteBuffer, stringPool: List<ByteArray>) {

        val spStartPos = bb.position()
        bb.putShort(CHUNK_TYPE_STR_POOL)  // Type
        bb.putShort(STRING_POOL_HEADER_SIZE)  // Header size
        val spSizePos = bb.position() // We store this position so that we may write the string pool size later on
        bb.position(bb.position() + CHUNK_SIZE_FIELD_SIZE) // Skip ahead to make room for XML chunk size

        bb.putInt(stringPool.size)  // String count
        bb.putInt(0)  // Style count
        bb.putInt(UTF8_ENCODING_FLAG)  // Flags set: encoding is UTF-8
        val spStringsStartPos = bb.position()
        bb.position(bb.position() + CHUNK_SIZE_FIELD_SIZE)
        bb.putInt(0)  // Styles start

        // 3.1 String indices
        var offset = 0
        for (str in stringPool) {
            bb.putInt(offset)
            offset += str.size + if (str.size > EIGHT_BIT_SIZE) STRING_DATA_ADDITIONAL_SPACE_HIGH else STRING_DATA_ADDITIONAL_SPACE_LOW
        }

        // Write the offset at which the string data starts
        var posBefore = bb.position()
        bb.putInt(spStringsStartPos, bb.position() - spStartPos)
        bb.position(posBefore)

        // String pool data
        for (str in stringPool) {
            // If the size of the string cannot be represented with 8 bits, then encode the size as two 8-bits
            if (str.size > EIGHT_BIT_SIZE) {
                val high = (str.size and 0xFF00 or 0x8000).ushr(8).toByte()
                val low = (str.size and 0xFF).toByte()
                bb.put(high)
                bb.put(low)
                bb.put(high)
                bb.put(low)
            } else {
                val len = str.size.toByte()
                bb.put(len)
                bb.put(len)
            }
            bb.put(str)
            bb.put(0.toByte())
        }

        if (bb.position() % 4 != 0) {
            // Padding to align on 32-bit
            bb.put(ByteArray(4 - (bb.position() % 4)))
        }

        // Write string pool chunk size
        posBefore = bb.position()
        bb.putInt(spSizePos, bb.position() - spStartPos)
        bb.position(posBefore)
    }

    private const val RESOURCE_MAP_HEADER_SIZE = 8.toShort()
    private const val RESOURCE_INDEX_SIZE = 4
    /**
     * Writes the Resource Map chunk to @param bb
     * See https://justanapplication.wordpress.com/2011/09/23/android-internals-binary-xml-part-four-the-xml-resource-map-chunk/ for more info
     */
    private fun makeResourceMapChunk(bb: ByteBuffer) {
        bb.putShort(CHUNK_TYPE_RES_MAP)  // Type
        bb.putShort(RESOURCE_MAP_HEADER_SIZE)  // Header size
        bb.putInt(RESOURCE_MAP_HEADER_SIZE + BIN_XML_ATTRS.size * RESOURCE_INDEX_SIZE)  // Chunk size

        for (attr in BIN_XML_ATTRS) {
            bb.putInt(attr)
        }
    }

    private const val VECTOR_ATTRIBUTE_COUNT = 4
    /**
     * Writes the Vector start tag to @param bb
     */
    private fun makeVectorStartTag(
        bb: ByteBuffer,
        width: Int,
        height: Int,
        viewportWidth: Float,
        viewportHeight: Float
    ) {
        val vstStartPos = bb.position()
        val vstSizePos = putStartTag(bb, VECTOR, VECTOR_ATTRIBUTE_COUNT)

        // Attributes
        // android:width="24dp", value type: dimension (dp)
        putAttribute(bb, WIDTH, NO_RAW_VALUE, VALUE_TYPE_DIMENSION, (width shl 8) + 1)

        // android:height="24dp", value type: dimension (dp)
        putAttribute(bb, HEIGHT, NO_RAW_VALUE, VALUE_TYPE_DIMENSION, (height shl 8) + 1)

        // android:viewportWidth="24", value type: float
        putAttribute(
            bb,
            VIEWPORT_WIDTH,
            NO_RAW_VALUE,
            VALUE_TYPE_FLOAT,
            java.lang.Float.floatToRawIntBits(viewportWidth)
        )

        // android:viewportHeight="24", value type: float
        putAttribute(
            bb,
            VIEWPORT_HEIGHT,
            NO_RAW_VALUE,
            VALUE_TYPE_FLOAT,
            java.lang.Float.floatToRawIntBits(viewportHeight)
        )

        // Write vector start tag chunk size
        val posBefore = bb.position()
        bb.putInt(vstSizePos, bb.position() - vstStartPos)
        bb.position(posBefore)
    }

    private const val XML_START_END_ELEMENT_HEADER_SIZE = 16.toShort()
    private const val XML_ATTRIBUTE_START_OFFSET_SIZE = 0x14.toShort()

    /**
     * Writes a start tag to @param bb
     */
    private fun putStartTag(bb: ByteBuffer, name: Int, attributeCount: Int): Int {
        // https://justanapplication.wordpress.com/2011/09/25/android-internals-binary-xml-part-six-the-xml-start-element-chunk/
        bb.putShort(CHUNK_TYPE_START_TAG)
        bb.putShort(XML_START_END_ELEMENT_HEADER_SIZE)  // Header size
        val sizePos = bb.position()

        bb.putInt(0) // Size, to be set later
        bb.putInt(0)  // Line number: None
        bb.putInt(-1)  // Comment: None

        bb.putInt(-1)  // Namespace: None
        bb.putInt(name)
        bb.putShort(XML_ATTRIBUTE_START_OFFSET_SIZE)  // Attributes start offset
        bb.putShort(XML_ATTRIBUTE_START_OFFSET_SIZE)  // Attributes size
        bb.putShort(attributeCount.toShort())  // Attribute count
        bb.putShort(0.toShort())  // ID attr: none
        bb.putShort(0.toShort())  // Class attr: none
        bb.putShort(0.toShort())  // Style attr: none

        return sizePos
    }

    private fun makePath(bb: ByteBuffer, path: Path) {
        // ==== Path start tag ====
        val pstStartPos = bb.position()
        val pstSizePos = putStartTag(bb, PATH, path.attributeCount)

        // Add Path optional attributes
        path.fillColor?.let { putAttribute(bb, FILL_COLOR, NO_RAW_VALUE, VALUE_TYPE_COLOR, it) }
        path.fillType?.let { putAttribute(bb, FILL_TYPE, NO_RAW_VALUE, VALUE_TYPE_INT, it) }
        path.strokeColor?.let { putAttribute(bb, STROKE_COLOR, NO_RAW_VALUE, VALUE_TYPE_COLOR, it) }
        path.strokeWidth?.let {
            putAttribute(
                bb,
                STROKE_WIDTH,
                NO_RAW_VALUE,
                VALUE_TYPE_FLOAT,
                java.lang.Float.floatToRawIntBits(it)
            )
        }

        // Add mandatory pathData
        val pathDataIndex = path.pathData.first
        putAttribute(bb, PATH_DATA, pathDataIndex, VALUE_TYPE_STRING, pathDataIndex)

        // Write path start tag chunk size
        val posBefore = bb.position()
        bb.putInt(pstSizePos, bb.position() - pstStartPos)
        bb.position(posBefore)

        // ==== Path end tag ====
        putEndTag(bb, PATH)
    }

    /**
     * Writes an end tag to @param bb
     */
    private fun putEndTag(bb: ByteBuffer, name: Int) {
        // https://justanapplication.wordpress.com/2011/09/26/android-internals-binary-xml-part-seven-the-xml-end-element-chunk/
        bb.putShort(CHUNK_TYPE_END_TAG)
        bb.putShort(XML_START_END_ELEMENT_HEADER_SIZE)  // Header size
        bb.putInt(24)  // Chunk size
        bb.putInt(0)  // Line number: none
        bb.putInt(-1)  // Comment: none
        bb.putInt(-1)  // Namespace: none
        bb.putInt(name)  // Name: vector
    }

    private const val VALUE_SIZE = 0x08.toShort()
    /**
     * Writes an attribute to @param bb
     */
    private fun putAttribute(
        bb: ByteBuffer, name: Int,
        rawValue: Int, valueType: Short, valueData: Int
    ) {
        // https://justanapplication.wordpress.com/2011/09/19/android-internals-resources-part-eight-resource-entries-and-values/#struct_Res_value
        bb.putInt(ANDROID_NAMESPACE)  // Namespace index in string pool (always the android namespace)
        bb.putInt(name)
        bb.putInt(rawValue)
        bb.putShort(VALUE_SIZE)  // Value size
        bb.putShort(valueType)
        bb.putInt(valueData)
    }

    data class VectorDrawableRepr(
        val width: Int,
        val height: Int,
        val viewportWidth: Float,
        val viewportHeight: Float,
        val paths: List<Path>
    )

    class Path private constructor(var pathData: Pair<Int, ByteArray>) {

        @ColorInt
        var fillColor: Int? = null
        var fillType: Int? = null
        @ColorInt
        var strokeColor: Int? = null
        var strokeWidth: Float? = null

        val attributeCount: Int
            get() {
                var attributeCount = 1
                fillColor?.let { attributeCount += 1 }
                fillType?.let { attributeCount += 1 }
                strokeColor?.let { attributeCount += 1 }
                strokeWidth?.let { attributeCount += 1 }

                return attributeCount
            }

        companion object {
            fun newInstance(pathData: String) =
                Path(Pair(NON_MAPPED_STRING, pathData.toByteArray(StandardCharsets.UTF_8)))
        }
    }
}
