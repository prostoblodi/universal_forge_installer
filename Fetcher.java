import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Fetcher {

    // Method to get Forge versions for a specific Minecraft version
    public static List<String> getForgeVersionsForMinecraft() throws IOException {
        List<String> versions = new ArrayList<>();
        Document document = Jsoup.connect("https://files.minecraftforge.net/net/minecraftforge/forge/").get();

        Elements links = document.select("a[href^=index_]");
        versions.add(document.select(".elem-active").text());

        for (Element link : links) {
            versions.add(link.text().trim());
        }
        return versions;
    }
}
