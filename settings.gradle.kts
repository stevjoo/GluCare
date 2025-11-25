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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Tambahkan JitPack disini dengan syntax Kotlin yang benar (pakai uri dan kutip dua)
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MAPMidTermProject"
include(":app")