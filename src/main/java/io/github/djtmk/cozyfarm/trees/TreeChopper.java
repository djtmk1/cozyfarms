package io.github.djtmk.cozyfarm.trees;

import io.github.djtmk.cozyfarm.GriefPreventionSupport;
import io.github.djtmk.cozyfarm.ReplantHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeChopper {

    private final JobsSupport jobsSupport;
    private final GriefPreventionSupport griefPreventionSupport;
    private final ReplantHandler replantHandler;

    public TreeChopper(JobsSupport jobsSupport, GriefPreventionSupport griefPreventionSupport, ReplantHandler replantHandler) {
        this.jobsSupport = jobsSupport;
        this.griefPreventionSupport = griefPreventionSupport;
        this.replantHandler = replantHandler;
    }

    public void chop(TreeFinder tree, Player p) {
        if (tree.leaves.size() == 0) return;

        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(tree.getBlock().getType(), tree.logs.size()));

        for (Block leaf : tree.leaves) {
            items.addAll(leaf.getDrops(p.getItemInUse()));
            if (griefPreventionSupport.canBuild(p, leaf)) {
                leaf.setType(Material.AIR);
            }
        }

        Map<Integer, ItemStack> overflow = p.getInventory().addItem(items.toArray(new ItemStack[0]));
        for (ItemStack of : overflow.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), of);
        }

        replantHandler.replant(p, tree.getBaseBlock().getBlock());

        for (Block log : tree.logs) {
            if (griefPreventionSupport.canBuild(p, log)) {
                log.setType(Material.AIR);
            }
        }

        if (p.getGameMode() == GameMode.CREATIVE) return;

        ItemStack tool = p.getInventory().getItemInMainHand();
        ItemMeta meta = tool.getItemMeta();
        Damageable damageable = (Damageable) meta;
        int damage = damageable.getDamage();
        if (damage + tree.logs.size() >= tool.getType().getMaxDurability()) {
            p.getInventory().setItemInMainHand(null);
            return;
        }

        damageable.setDamage(damageable.getDamage() + tree.logs.size());
        tool.setItemMeta(meta);

        jobsSupport.updateStats(p, tree.logs.size());
    }
}