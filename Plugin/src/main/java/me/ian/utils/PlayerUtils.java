package me.ian.utils;

import com.destroystokyo.paper.Title;
import com.moandjiezana.toml.Toml;
import me.ian.PVPHelper;
import me.ian.command.CommandManager;
import me.ian.lobby.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 * @author SevJ6
 */
public class PlayerUtils {

    public static void facePlayersTowardsEachOther(Player player1, Player player2) {
        // Get the direction from player1 to player2
        Vector direction1To2 = player2.getLocation().toVector().subtract(player1.getLocation().toVector()).normalize();
        Vector direction2To1 = player1.getLocation().toVector().subtract(player2.getLocation().toVector()).normalize();

        // Calculate yaw and pitch for player1 to face player2
        float yaw1 = (float) Math.toDegrees(Math.atan2(direction1To2.getZ(), direction1To2.getX())) - 90;
        float pitch1 = (float) Math.toDegrees(-Math.asin(direction1To2.getY()));

        // Calculate yaw and pitch for player2 to face player1
        float yaw2 = (float) Math.toDegrees(Math.atan2(direction2To1.getZ(), direction2To1.getX())) - 90;
        float pitch2 = (float) Math.toDegrees(-Math.asin(direction2To1.getY()));

        // Set player1's rotation
        player1.teleport(player1.getLocation().setDirection(direction1To2));
        player1.getLocation().setYaw(yaw1);
        player1.getLocation().setPitch(pitch1);

        // Set player2's rotation
        player2.teleport(player2.getLocation().setDirection(direction2To1));
        player2.getLocation().setYaw(yaw2);
        player2.getLocation().setPitch(pitch2);
    }

    public static void sendTitle(Player player, String title, String subTitle) {
        int stay = 30;      // Duration in ticks for display time (1.5 seconds)
        int fadeOut = 10;   // Duration in ticks for fade-out (0.5 seconds)
        player.sendTitle(Utils.translateChars(title), Utils.translateChars(subTitle), 0, stay, fadeOut);
    }

    public static void sendPling(Player player, float pitch) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 10f, pitch);
    }

    // Method to fetch skin properties (textures and signature) by UUID
    public static NPC.SkinTexture getSkinProperties(String uuid) {
        String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        String[] skinProperties = new String[2]; // 0: textures, 1: signature

        try {
            // Open connection to Mojang's session server
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Check for a successful response code (HTTP 200)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // Parse the JSON response
                JSONObject jsonResponse = (JSONObject) JSONValue.parseWithException(reader);
                JSONArray propertiesArray = (JSONArray) jsonResponse.get("properties");

                for (Object object : propertiesArray) {
                    JSONObject property = (JSONObject) object;
                    if (property.containsKey("value")) skinProperties[0] = (String) property.get("value");
                    if (property.containsKey("signature")) skinProperties[1] = (String) property.get("signature");
                }

                reader.close();
            } else {
                System.out.println("Error: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NPC.SkinTexture(skinProperties[0], skinProperties[1]);
    }

    public static void teleportToSpawn(Player player) {
        Toml config = PVPHelper.INSTANCE.getRunningConfig().getToml().getTable("lobby_spawn");
        player.teleport(new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"), config.getDouble("yaw").floatValue(), config.getDouble("pitch").floatValue()));
    }

    public static void kick(Player player, String reason) {
        Utils.run(() -> {
            player.kickPlayer(reason);
            PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("%s has been kicked for: %s", player.getName(), reason));
        });
    }

    public static void sendHelpMessage(Player player) {
        CommandManager commandManager = PVPHelper.INSTANCE.getCommandManager();

        StringBuilder sb = new StringBuilder();
        commandManager.getCommands()
                .stream()
                .filter(pluginCommand -> !pluginCommand.getCommandName().equals("help"))
                .filter(pluginCommand -> !pluginCommand.isAdminOnly())
                .map(pluginCommand -> Bukkit.getPluginCommand(pluginCommand.getCommandName()))
                .forEach(bukkitCommand -> {

                    sb.append(String.format(
                            "&a%s &r- &3%s\n&7%s\n",
                            bukkitCommand.getName(),
                            bukkitCommand.getDescription(),
                            bukkitCommand.getUsage()
                    ));
                });

        Utils.sendMessage(player, sb.toString());
    }
}
