[![](https://jitpack.io/v/amir1376/debugboard.svg)](https://jitpack.io/#amir1376/debugboard)
## Debug Board

Runtime debug utils for kotlin

## Features

- Watch variables when your app is running
- Inspect network request/response
- Show logs
- Directly in your browser

## Setup

### Dependency

in your gradle build script file

- add jitpack repository

```kotlin 
repositories {
    //...
    maven("https://jitpack.io")
}
```

- declare dependencies

```kotlin 
dependencies {
    //core library
    implementation("com.github.amir1376.debugboard:core")
    //backend for Web Panel
    implementation("com.github.amir1376.debugboard:core")

    //add one of these integrations for network inspection
    implementation("com.github.amir1376.debugboard:ktor")
    implementation("com.github.amir1376.debugboard:okhttp")

    // integration for android timber library
    implementation("com.github.amir1376.debugboard:timber")
}
```

## Usage

first of all create an instance of `DebugBoard`
and also start embedded webserver to access the Web Panel

###### note: server listens on port 8000 by default
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

add to watch list

```kotlin
debugBoard.variableWatcher.addWatch(watchable)
```

and after you don't want to watch this anymore

```kotlin
debugBoard.variableWatcher.removeWatch(watchable)
```

now when `stateFlow` changes you will be notified in Web Panel
```kotlin
stateFlow.value = "ha ha"  
```


### Network inspection

add one of network library integrations to your project dependencies

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

now all your request and response will be available in the Web Panel

you can send some logs to the log panel here is an example

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
if you use [timber library](https://github.com/JakeWharton/timber) you can plant `DebugBoardTree` to add your log data
into the Web Panel
in your Application class initial `timber` with

```kotlin
Timber.plant(
    DebugBoardTree(debugBoard),
    Timber.DebugTree()
)
```

## todos
1. [ ] add jetpack compose integration and utilities
2. [ ] improve ktor plugin to handle errors
3. [ ] add some screenshots from the Web Panel

