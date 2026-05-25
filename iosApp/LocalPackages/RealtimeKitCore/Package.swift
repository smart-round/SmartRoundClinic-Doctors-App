// swift-tools-version:5.5
import PackageDescription

let package = Package(
    name: "RealtimeKitCore",
    platforms: [.iOS(.v13)],
    products: [
        .library(name: "RealtimeKitCore", targets: ["RealtimeKitCore"]),
    ],
    dependencies: [
        .package(
            url: "https://github.com/cloudflare/realtimekit-ios-core.git",
            from: "2.0.0"
        ),
    ],
    targets: [
        .target(
            name: "RealtimeKitCore",
            dependencies: [
                .product(name: "RealtimeKit", package: "realtimekit-ios-core"),
            ],
            path: "Sources/RealtimeKitCore"
        ),
    ]
)
