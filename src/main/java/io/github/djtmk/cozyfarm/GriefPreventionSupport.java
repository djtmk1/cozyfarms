package io.github.djtmk.cozyfarm;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class GriefPreventionSupport {

    private final CozyFarm plugin;

    public GriefPreventionSupport(CozyFarm plugin) {
        this.plugin = plugin;
    }

    public boolean canBuild(Player p, Block b) {
        if (!plugin.griefPreventionFound) return true;

        UUID uuid = p.getUniqueId();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(b.getLocation(), false, null);
        if (claim != null) {
            return claim.getOwnerID().equals(uuid) || Objects.equals(claim.getPermission(String.valueOf(uuid)), ClaimPermission.Build);
        }
        return true;
    }
}