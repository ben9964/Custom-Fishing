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

package net.momirealms.customfishing.manager;

import net.momirealms.customfishing.CustomFishing;
import net.momirealms.customfishing.helper.Log;
import net.momirealms.customfishing.integration.*;
import net.momirealms.customfishing.integration.block.VanillaBlockImpl;
import net.momirealms.customfishing.integration.enchantment.VanillaImpl;
import net.momirealms.customfishing.integration.item.*;
import net.momirealms.customfishing.integration.mob.MythicMobsMobImpl;
import net.momirealms.customfishing.integration.papi.PlaceholderManager;
import net.momirealms.customfishing.integration.skill.mcMMOImpl;
import net.momirealms.customfishing.object.Function;
import net.momirealms.customfishing.util.AdventureUtils;
import net.momirealms.customfishing.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntegrationManager extends Function {

    private SkillInterface skillInterface;
    private ItemInterface[] itemInterfaces;
    private MobInterface mobInterface;
    private BlockInterface blockInterface;
    private EnchantmentInterface enchantmentInterface;
    private final PlaceholderManager placeholderManager;
    private VaultHook vaultHook;
    private final CustomFishing plugin;
    private final PluginManager pluginManager;

    public IntegrationManager(CustomFishing plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
        this.placeholderManager = new PlaceholderManager(plugin);
    }

    @Override
    public void load() {
        this.placeholderManager.load();
        hookSkills();
        hookItems();
        hookVault();
        hookMobs();
        hookBlocks();
        hookEnchants();
    }

    @Override
    public void unload() {
        this.skillInterface = null;
        this.itemInterfaces = null;
        this.mobInterface = null;
        this.blockInterface = null;
        this.enchantmentInterface = null;
        this.placeholderManager.unload();
    }

    private void hookEnchants() {
        this.enchantmentInterface = new VanillaImpl();
    }

    private void hookMobs() {
        if (pluginManager.isPluginEnabled("MythicMobs") && pluginManager.getPlugin("MythicMobs").getDescription().getVersion().startsWith("5")) {
            this.mobInterface = new MythicMobsMobImpl();
        }
    }

    private void hookBlocks() {
        this.blockInterface = new VanillaBlockImpl();
    }

    private void hookSkills() {
        if (pluginManager.isPluginEnabled("mcMMO")) {
            this.skillInterface = new mcMMOImpl();
            hookMessage("mcMMO");
        }
    }

    private void hookVault() {
        if (pluginManager.isPluginEnabled("Vault")) {
            vaultHook = new VaultHook();
            if (!vaultHook.initialize()) {
                Log.warn("Failed to initialize Vault!");
            }
            else hookMessage("Vault");
        }
    }

    private void hookItems() {
        List<ItemInterface> itemInterfaceList = new ArrayList<>();
        if (pluginManager.isPluginEnabled("MMOItems")) {
            itemInterfaceList.add(new MMOItemsItemImpl());
            hookMessage("MMOItems");
        }
        if (pluginManager.isPluginEnabled("MythicMobs") && pluginManager.getPlugin("MythicMobs").getDescription().getVersion().startsWith("5")) {
            itemInterfaceList.add(new MythicMobsItemImpl());
            hookMessage("MythicMobs");
        }

        itemInterfaceList.add(new CustomFishingItemImpl(plugin));
        this.itemInterfaces = itemInterfaceList.toArray(new ItemInterface[0]);
    }

    @Nullable
    public SkillInterface getSkillInterface() {
        return skillInterface;
    }

    @NotNull
    public ItemInterface[] getItemInterfaces() {
        return itemInterfaces;
    }

    @Nullable
    public MobInterface getMobInterface() {
        return mobInterface;
    }

    @NotNull
    public BlockInterface getBlockInterface() {
        return blockInterface;
    }

    @NotNull
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    @NotNull
    public EnchantmentInterface getEnchantmentInterface() {
        return enchantmentInterface;
    }

    @NotNull
    public ItemStack build(String key) {
        for (ItemInterface itemInterface : getItemInterfaces()) {
            ItemStack itemStack = itemInterface.build(key, null);
            if (itemStack != null) {
                return itemStack;
            }
        }
        return new ItemStack(Material.AIR);
    }

    @NotNull
    public ItemStack build(String key, Player player) {
        for (ItemInterface itemInterface : getItemInterfaces()) {
            ItemStack itemStack = itemInterface.build(key, player);
            if (itemStack != null) {
                return itemStack;
            }
        }
        return new ItemStack(Material.AIR);
    }

    @Nullable
    public String getItemID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        for (int i = 0, size = itemInterfaces.length; i < size; i++) {
            String id = itemInterfaces[size - i - 1].getID(itemStack);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    public void loseCustomDurability(ItemStack itemStack, Player player) {
        Damageable damageable = (Damageable) itemStack.getItemMeta();
        if (damageable.isUnbreakable()) return;
        for (ItemInterface itemInterface : getItemInterfaces()) {
            if (itemInterface.loseCustomDurability(itemStack, player)) {
                return;
            }
        }
    }

    private void hookMessage(String plugin){
        AdventureUtils.consoleMessage("[CustomFishing] " + plugin + " hooked!");
    }

    @Nullable
    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
