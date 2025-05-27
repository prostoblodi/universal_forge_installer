import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Fetcher {
    public static List<String> getMinecraftVersionsForForge() throws IOException {
        List<String> versions = new ArrayList<>();
        Document document = Jsoup.connect("https://files.minecraftforge.net/net/minecraftforge/forge/").get();

        Elements as = document.select("a[href^=index_]");
        versions.add(document.select(".elem-active").text().trim());

        for (Element a : as) {
            versions.add(a.text().trim());
        }
        return versions;
    }

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

        return versions;
    }

//    public static void main(String[] args) throws IOException {
//        System.out.println(getForgeVersionsForMinecraft("1.21.1"));
//        System.out.println(getMinecraftVersionsForForge());
//    }
}
