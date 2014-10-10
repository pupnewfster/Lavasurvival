package me.eddiep.commands;

import me.eddiep.Lavasurvival;
import me.eddiep.system.setup.SetupMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSetupMap extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (Lavasurvival.INSTANCE.getSetups().containsKey(p.getUniqueId())) {
                SetupMap s = Lavasurvival.INSTANCE.getSetups().get(p.getUniqueId());
                s.sendMessage("Aborted..");
                s.end();
                Lavasurvival.INSTANCE.getSetups().remove(p.getUniqueId());
            } else {
                SetupMap setup = new SetupMap(p, Lavasurvival.INSTANCE);
                setup.start();
                Lavasurvival.INSTANCE.getSetups().put(p.getUniqueId(), setup);
            }
        } else
            sender.sendMessage("This command can only be used in game..");
        return true;
    }
}
