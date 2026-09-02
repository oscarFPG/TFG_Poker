# TFG_Poker
Poker application developed in Java as part of a Bachelor's Thesis.
This application allows humans and bots to play No-Limit Texas Hold'em (NLHE) poker games.


# Table of Contents
* [Project Requirements](#project-requirements)
* [Installation Guide](#installation-guide)
  * [Windows](#windows)
  * [Linux](#linux)
* [VS Code Configuration](#vs-code-configuration)
* [Running the Project](#running-the-project)
* [Troubleshooting](#troubleshooting)


# Project Requirements
The following components are required to build and run the project:

* **Java SDK 21**
* **JavaFX 21**
* **Maven (Optional)**
* **Visual Studio Code**

The project includes several `.cmd` files that act as **Maven wrappers**, so a separate global Maven installation is not required on Windows.

To check the installed Java version:
```bash
java -version
```

To check the Java compiler version:
```bash
javac -version
```

Both commands should report **Java 21**.

> [!NOTE] Although the project uses Maven, Maven commands are executed through the wrappers included in the project. This avoids depending on a globally installed Maven version.


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

The necessary JavaFX dependencies are defined in the project's Maven configuration and will be downloaded automatically when Maven builds the project.

---

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

Add the following lines:
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
Maven will automatically download the required JavaFX dependencies when the project is built.

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

The installed JDKs should be displayed. Make sure that **JDK 21** is selected for the project.

If multiple Java versions are installed, make sure that Java 21 is configured as the JDK used by the project.

# Running the Project

The project uses **Maven** to manage dependencies, compile the source code, and run the application.

Several `.cmd` files are included in the project to simplify the execution of Maven commands without installing Maven, but it can be compiled and executed using your system Maven installation.

## Windows

Open the integrated terminal in VS Code and make sure you are in the project's root directory.
The Maven wrapper files included in the project can then be used to execute the required Maven commands.

For example:
```cmd
mvnw.cmd <command>
```

Or using the system installation:
```cmd
mvn <command>
```

## Linux

On Linux, Maven can be executed using the Maven wrapper included in the project.

If the wrapper does not have execution permissions, run:
```bash
chmod +x mvnw
```

The Maven wrapper can then be executed with:
```bash
./mvnw <command>
```

Or:
```bash
mvn <command>
```


## Running from VS Code

The project can be run from VS Code using either of the following methods:
1. **Maven for Java extension** — use the Maven panel to access the project's available Maven goals.
2. **Integrated terminal** — execute the Maven wrapper or the provided `.cmd` scripts.

It is recommended to use the commands provided specifically by the project to ensure that the application is built and executed using the expected configuration.

> [!NOTE] This guide assumes that Maven is installed and configured as a system-wide command. All Maven commands can also be executed using the Maven Wrapper `.cmd` files included with the project.


First, we are going to install the project's dependencies and compile the entire project by running the following commands in the **/tfgpoker** directory:
1. `mvn clean install -DskipTests`
2. `mvn clean compile`

The first command cleans the project, installs all required dependencies **without running the project tests**, and builds the project while skipping the tests. The second command performs a clean compilation of the project.


### Server application

> [!IMPORTANT] We highly recommend running the server from a Linux machine

#### Open port 5005

To allow external clients to communicate with the server application, the port used by the application must be open in the Linux firewall.

The following example opens port `5005/TCP`.

#### UFW (Ubuntu/Debian)
```bash
sudo ufw allow in 5005/tcp
sudo ufw allow out 5005/tcp
sudo ufw status
```

#### firewalld (Fedora/RHEL/CentOS)
```bash
sudo firewall-cmd --permanent --add-port=5005/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports
```

#### Verify the port
```bash
sudo ss -lntp | grep :5005
```

The application should listen on `0.0.0.0:5005` to accept external connections. If it listens on `127.0.0.1:5005`, it will only be accessible from the local machine.


#### Run application
```bash
cd .\tfgpoker\
mvn -pl server -Prun exec:java
```

This should be the output in our terminal:
![Server run output](/images/server-run.png)


### Client application

#### Run application:
```bash
cd .\tfgpoker\
mvn -pl client javafx:run
```

We should see this window:
![Client main window](/images/client-main-window.png)

After clicking the start button and we can provide the server public IP address:
![Client login window](/images/client-login-window.png)

> [!NOTE] If both the server and client applications are running on the same machine, leave the IP address field empty and enter only a name to connect to the server.

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

## Maven cannot download dependencies

Make sure that the computer has an active Internet connection and that Maven can access the required repositories.

The build can also be executed again so Maven can check for and download any missing dependencies.

## JavaFX errors

If an error related to JavaFX occurs:

* Make sure that **Java 21** is being used.
* Check that the JavaFX dependencies are correctly defined in `pom.xml`.
* Run the project using the Maven commands provided by the project.
* Avoid mixing a manual JavaFX installation with the JavaFX dependencies managed by Maven.

# Installation Summary

| Component         | Version                |
| ----------------- | ---------------------- |
| Java SDK          | 21                     |
| JavaFX            | 21                     |
| Maven             | Managed by the project |
| IDE (preference)  | Visual Studio Code     |
| Operating Systems | Windows / Linux        |