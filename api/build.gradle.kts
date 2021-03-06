plugins {
    `maven-publish`
}

dependencies {
    compileOnlyApi("net.pl3x.purpur", "purpur-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnlyApi("org.checkerframework", "checker-qual", "3.9.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories.maven {
        url = uri("https://repo.pl3x.net/snapshots")
        credentials(PasswordCredentials::class)
    }
}
