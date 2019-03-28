#!/usr/bin/env kscript

//DEPS org.redundent:kotlin-xml-builder:1.4.5
//INCLUDE ../PPPShared/src/commonMain/kotlin/ui/PPPColor.kt

import org.redundent.kotlin.xml.*
import java.io.File

val colors = PPPColor.values()

val colorResXML = xml("resources") {
    for (color in colors) {
        color?.let {
            "color" {
                -it.hexColor
            attribute("name", color.name.decapitalize())
            }
        }
    }
}

val xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + colorResXML.toString()

val currentDirectory = File("").getAbsoluteFile()
val colorXMLPath = File(currentDirectory, "../android/src/main/res/values/colors.xml")
colorXMLPath.writeText(xmlString)
