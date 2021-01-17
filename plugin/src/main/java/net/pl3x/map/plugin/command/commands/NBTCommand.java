package net.pl3x.map.plugin.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.pl3x.map.plugin.Pl3xMap;
import net.pl3x.map.plugin.command.CommandManager;
import net.pl3x.map.plugin.command.Pl3xMapCommand;
import net.pl3x.map.plugin.configuration.Lang;
import net.pl3x.map.plugin.util.BiomeThing;
import net.pl3x.map.plugin.util.Numbers;
import net.pl3x.map.plugin.util.RegionLoader;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NBTCommand extends Pl3xMapCommand {

    public NBTCommand(final @NonNull Pl3xMap plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("nbt")
                        .meta(CommandMeta.DESCRIPTION, "Gets a regions nbt data")
                        .permission("pl3xmap.command.nbt")
                        .senderType(Player.class)
                        .handler(this::player));
    }

    private void player(final @NonNull CommandContext<CommandSender> context) {
        final Player sender = (Player) context.getSender();
        this.execute(sender);
    }

    private void execute(final @NonNull Player player) {
        long now = System.nanoTime();

        Location loc = player.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

        NBTTagCompound nbt = new RegionLoader().getRegionNBT(world,
                Numbers.blockToChunk(x),
                Numbers.blockToChunk(z));
        NBTTagCompound level = nbt.getCompound("Level");

        /*
        NBTTagList sections = new NBTTagList();
        NBTTagList sectionsList = level.getList("Sections", 10);
        for (int i = 0; i < sectionsList.size(); i++) {
            NBTTagCompound compound = sectionsList.getCompound(i);
            compound.remove("startlight");
            compound.remove("BlockLight");
            compound.remove("SkyLight");
            sections.add(compound);
        }
        */

        int[] biomes = level.getIntArray("Biomes");
        BiomeThing biomeThing = new BiomeThing(world, biomes);

        //long[] heightmap = level.getCompound("Heightmaps").getLongArray("WORLD_SURFACE");

        //Lang.send(sender, sections.toString());
        //Lang.send(sender, Arrays.toString(biomes));
        //Lang.send(sender, Arrays.toString(heightmap));

        Biome biome = biomeThing.toBiome(biomeThing.get(x, y, z));
        Lang.send(player, biome.name());
        Lang.send(player, "Nanos: " + (System.nanoTime() - now));
    }
}
