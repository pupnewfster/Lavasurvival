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
package net.nyvaria.openanalytics.bukkit;

import net.nyvaria.openanalytics.bukkit.client.ClientList;
import net.nyvaria.openanalytics.bukkit.cmd.AnalyticsCommand;
import net.nyvaria.openanalytics.bukkit.listener.OpenAnalyticsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * @author Paul Thompson
 */
public class OpenAnalytics extends JavaPlugin {
    public static final String PERM_ROOT = "openanalytics";

    //Gotta stay with an instance variable until spigot updates to 1.7.4
    private static OpenAnalytics instance = null;

    //Tracker and Listeners and a List (oh my)
    private OpenAnalyticsTracker tracker = null;
    private OpenAnalyticsListener listener = null;
    private ClientList clientList = null;

    /**
     * Override the onEnable & onDisable methods
     */
    @Override
    public void onEnable() {
        try {
            //Save the instance
            OpenAnalytics.instance = this;

            //Initialise or update the configuration
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();

            //Create the tracker
            tracker = new OpenAnalyticsTracker(this);

            //Create the client list and add all currently logged in players
            clientList = new ClientList();
            Bukkit.getOnlinePlayers().forEach(p -> clientList.put(p));

            //Create and register the listeners
            listener = new OpenAnalyticsListener(this);

            //Create the commands and set the executors and completers
            AnalyticsCommand cmdAnalytics = new AnalyticsCommand();
            getCommand(AnalyticsCommand.CMD).setExecutor(cmdAnalytics);
            getCommand(AnalyticsCommand.CMD).setTabCompleter(cmdAnalytics);
        } catch (Exception e) {
            log("Enabling %1$s failed - %2$s", getNameAndVersion(), e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            OpenAnalytics.instance = null;
        } finally {
            log("Enabling %1$s successful", getNameAndVersion());
        }
    }

    @Override
    public void onDisable() {
        //Destroy the listener and tracker
        listener = null;
        tracker = null;
        //Destroy the client list
        clientList = null;

        //Clear the instance
        OpenAnalytics.instance = null;

        //Print a lovely log message
        log("Disabling %s successful", getNameAndVersion());
    }

    /**
     * Get the instance of the OpenAnalytics plugin from Bukkit.
     *
     * @return OpenAnalytics
     */
    public static OpenAnalytics getInstance() {
        //return JavaPlugin.getPlugin(OpenAnalytics.class);
        return instance;
    }

    /**
     * Getters
     */
    public OpenAnalyticsListener getListener() {
        return listener;
    }

    public OpenAnalyticsTracker getTracker() {
        return tracker;
    }

    public ClientList getClientList() {
        return clientList;
    }

    public void log(String msg) {
        this.log(Level.INFO, msg);
    }

    public void log(String msg, Object... args) {
        this.log(String.format(msg, args));
    }

    public void log(Level level, String msg) {
        this.getLogger().log(level, msg);
    }

    public void log(Level level, String msg, Object... args) {
        this.log(level, String.format(msg, args));
    }

    protected String getNameAndVersion() {
        return this.getName() + " v" + this.getDescription().getVersion();
    }
}