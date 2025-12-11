rootProject.name = "collector"

include(
    "collector-common",
    "collector-engine",
    "collector-outport",
    "collector-adapters:adapter-common",
    "collector-adapters:adapter-woowahan",
)
