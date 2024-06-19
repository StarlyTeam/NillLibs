package kr.starly.libs.todo_glow;

//import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
//import kr.starly.libs.NillLibs;
//import kr.starly.libs.protocol.PacketListenerPriority;
//import kr.starly.libs.protocol.TinyProtocol;
//import kr.starly.libs.protocol.listener.PacketListener;
//import kr.starly.libs.protocol.listener.PacketSendListener;
//import kr.starly.libs.reflect.resolver.MethodResolver;
//import kr.starly.libs.scheduler.Do;
//import net.md_5.bungee.api.ChatColor;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientGamePacketListener;
//import net.minecraft.network.protocol.game.ClientboundBundlePacket;
//import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.EntityDataSerializer;
//import net.minecraft.network.syncher.SynchedEntityData;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.HandlerList;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.plugin.Plugin;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.*;

//@SuppressWarnings("unchecked")
//public class GlowApi implements Listener {
//
//    private final @NotNull Plugin plugin;
//    private PacketListener packetListener;
//    private Map<Player, PlayerEntityData> glowingEntities;
//    private Map<Player, PlayerBlockData> glowingBlocks;
//
//    public GlowApi(@NotNull Plugin plugin) {
//        this.plugin = plugin;
//
//        enable();
//    }
//
//    private void enable() {
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//        this.glowingEntities = new HashMap<>();
//        this.glowingBlocks = new HashMap<>();
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        this.packetListener = tinyProtocol.getPacketListenerManager().registerPacketListener(PacketListenerPriority.NORMAL, (PacketSendListener) (event) -> {
//            if (event.getPacket() instanceof ClientboundSetEntityDataPacket packet
//                    && GlowingPackets.packets.asMap().remove(packet) == null) {
//                int entityId = packet.id();
//                Player player = event.getPlayer();
//
//                PlayerEntityData playerData = glowingEntities.get(player);
//                if (playerData == null) return;
//
//                GlowingEntityData glowingData = playerData.getGlowingDatas().get(entityId);
//                if (glowingData == null) return;
//
//                List<SynchedEntityData.DataValue<?>> packedItems = packet.packedItems();
//                if (packedItems == null) return;
//
//                boolean containsFlags = false;
//                boolean edited = false;
//                for (int i = 0; i < packedItems.size(); i++) {
//                    SynchedEntityData.DataValue<Byte> item = (SynchedEntityData.DataValue<Byte>) packedItems.get(i);
//                    EntityDataAccessor<?> watcherObject = (EntityDataAccessor<?>) new MethodResolver(EntityDataSerializer.class).resolveWrapper("createAccessor").invoke(item.value(), item.id());
//
//                    if (!watcherObject.equals(GlowingPackets.dataAccessor)) {
//                        containsFlags = true;
//                        byte flags = item.value();
//                        glowingData.setOtherFlags(flags);
//                        byte newFlags = GlowingPackets.computeFlags(glowingData);
//                        if (newFlags != flags) {
//                            edited = true;
//                            packedItems = new ArrayList<>(packedItems);
//                            packedItems.set(i, SynchedEntityData.DataValue.create(GlowingPackets.dataAccessor, newFlags));
//                            break;
//                        }
//                    }
//
//                    if (!edited && !containsFlags) {
//                        byte flags = GlowingPackets.computeFlags(glowingData);
//                        if (flags != 0) {
//                            edited = true;
//                            packedItems = new ArrayList<>(packedItems);
//                            packedItems.add(SynchedEntityData.DataValue.create(GlowingPackets.dataAccessor, flags));
//                        }
//                    }
//
//                    if (edited) {
//                        Packet<?> newPacket = new ClientboundSetEntityDataPacket(entityId, packedItems);
//                        GlowingPackets.packets.put(newPacket, GlowingPackets.dummy);
//                        tinyProtocol.sendPackets(player, newPacket);
//
//                        return;
//                    }
//                }
//            } else if (event.getPacket() instanceof ClientboundBundlePacket packet) {
//                Iterable<Packet<? super ClientGamePacketListener>> subPackets = packet.subPackets();
//                for (Packet<?> rawSubPacket : subPackets) {
//                    if (rawSubPacket instanceof ClientboundSetEntityDataPacket subPacket) {
//                        int entityId = subPacket.id();
//                        Player player = event.getPlayer();
//
//                        PlayerEntityData playerData = glowingEntities.get(player);
//                        if (playerData == null) continue;
//
//                        GlowingEntityData glowingData = playerData.getGlowingDatas().get(entityId);
//                        if (glowingData == null) continue;
//
//                        Do.asyncLater(1L, () -> GlowingPackets.updateGlowingState(glowingData));
//                    }
//                }
//            }
//        });
//    }
//
//    public void disable() {
//        HandlerList.unregisterAll(this);
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        tinyProtocol.getPacketListenerManager().unregisterPacketListener(packetListener);
//
//        glowingEntities = null;
//    }
//
//    @EventHandler
//    public void onQuit(PlayerQuitEvent event) {
//        glowingEntities.remove(event.getPlayer());
//    }
//
//    @EventHandler
//    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
//        PlayerBlockData playerData = glowingBlocks.get(event.getPlayer());
//        if (playerData == null) return;
//
//        playerData.getGlowingDatas().forEach((location, blockData) -> {
//            if (Objects.equals(location.getWorld(), event.getWorld()) && location.getBlockX() >> 4 == event.getChunk().getX() && location.getBlockZ() >> 4 == event.getChunk().getZ()) {
//                blockData.spawn();
//            }
//        });
//    }
//
//    public void setGlowing(Entity entity, Player receiver) {
//        setGlowing(entity, receiver, null);
//    }
//
//    public void setGlowing(Entity entity, Player receiver, ChatColor color) {
//        String teamID = entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
//        setGlowing(entity.getEntityId(), teamID, receiver, color, GlowingPackets.getEntityFlags(entity));
//    }
//
//    public void setGlowing(int entityId, String teamId, Player receiver) {
//        setGlowing(entityId, teamId, receiver, null, (byte) 0);
//    }
//
//    public void setGlowing(int entityId, String teamId, Player receiver, ChatColor color) {
//        setGlowing(entityId, teamId, receiver, color, (byte) 0);
//    }
//
//    public void setGlowing(int entityId, String teamId, Player receiver, ChatColor color, byte otherFlags) {
//        PlayerEntityData playerData = glowingEntities.get(receiver);
//        if (playerData == null) {
//            playerData = new PlayerEntityData(receiver);
//            glowingEntities.put(receiver, playerData);
//        }
//
//        GlowingEntityData glowingData = playerData.getGlowingDatas().get(entityId);
//        if (glowingData == null) {
//            glowingData = new GlowingEntityData(playerData, entityId, teamId, color, otherFlags, true);
//            playerData.getGlowingDatas().put(entityId, glowingData);
//
//            GlowingPackets.createGlowing(glowingData);
//            if (color != null) GlowingPackets.setGlowingColor(glowingData);
//        } else {
//            if (Objects.equals(glowingData.getColor(), color)) return;
//
//            if (color == null) {
//                GlowingPackets.removeGlowingColor(glowingData);
//                glowingData.setColor(null);
//            } else {
//                glowingData.setColor(color);
//                GlowingPackets.setGlowingColor(glowingData);
//            }
//        }
//    }
//
//    public void setGlowing(@NotNull Block block, @NotNull Player receiver, @NotNull ChatColor color) {
//        setGlowing(block.getLocation(), receiver, color);
//    }
//
//    public void setGlowing(@NotNull Location block, @NotNull Player receiver, @NotNull ChatColor color) {
//        block = normalizeLocation(block);
//
//        PlayerBlockData playerData = glowingBlocks.computeIfAbsent(Objects.requireNonNull(receiver), PlayerBlockData::new);
//        GlowingBlockData blockData = playerData.getGlowingDatas().get(block);
//        if (blockData == null) {
//            blockData = new GlowingBlockData(receiver, block, color);
//            playerData.getGlowingDatas().put(block, blockData);
//            if (canSee(receiver, block)) blockData.spawn();
//        } else {
//            blockData.setColor(color);
//        }
//    }
//
//    public void unsetGlowing(Entity entity, Player receiver) {
//        unsetGlowing(entity.getEntityId(), receiver);
//    }
//
//    public void unsetGlowing(int entityID, Player receiver) {
//        PlayerEntityData playerData = glowingEntities.get(receiver);
//        if (playerData == null) return;
//
//        GlowingEntityData glowingData = playerData.getGlowingDatas().remove(entityID);
//        if (glowingData == null) return;
//
//        GlowingPackets.removeGlowing(glowingData);
//        if (glowingData.getColor() != null) GlowingPackets.removeGlowingColor(glowingData);
//    }
//
//    public void unsetGlowing(@NotNull Block block, @NotNull Player receiver) {
//        unsetGlowing(block.getLocation(), receiver);
//    }
//
//    public void unsetGlowing(@NotNull Location block, @NotNull Player receiver) {
//        block = normalizeLocation(block);
//
//        PlayerBlockData playerData = glowingBlocks.get(receiver);
//        if (playerData == null) return;
//
//        GlowingBlockData blockData = playerData.getGlowingDatas().remove(block);
//        if (blockData == null) return;
//
//        blockData.remove();
//        if (playerData.getGlowingDatas().isEmpty()) glowingBlocks.remove(receiver);
//    }
//
//    private @NotNull Location normalizeLocation(@NotNull Location location) {
//        location.checkFinite();
//        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
//    }
//
//    private boolean canSee(Player player, Location location) {
//        int viewDistance = Math.min(player.getViewDistance(), Bukkit.getViewDistance());
//        int deltaChunkX = (player.getLocation().getBlockX() >> 4) - (location.getBlockX() >> 4);
//        int deltaChunkZ = (player.getLocation().getBlockZ() >> 4) - (location.getBlockZ() >> 4);
//        int chunkDistanceSquared = deltaChunkX * deltaChunkX + deltaChunkZ * deltaChunkZ;
//        return chunkDistanceSquared <= viewDistance * viewDistance;
//    }
//}

class GlowApi {}