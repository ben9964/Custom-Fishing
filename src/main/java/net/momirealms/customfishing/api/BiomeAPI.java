package net.momirealms.customfishing.api;

import com.comphenix.protocol.wrappers.MinecraftKey;
import net.minecraft.core.Holder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.generator.BiomeProvider;

public class BiomeAPI {

    public static String getBiome(Location location) {
        var key = location.getWorld().getBiome(location).getKey();
        return key.namespace() + ":" + key.getKey();
    }
}
