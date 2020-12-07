package com.github.shyiko.klob.internal

import com.github.shyiko.klob.Glob.IterationOption
import java.io.File
import java.io.FileFilter
import java.nio.file.Path
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.EnumSet

internal fun FileFilter.and(fileFilter: FileFilter) =
    FileFilter { file -> this@and.accept(file) && fileFilter.accept(file) }

/**
 * On Windows: "C:\io" -> "/C:/io", does nothing everywhere else.
 */
internal fun slash(path: String) = path.replace('\\', '/')
    .let { if (!it.startsWith("/") && it.contains(":/")) "/$it" else it }
internal fun fromSlash(path: String) = path.replace('/', File.separatorChar)
    .let { if (it.contains(":/")) it.removePrefix("/") else it }

internal fun visit(dir: File, filter: FileFilter, directoryModeFilter: FileFilter?): Sequence<Path> {
    val stack = ArrayDeque<File>().apply { push(dir) }
    return generateSequence(fun (): Path? {
        while (true) {
            val file = stack.pollLast()
            if (file == null || (file.isFile && directoryModeFilter == null)) {
                return file?.toPath()
            }
            if (file.isDirectory) {
                val fileList = file.listFiles(filter)
                fileList.sortDescending() // maintain stable order
                if (fileList != null) {
                    stack.addAll(fileList)
                }
                if (directoryModeFilter != null && directoryModeFilter.accept(file)) {
                    return file.toPath()
                }
            }
        }
    })
}

internal fun visit(path: Path, option: EnumSet<IterationOption>, patterns: List<String>): Sequence<Path> {
    val includeChildren = !option.contains(IterationOption.SKIP_CHILDREN)
    val directoryMode = option.contains(IterationOption.DIRECTORY)

    if (includeChildren && directoryMode) {
        throw UnsupportedOperationException(
            "Glob.IterationOption.DIRECTORY must be used together with Glob.IterationOption.SKIP_CHILDREN " +
            "(please create a ticket at https://github.com/shyiko/klob/issue if it doesn't fit your needs)"
        )
    }

    val baseDir = path.toString()
    val filter = GlobFileFilter(baseDir,
        *patterns.toTypedArray(),
        includeChildren = includeChildren
    ).let {
        if (option.contains(IterationOption.SKIP_HIDDEN))
            it.and(HiddenFileFilter(reverse = true)) else it
    }

    val directoryModeFilter = when {
        option.contains(IterationOption.DIRECTORY) ->
            GlobFileFilter(baseDir,
                *patterns.toTypedArray(),
                includeChildren = includeChildren,
                forceExactMatch = true
            )
        else -> null
    }

    return patterns
        .asSequence()
        .map { Glob.prefix(slash(it)) }
        .distinct()
        .map {
            val file = if (it.startsWith("/"))
                File(fromSlash(it))
            else
                File(baseDir, fromSlash(it))

            visit(file, filter, directoryModeFilter)
        }.flatten()
}
