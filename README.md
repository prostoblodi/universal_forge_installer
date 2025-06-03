# An application that allows you to download and install any version of Forge.
![изображение](https://github.com/user-attachments/assets/1037d3ee-0ce3-46d5-a79f-1a92aec38ff9)


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



