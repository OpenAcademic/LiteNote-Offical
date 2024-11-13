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
        maven {
            url = uri("https://repo1.maven.org/maven2/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repo1.maven.org/maven2/")

        }
        maven { url = uri("https://jitpack.io") }
    }

}

rootProject.name = "Lite Note"
include(":app")
 