# TFG_Poker

Poker application developed in Java as part of a Bachelor's Thesis.
This application allows humans and bots to play No-Limit Texas Hold'em (NLHE) poker games.

The application follows a distributed client-server architecture. The server is responsible for managing the game environment, while clients connect to the server to participate in poker games.

# Table of Contents

* [Project Requirements](#project-requirements)
* [Project Structure](#project-structure)
* [Architecture](#architecture)
* [Release Version — No Installation Required](#release-version--no-installation-required)

* [Installation Guide](#installation-guide)
  * [Windows](#windows)
  * [Linux](#linux)

* [VS Code Configuration](#vs-code-configuration)
* [Building the Project](#building-the-project)

* [Running the Project](#running-the-project)
  * [Server application](#server-application)
  * [Client application](#client-application)

* [Network Configuration](#network-configuration)
* [Connectivity Test](#connectivity-test)
* [Troubleshooting](#troubleshooting)
* [Installation Summary](#installation-summary)
* [Quick start](#quick-start)


# Project Requirements

The following components are required to build and run the project:
* **Java SDK 21**
* **JavaFX 21** (Handled by Maven)
* **Visual Studio Code** (recommended)

**Maven is optional.**

The project includes the **Maven Wrapper**, so a separate global Maven installation is not required. The Maven Wrapper downloads and uses the Maven version configured by the project.

On Windows, use `mvnw.cmd`.
On Linux, use `./mvnw`.

## Check Java installation

To check the installed Java version:
```bash
java -version
```

To check the Java compiler version:
```bash
javac -version
```

Both commands should report **Java 21**.

## Check Maven Wrapper

On Windows:
```cmd
mvnw.cmd -version
```

On Linux:
```bash
./mvnw -version
```

The output should indicate that Maven is running with **Java 21**.

> [!NOTE]
> Although the project uses Maven, a global Maven installation is not required. The Maven Wrapper included in the project should be used whenever possible to ensure that the expected Maven version is used.

# Project Structure

The project is organized as a Maven multi-module project.
The main components include:
```text
tfgpoker/
├── pom.xml
├── client/
├── server/
└── ...
```

The **client** module contains the client application and its JavaFX graphical user interface.
The **server** module contains the server application responsible for accepting client connections and managing the server-side functionality.

The root `pom.xml` manages the Maven project and its modules.

> [!NOTE]
> The exact internal package and class structure may vary depending on the implementation. The important modules for running the distributed application are `client` and `server`.

# Architecture

The application uses a distributed client-server architecture.
```text
                  TCP connection
                    Port 5005
                         │
        ┌────────────────┴────────────────┐
        │                                 │
   Client 1                           Client 2
   JavaFX                              JavaFX
        │                                 │
        └────────────────┬────────────────┘
                         │
                         ▼
                  Poker Server
                    Port 5005
```

The server must be running before external clients attempt to connect.
Multiple clients can connect to the same server, depending on the application's implementation and configuration.

## Server

The server application listens for client connections on:
```text
TCP port: 5005
```

When the server is running on a remote machine, clients must be able to establish a TCP connection to the server's IP address on port `5005`.

## Client

The client application provides the JavaFX graphical interface.
When connecting to a remote server, the client must be provided with the server's reachable IP address.

If the client and server are running on the same machine, the IP address can be left empty according to the application's current behavior.

# Release Version — No Installation Required

A pre-built version of the application is available through the project's GitHub Releases.

This version includes everything required to run the application, including the Java runtime and all necessary dependencies. **No installation or configuration is required.**

> [!NOTE]
> This version includes a limited selection of pre-configured artificial intelligence models. If you wish to customize, replace, or remove these models, it is recommended to download the repository and open it in an IDE, giving you full control over the project's configuration and source code.

## 1. Download the latest Release

Go to the project's **Releases** section on GitHub and download the ZIP file corresponding to the desired release.
Extract the downloaded ZIP file to a location of your choice.

## 2. Start the server

Open the extracted folder and run:
```text
run-server.bat
```

This will launch the poker server.
The server must be running before starting the clients.

## 3. Start the client

To launch a client application, run:
```text
run-client.bat
```

A JavaFX client window will open.
Multiple clients can be started by running `run-client.bat` multiple times.

> [!NOTE]
> The Release version is intended for users who only want to run the application. It does not require Java, Maven, JavaFX, or any other additional software to be installed.**

> [!IMPORTANT]
> The server and clients must still be able to communicate over TCP port `5005`. If the server and client are running on different machines, network and firewall configuration may be required. See the [Network Configuration](#network-configuration) section for more information.


# Installation Guide

## Windows

### 1. Install Java SDK 21

Install a distribution of **Java SDK 21**.

It is recommended to install it in a path similar to:
```text
C:\Program Files\Java\jdk-21
```

Once Java has been installed, open a new terminal and check the installation:
```cmd
java -version
```

```cmd
javac -version
```

Both commands should display Java version 21.

### 2. Configure JAVA_HOME

The `JAVA_HOME` environment variable must point to the Java SDK installation.

In Windows:
1. Search for **"Edit the system environment variables"**.
2. Select **"Environment Variables"**.
3. Create a new variable named:
```text
JAVA_HOME
```

4. Set its value to the JDK installation directory, for example:
```text
C:\Program Files\Java\jdk-21
```

5. Edit the `Path` variable.
6. Add:
```text
%JAVA_HOME%\bin
```

After making these changes, open a new terminal and check the configuration:
```cmd
echo %JAVA_HOME%
```

```cmd
java -version
```

### 3. JavaFX 21

The project uses **JavaFX 21** for its graphical user interface.

JavaFX is managed through **Maven**, so a manual installation of the JavaFX SDK is not required.

The necessary JavaFX dependencies are defined in the project's Maven configuration and will be downloaded automatically when Maven builds or runs the client application.

> [!NOTE]
> Avoid installing and manually configuring a separate JavaFX SDK unless the project configuration specifically requires it. Mixing a manual JavaFX installation with the Maven-managed dependencies can cause configuration and runtime problems.

## Linux

### 1. Install Java SDK 21

On Debian/Ubuntu-based distributions, Java 21 can be installed with:
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

Check the installation:
```bash
java -version
```

```bash
javac -version
```

Both commands should display Java version 21.

### 2. Configure JAVA_HOME

To find the Java installation path, use:
```bash
readlink -f $(which java)
```

The JDK is usually installed in a path similar to:
```text
/usr/lib/jvm/java-21-openjdk-amd64
```

To configure `JAVA_HOME`, edit the Bash configuration file:
```bash
nano ~/.bashrc
```

Add the following lines, adjusting the path if necessary:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

Apply the changes:
```bash
source ~/.bashrc
```

Check the configuration:
```bash
echo $JAVA_HOME
```

```bash
java -version
```

### 3. JavaFX 21

JavaFX is managed through **Maven**, so no manual JavaFX SDK installation is required.

Maven will automatically download the required JavaFX dependencies when the project is built or the client is run.

# VS Code Configuration

Once Java 21 has been installed, open the project's root directory with **Visual Studio Code**.

## Recommended Extensions

The following extensions are recommended for working with the project:
* **Extension Pack for Java**
* **Language Support for Java™ by Red Hat**
* **Debugger for Java**
* **Test Runner for Java**
* **Maven for Java**

The **Maven for Java** extension allows Maven projects and their available goals to be managed directly from VS Code.

## Configure the JDK

Make sure that VS Code is using **Java 21**.

Open the Command Palette:
```text
Ctrl + Shift + P
```

Search for:
```text
Java: Configure Java Runtime
```

The installed JDKs should be displayed.

Make sure that **JDK 21** is selected for the project.

If multiple Java versions are installed, make sure that Java 21 is configured as the JDK used by the project.

# Building the Project

Before running the applications, it is recommended to build the complete project and download its dependencies.

Open a terminal in the project's root directory.

## Windows

Use the Maven Wrapper:
```cmd
mvnw.cmd clean install -DskipTests
```

## Linux

First make sure that the Maven Wrapper has execution permissions:
```bash
chmod +x mvnw
```

Then run:
```bash
./mvnw clean install -DskipTests
```

The command cleans the project, downloads the required dependencies, compiles the modules and installs the generated artifacts while skipping the tests.

> [!NOTE]
> A separate `clean compile` command is not necessary after `clean install`, because the Maven `install` lifecycle already includes the compilation phase.

## Verify Maven Java version

To verify which Java installation Maven is using:

Windows:
```cmd
mvnw.cmd -version
```

Linux:
```bash
./mvnw -version
```

Make sure the Java version reported by Maven is **21**.

# Running the Project

The server and client applications must be started separately.
The recommended workflow is:
```text
1. Start the server
2. Make sure port 5005 is accessible
3. Start one or more clients
4. Connect the clients to the server
```

# Server application

> [!IMPORTANT]
> Running the server on a Linux machine is recommended.

## Open port 5005

If clients connect from another machine, the server must accept incoming TCP connections on port `5005`.

### UFW (Ubuntu/Debian)

To allow incoming connections:
```bash
sudo ufw allow in 5005/tcp
```

If outgoing traffic also needs to be explicitly configured:
```bash
sudo ufw allow out 5005/tcp
```

Check the firewall status:
```bash
sudo ufw status
```

### firewalld (Fedora/RHEL/CentOS)

```bash
sudo firewall-cmd --permanent --add-port=5005/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports
```

## Verify that the server is listening

After starting the server, verify that port `5005` is listening:
```bash
sudo ss -lntp | grep :5005
```

The server should listen on:
```text
0.0.0.0:5005
```

This allows connections through the available network interfaces.

If the server listens on:
```text
127.0.0.1:5005
```

it will only accept connections originating from the local machine.

## IMPORTANT: FIREWALL / ROUTER CONFIGURATION

If you cannot connect to the server, you may need to allow traffic through port `5005` in your router's firewall.
> [!IMPORTANT]
> Opening TCP port `5005` in a firewall only provides network connectivity. It does not encrypt or otherwise secure the application protocol.

### How to configure it

1. Open your router's web administration interface. This is usually accessible by entering the router's IP address (for example, `192.168.1.1` or `192.168.0.1`) in your web browser.
2. Log in using your router's administrator credentials.
3. Look for a section named **Firewall**, **Port Forwarding**, **NAT**, **Virtual Server**, or **Port Mapping**. The exact name depends on your router model.
4. Create a rule allowing/forwarding **TCP traffic on port `5005`** to the local IP address of the computer running the server.
5. Save the configuration and restart the router if required.

| Setting            | Value                            |
| ------------------ | -------------------------------- |
| **Protocol**       | `TCP`                            |
| **External Port**  | `5005`                           |
| **Internal Port**  | `5005`                           |
| **Destination IP** | *Local IP address of the server* |

> **Note:** The exact steps and terminology may vary depending on your router manufacturer and firmware version.

## Credentials Configuration

The server requires a `credentials.json` file containing the credentials and configuration required by the application.

The file must be created at the following path:
```text
tfgpoker/server/src/main/resources/credentials.json
```

The `credentials.json` file must be configured according to the structure and requirements described in the project's Javadoc.

For detailed information about the required fields, format, and configuration, please refer to the **[Server Javadoc documentation](./tfgpoker/server/documentation/javadoc/apidocs)** and the **[Client Javadoc documentation](./tfgpoker/client/documentation/javadoc/apidocs)**.

> [!IMPORTANT]
> The `credentials.json` file may contain sensitive information. Do not commit personal credentials, passwords, API keys, or other secrets to a public repository.

## Ollama model installation

The project uses **Ollama** to run the required artificial intelligence models locally.

First, download and install the **Ollama client** from the official website:

[Download Ollama](https://ollama.com/download?utm_source=chatgpt.com)

Once Ollama is installed, open PowerShell from the project's root directory and run the provided setup script:

If you want a lighter version (4.5GB):
```powershell
.\setup-llama.ps1
```

If you want a more powerful version (8GB):
```powershell
.\setup-llama_8gb.ps1
```

Both scripts are located at:
```text
tfgpoker/setup-llama.ps1
tfgpoker/setup-llama_8gb.ps1
```

This script automatically downloads and configures the AI models required by the application.


## Run the server

From the project root directory:
```bash
cd ./tfgpoker
```

On Linux:
```bash
./mvnw -pl server -Prun exec:java
```

On Windows:
```cmd
mvnw.cmd -pl server -Prun exec:java
```

The server should start and listen for incoming client connections on TCP port `5005`.

# Client application

The client application uses JavaFX and can be started independently from the server. There is no need to install JavaFX to launch this application from and IDE or terminal.

## Run the client

From the project root directory:
Windows:
```cmd
mvnw.cmd -pl client javafx:run
```

Linux:
```bash
./mvnw -pl client javafx:run
```

The JavaFX client window should appear.

After clicking the start button, the client can be configured with the server's IP address.

If the server is running on another machine, enter the server's reachable IP address.

If both the server and client applications are running on the same machine, leave the IP address field empty and enter only a name to connect to the server.

# Network Configuration

When the server and client run on different machines, network connectivity must be configured correctly.

The basic configuration is:
```text
Client
  │
  │ TCP
  │
  ▼
Server IP address : 5005
```

## Local network

If the client and server are on the same local network, the client can normally connect using the loopback address.

## Public Internet

If the server is behind a router or NAT and clients need to connect from outside the local network, the router must forward incoming TCP traffic from port `5005` to the server's local IP address.

The general configuration is:
```text
Internet
  │
  │ TCP 5005
  ▼
Router / NAT
  │
  │ TCP 5005
  ▼
Server
192.168.x.x:5005
```
The exact port forwarding procedure depends on the router.

# Connectivity Test

Before troubleshooting the application itself, verify that the client machine can reach the server.

## Windows

From PowerShell:
```powershell
Test-NetConnection <SERVER_IP> -Port 5005
```

For example:
```powershell
Test-NetConnection 192.168.1.100 -Port 5005
```

A successful test should report:
```text
TcpTestSucceeded : True
```

## Linux

If `netcat` is installed:
```bash
nc -vz <SERVER_IP> 5005
```

For example:
```bash
nc -vz 192.168.1.100 5005
```

If the connection succeeds, the network path to the server is available.
If the connection fails, check:

* Server status.
* Server listening address.
* Server firewall.
* Router/NAT configuration.
* Cloud/VPS firewall rules.
* IP address used by the client.
* Network connectivity between the machines.


# Troubleshooting

## Java is not recognized

If an error indicates that `java` or `javac` is not recognized as a command, check that:

* Java SDK 21 is installed.
* `JAVA_HOME` points to the correct JDK installation.
* The JDK `bin` directory is included in `PATH`.
* A new terminal was opened after modifying the environment variables.

Check the installation with:
```bash
java -version
```

and:
```bash
javac -version
```

## Maven uses the wrong Java version

Run:

Windows:
```cmd
mvnw.cmd -version
```

Linux:
```bash
./mvnw -version
```

Check the Java version shown in the Maven output.

If it is not Java 21, verify:

* `JAVA_HOME`.
* The system `PATH`.
* The Java runtime selected by VS Code.
* Any other installed Java versions.

## Maven Wrapper does not run on Linux

If the wrapper does not have execution permissions, run:
```bash
chmod +x mvnw
```

Then:
```bash
./mvnw -version
```

## Maven cannot download dependencies

Make sure that:

* The computer has an active Internet connection.
* Maven can access the required repositories.
* No proxy or firewall is blocking Maven.
* The Maven Wrapper can execute correctly.

Run the build again:
```bash
./mvnw clean install -DskipTests
```

On Windows:
```cmd
mvnw.cmd clean install -DskipTests
```

## JavaFX errors

If an error related to JavaFX occurs:

* Make sure that **Java 21** is being used.
* Check that the JavaFX dependencies are correctly defined in `pom.xml`.
* Run the project using the Maven commands provided by the project.
* Avoid mixing a manual JavaFX installation with the JavaFX dependencies managed by Maven.

## Client cannot connect to server

Check the following:

1. The server application is running.
2. The server is listening on TCP port `5005`.
3. The server is listening on `0.0.0.0:5005` when remote connections are required.
4. Port `5005/TCP` is allowed by the server firewall.
5. If applicable, the router forwards port `5005` to the server.
6. If applicable, the cloud/VPS firewall allows port `5005`.
7. The client is using the correct server IP address.
8. The client machine can reach the server.

Test the connection with:

Windows:
```powershell
Test-NetConnection <SERVER_IP> -Port 5005
```

Linux:
```bash
nc -vz <SERVER_IP> 5005
```

## Server works locally but not remotely

If the client can connect when both applications run on the same machine but cannot connect remotely, verify:

* The server is not bound only to `127.0.0.1`.
* The operating system firewall allows TCP port `5005`.
* The router/NAT forwards TCP port `5005` if necessary.
* The client is using the correct public or private IP address for the network configuration.
* Any cloud/VPS firewall allows the connection.

## VS Code is using the wrong Java version

Open the Command Palette:
```text
Ctrl + Shift + P
```

Select:
```text
Java: Configure Java Runtime
```

Make sure that **JDK 21** is selected for the project.

# Installation Summary

| Component                 | Version / Configuration                 |
| ------------------------- | --------------------------------------- |
| Java SDK                  | 21                                      |
| JavaFX                    | 21                                      |
| Maven                     | Maven Wrapper included with the project |
| Global Maven installation | Optional                                |
| IDE                       | Visual Studio Code                      |
| Client                    | JavaFX application                      |
| Server                    | Java application                        |
| Communication             | TCP                                     |
| Server port               | 5005                                    |
| Operating Systems         | Windows / Linux                         |

# Quick Start

For a quick installation and execution:

## 1. Install Java 21

Verify:
```bash
java -version
javac -version
```

Both should report Java 21.

## 2. Open the project

Open the project root directory in VS Code.

## 3. Build the project

Windows:
```cmd
mvnw.cmd clean install -DskipTests
```

Linux:
```bash
chmod +x mvnw
./mvnw clean install -DskipTests
```

## 4. Start the server

Windows:
```cmd
mvnw.cmd -pl server -Prun exec:java
```

Linux:
```bash
./mvnw -pl server -Prun exec:java
```

## 5. Start the client

Windows:
```cmd
mvnw.cmd -pl client javafx:run
```

Linux:
```bash
./mvnw -pl client javafx:run
```

## 6. Connect to the server

If the server is running on the same machine, leave the IP address field from client application empty.
If the server is running on another machine, enter its reachable IP address and make sure TCP port `5005` is accessible.

The client can then connect to the server and participate in the poker application.