package com.hedvig.android.owldroid.service.remotevectordrawable

import org.junit.Test
import java.io.File
import java.nio.charset.StandardCharsets

class VectorDrawableCreatorTest {

    val mockData: VectorDrawableCreator.VectorDrawableRepr by lazy {
        VectorDrawableCreator.VectorDrawableRepr(
            32, 32, 32f, 32f,
            listOf(VectorDrawableCreator.Path
                .newInstance("M15.804878 0a15.804878 16 0 1 0 0 32 15.804878 16 0 1 0 0-32z")
                .apply {
                    fillColor = 0xfff9fafb.toInt()
                    fillType = Pair(-1, "nonZero".toByteArray(StandardCharsets.UTF_8))
                },
                VectorDrawableCreator.Path
                    .newInstance("M15.2 10.8h1.6v6.4h-1.6z")
                    .apply {
                        fillColor =
                    })
        )
    }

    @Test
    fun createBinaryDrawableXml() {
        val xml = File(javaClass.classLoader.getResource("test_vector_drawable.xml").file).readText()
        val subject = VectorDrawableParser.parseVectorDrawable(xml)

        val result = VectorDrawableCreator.createBinaryDrawableXml(subject)
    }
}