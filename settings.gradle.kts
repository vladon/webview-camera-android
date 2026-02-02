pluginManagement {
    repositories {
        maven { url = uri("file:///D:/projects/test-webview/repo") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven { url = uri("file:///D:/projects/test-webview/repo") }
        google()
        mavenCentral()
    }
}

rootProject.name = "WebViewCamera"
include(":app")
