rootProject.name = "ktor-hexagonal-multimodule"
include(
    "app:infra",
    "app:core",
    "app:adapters:env",
    "app:adapters:primary-web",
    "list",
    "utilities"
)
