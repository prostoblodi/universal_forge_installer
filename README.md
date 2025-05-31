# An application that will allow you to download and install any version of Minecraft Forge. Currently, you can do this with versions of Forge that use a version of Minecraft higher than 1.5.1.
![изображение](https://github.com/user-attachments/assets/84531044-8185-48dd-902b-1c4ced9ad554)

# How to run it?
First, you need to download .jar file from [latest available version of UFI](https://github.com/prostoblodi/universal_forge_installer/releases/latest)

After that, just double click on it.

## Running in the console
Instead of double-clicking, you should write to the console:

`cd /ufi/download/directory/`, where /ufi/download/directory is the directory where the downloaded .jar is located

`java -jar Universal-Forge-Installer-1.0.jar`

# How do I change the appearance of the application?
At the moment, almost all appearance settings are in the [styles.css file](https://github.com/prostoblodi/universal_forge_installer/blob/main/src/main/resources/styles.css), and you have to change them manually. In the future, this will be built into the application itself.

# How does it work?
This application gets version information from the [official Minecraft Forge website](https://files.minecraftforge.net/net/minecraftforge/forge/), then downloads it from the [maven repository Forge](https://maven.minecraftforge.net/net/minecraftforge/forge/)



