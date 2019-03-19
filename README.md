# PorchPirateProtector
A Kotlin/Native Ecosystem to prevent package pilfering

## What now? 

### What the hell is a Porch Pirate?

A Porch Pirate is someone who steals a package which has been delivered from the porch of someone who is not home. An example of this can be seen in one of my favorite YouTube videos ever, [Package Thief vs. Glitter Bomb Trap](https://www.youtube.com/watch?v=xoxhDk-hwuo). 

### How Does This Prevent Porch Piracy?

Unlike the YouTuber who made the focus of his innovation Utterly Delightful Revenge, I have never worked at NASA, so my level of innovation went more to the "stop packages from getting stolen" side of things. 

This project is designed to work with a prototype wooden box which will eventually be evolved into some kind of metal box you can bolt to the side of your house. An iOS and Android app will talk to a server, and the server will send commands and receive messages from a Raspberry Pi attached to a servo motor which can lock and unlock the box.

This project will use [Kotlin/Native](https://kotlinlang.org/docs/reference/native-overview.html) to allow code to be shared across all four software parts of the project (iOS, Android, Server, and RPi). 

### What the hell is Kotlin/Native?

Kotlin/Native and Kotlin Multiplatform projects are ways that JetBrains, the team behind Kotlin, have created to allow you to use Kotlin across multiple platforms. 

Kotlin was originally built to work with only the Java Virtual Machine, but JetBrains have also built a compiler, known as Konan, that outputs [LLVM](https://llvm.org/)-intermediate representation bytecode, which allows code to be handed to the LLVM compiler so it can be compiled in many of the places where LLVM allows code to run. 

Specifically, this includes iOS, Mac, Windows, and assorted flavors of Linux (including Raspberry Pi). 

A multiplatform project is a project which allows you to build shared code for both JVM-based projects and Native projects (as well as Javascript projects, but we won't be working with that here). 

**Kotlin/Native and Multiplatform projects are still in beta**. Things are going to break. This is a super-exciting ecosystem 

## Project Overview

There are (going to be) five primary parts of this application: 

1. `PPPShared`, the shared framework used across all platforms
2. An Android app, built in Kotlin, with the `PPPShared` module as a local dependency. 
3. An iOS app, built in Swift and which leverages an Objective-C framework built by Kotlin/Native as a dependency.
4. A [ktor](https://ktor.io) server, which runs on the JVM in a Docker container, and which uses the `PPPShared` module as a local dependency.
5. **[NOT BUILT YET]** A small app that will allow the Raspberry Pi to pair with a device, listen for commands from the server, and send results back to the server.

### Folder Structure

Folder structure in Gradle projects is extremely important - it helps define where things are placed so that you can reuse them. Here's a general overview of what the folder structure looks like: 

Note: For `android` several things are in `java` folders, but they actually contain Kotlin code. 

```
code
 build.gradle // basic gradle information for the entire project
 gradle.properties // Definitions of things used across all gradle files in the project
 settings.gradle // General settings for entire project
 |- android
     build.gradle // Android app-specific gradle file
     |- src
         |- androidTest
             |- java // Tests which require Android elements
         |- main
             |- java // main code for the Android app
         |- test
             |- java // Tests which only require the JVM 
 |- iOS
     |- PorchPirateProtector // Main Swift code for iOS app
     |- PorchPirateProtector.xcodeproj // Open this to see the iOS app
     |- PorchPirateProtectorTests // iOS-specific Swift tests.
 |- PPPShared
     build.gradle // Gradle file for everything in the shared lib on all platforms
     |- src
         |- androidMain
             |- kotlin // Library Platform-specific implementations for Android
         |- androidTest
             |- kotlin // Test platform-specific implementations for Android/JVM
         |- commonMain
             |- kotlin // Shared Kotlin code across JVM (android, server) and Native (iOS)
         |- commonTest
             |- kotlin // Shared test code across JVM and Native
         |- iosMain
             |- kotlin // Library platform-specific implementations for iOS
         |- iosTest
             |- kotlin // Test platform-specific implementations for iOS/Native.
 |- server
     build.gradle // Gradle file for server-specific setup
     |- scripts // Some helper scripts I made because Docker syntax is annoying
     |- src // Server application source code
```

### Architecture

This project uses one of the many `Model-View-PleaseDearGodAnythingButTheModelOrView` architectures which proliferate on mobile development platforms, specifically [`Model-View-Presenter`](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter). 

This architecture was selected for a couple of reasons: 

- It is the same architecture the [KotlinConf app](https://github.com/JetBrains/kotlinconf-app) uses, which is a very, very helpful reference
- Most business and data logic becomes centralized in the `presenter` object, which can be shared across platforms
- A "view" is actually just a Kotlin `interface`, meaning that each platform has to implement its own version of what a view is.

On iOS, the items implementing the `view` interface (translated into an Obj-C protocol) tend to be `UIViewControllers`. On Android, they tend to be `Fragments` (sorry, [Jake](https://twitter.com/jakewharton)). 

While testing, you don't even need to have a `view` that does any kind of drawing on the screen. You can simply create a class which implements the `view` protocol and has backing variables you can check to see if various methods got hit. This makes it possible to run tests without the iOS and Android runtimes (and therefore run *way* faster than UI tests). 

## How Do I Run All This? 

In order to run all elements of this project you will need: 

- A Mac (sorry, can't compile iOS on anything else)
- Xcode
- Android Studio
- Docker for Mac 

### Common Tests

To run the Common tests, you need to run `PPPShared`'s `androidTest` task, you have a couple options. You can either use the Gradle sub-window of Android Studio and double click under `code > PPPShared > androidTest` to run the android tests. 

If you prefer the command line, from the root of the project you can run: 

```
./gradlew PPPShared:androidTest
```

Through either method, results of your tests will be output to [`code/PPPShared/build/reports/tests/androidTest/index.html`](code/PPPShared/build/reports/tests/androidTest/index.html). Note that this file is not checked into version control, so it won't appear on this repo, only locally.

### Android App

In Android Studio, you should have an `app` configuration created by default when AS recognizes that the project contains an Android application target. 

If this does not get created, go to `Edit Configurations...`, add a new Android app configuration, and set it to use the `android` module. Once that's set up, you should be able to use the Run button in Android studio to run the application.

### iOS App

In iOS, select the `PorchPirateProtector` scheme and hit the run button. If for some reason the system decides to get in a loop where it can't find the iOS binaries, go back to Android Studio and run the `packForXcode` task under `code > PPPShared > other` or run: 

```
/.gradlew PPPShared:packForXcode
```

at the command line from the project root. 

### Server

First, make sure Docker for Mac is running. Otherwise the rest of this will faill spectacularly. 

**// TODO: Automate most of this with a `docker-compose.yml` file.**

#### Setting up the MySQL Database

>**NOTE:** The setup only has to be done once. Once you've setup your database, you just need to run the [convenience shell script](code/server/scripts/docker_mysql_start.sh) to start your database before starting the server.

Next, you'll need to make sure you have a docker image that will serve up MySQL 5.7.24. Follow the instructions on [the MySQLServer DockerHub page](https://hub.docker.com/r/mysql/mysql-server) to get the 5.7.24 image installed - make sure you set up a `root` user with a proper password before proceeding.

Once that's installed, run the convenience [`docker_mysql_start.sh`](code/server/scripts/docker_mysql_start.sh) script (you may need to make it locally excecutable) to start that container, run it headless, and print out its current information. 

Grab the `IPAddress` from that and make sure it's the same one as in the [`Database.kt`](code/server/src/no/bakkenbaeck/porchpirateprotector/Database.kt) file - otherwise you'll need to update that file. You will need to get into the container and make sure you've got a database which matches the db name in that file, and a user which matches the DB user in that file. 

To do that, you'll need to get into the container's shell. To do so, run: 

```
docker exec -it mysql1 /bin/sh
```

You'll need to make sure that you have a MySQL user setup with a database name, username, and password matching those in the  file on that database. 

To get into the MySQL command line once you're in the container's shell, run: 

```
mysql -u root -p
```

and hit enter. You will be prompted for your password. Once you're in to MySQL, add a new database: 

```
create database ppp;
```

Next a new user which will allow you to access the database you just created with password authorization, with username and password matching those in [Database.kt](code/server/src/no/bakkenbaeck/porchpirateprotector/Database.kt): 

```
GRANT ALL PRIVILEGES ON ppp.* TO 'ppp-database'@'%' IDENTIFIED BY 'password';
```

You should then flush privileges to make sure changes are properly applied: 

```
flush privileges;
```

Next, you can leave the MySQL command line by typing

```
exit
```

At this point, you'll be back in the container's command line. You can also leave this by typing:

```
exit
```

#### Building And Running The Server App

Run the [`docker_rebuild.sh`](code/server/scripts/docker_rebuild.sh) script in order to create a new `jar` based on the code in `server`, and then to create a docker image with that `jar`. 

When that completes successfully, you can run [`docker_run.sh`](code/server/scripts/docker_run.sh) to start the docker container. 

Note that anytime you make a change to the code in `server`, you'll need to run 

```
docker stop ppp
```

in order to stop the running container, then rebuild and rerun the server to allow your changes to apply. You do not need to rerun the MySQL server. 

### Is this thing on? 

After all that, in a terminal window on your Mac, run:

```
docker ps
```

This will print the status of all your currently running containers. You should see the two containers, `ppp` (containing the server) and `mysql1` (containing the database) in the list. 




