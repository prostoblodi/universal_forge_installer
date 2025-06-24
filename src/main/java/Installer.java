import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

abstract class Installer implements Runnable {

    private static String fileName;
    private static Path filePath;

    // Return all versions of Minecraft for which Forge is available
    protected static List<String> getMinecraftVersionsForForge() throws IOException {
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
    protected static List<String> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
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

    // Download Forge
    protected static void download_forge(String minecraftVersion, Pair<String, Byte> forgeVersionPair) throws IOException, URISyntaxException {
        Path forgeJarsDir = Paths.get(System.getProperty("user.home"), "UFI", "ForgeJars", String.valueOf(minecraftVersion));

        String forgeVersion = forgeVersionPair.getKey();

        Document document = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
        Elements downloadLinks;

        byte howOldIndex = howOldIndex(minecraftVersion);

        if (!forgeJarsDir.toFile().exists()) {
            Files.createDirectory(forgeJarsDir);
            System.out.println("The ForgeJars folder is successfully created.");
        }

        if (howOldIndex == 2) {
            fileName = String.format("Forge_%s_%s.jar", forgeVersion, minecraftVersion);
            downloadLinks = document.select("a:contains(Installer)");
        } else {
            if (howOldIndex == 1){downloadLinks = document.select("a:contains(Universal)");}
            else {downloadLinks = document.select("a:contains(Client)");}
            fileName = String.format("Forge_%s_%s.zip", forgeVersion, minecraftVersion);
        }

        filePath = forgeJarsDir.resolve(fileName).toAbsolutePath();

        Element hasDownloadLink = downloadLinks.get(forgeVersionPair.getValue());
        URL url = new URI(hasDownloadLink.attr("href").split("&url=")[1]).toURL();

        System.out.printf("Download file %s to %s... %n", fileName, filePath);

        long startTime = System.currentTimeMillis();

        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            UFI.updateStatusLabel((byte) 5);
            throw new IOException("Error downloading Forge from URL: " + url, e);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.printf("%s is successfully downloaded to: %s%n", fileName, filePath);
        System.out.printf("Download took %d milliseconds (%.2f seconds)%n", duration, duration / 1000.0);
    }

    // Run Forge
    protected static void run_forge() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        // some comment that prevents from "It's possible to extract method returning 'command' from a long surrounding method"
        String[] command;

        final String reducedPath = filePath.getParent().toString();
        final String modifiedFileName = String.format("'%s'", fileName);

        String args = "";
        if(UFI.customForgeLaunch){args = String.format(" --installClient %s", UFI.minecraftFolder);}

        if (isWindows) {
            command = new String[]{
                    "cmd", "/c",
                    "cd /d " + reducedPath + " && java -jar " + modifiedFileName + args
            };
        } else {
            command = new String[]{
                    "bash", "-c",
                    "cd " + reducedPath + " && java -jar " + modifiedFileName + args
            };
        }

        try {
            if (UFI.customForgeLaunch){
                new ProcessBuilder(command)
                        .inheritIO()
                        .start()
                        .waitFor();
                System.out.println("Forge installed!");
            } else {
                new ProcessBuilder(command)
                        .inheritIO()
                        .start();
                System.out.println("Forge file launched!");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines the "age index" of a Minecraft version based on the provided version string.
     *
     * @param minecraftVersion the version string in the format "x.y.z" (e.g., "1.5.2").
     * @return a byte representing the "age index" of the version:
     *         <ul>
     *           <li><code>2</code> for versions newer than or equal to 1.5.2</li>
     *           <li><code>1</code> for versions 1.4.x, 1.5.x, or 1.3.2</li>
     *           <li><code>0</code> for older versions</li>
     *         </ul>
     */
    protected static byte howOldIndex(String minecraftVersion){
        String[] versionStringParsed = minecraftVersion.split("\\.");
        int[] versionParsed = new int[versionStringParsed.length];

        for (int i = 0; i < versionStringParsed.length; i++) {
            versionParsed[i] = Integer.parseInt(versionStringParsed[i]);
        }

        if (versionParsed[1] > 5 || (versionParsed[1] == 5 && versionParsed[2] == 2)) {
            return (byte) 2;
        } else if(versionParsed[1] == 4 || (versionParsed[1] == 5) || (versionParsed[1] == 3 && versionParsed[2] == 2)){
            return (byte) 1;
        } else {
            return (byte) 0;
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
