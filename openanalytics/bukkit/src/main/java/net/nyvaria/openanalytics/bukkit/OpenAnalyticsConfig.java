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

/**
 * @author Paul Thompson
 */
public class OpenAnalyticsConfig {
    public static final String TRACKING_ID = "tracking-id";
    public static final String HOST_NAME = "host-name";
    public static final String USE_METRICS = "use-metrics";
    public static final String IN_BUNGEECORD = "in-bungeecord";

    private OpenAnalyticsConfig() {
        //Prevent instantiation
    }

    //Tracking ID
    public static String getTrackingID() {
        return OpenAnalytics.getInstance().getConfig().contains(TRACKING_ID) ? OpenAnalytics.getInstance().getConfig().getString(TRACKING_ID) : null;
    }

    public static void setTrackingID(String trackingID) {
        OpenAnalytics.getInstance().getConfig().set(TRACKING_ID, trackingID);
        OpenAnalytics.getInstance().saveConfig();
    }

    //Hostname
    public static String getHostName() {
        return OpenAnalytics.getInstance().getConfig().contains(HOST_NAME) ? OpenAnalytics.getInstance().getConfig().getString(HOST_NAME) : null;
    }

    public static void setHostName(String hostname) {
        OpenAnalytics.getInstance().getConfig().set(HOST_NAME, hostname);
        OpenAnalytics.getInstance().saveConfig();
    }

    //Use Metrics
    public static Boolean getUseMetrics() {
        return OpenAnalytics.getInstance().getConfig().contains(USE_METRICS) ? OpenAnalytics.getInstance().getConfig().getBoolean(USE_METRICS) : null;
    }

    public static void setUseMetrics(boolean useMetrics) {
        OpenAnalytics.getInstance().getConfig().set(USE_METRICS, useMetrics);
        OpenAnalytics.getInstance().saveConfig();
    }

    //In BungeeCord
    public static Boolean getInBungeeCord() {
        return OpenAnalytics.getInstance().getConfig().contains(IN_BUNGEECORD) ? OpenAnalytics.getInstance().getConfig().getBoolean(IN_BUNGEECORD) : null;
    }

    public static void setInBungeeCord(boolean inBungeeCord) {
        OpenAnalytics.getInstance().getConfig().set(IN_BUNGEECORD, inBungeeCord);
        OpenAnalytics.getInstance().saveConfig();
    }
}