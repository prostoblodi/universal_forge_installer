import java.io.*;
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

    // method, that returns all versions of minecraft, for which forge is available
    public static List<String> getMinecraftVersionsForForge() throws IOException {
        List<String> versions = new ArrayList<>();
        Document document = Jsoup.connect("https://files.minecraftforge.net/net/minecraftforge/forge/").get();

        Elements as = document.select("a[href^=index_]");
        versions.add(document.select(".elem-active").text().trim());

        for (Element a : as) {
            versions.add(a.text().trim());
        }

        System.out.println("Versions of minecraft successfully received: " + versions);
        return versions;
    }

    // method, that returns all versions of forge, available for the entered version of minecraft
    public static List<String> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
        List<String> versions = new ArrayList<>();
        if (Objects.equals(minecraftVersion, "")) {
            return versions;
        }

        Document document = Jsoup.connect(String.format("https://files.minecraftforge.net/net/minecraftforge/forge/index_%s.html", minecraftVersion)).get();
        Elements tds = document.select(".download-version");

        for (Element td : tds){
            versions.add(td.text().trim());
        }

        System.out.println("Versions of forge successfully received: " + versions);
        return versions;
    }

    public static void install_forge(String minecraftVersion, String forgeVersion) throws IOException {
        String userDir = System.getProperty("user.dir");
        Path forgeJarsDir = Paths.get(userDir, "ForgeJars");

        if (!forgeJarsDir.toFile().exists()) {
            Files.createDirectory(forgeJarsDir);
            System.out.println("The ForgeJars folder has been successfully created.");
        }

        String fileName = String.format("Forge_%s(%s).jar", forgeVersion, minecraftVersion);
        Path filePath = forgeJarsDir.resolve(fileName);

        if (!filePath.toAbsolutePath().toFile().exists()) {
            URL url = new URL(String.format(
                    "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-installer.jar",
                    minecraftVersion, forgeVersion, minecraftVersion, forgeVersion
            ));

            System.out.println("File Download...");

            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Forge has been successfully downloaded to: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                throw new IOException("Error downloading Forge from URL: " + url, e);
            }
        }

        try {
            Runtime.getRuntime().exec(String.format("java -jar %s", filePath.toAbsolutePath()));
            System.out.printf("Forge file at %s launched!%n", filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) throws IOException {
// //        System.out.println(getForgeVersionsForMinecraft("1.21.1"));
// //        System.out.println(getMinecraftVersionsForForge());
// //        download_forge("1.17.1","37.1.1");
// //           try {
// //               Runtime.getRuntime().exec("usr/lib/jvm/java-24-openjdk/bin/java --version");
// //           } catch (Exception e){
// //               System.out.println(e);
// //           }
//    }
}
