# klob [![Build Status](https://travis-ci.org/shyiko/klob.svg?branch=master)](https://travis-ci.org/shyiko/klob) [![Maven Central](https://img.shields.io/maven-central/v/com.github.shyiko.klob/klob.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.shyiko.klob%22%20AND%20a%3A%22klob%22) <img src="https://img.shields.io/badge/dependencies-0-green.svg"> <a href="https://ktlint.github.io/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg" alt="ktlint"></a>

A [.gitgnore](https://git-scm.com/docs/gitignore#_pattern_format)-pattern-based glob library for Kotlin and/or Java 8+.  
Used by/Extracted from [ktlint](https://github.com/shyiko/ktlint).

## Usage

```xml
<dependency>
  <groupId>com.github.shyiko.klob</groupId>
  <artifactId>klob</artifactId>
  <version>0.1.0</version>
  <!-- omit classifier below if you plan to use this library in koltin -->
  <classifier>kalvanized</classifier>
</dependency>
```

> (java)

```java
Path path = Glob.from("src/**/*.kt", "!src/generated")
    .iterate(Paths.get("."))/*: Iterator<Path> */.next()
```

## Development

```sh
git clone https://github.com/shyiko/klob && cd klob
./mvnw # shows how to build, test, etc. project
```

## Legal

All code, unless specified otherwise, is licensed under the [MIT](https://opensource.org/licenses/MIT) license.  
Copyright (c) 2017 Stanley Shyiko.
