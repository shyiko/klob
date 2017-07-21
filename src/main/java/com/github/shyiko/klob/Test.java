package com.github.shyiko.klob;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) {
        for (Path path : (Iterable<Path>)() -> Glob.from("**/*.java", "**/*.kt").iterate(Paths.get("."))) {
            System.out.println(path);
        }
    }
}
