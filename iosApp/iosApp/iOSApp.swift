import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    // instantiate Koin for iOS
    init() {
        KoinInitializerKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}