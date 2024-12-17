package io.github.djtmk.cozyfarm;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import java.util.Arrays;
import java.util.List;

public class StemFinder {

    public final List<Material> stems = Arrays.asList(Material.ATTACHED_MELON_STEM, Material.ATTACHED_PUMPKIN_STEM);


    public boolean find(Block b) {
        Block[] blocks = {b.getRelative(BlockFace.NORTH), b.getRelative(BlockFace.EAST), b.getRelative(BlockFace.SOUTH), b.getRelative(BlockFace.WEST)};

        for (Block faces : blocks) {
            if (stems.contains(faces.getType())) {
                BlockData blockData = faces.getBlockData();
                Directional directional = (Directional) blockData;
                if (faces.getRelative(directional.getFacing()).equals(b)) {
                    return true;
                }
            }
        }
        return false;
    }
}
