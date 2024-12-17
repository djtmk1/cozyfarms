package io.github.djtmk.cozyfarm.trees;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import io.github.djtmk.cozyfarm.CozyFarm;
import net.Zrips.CMILib.Equations.Parser;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.gamingmesh.jobs.Jobs.getEconomy;

public class JobsSupport {

    private final CozyFarm plugin;

    public JobsSupport(CozyFarm plugin) {
        this.plugin = plugin;
    }

    public void updateStats(Player p, int logs) {
        if (!plugin.jobsFound) return;

        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        if (jobsPlayer == null) {
            return;
        }

        List<JobProgression> jobs = jobsPlayer.getJobProgression();
        for (JobProgression prog : jobs) {
            if (Objects.equals(prog.getJob().getName(), "Woodcutter")) {
                Parser moneyEquation = prog.getJob().getMoneyEquation();
                moneyEquation.setVariable("numjobs", jobs.size());
                moneyEquation.setVariable("baseincome", logs);
                moneyEquation.setVariable("joblevel", prog.getLevel());
                Parser pointsEquation = prog.getJob().getPointsEquation();
                pointsEquation.setVariable("numjobs", jobs.size());
                pointsEquation.setVariable("basepoints", logs);
                pointsEquation.setVariable("joblevel", prog.getLevel());
                addPointsAndMoney(jobsPlayer, pointsEquation.getValue(), moneyEquation.getValue());
                Parser xpEquation = prog.getJob().getXpEquation();
                xpEquation.setVariable("numjobs", jobs.size());
                xpEquation.setVariable("baseexperience", logs);
                xpEquation.setVariable("joblevel", prog.getLevel());
                prog.addExperience(xpEquation.getValue());
            }
        }
    }

    public void addPointsAndMoney(JobsPlayer player, double pointValue, double moneyValue) {
        BufferedEconomy economy = getEconomy();
        if (economy == null || (pointValue + moneyValue) <= 0) {
            return;
        }
        HashMap<CurrencyType, Double> map = new HashMap<>();
        if (pointValue > 0) {
            map.put(CurrencyType.POINTS, pointValue);
        }
        if (moneyValue > 0) {
            map.put(CurrencyType.MONEY, moneyValue);
        }
        economy.pay(player, map);
    }
}
