package me.ian.arena;

import me.ian.PVPHelper;
import org.bukkit.Bukkit;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;

public class ArenaCleaner {

    private static final long DEFAULT_FREQUENCY_HOURS = 48; // 2 days default
    private static final long DEFAULT_INITIAL_DELAY_MINUTES = 1; // 1 minute default
    private static final long HOUR_IN_MS = 60 * 60 * 1000; // 1 hour in milliseconds
    private static final long MINUTE_IN_MS = 60 * 1000; // 1 minute in milliseconds

    /**
     * Gets the configured frequency for arena clearing in milliseconds
     * @return frequency in milliseconds
     */
    private static long getClearFrequency() {
        try {
            // Get frequency in hours from config, default to 48 hours if not found
            long frequencyHours = PVPHelper.INSTANCE.getConfig()
                    .getLong("arena.clear_frequency", DEFAULT_FREQUENCY_HOURS);

            // Convert to milliseconds
            return frequencyHours * HOUR_IN_MS;
        } catch (Exception e) {
            PVPHelper.INSTANCE.getLogger().log(Level.WARNING,
                    "Failed to read arena clear frequency from config. Using default: " + DEFAULT_FREQUENCY_HOURS + " hours");
            return DEFAULT_FREQUENCY_HOURS * HOUR_IN_MS;
        }
    }

    /**
     * Gets the configured initial delay in milliseconds
     * @return initial delay in milliseconds
     */
    private static long getInitialDelay() {
        try {
            // Get initial delay in minutes from config, default to 1 minute if not found
            long delayMinutes = PVPHelper.INSTANCE.getConfig()
                    .getLong("arena.initial_delay", DEFAULT_INITIAL_DELAY_MINUTES);

            // Convert to milliseconds
            return delayMinutes * MINUTE_IN_MS;
        } catch (Exception e) {
            PVPHelper.INSTANCE.getLogger().log(Level.WARNING,
                    "Failed to read initial delay from config. Using default: " + DEFAULT_INITIAL_DELAY_MINUTES + " minutes");
            return DEFAULT_INITIAL_DELAY_MINUTES * MINUTE_IN_MS;
        }
    }

    /**
     * Initializes the arena clearing scheduler
     */
    public static void initializeScheduler() {
        long frequencyTicks = getClearFrequency() / 50; // Convert ms to ticks (20 ticks per second)
        long initialDelayTicks = getInitialDelay() / 50; // Convert ms to ticks

        // Schedule the repeating task
        Bukkit.getScheduler().runTaskTimer(PVPHelper.INSTANCE, () -> {
            // Get current time in Arizona timezone
            ZonedDateTime arizonaTime = ZonedDateTime.now(ZoneId.of("America/Phoenix"));

            // Log the start of arena clearing
            PVPHelper.INSTANCE.getLogger().log(Level.INFO,
                    String.format("Starting scheduled arena clear at %s (Frequency: %.1f hours)",
                            arizonaTime, getClearFrequency() / (double)HOUR_IN_MS));

            // Get arena manager instance and clear each arena
            ArenaManager arenaManager = PVPHelper.INSTANCE.getArenaManager();
            for (Arena arena : arenaManager.getArenas()) {
                try {
                    arena.clear();
                    PVPHelper.INSTANCE.getLogger().log(Level.INFO, "Successfully cleared arena: " + arena.getName());
                } catch (Exception e) {
                    PVPHelper.INSTANCE.getLogger().log(Level.SEVERE, "Failed to clear arena: " + arena.getName(), e);
                }
            }
        }, initialDelayTicks, frequencyTicks);

        PVPHelper.INSTANCE.getLogger().log(Level.INFO,
                String.format("Arena clearing scheduler initialized (Initial delay: %d minutes, Frequency: %.1f hours)",
                        getInitialDelay() / MINUTE_IN_MS,
                        getClearFrequency() / (double)HOUR_IN_MS));
    }
}