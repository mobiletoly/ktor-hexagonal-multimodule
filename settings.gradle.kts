rootProject.name = "ktor-hexagonal-multimodule"
include(
    "app:infra",
    "app:core",
    "app:common",
    "app:adapters:env",
    "app:adapters:persistence",
    "app:adapters:remoting",
    "app:adapters:primary-web",
)
