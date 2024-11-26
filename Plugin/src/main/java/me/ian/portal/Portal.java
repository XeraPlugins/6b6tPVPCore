package me.ian.portal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ian.utils.area.BoundingBox;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class Portal {

    private final String name;
    private final BoundingBox boundingBox;
    private final Location exitLocation;

    public void teleport(Player player) {
        player.teleport(exitLocation);
    }
}
