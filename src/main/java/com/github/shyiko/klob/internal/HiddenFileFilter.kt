package com.github.shyiko.klob.internal

import java.io.File
import java.io.FileFilter

internal class HiddenFileFilter(private val reverse: Boolean = false) : FileFilter {

    override fun accept(file: File): Boolean = file.isHidden != reverse
}
