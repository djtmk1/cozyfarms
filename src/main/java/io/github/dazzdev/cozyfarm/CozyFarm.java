package io.github.dazzdev.cozyfarm;

import io.github.dazzdev.cozyfarm.events.BlockBreak;
import io.github.dazzdev.cozyfarm.trees.JobsSupport;
import io.github.dazzdev.cozyfarm.trees.TreeChopper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CozyFarm extends JavaPlugin {

    public boolean jobsFound = false;
    public boolean griefPreventionFound = false;

    @Override
    public void onEnable() {
        JobsSupport jobsSupport = new JobsSupport(this);
        GriefPreventionSupport griefPreventionSupport = new GriefPreventionSupport(this);
        ReplantHandler replantHandler = new ReplantHandler(this);
        TreeChopper treeChopper = new TreeChopper(jobsSupport, griefPreventionSupport, replantHandler);
        StemFinder stemFinder = new StemFinder();

        Bukkit.getPluginManager().registerEvents(new BlockBreak(griefPreventionSupport, replantHandler, treeChopper, stemFinder), this);

        if (Bukkit.getPluginManager().getPlugin("Jobs") != null && Bukkit.getPluginManager().getPlugin("CMILib") != null) {
            jobsFound = true;
            getLogger().info("Jobs plugin (and CMILib) found.");
        }

        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
            griefPreventionFound = true;
            getLogger().info("GriefPrevention plugin found.");
        }
        getLogger().info("CozyFarm enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CozyFarm says bye.");
    }
}
