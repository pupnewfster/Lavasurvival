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
package net.nyvaria.openanalytics.bukkit.cmd.analytics;

import net.nyvaria.openanalytics.bukkit.OpenAnalytics;
import net.nyvaria.openanalytics.bukkit.cmd.NyvariaCommand;
import net.nyvaria.openanalytics.bukkit.cmd.NyvariaSubCommand;
import net.nyvaria.openanalytics.bukkit.cmd.analytics.admin.SetSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Thompson
 */
public class AdminSubCommand extends NyvariaSubCommand {
    public static final String CMD_ADMIN = "admin";
    public static final String PERM_ADMIN = OpenAnalytics.PERM_ROOT + "." + CMD_ADMIN;

    private final List<NyvariaSubCommand> subcmds;

    public AdminSubCommand(NyvariaCommand parentCmd) {
        super(parentCmd);
        subcmds = new ArrayList<>();
        subcmds.add(new SetSubCommand(parentCmd, this));
    }

    @Override
    public boolean match(String subCmdName) {
        return subCmdName != null && subCmdName.equalsIgnoreCase(CMD_ADMIN);
    }

    @Override
    public List<String> getCommands() {
        return getCommands(null);
    }

    @Override
    public List<String> getCommands(String prefix) {
        List<String> commands = new ArrayList<>();
        if ((prefix == null) || CMD_ADMIN.startsWith(prefix.toLowerCase())) commands.add(CMD_ADMIN);
        return commands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args, int nextArgIndex) {
        return NyvariaCommand.onTabCompleteForSubCommands(sender, cmd, subcmds, args, nextArgIndex);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String[] args, int nextArgIndex) {
        if (!NyvariaCommand.hasCommandPermission(sender, PERM_ADMIN))
            return true;

        //Check if we have enough arguments
        if (args.length < nextArgIndex + 1) {
            usage(sender, cmd, args, nextArgIndex);
            return true;
        }

        //Get the sub-command name
        String subCmdName = args[nextArgIndex];

        //Iterate through the sub-commands
        for (NyvariaSubCommand subcmd : subcmds)
            if (subcmd.match(subCmdName))
                return subcmd.onCommand(sender, cmd, args, nextArgIndex + 1);

        //Must not have matched a sub-command, show the usage
        for (NyvariaSubCommand subcmd : subcmds)
            subcmd.usage(sender, cmd, args, nextArgIndex + 1);
        return true;
    }

    @Override
    public void usage(CommandSender sender, Command cmd, String[] args, int nextArgIndex) {
        if (sender.hasPermission(PERM_ADMIN))
            for (NyvariaSubCommand subcmd : subcmds)
                subcmd.usage(sender, cmd, args, nextArgIndex + 1);
    }
}