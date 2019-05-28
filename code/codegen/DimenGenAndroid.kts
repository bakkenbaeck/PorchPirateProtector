#!/usr/bin/env kscript

//DEPS org.redundent:kotlin-xml-builder:1.5.1
//INCLUDE ../PPPShared/src/commonMain/kotlin/extension/StringExtensions.kt
//INCLUDE ../PPPShared/src/commonMain/kotlin/ui/Margin.kt
//INCLUDE ../PPPShared/src/commonMain/kotlin/ui/FontSize.kt

import org.redundent.kotlin.xml.*
import java.io.File

val margins = Margin.values()
val fontSizes = FontSize.values()

val dimenResXML = xml("resources") {
    for (margin in margins) {
        "dimen" {
            -"${margin.points}dp"
            attribute("name", "margin_${margin.name.camelToSnakeCase()}")
        }
    }
    for (fontSize in fontSizes) {
            "dimen" {
                -"${fontSize.defaultPoints}sp"
                attribute("name", "font_size_${fontSize.name.camelToSnakeCase()}")
            }
    }
}

val printOptions = PrintOptions(pretty = true, singleLineTextElements = true)
val xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + dimenResXML.toString(printOptions = printOptions)

val currentDirectory = File("").getAbsoluteFile()
val dimenXMLPath = File(currentDirectory, "../android/src/main/res/values/dimens.xml")
dimenXMLPath.writeText(xmlString)

println("Generated Android dimens!")
