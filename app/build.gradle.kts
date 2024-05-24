import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    //implementation("com.google.accompanist:accompanist-gestures:0.31.2-alpha")
//    implementation(compose.ui)
//    implementation(compose.foundation)
    testImplementation("org.jetbrains.kotlin:kotlin-test")

//    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.neo4j.driver:neo4j-java-driver:4.3.3")
    //implementation("io.github.microutils", "kotlin-logging-jvm", "2.0.6")
//    implementation("io.coil-kt:coil-compose:2.2.2")
//    implementation("com.google.accompanist:accompanist-flowlayout:0.24.8-beta")
//    implementation("io.coil-kt:coil")
    //implementation(androidx.compose.material3:material3:1.0.0-alpha02)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "graphs"
            packageVersion = "1.0.0"
        }
    }
}
