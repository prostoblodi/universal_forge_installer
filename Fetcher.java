import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Fetcher {

    private static final String MC_VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final String FORGE_MAVEN_BASE = "https://maven.minecraftforge.net/net/minecraftforge/forge/";

    // Method to get all Minecraft versions starting from 1.1
    public static List<String> getMinecraftVersions() throws IOException {
        List<String> versions = new ArrayList<>();
        URL url = new URL(MC_VERSION_MANIFEST);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray versionsArray = json.getAsJsonArray("versions");

            for (JsonElement element : versionsArray) {
                JsonObject versionObject = element.getAsJsonObject();
                String id = versionObject.get("id").getAsString();
                if (id.compareTo("1.1") >= 0) {
                    versions.add(id);
                }
            }
        }

        return versions;
    }

    // Method to get all Forge versions
    public static List<String> getForgeVersions() throws IOException {
        Document document = Jsoup.connect("https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.5.html").get();
        Elements elements = document.select(".download-version"); // Замените "my-class" на нужное имя класса

        // Выводим содержимое каждого элемента
        for (Element element : elements) {
            System.out.println(element.text());
        }
        return List.of();
    }

    // Method to get Forge versions for a specific Minecraft version
    public static List<String> getForgeVersionsForMinecraft(String minecraftVersion) throws IOException {
        List<String> allForgeVersions = getForgeVersions();
        List<String> compatibleVersions = new ArrayList<>();

        for (String forgeVersion : allForgeVersions) {
            if (forgeVersion.startsWith(minecraftVersion + "-")) {
                compatibleVersions.add(forgeVersion);
            }
        }

        return compatibleVersions;
    }

    // Method to download a specific Forge version
    public static void downloadForge(String forgeVersion, String outputFilePath) throws IOException {
        String baseUrl = "https://maven.minecraftforge.net/net/minecraftforge/forge/";
        String downloadUrl = baseUrl + forgeVersion + "/forge-" + forgeVersion + "-installer.jar";

        // Проверяем доступность URL
        HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            throw new FileNotFoundException("Forge version not found at: " + downloadUrl);
        }

        // Скачивание файла
        System.out.println("Downloading: " + downloadUrl);
        try (InputStream inputStream = new URL(downloadUrl).openStream();
             FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("Forge version downloaded to: " + outputFilePath);
    }
}
