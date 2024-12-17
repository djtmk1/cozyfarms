package io.github.djtmk.cozyfarm.trees;

import io.github.djtmk.cozyfarm.ReplantHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;

import java.util.HashSet;

public class TreeFinder {

    private final ReplantHandler replantHandler;

    public HashSet<Block> logs;
    public HashSet<Block> leaves;

    private final Block block;
    private Location base;

    public TreeFinder(Block block, ReplantHandler replantHandler) {
        this.block = block;
        this.replantHandler = replantHandler;

        base = block.getLocation();

        logs = new HashSet<>();
        leaves = new HashSet<>();

        if (find(block)) return;

        findBase(block);
    }

    private boolean find(Block block) {
        // Algorithm for logs
        if (replantHandler.treeLogs.contains(block.getType())) {
            logs.add(block);

            // Check all surrounding blocks
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Get new location
                        Block check = block.getLocation().add(x, y, z).getBlock();

                        // Recursively find surrounding blocks
                        if ((replantHandler.treeLogs.contains(block.getType()) || isLeaf(check)) && !logs.contains(check) && !leaves.contains(check)) {
                            if (find(check)) return true;
                        }
                    }
                }
            }
        }

        // Algorithm for leaves
        else if (isLeaf(block)) {
            leaves.add(block);

            int y = 0;
            while (true) {
                HashSet<Block> local_set = new HashSet<>();

                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Block check = block.getLocation().add(x, y, z).getBlock();

                        if ((isLeaf(check)) && !leaves.contains(check)) {

                            // Maybe a way to check if it's the same tree?
                            int check_dist = ((Leaves) check.getBlockData()).getDistance();
                            int this_dist = ((Leaves) block.getBlockData()).getDistance();
                            if (check_dist <= this_dist) continue;

                            local_set.add(check);
                            if (find(check)) return true;
                        }
                    }
                }

                if (local_set.size() == 0) break;

                ++y;
            }
        }

        return false;
    }

    private boolean isLeaf(Block b) {
        return b.getBlockData() instanceof Leaves;
    }

    private void findBase(Block block) {
        Location loc = block.getLocation();

        while (true) {
            loc.subtract(0, 1, 0);
            Block check = loc.getBlock();

            if (!replantHandler.treeLogs.contains(check.getType())) {
                break;
            }
        }

        loc.add(0, 1, 0);
        base = loc;
    }

    public Block getBlock() {
        return this.block;
    }

    public Location getBaseBlock() {
        return base;
    }
}