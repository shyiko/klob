package com.github.shyiko.klob.internal

import com.github.shyiko.klob.Glob.IterationOption
import java.io.File
import java.io.FileFilter
import java.nio.file.Path
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.EnumSet

fun FileFilter.and(fileFilter: FileFilter) =
    FileFilter { file -> this@and.accept(file) && fileFilter.accept(file) }

/**
 * On Windows: "C:\io" -> "/C:/io", does nothing everywhere else.
 */
fun slash(path: String) = path.replace('\\', '/').let { if (!it.startsWith("/") && it.contains(":/")) "/$it" else it }
fun fromSlash(path: String) = path.replace('/', File.separatorChar)
    .let { if (it.contains(":/")) it.removePrefix("/") else it }

fun visit(dir: File, filter: FileFilter): Sequence<Path> {
    val stack = ArrayDeque<File>().apply { push(dir) }
    return generateSequence(fun (): Path? {
        while (true) {
            val file = stack.pollLast()
            if (file == null || file.isFile) {
                return file?.toPath()
            }
            if (file.isDirectory) {
                val fileList = file.listFiles(filter)
                if (fileList != null) {
                    stack.addAll(fileList)
                }
            }
        }
    })
}

fun visit(path: Path, option: EnumSet<IterationOption>, patterns: List<String>): Sequence<Path> {
    val baseDir = path.toString()
    val filter = GlobFileFilter(baseDir, *patterns.toTypedArray())
        .let { if (option.contains(IterationOption.SKIP_HIDDEN)) it.and(HiddenFileFilter(reverse = true)) else it }
    return patterns
        .map { Glob.prefix(slash(it)) }
        .distinct()
        .map { (if (it.startsWith("/")) File(fromSlash(it)) else File(baseDir, fromSlash(it))).canonicalPath }
        // remove overlapping paths (e.g. /a & /a/b -> /a)
        .sorted()
        .fold(ArrayList<String>(), { r, v -> if (r.isEmpty() || !v.startsWith(r.last())) { r.add(v) }; r })
        .map { visit(File(it), filter) }
        .asSequence()
        .flatten()
}
