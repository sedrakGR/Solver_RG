

plugins {
    id("java")
    id("war")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories { mavenCentral() }

dependencies {
    // compile against your vendored jars but don't repackage them (we already ship WEB-INF/lib)
    compileOnly(fileTree(mapOf("dir" to "WebContent/WEB-INF/lib", "include" to listOf("*.jar"))))
    compileOnly("junit:junit:4.13.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "windows-1252" // or windows-1251 if you needed that earlier
    options.release.set(11)
}

tasks.war {
    webAppDirectory.set(file("WebContent"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("ssrgt_solver.war")
}

sourceSets {
    named("main") {
        java.setSrcDirs(listOf("src"))
        // IMPORTANT: put configs from src onto the classpath
        resources {
            setSrcDirs(listOf("WebContent", "src"))
            include("**/*.xml", "**/*.properties", "**/*.json")
        }
    }
}

