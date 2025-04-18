pluginManagement {
    repositories {
        google()
        maven(url ="https://jitpack.io" )
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.7.3"
        id("com.google.gms.google-services") version "4.4.2"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "ViDuVideoWithFireBase"
include(":app")
 