import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class Universal {
    protected static final HashMap<String, List<Pair<String, Byte>>> minecraftToForgeVersions = new HashMap<>();
    protected static final HashMap<String, List<String>> minecraftToSpecifiedForgeVersions = new HashMap<>();

    protected static byte defaultMinecraftVersion;
    protected static byte defaultForgeVersion;
    protected static boolean enableForgeCaching;
    protected static boolean enableForgeFileCaching;
    protected static boolean customForgeLaunch;
    protected static String minecraftFolder;
    protected static String lastUsedMinecraftVersion;

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
