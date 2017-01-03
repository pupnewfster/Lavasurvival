/**
 * Copyright (c) 2013-2014
 * Paul Thompson <captbunzo@gmail.com> / Nyvaria <geeks@nyvaria.net>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.nyvaria.openanalytics.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Paul Thompson
 */
public abstract class NyvariaCommand implements CommandExecutor {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasCommandPermission(CommandSender sender, String permission) {
        if (sender instanceof Player)
            return NyvariaCommand.hasCommandPermission((Player) sender, permission);
        else if (sender instanceof ConsoleCommandSender)
            return NyvariaCommand.hasCommandPermission((ConsoleCommandSender) sender, permission);
        return false;
    }

    public static boolean hasCommandPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage("Unknown command. Type \"help\"for help...");
            return false;
        }
        return true;
    }

    public static boolean hasCommandPermission(ConsoleCommandSender console, String permission) {
        if (!console.hasPermission(permission)) {
            console.sendMessage("Unknown command. Type \"help\"for help....");
            return false;
        }
        return true;
    }

    public static List<String> onTabCompleteForSubCommands(CommandSender sender, Command cmd, List<NyvariaSubCommand> subcmds, String[] args, int nextArgIndex) {
        List<String> completions = new ArrayList<>();
        //First, if there is no sub-command ... return all sub-commands
        if (args.length < nextArgIndex + 1) {
            for (NyvariaSubCommand subcmd : subcmds)
                completions.addAll(subcmd.getCommands());
            Collections.sort(completions);
            return completions;
        }

        String subCmdName = args[nextArgIndex];
        //Second, if the sub-command matches a sub-command ... return that sub commands completions
        if (args.length >= nextArgIndex + 2)
            for (NyvariaSubCommand subcmd : subcmds)
                if (subcmd.match(subCmdName))
                    return subcmd.onTabComplete(sender, cmd, args, nextArgIndex + 1);

        //Last, return all sub-commands that match the sub-command prefix given as an argument
        for (NyvariaSubCommand subcmd : subcmds)
            completions.addAll(subcmd.getCommands(subCmdName));
        Collections.sort(completions);
        return completions;
    }
}