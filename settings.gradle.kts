pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    // Declare plugin versions here (adjust versions as needed)
    plugins {
        // Android Gradle Plugin
        id("com.android.application") version "8.0.2"

        // Kotlin plugins
        id("org.jetbrains.kotlin.android") version "2.0.21"
        id("org.jetbrains.kotlin.kapt") version "1.9.0"

        // Hilt plugin
        id("com.google.dagger.hilt.android") version "2.45"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Andy"
include(":app")
