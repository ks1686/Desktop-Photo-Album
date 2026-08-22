plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("model.Photos")
}

javafx {
    version = "21.0.6"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Builds a self-contained runnable jar: `java -jar` works on any JDK 21+
 * install with no JavaFX setup. The manifest points at a plain launcher
 * class because JavaFX rejects Application subclasses launched from a jar.
 */
tasks.jar {
    archiveFileName.set("DesktopPhotoAlbum.jar")
    manifest {
        attributes("Main-Class" to "launcher.Launcher")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("module-info.class", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}
