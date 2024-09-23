/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customfishing.helper;

import net.momirealms.customfishing.CustomFishing;
import net.momirealms.customfishing.manager.ConfigManager;
import net.momirealms.customfishing.util.AdventureUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionHelper {
    private final CustomFishing plugin;
    private final boolean isSpigot;
    private final boolean isFolia;
    private final String pluginVersion;

    public VersionHelper(CustomFishing plugin) {
        this.plugin = plugin;
        String server_name = plugin.getServer().getName();
        isSpigot = server_name.equals("CraftBukkit");
        isFolia = server_name.equals("DirtyFolia");
        pluginVersion = plugin.getDescription().getVersion();
    }

    public void checkUpdate() {
        plugin.getScheduler().runTaskAsync(() -> {
            try {
                URL url = new URL("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=2723&key=version");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(60000);
                InputStream inputStream = conn.getInputStream();
                String newest = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                String current = plugin.getDescription().getVersion();
                inputStream.close();

                if (!compareVer(newest, current)) {
                    AdventureUtils.consoleMessage(ConfigManager.lang.equalsIgnoreCase("chinese") ? "[CustomFishing] 当前已是最新版本" : "[CustomFishing] You are using the latest version.");
                    return;
                }

                if (ConfigManager.lang.equalsIgnoreCase("chinese")) {
                    AdventureUtils.consoleMessage("[CustomFishing] 当前版本: <red>" + current);
                    AdventureUtils.consoleMessage("[CustomFishing] 最新版本: <green>" + newest);
                    AdventureUtils.consoleMessage("[CustomFishing] 请到 <u>售后群<!u> 或 <u>https://polymart.org/resource/customfishing.2723<!u> 获取最新版本.");
                }
                else {
                    AdventureUtils.consoleMessage("[CustomFishing] Current version: <red>" + current);
                    AdventureUtils.consoleMessage("[CustomFishing] Latest version: <green>" + newest);
                    AdventureUtils.consoleMessage("[CustomFishing] Update is available: <u>https://polymart.org/resource/customfishing.2723<!u>");
                }
            } catch (Exception exception) {
                Log.warn("Error occurred when checking update");
            }
        });
    }

    private boolean compareVer(String newV, String currentV) {
        if (newV == null || currentV == null || newV.isEmpty() || currentV.isEmpty()) {
            return false;
        }
        String[] newVS = newV.split("\\.");
        String[] currentVS = currentV.split("\\.");
        int maxL = Math.min(newVS.length, currentVS.length);
        for (int i = 0; i < maxL; i++) {
            try {
                String[] newPart = newVS[i].split("-");
                String[] currentPart = currentVS[i].split("-");
                int newNum = Integer.parseInt(newPart[0]);
                int currentNum = Integer.parseInt(currentPart[0]);
                if (newNum > currentNum) {
                    return true;
                } else if (newNum < currentNum) {
                    return false;
                } else if (newPart.length > 1 && currentPart.length > 1) {
                    String[] newHotfix = newPart[1].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    String[] currentHotfix = currentPart[1].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    // hotfix2 & hotfix
                    if (newHotfix.length == 2 && currentHotfix.length == 1) return true;
                        // hotfix3 & hotfix2
                    else if (newHotfix.length > 1 && currentHotfix.length > 1) {
                        int newHotfixNum = Integer.parseInt(newHotfix[1]);
                        int currentHotfixNum = Integer.parseInt(currentHotfix[1]);
                        if (newHotfixNum > currentHotfixNum) {
                            return true;
                        } else if (newHotfixNum < currentHotfixNum) {
                            return false;
                        } else {
                            return newHotfix[0].compareTo(currentHotfix[0]) > 0;
                        }
                    }
                } else if (newPart.length > 1) {
                    return true;
                } else if (currentPart.length > 1) {
                    return false;
                }
            }
            catch (NumberFormatException ignored) {
                return false;
            }
        }
        // if common parts are the same, the longer is newer
        return newVS.length > currentVS.length;
    }

    public boolean isSpigot() {
        return isSpigot;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public boolean isFolia() {
        return isFolia;
    }
}