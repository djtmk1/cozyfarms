package io.github.dazzdev.cozyfarm.events;

import io.github.dazzdev.cozyfarm.GriefPreventionSupport;
import io.github.dazzdev.cozyfarm.ReplantHandler;
import io.github.dazzdev.cozyfarm.StemFinder;
import io.github.dazzdev.cozyfarm.trees.TreeChopper;
import io.github.dazzdev.cozyfarm.trees.TreeFinder;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockBreak implements Listener {

    private final GriefPreventionSupport griefPreventionSupport;
    private final ReplantHandler replantHandler;
    private final TreeChopper treeChopper;
    private final StemFinder stemFinder;

    public BlockBreak(GriefPreventionSupport griefPreventionSupport, ReplantHandler replantHandler, TreeChopper treeChopper, StemFinder stemFinder) {
        this.griefPreventionSupport = griefPreventionSupport;
        this.replantHandler = replantHandler;
        this.treeChopper = treeChopper;
        this.stemFinder = stemFinder;
    }

    public final List<Material> axes = Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        Material m = b.getType();
        BlockData blockData = b.getBlockData();
        Player p = e.getPlayer();
        if (!griefPreventionSupport.canBuild(p, b)) {
            return;
        }
        if (!axes.contains(p.getInventory().getItemInMainHand().getType())) {
            return;
        }
        if (replantHandler.mainCrops.contains(m)) {
            Ageable age = (Ageable) blockData;
            if (age.getAge() == age.getMaximumAge()) {
                e.setDropItems(false);
                Map<Integer, ItemStack> overflow = p.getInventory().addItem(b.getDrops(p.getItemInUse()).toArray(new ItemStack[0]));
                for (ItemStack item : overflow.values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                }
                replantHandler.replant(p, b);
            }
        } else if (replantHandler.tallCrops.contains(m)) {
            int height = 1;
            Block currentBlock = b;
            while (true) {
                if (currentBlock.getRelative(BlockFace.UP).getType() == m) {
                    height++;
                    currentBlock = currentBlock.getRelative(BlockFace.UP);
                    currentBlock.setType(Material.AIR);
                } else {
                    break;
                }
            }
            if (height == 1) return;
            e.setDropItems(false);
            Map<Integer, ItemStack> overflow = p.getInventory().addItem(new ItemStack(m, height));
            for (ItemStack item : overflow.values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
            replantHandler.replant(p, b);
        } else if (replantHandler.treeLogs.contains(m)) {
            TreeFinder tree;
            tree = new TreeFinder(b, replantHandler);
            treeChopper.chop(tree, p);
        } else if (replantHandler.stemCrops.contains(m) && stemFinder.find(b)) {
            e.setDropItems(false);
            Map<Integer, ItemStack> overflow = p.getInventory().addItem(b.getDrops(p.getItemInUse()).toArray(new ItemStack[0]));
            for (ItemStack item : overflow.values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
        }
    }
}