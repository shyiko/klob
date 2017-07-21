package com.github.shyiko.klob;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;

import static com.github.shyiko.klob.$Glob.EMPTY_IP_SET;
import static com.github.shyiko.klob.internal.PackageKt.visit;

public interface Glob {
    static Glob from(String... pattern) {
        return new Glob() {
            @Override
            public Iterator<Path> iterate(Path path, IterationOption... option) {
                return visit(path,
                    option == null || option.length == 0 ? EMPTY_IP_SET : EnumSet.copyOf(Arrays.asList(option)),
                    Arrays.asList(pattern)
                ).iterator();
            }

            @Override
            public Iterator<Path> iterate(Path path) {
                return iterate(path, new IterationOption[0]);
            }
        };
    }

    Iterator<Path> iterate(Path path, IterationOption... option);
    Iterator<Path> iterate(Path path);

    enum IterationOption {
        SKIP_HIDDEN
    }
}

class $Glob { static EnumSet EMPTY_IP_SET = EnumSet.noneOf(Glob.IterationOption.class); }
