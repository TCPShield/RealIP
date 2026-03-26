buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    idea
}

version = "2.8.2"
group = "net.tcpshield.tcpshield"

base {
    archivesName.set("TCPShield")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

sourceSets {
    named("main") {
        java.srcDirs("src/main/java")
        resources.srcDirs("src/main/resources")
        resources.exclude("plugin.yml", "bungee.yml", "velocity-plugin.json")
    }
    named("test") {
        java.srcDirs("src/test/java")
        resources.srcDirs("src/test/resources")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven(url = "https://repo1.maven.org/maven2/")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven(url = "https://repo.dmulloy2.net/nexus/repository/public/") // ProtocolLib
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots") // Bungee
    maven { // Paper
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven(url = "https://repo.opencollab.dev/maven-snapshots/") // Floodgate
}

dependencies {
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0-M1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0-M1")
}

val generateVersionedResources by tasks.registering(Sync::class) {
    into(layout.buildDirectory.dir("generated/versioned-resources/main"))
    from("src/main/resources") {
        include("plugin.yml", "bungee.yml", "velocity-plugin.json")
        expand("version" to project.version.toString())
    }
}

tasks.named<Copy>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(generateVersionedResources)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

configurations.named("testImplementation") {
    extendsFrom(configurations.named("compileOnly").get())
}

defaultTasks("build")
