rootProject.name = "ktor-hexagonal-multimodule"
include(
    ":app:infra",
    ":app:adapters:primary-web",
    "list",
    "utilities"
)
