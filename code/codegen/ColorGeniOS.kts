#!/usr/bin/env kscript

//INCLUDE ../PPPShared/src/commonMain/kotlin/ui/PPPColor.kt

import java.io.File

fun colorJSON(forColor: PPPColor): String {
    return """
{
  "info" : {
    "version" : 1,
    "author" : "xcode"
  },
  "colors" : [
    {
      "idiom" : "universal",
      "color" : {
        "color-space" : "srgb",
        "components" : {
          "red" : "${forColor.red}",
          "alpha" : "1.000",
          "blue" : "${forColor.blue}",
          "green" : "${forColor.green}"
        }
      }
    }
  ]
}
"""
}

val colors = PPPColor.values()

val currentDirectory = File("").getAbsoluteFile()

for (color in colors) {
    val json = colorJSON(color)
    val folderName = "${color.name.decapitalize()}.colorset"
    val fileName = "Contents.json"
    val path = "../iOS/PorchPirateProtector/Assets.xcassets/Colors/$folderName/$fileName"
    val colorJSONFile = File(currentDirectory, path)
    if (!colorJSONFile.exists()) {
        colorJSONFile.getParentFile().mkdirs()
        colorJSONFile.createNewFile()
    }

    colorJSONFile.writeText(json)
}