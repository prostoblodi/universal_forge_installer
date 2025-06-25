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

    private static Document forgePageDocument;

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
    protected static List<List<String>> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
        List<List<String>> output = new ArrayList<>();
        List<String> versions = new ArrayList<>();
        List<String> specificalVersions = new ArrayList<>();

        if (Objects.equals(minecraftVersion, "")) {
            return output;
        }

        Document document = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
        forgePageDocument = document;

        Elements tds = document.select(".download-version");

        specificalVersions.add(tds.select("td:has(i.promo-latest)").text());
        specificalVersions.add(!tds.select("td:has(i.promo-recommended)").text().isEmpty() ? tds.select("td:has(i.promo-recommended)").text() : tds.select("td:has(i.promo-latest)").text());

        for (Element td : tds) {
            versions.add(td.text().trim());
        }

        specificalVersions.add(versions.getLast());

        output.add(versions);
        output.add(specificalVersions);

        System.out.println("Versions of Forge successfully received: " + output);
        return output;
    }

    // Download Forge
    protected static void download_forge(String minecraftVersion, Pair<String, Byte> forgeVersionPair) throws IOException, URISyntaxException {
        Path forgeJarsDir = Paths.get(System.getProperty("user.home"), "UFI", "ForgeJars", String.valueOf(minecraftVersion));

        String forgeVersion = forgeVersionPair.getKey();

        Elements downloadLinks;

        byte howOldIndex = howOldIndex(minecraftVersion);

        if (!forgeJarsDir.toFile().exists()) {
            Files.createDirectory(forgeJarsDir);
            System.out.println("The ForgeJars folder is successfully created.");
        }

        if (howOldIndex == 2) {
            fileName = String.format("Forge_%s_%s.jar", forgeVersion, minecraftVersion);
            downloadLinks = forgePageDocument.select("a:contains(Installer)");
        } else {
            if (howOldIndex == 1){downloadLinks = forgePageDocument.select("a:contains(Universal)");}
            else {downloadLinks = forgePageDocument.select("a:contains(Client)");}
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
        String[] command;
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

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
    protected static byte howOldIndex(String minecraftVersion) {
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
}
