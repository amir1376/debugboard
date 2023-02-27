[![](https://jitpack.io/v/amir1376/debugboard.svg)](https://jitpack.io/#amir1376/debugboard)
## Debug Board

Runtime debug utils for kotlin that has a web panel

## Features

- Watch variables when your app is running
- Inspect network request/response
- Show logs
- Directly in your browser

## Setup

### Dependency

In your gradle build script file

- Add the jitpack repository

```kotlin 
repositories {
    //...
    maven("https://jitpack.io")
}
```

- Declare dependencies

```kotlin 
dependencies {
    //core library
    implementation("com.github.amir1376.debugboard:core")
    //backend for Web Panel
    implementation("com.github.amir1376.debugboard:backend")

    //add one of these integrations for network inspection
    implementation("com.github.amir1376.debugboard:ktor")
    implementation("com.github.amir1376.debugboard:okhttp")

    // integration for android timber library
    implementation("com.github.amir1376.debugboard:timber")
}
```

## Usage

First of all create an instance of `DebugBoard`
and also start embedded webserver to access the Web Panel

###### Note: server listens on port 8000 by default
(also server address will be logged in stdout)
```kotlin
val debugBoard = DebugBoard()
DebugBoardBackend(debugBoard = debugBoard).startWithDefaultServer()
```

### Watch a variable

To watch a variable , your variable must be an observable or somehow notify library about its update
here I use a StateFlow

```kotlin
val stateFlow = MutableStateFlow("bla bla")
val watchable = FlowWatchable("variable", flow)
```

Add to watch list

```kotlin
debugBoard.variableWatcher.addWatch(watchable)
```

And after you don't want to watch this anymore

```kotlin
debugBoard.variableWatcher.removeWatch(watchable)
```

Now when `stateFlow` changes you will be notified in Web Panel
```kotlin
stateFlow.value = "ha ha"  
```


### Network inspection

Add one of network library integrations to your project dependencies

- #### [Ktor client](https://github.com/ktorio/ktor)

```kotlin
HttpClient {
    //...
    install(KtorDebugBoard) {
        debugBoard(debugBoard)
    }
}
```

- #### [Okhttp client](https://github.com/square/okhttp)

```kotlin
OkHttpClient
    .Builder()
    .addInterceptor(OkHttpDebugBoardInterceptor(debugBoard))
    .build()
```

Now all your request and response will be available in the Web Panel

You can send some logs to the log panel here is an example

```kotlin
debugBoard.logger.log(
    LogData(
        LogLevel.Debug,
        "TAG",
        "my message"
    )
)
```
#### Timber
If you use [timber library](https://github.com/JakeWharton/timber) you can plant `DebugBoardTree` to add your log data
into the Web Panel
in your Application class initial `timber` with

```kotlin
Timber.plant(
    DebugBoardTree(debugBoard),
    Timber.DebugTree()
)
```

## Todos
1. [ ] Add jetpack compose integration and utilities
2. [ ] Improve ktor plugin to handle errors
3. [ ] Add some screenshots from the Web Panel
4. [ ] Add database management
