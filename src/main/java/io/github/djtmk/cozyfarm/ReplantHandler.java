package io.github.djtmk.cozyfarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ReplantHandler {

    private final CozyFarm plugin;

    public ReplantHandler(CozyFarm plugin) {
        this.plugin = plugin;
    }

    public final List<Material> mainCrops = Arrays.asList(Material.WHEAT, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART, Material.CARROTS, Material.COCOA);
    public final List<Material> tallCrops = Arrays.asList(Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO);
    public final List<Material> treeLogs = Arrays.asList(Material.OAK_LOG, Material.STRIPPED_OAK_LOG, Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG, Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG);
    public final List<Material> stemCrops = Arrays.asList(Material.MELON, Material.PUMPKIN);


    public void replant(Player p, Block b) {
        Material m = b.getType();
        for (ItemStack item : p.getInventory()) {
            if (item != null && item.getType() == getItem(m)) {
                if (tallCrops.contains(m) && b.getRelative(BlockFace.DOWN).getType() == m) {
                    return;
                }
                if (treeLogs.contains(m)) {
                    switch (b.getRelative(BlockFace.DOWN).getType()) {
                        case DIRT:
                        case COARSE_DIRT:
                        case GRASS_BLOCK:
                        case PODZOL:
                            break;
                        default:
                            return;
                    }
                }
                BlockData blockData = b.getBlockData();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    b.setType(treeLogs.contains(m) ? getItem(m) : m);
                    if (m == Material.COCOA) {
                        Directional newDirectional = (Directional) b.getBlockData();
                        newDirectional.setFacing(((Directional) blockData).getFacing());
                        b.setBlockData(newDirectional);
                    }
                });
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }
    }

    private Material getItem(Material crop) {
        switch (crop) {
            case WHEAT:
                return Material.WHEAT_SEEDS;
            case POTATOES:
                return Material.POTATO;
            case BEETROOTS:
                return Material.BEETROOT_SEEDS;
            case CARROTS:
                return Material.CARROT;
            case COCOA:
                return Material.COCOA_BEANS;
            case PUMPKIN:
                return Material.PUMPKIN_SEEDS;
            case MELON:
                return Material.MELON_SEEDS;
            case OAK_LOG:
            case STRIPPED_OAK_LOG:
                return Material.OAK_SAPLING;
            case BIRCH_LOG:
            case STRIPPED_BIRCH_LOG:
                return Material.BIRCH_SAPLING;
            case SPRUCE_LOG:
            case STRIPPED_SPRUCE_LOG:
                return Material.SPRUCE_SAPLING;
            default:
                return crop;
        }
    }
}