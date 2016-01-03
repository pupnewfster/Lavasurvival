package me.eddiep.minecraft.ls.commands;

import com.crossge.necessities.RankManager.Rank;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdAirc extends Cmd {

    @Override
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Gamemode game = Gamemode.getCurrentGame();
            if (game == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: there is no game running.");
                return true;
            }

            int count = game.countAirBlocksAround(player, 10);
            player.sendMessage("You have " + count + " air blocks around you!");

            ArrayList<Rank> ranks = Lavasurvival.INSTANCE.getRankManager().getOrder();
            for (Rank rank : ranks) {
                double bonusAdd = (1 + ranks.indexOf(rank)) / 2.0;

                double reward = 100 + (bonusAdd * count);
                player.sendMessage(rank.getColor() + rank.getName() + ChatColor.RESET + " - " + reward);
            }
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }

    @Override
    public String getName() {
        return "airc";
    }
}
