rootProject.name = "ktor-hexagonal-multimodule"
include(
    "app:infra",
    "app:core",
    "app:common",
    "app:adapters:env",
    "app:adapters:persist",
    "app:adapters:remoting",
    "app:adapters:primary-web",
)
