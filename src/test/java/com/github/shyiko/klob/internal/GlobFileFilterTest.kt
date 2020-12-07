package com.github.shyiko.klob.internal

import com.github.shyiko.klob.Glob
import org.testng.Assert.assertFalse
import org.testng.Assert.assertTrue
import org.testng.annotations.Test
import java.io.File
import java.nio.file.Path

class GlobFileFilterTest {

    private val dir = File("/tmp").canonicalPath

    @Test
    fun testNegation() {
        val filter = GlobFileFilter(dir, "$dir/**/*.kt", "!$dir/**/*test*/**/*.kt", "!$dir/**/prefix*/**/*.kt",
            "!$dir/**/*suffix/**/*.kt")
        assertTrue(filter.accept(File("$dir/a.kt")))
        assertFalse(filter.accept(File("$dir/a/test_/a.kt")))
        assertFalse(filter.accept(File("$dir/a/_test_/a.kt")))
        assertFalse(filter.accept(File("$dir/a/_test/a.kt")))
        assertFalse(filter.accept(File("$dir/a/prefix_/a.kt")))
        assertFalse(filter.accept(File("$dir/a/prefix/a.kt")))
        assertTrue(filter.accept(File("$dir/a/_prefix/a.kt")))
        assertFalse(filter.accept(File("$dir/a/_suffix/a.kt")))
        assertFalse(filter.accept(File("$dir/a/suffix/a.kt")))
        assertTrue(filter.accept(File("$dir/a/suffix_/a.kt")))
        assertTrue(GlobFileFilter("/C:/ktlint", "/C:/ktlint/src/**/*.kt")
            .accept(File("/C:/ktlint/src/test/kotlin/com/github/shyiko/ktlint/LinterTest.kt")))
    }

    /**
     * Test paths for which the directories are a substring of another, using the [GlobFileFilter] class directly.
     */
    @Test
    fun testSubPathFold_regression() {
        val filter = GlobFileFilter(dir, "$dir/a/b/a-b.txt", "$dir/a/b1/a-b1.txt")

        assertTrue(filter.accept(File("$dir/a/b/a-b.txt")))
        assertTrue(filter.accept(File("$dir/a/b1/a-b1.txt")))
    }

    /**
     * Test paths for which the directories are a substring of another, using the the [Glob] interface the way a user normally would.
     */
    @Test
    fun testSubPathFold_regressionFromFiles() {
        val basePath = File("test-files").toPath().toAbsolutePath()
        val files = Glob.from("a/b/a-b.txt", "a/b1/a-b1.txt", "a/b/c/a-b-c.txt").iterate(basePath, Glob.IterationOption.SKIP_HIDDEN).asSequence().toList().map {
            it.toString().removePrefix(basePath.toString())
        }.toHashSet()

        assertTrue(files.contains("/a/b/a-b.txt"), "/a/b/a-b.txt not matched.\n$files")
        assertTrue(files.contains("/a/b1/a-b1.txt"), "/a/b1/a-b1.txt not matched\n$files")
        assertTrue(files.contains("/a/b/c/a-b-c.txt"), "/a/b/c/a-b-c.txt not matched\n$files")
        assertTrue(3 == files.size, "Only 3 files should match instead of ${files.size}\n${files}")
    }

}
