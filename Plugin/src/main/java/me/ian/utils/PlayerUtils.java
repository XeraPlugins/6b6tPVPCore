package me.ian.utils;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    // Method to fetch skin properties (textures and signature) by UUID
    public static String[] getSkinProperties(String uuid) {
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

        return skinProperties; // returns [textures, signature] or [null, null] if not found
    }
}
