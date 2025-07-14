# WORKS ONLY ON LINUX RN!
## Needs java 24+ to work

# An application that allows you to download and install any version of Forge.
<img width="442" height="393" alt="изображение" src="https://github.com/user-attachments/assets/0bccce24-d689-45ca-b313-1876bb2e7bd2" />


# How to run it?
First, you need to download .jar file from [latest available version of UFI](https://github.com/prostoblodi/universal_forge_installer/releases/latest)

After that, just double click on it.

## Running in the console
Instead of double-clicking, you should write to the console:

`cd /ufi/download/directory/`, where /ufi/download/directory is the directory where the downloaded .jar is located

`java -jar Universal-Forge-Installer-1.1.jar`

# How do I change the appearance of the application?
At the moment, almost all appearance settings are in the [styles.css file](https://github.com/prostoblodi/universal_forge_installer/blob/main/src/main/resources/styles.css), and you have to change them manually. In the future, this will be built into the application itself.

# How does it work?
This application gets version information from the [official Minecraft Forge website](https://files.minecraftforge.net/net/minecraftforge/forge/), then downloads it from the [maven repository Forge](https://maven.minecraftforge.net/net/minecraftforge/forge/)



