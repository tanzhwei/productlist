pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // 阻止子项目定义 repositories
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "backend"