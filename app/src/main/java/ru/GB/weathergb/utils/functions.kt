package ru.GB.weathergb.utils

import java.io.BufferedReader
import java.util.stream.Collectors


fun getLines(reader: BufferedReader): String {
    return reader.useLines { it.joinToString("\n") }
}