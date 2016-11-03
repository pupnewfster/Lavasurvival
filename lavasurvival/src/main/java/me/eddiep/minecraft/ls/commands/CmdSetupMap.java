package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.system.setup.SetupMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdSetupMap implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (Lavasurvival.INSTANCE.getSetups().containsKey(p.getUniqueId())) {
                SetupMap s = Lavasurvival.INSTANCE.getSetups().get(p.getUniqueId());
                s.sendMessage("Aborted..");
                s.end();
                Lavasurvival.INSTANCE.removeFromSetup(p.getUniqueId());
            } else {
                SetupMap setup = new SetupMap(p, Lavasurvival.INSTANCE);
                setup.start();
                Lavasurvival.INSTANCE.addToSetup(p.getUniqueId(), setup);
            }
        } else
            sender.sendMessage("This command can only be used in game..");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "setupmap";
    }
}