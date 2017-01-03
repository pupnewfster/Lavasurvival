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
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Thompson
 */
public abstract class NyvariaSubCommand {
    protected final NyvariaCommand parentCmd;
    protected final NyvariaSubCommand parentSubCmd;

    protected NyvariaSubCommand(NyvariaCommand parentCmd) {
        this(parentCmd, null);
    }

    protected NyvariaSubCommand(NyvariaCommand parentCmd, NyvariaSubCommand parentSubCmd) {
        this.parentCmd = parentCmd;
        this.parentSubCmd = parentSubCmd;
    }

    public abstract boolean match(String subCmdName);

    public abstract boolean onCommand(CommandSender sender, Command cmd, String[] args, int nextArgIndex);

    public abstract void usage(CommandSender sender, Command cmd, String[] args, int nextArgIndex);

    public List<String> getCommands() {
        return getCommands(null);
    }

    public List<String> getCommands(String prefix) {
        return new ArrayList<>();
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args, int nextArgIndex) {
        return new ArrayList<>();
    }
}