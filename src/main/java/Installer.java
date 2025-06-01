import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Installer {

    // Return all versions of Minecraft for which Forge is available
    public static List<String> getMinecraftVersionsForForge() throws IOException {
        List<String> versions = new ArrayList<>();
        Document document = Jsoup.connect("https://files.minecraftforge.net/net/minecraftforge/forge/").get();

        Elements as = document.select("a[href^=index_]");
        versions.add(document.select(".elem-active").text().trim());

        for (Element a : as) {
            versions.add(a.text().trim());
        }

        System.out.println("Versions of Minecraft successfully received: " + versions);
        return versions;
    }

    // Return all versions of Forge available for the entered Minecraft version
    public static List<String> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
        List<String> versions = new ArrayList<>();
        if (Objects.equals(minecraftVersion, "")) {
            return versions;
        }

        Document document = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
        Elements tds = document.select(".download-version");

        for (Element td : tds) {
            versions.add(td.text().trim());
        }

        System.out.println("Versions of Forge successfully received: " + versions);
        return versions;
    }

    // Download and run Forge
    public static void install_forge(String minecraftVersion, String forgeVersion) throws IOException, URISyntaxException {
        String userDir = System.getProperty("user.dir");
        Path forgeJarsDir = Paths.get(userDir, "ForgeJars", String.valueOf(minecraftVersion));

        if (!forgeJarsDir.toFile().exists()) {
            Files.createDirectory(forgeJarsDir);
            System.out.println("The ForgeJars folder is successfully created.");
        }

        String[] versionParsed = minecraftVersion.split("\\.");
        boolean isNewIndex = versionParsed.length > 2 &&
                (Integer.parseInt(versionParsed[1]) > 5 ||
                        (Integer.parseInt(versionParsed[1]) == 5 && Integer.parseInt(versionParsed[2]) == 2));

        if (isNewIndex) {
            String fileName = String.format("Forge_%s_%s.jar", forgeVersion, minecraftVersion);
            Path filePath = forgeJarsDir.resolve(fileName).toAbsolutePath();

            if (!filePath.toFile().exists()) {
                URL url = new URI(String.format(
                        "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-installer.jar",
                        minecraftVersion, forgeVersion, minecraftVersion, forgeVersion
                )).toURL();

                System.out.printf("Download file %s to %s... %n", fileName, filePath);

                try (InputStream inputStream = url.openStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.printf("%s is successfully downloaded to: %s%n", fileName, filePath);
                } catch (IOException e) {
                    throw new IOException("Error downloading Forge from URL: " + url, e);
                }
            }
            run_forge(filePath, fileName);
        } else {
            String fileName = String.format("Forge_%s_%s.zip", forgeVersion, minecraftVersion);
            Path filePath = forgeJarsDir.resolve(fileName).toAbsolutePath();

            if (!filePath.toFile().exists()) {
                URL url = new URI(String.format(
                        "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-universal.zip",
                        minecraftVersion, forgeVersion, minecraftVersion, forgeVersion)).toURL();

                System.out.printf("Download file %s to %s....", fileName, filePath);
                try (InputStream inputStream = url.openStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.printf("%s is successfully downloaded to: %s%n", fileName, filePath);
                } catch (IOException e) {
                    throw new IOException("Error downloading Forge from URL: " + url, e);
                }
            }
        }
    }

    private static void run_forge(Path filePath, String fileName) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        //noinspection MethodCanBeExtracted
        String[] command;

        final String reducedPath = filePath.getParent().toString();
        final String modifiedFileName = String.format("'%s'", fileName);
        if (isWindows) {
            command = new String[]{
                    "cmd", "/c",
                    "cd /d " + reducedPath + " && java -jar " + modifiedFileName
            };
        } else {
            command = new String[]{
                    "bash", "-c",
                    "cd " + reducedPath + " && java -jar " + modifiedFileName
            };
        }

        try {
            new ProcessBuilder(command)
                    .inheritIO()
                    .start();
            System.out.println("Forge file launched!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Main method for testing purposes
//    public static void main(String[] args) throws IOException {
// //        System.out.println(getForgeVersionsForMinecraft("1.21.1"));
// //        System.out.println(getMinecraftVersionsForForge());
// //        download_forge("1.17.1", "37.1.1");
// //           try {
// //               Runtime.getRuntime().exec("usr/lib/jvm/java-24-openjdk/bin/java --version");
// //           } catch (Exception e) {
// //               System.out.println(e);
// //           }
//    }
}
