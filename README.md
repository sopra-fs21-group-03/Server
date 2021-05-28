# Poker Game from Group 03, SoPra FS 21 

## Introduction

The goal of this project was to create a Poker game with a lobby system, so that Users can register or login to our system, can chose a lobby where they want to play and, once 5 players are in a specific lobby and are ready, can play Poker. If the User does not have an account yet in our system, he must register to be considered as a valid User. Once he is registered, he can login again with his chosen username and password, if he was offline before. Our Poker game contains a Poker-Instructions page for User that do not know how to play Poker. When playing, a User can click on a button where the poker hands are shown. Therefore, a User can get guidance if he needs some. When playing and on turn, a User can raise, check, fold or call. A game session also contains a chat/log window, where Users can chat or see what events happened in the game. Once a game round is finished, the Server determines the winner(s) and organizes the next game round. A User can always, if he likes, leave a game session and chose in the lobby selection screen a different lobby where he wants to play.

## Technologies

Server (BackEnd):
-	Spring Boot
-	Hibernate
-	Java

## High-level Components 

GameController: The GameController contains all API endpoints that are related to games. Therefore, all endpoints start with /games. The endpoints are designed in a RESTful way. When an endpoint gets called, the GameController orchestrates the order in which the methods should be performed. Therefore, the GameController is responsible for the high-level organization. The GameController does not contain any methods that help execute and perform the game logic, but these methods are implemented inside the GameService.  

Link:  
https://github.com/sopra-fs21-group-03/Server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/controller/GameController.java  

GameService: The GameService contains all the methods that are important to have a running game session. The GameService ensures that the flow of a game is correct. It ensures that no user at no point runs in a deadlock during a game and that all users can do what they should be able to do during the game but not more. It mostly enforces this by checking an authentication token or checking internal conditions. 

Link: 
https://github.com/sopra-fs21-group-03/Server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/service/GameService.java  
 

LobbyController: This component is responsible for putting or removing users to a lobby/game session. Also, this component has an API call for changing the “ready” status of a User. When 5 users are in a lobby, and all are ready, the LobbyController delegates to the LobbyService, that the game session has to be started and setuped now. 
Again, a Controller always has a specific Service conntected to it. The fitting Service for the LobbyController is the LobbyService, which handles all specific lobby functionality. 

Link: 
https://github.com/sopra-fs21-group-03/Server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/controller/LobbyController.java  

## Launch & Deployment

### Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs21` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

### Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Testing

Have a look here: https://www.baeldung.com/spring-boot-testing

### External Dependencies 

We have no external database that needs to be running or external dependencies. 

### Releases 

When you want to publish a release, create a tag on GitHub for the repository

## Roadmap 

New developers could add: 

1. Functionality, such that a User can chose a picture from his computer or the Internet, upload this picture to our Server and this picture will be set as his profile picture. 

2. A User overview and User search function: In a search dialog, a User can type in the username of another user to find him and communicate with him. Once the User found the User he was looking for, he can inspect the profile page of this User. A profile page might contain an overview of all the wins/losses a user has experienced. Also, the profile page could show the profile picture of a User. 

3. Chatting which is independent of a game session: Right now, chatting is bound to a game session. With this functionality, a user can have a chat window all the time (also in the lobby) and chat with a specific User or, if he created a chat group, can chat with multiple Users. 

## Authors and acknowledgment 

Group leader: Jonas Graze 
Github: sanoj765 

Carlos Kirchdorfer  
Github: mr-carlitos 

Luca Huber  
Github: cobankar 

Loris Keist  
Github: sironitro 

Samuele Giunta  
Github: Samy1101 

## License 

MIT License 

Copyright (c) 2021 Sopra Group 03 

Permission is hereby granted, free of charge, to any person obtaining a copy 

of this software and associated documentation files (the "Software"), to deal 

in the Software without restriction, including without limitation the rights 

to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 

copies of the Software, and to permit persons to whom the Software is 

furnished to do so, subject to the following conditions: 

 

The above copyright notice and this permission notice shall be included in all 

copies or substantial portions of the Software. 

 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 

IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 

FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 

AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 

LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 

OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 

SOFTWARE.


