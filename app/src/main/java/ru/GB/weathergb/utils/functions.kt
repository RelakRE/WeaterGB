package ru.GB.weathergb.utils

import java.io.BufferedReader


fun getLines(reader: BufferedReader): String {
    return reader.useLines { it.joinToString("\n") }
}