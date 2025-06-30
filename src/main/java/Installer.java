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

        System.out.printf("%n***%nVersions of Minecraft successfully received: " + versions + "%n***%n%n");
        return versions;
    }

    // Return all versions of Forge available for the entered Minecraft version
    protected static List<String> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
        List<String> versions = new ArrayList<>();
        List<String> specificalVersions = new ArrayList<>();

        if (Objects.equals(minecraftVersion, "")) {
            return versions;
        }

        if (forgePageDocument == null){
            forgePageDocument = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
        }

        Elements tds = forgePageDocument.select(".download-version");

        for (Element td : tds) {
            versions.add(td.text().trim());
        }

        if (Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion) == null) {
            specificalVersions.add(tds.select("td:has(i.promo-latest)").text().trim());
            specificalVersions.add(!tds.select("td:has(i.promo-recommended)").text().isEmpty() ? tds.select("td:has(i.promo-recommended)").text() : tds.select("td:has(i.promo-latest)").text().trim());
            specificalVersions.add(versions.getLast().trim());
            Universal.minecraftToSpecifiedForgeVersions.put(minecraftVersion, specificalVersions);
        } else {
            specificalVersions.add(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getFirst());
            specificalVersions.add(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).get(1));
            specificalVersions.add(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getLast());
        }

        System.out.printf("%n***%nVersions of Forge successfully received: " + versions + "%n***%n%n");
        return versions;
    }

    // Download Forge
    protected static void download_forge(String minecraftVersion, Pair<String, Byte> forgeVersionPair) throws IOException, URISyntaxException {
        Path forgeJarsDir = Paths.get(System.getProperty("user.home"), "UFI", "ForgeJars", String.valueOf(minecraftVersion));

        String forgeVersion = forgeVersionPair.getKey();

        Elements downloadLinks;

        byte howOldIndex = Universal.howOldIndex(minecraftVersion);

        if (!forgeJarsDir.toFile().exists()) {
            Files.createDirectory(forgeJarsDir);
            System.out.println("The ForgeJars folder is successfully created.");
        }

        if (forgePageDocument == null){
            forgePageDocument = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
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
        if (!filePath.toFile().exists()) {
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
    }


    // Run Forge
    protected static void run_forge() {
        String[] command;
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        final String reducedPath = filePath.getParent().toString();
        final String modifiedFileName = String.format("'%s'", fileName);

        String args = "";
        if(Universal.customForgeLaunch){args = String.format(" --installClient %s", Universal.minecraftFolder);}

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
            if (Universal.customForgeLaunch){
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
}
