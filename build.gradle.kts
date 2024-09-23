plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "net.momirealms"
version = "1.3.2.5"

repositories {
    maven {name = "aliyun-repo"; url = uri("https://maven.aliyun.com/repository/public/")}
    maven {name = "papermc-repo"; url = uri("https://papermc.io/repo/repository/maven-public/")}
    maven {name = "sonatype-repo"; url = uri("https://oss.sonatype.org/content/groups/public/")}
    maven {name = "dmulloy2-repo"; url = uri("https://repo.dmulloy2.net/repository/public/")}
    maven {name = "clip-repo"; url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")}
    maven {name = "codemc-repo"; url = uri("https://repo.codemc.org/repository/maven-public/")}
    maven {name = "sk89q-repo"; url = uri("https://maven.enginehub.org/repo/")}
    maven {name = "jitpack-repo"; url = uri("https://jitpack.io")}
    maven {name = "Lumine-repo"; url = uri("https://mvn.lumine.io/repository/maven-public")}
    maven {name = "rapture-repo"; url = uri("https://repo.rapture.pw/repository/maven-releases/")}
    maven {name = "mmo-repo"; url = uri("https://nexus.phoenixdevt.fr/repository/maven-public/")}
    maven {name = "i-repo"; url = uri("https://r.irepo.space/maven/")}
    maven {name = "auxilor-repo"; url = uri("https://repo.auxilor.io/repository/maven-public/")}
    maven {name = "betonquest-repo"; url = uri("https://betonquest.org/nexus/repository/betonquest/")}
    mavenCentral()
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("commons-io:commons-io:2.11.0")
    compileOnly("redis.clients:jedis:4.4.3")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("io.lumine:Mythic-Dist:5.2.1")
    compileOnly("dev.dejvokep:boosted-yaml:1.3.1")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")

    shadow("de.tr7zw:item-nbt-api:2.13.2")
    shadow("org.bstats:bstats-bukkit:3.0.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.release = 21
    options.encoding = "UTF-8"
}

tasks{
    build{
        dependsOn(shadowJar)
    }

    shadowJar {
        configurations = listOf(project.configurations.getByName("shadow"))

        archiveFileName.set(rootProject.name + "-Spigot.jar")

        relocate("de.tr7zw", "net.momirealms.customfishing.libs.de.tr7zw")
        relocate("org.bstats", "net.momirealms.customfishing.libs.org.bstats")
    }
}


