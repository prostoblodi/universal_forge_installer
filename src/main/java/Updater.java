import javafx.util.Pair;

import java.io.IOException;
import java.time.Instant;

class Updater {

    protected static Instant lastRun = Instant.EPOCH;
    private static long updatePeriodMillis = 60 * 60 * 1000;

    protected static void checkUpdates() {
        Instant now = Instant.now();
        long elapsed = now.toEpochMilli() - lastRun.toEpochMilli();
        if (elapsed >= updatePeriodMillis) {
            try {
                UFI.showMinecraftVersions(true);
            } catch (IOException e){
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }

            lastRun = now;
        }
    }

    protected static void setTiming(byte timingCode) {
        switch (timingCode) {
            case 0:  updatePeriodMillis = Long.MAX_VALUE; break;
            case 1:  updatePeriodMillis = 24L * 60 * 60 * 1000; break;
            case 2:  updatePeriodMillis = 7L * 24 * 60 * 60 * 1000; break;
            case 3:  updatePeriodMillis = 30L * 24 * 60 * 60 * 1000; break;
            case 4:  updatePeriodMillis = 365L * 24 * 60 * 60 * 1000; break;
            case 5:  updatePeriodMillis = 10L * 365 * 24 * 60 * 60 * 1000; break;
            case 6:  updatePeriodMillis = 100L * 365 * 24 * 60 * 60 * 1000; break;
            case 7:  updatePeriodMillis = 1000L * 365 * 24 * 60 * 60 * 1000; break;
        }
    }


    protected static void setCustomTiming(Pair<Short, Byte> customTiming) {
        short amount = customTiming.getKey();
        byte unitCode = customTiming.getValue();

        long millisPerUnit = switch (unitCode) {
            case 0 -> 60L * 60 * 1000;
            case 1 -> 24L * 60 * 60 * 1000;
            case 2 -> 7L * 24 * 60 * 60 * 1000;
            case 3 -> 365L * 24 * 60 * 60 * 1000;
            case 4 -> 10L * 365 * 24 * 60 * 60 * 1000;
            case 5 -> 100L * 365 * 24 * 60 * 60 * 1000;
            case 6 -> 1000L * 365 * 24 * 60 * 60 * 1000;
            default -> 60 * 60 * 1000;
        };

        updatePeriodMillis = (long) amount * millisPerUnit;
    }
}
