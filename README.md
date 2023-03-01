[![](https://jitpack.io/v/amir1376/debugboard.svg)](https://jitpack.io/#amir1376/debugboard)

## Debug Board

Runtime debug utils for kotlin that has a web panel

<img alt="logo" src="static/logo.svg"/>

## Features

- Watch variables when your app is running
- Inspect network request/response
- Show logs
- Directly in your browser

## Setup

### Dependency

Add to your gradle build script file

```kotlin 
//build.gradle.kts

repositories {
    //...
    maven("https://jitpack.io")
}

dependencies {
    //...
    val version =/*see the last version above*/
        //core library
        implementation("com.github.amir1376.debugboard:core:$version")
    //backend for Web Panel
    implementation("com.github.amir1376.debugboard:backend:$version")
    //optional integrations
    //add one of these integrations for network inspection
    implementation("com.github.amir1376.debugboard:ktor:$version")
    implementation("com.github.amir1376.debugboard:okhttp:$version")
    // integration for android timber library
    implementation("com.github.amir1376.debugboard:timber:$version")
    // integration for jetpack compose library 
    implementation("com.github.amir1376.debugboard:compose:$version")
}
```

## Usage

Start embedded Web Server to access the Web Panel

###### Note: server listens on port 8000 by default

(also server address will be logged in `STDOUT`)

```kotlin
DebugBoardBackend().startWithDefaultServer()
```

### Watch a variable
![Watcher in panel](static/watcher_panel.png)

To watch a variable you have to passed it into addWatch

your variable must be an observable or somehow notify library about its update
here I use a StateFlow

```kotlin
val stateFlow = MutableStateFlow("bla bla")
val removeWatch = addWatch("specifyName", stateFlow) 
```

Now when `stateFlow` changes you will be notified in Web Panel

```kotlin
stateFlow.value = "ha ha"  
```

And after you don't want to watch this anymore
call returned function from the addWatch() to remove from watchlist

```kotlin
removeWatch()
```

### Network inspection

![Network Panel](/static/network_panel.png)

Add one of network library integrations to your project dependencies

- #### [Ktor client](https://github.com/ktorio/ktor)

```kotlin
HttpClient {
    //...
    install(KtorDebugBoard)
}
```

- #### [Okhttp client](https://github.com/square/okhttp)

```kotlin
OkHttpClient
    .Builder()
    //...
    .addInterceptor(OkHttpDebugBoardInterceptor())
    .build()
```

Now all your request and response will be available in the Web Panel

### Log
You can send some logs to the log panel here is an example

```kotlin
DebugBoard.Default.logger.log(
    LogData(
        LogLevel.Debug,
        "TAG",
        "my message"
    )
)
```

#### Jetpack Compose

You can use AddWatch composable function
to watch a variable inside a composition scope
it will be automatically removed after Composable exits from the screen

```kotlin
@Composable
fun MyPage() {
    val myUiState =/*...*/
        AddWatch("MyPageState", myUiState)
}
```

#### Timber

If you use [timber library](https://github.com/JakeWharton/timber) you can plant `DebugBoardTree` to add your log data
into the Web Panel
in your Application class initial `timber` with

```kotlin
Timber.plant(
    DebugBoardTree(),
    Timber.DebugTree()
)
```

## Todos

1. [X] Add jetpack compose integration and utilities
2. [X] Improve ktor plugin to handle errors
3. [X] Add some screenshots from the Web Panel
4. [X] Improve log and add filter in Web Panel
5. [ ] Add database into panel
