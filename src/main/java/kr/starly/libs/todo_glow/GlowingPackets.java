package kr.starly.libs.todo_glow;

//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import kr.starly.libs.NillLibs;
//import kr.starly.libs.protocol.TinyProtocol;
//import kr.starly.libs.reflect.resolver.FieldResolver;
//import net.md_5.bungee.api.ChatColor;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
//import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
//import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.phys.Vec3;
//import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_20_R4.entity.CraftEntity;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//class GlowingPackets {
//
//    static final Cache<Object, Object> packets = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
//    static final Object dummy = new Object();
//
//    static final Map<ChatColor, TeamData> teams = new HashMap<>();
//
//    static final byte GLOWING_FLAG = 1 << 6;
//    static final EntityDataAccessor<Byte> dataAccessor = new FieldResolver(net.minecraft.world.entity.Entity.class)
//            .resolveAccessor("DATA_SHARED_FLAGS_ID")
//            .get(null);
//
//    public static byte getEntityFlags(Entity entity) {
//        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
//        SynchedEntityData entityData = nmsEntity.getEntityData();
//        return entityData.get(dataAccessor);
//    }
//
//    public static void createGlowing(GlowingEntityData glowingData) {
//        setMetadata(
//                glowingData.getPlayerData().getPlayer(),
//                glowingData.getEntityId(),
//                computeFlags(glowingData),
//                true
//        );
//    }
//
//    public static byte computeFlags(GlowingEntityData glowingData) {
//        byte newFlags = glowingData.getOtherFlags();
//        if (glowingData.isEnabled()) {
//            newFlags |= GLOWING_FLAG;
//        } else {
//            newFlags &= ~GLOWING_FLAG;
//        }
//        return newFlags;
//    }
//
//    public static void removeGlowing(GlowingEntityData glowingData) {
//        setMetadata(glowingData.getPlayerData().getPlayer(), glowingData.getEntityId(), glowingData.getOtherFlags(), true);
//    }
//
//    public static void updateGlowingState(GlowingEntityData glowingData) {
//        if (glowingData.isEnabled()) createGlowing(glowingData);
//        else removeGlowing(glowingData);
//    }
//
//    public static void setMetadata(Player player, int entityId, byte flags, boolean ignore) {
//        List<SynchedEntityData.DataValue<?>> dataItems = new ArrayList<>(1);
//        dataItems.add(SynchedEntityData.DataValue.create(dataAccessor, flags));
//
//        Packet<?> packetMetadata = new ClientboundSetEntityDataPacket(entityId, dataItems);
//        if (ignore) packets.put(packetMetadata, dummy);
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        tinyProtocol.sendPackets(player, packetMetadata);
//    }
//
//    public static void setGlowingColor(GlowingEntityData glowingData) {
//        boolean sendCreation = false;
//        if (glowingData.getPlayerData().getSentColors() == null) {
//            glowingData.getPlayerData().setSentColors(Set.of(glowingData.getColor()));
//            sendCreation = true;
//        } else if (glowingData.getPlayerData().getSentColors().add(glowingData.getColor())) {
//            sendCreation = true;
//        }
//
//        TeamData teamData = teams.get(glowingData.getColor());
//        if (teamData == null) {
//            teamData = new TeamData(glowingData.getColor());
//            teams.put(glowingData.getColor(), teamData);
//        }
//
//        Packet<?> entityAddPacket = teamData.getEntityAddPacket(glowingData.getTeamId());
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        if (sendCreation) {
//            tinyProtocol.sendPackets(glowingData.getPlayerData().getPlayer(), teamData.getCreationPacket(), entityAddPacket);
//        } else {
//            tinyProtocol.sendPackets(glowingData.getPlayerData().getPlayer(), entityAddPacket);
//        }
//    }
//
//    public static void removeGlowingColor(GlowingEntityData glowingData) {
//        TeamData teamData = teams.get(glowingData.getColor());
//        if (teamData == null) return;
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        tinyProtocol.sendPackets(glowingData.getPlayerData().getPlayer(), teamData.getEntityRemovePacket(glowingData.getTeamId()));
//    }
//
//    public static void createEntity(Player player, int entityId, UUID entityUuid, EntityType<?> entityType, Location location) {
//        Packet<?> packet = new ClientboundAddEntityPacket(
//                entityId, entityUuid,
//                location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(),
//                entityType, 0, new Vec3(0, 0, 0), 0d);
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        tinyProtocol.sendPackets(player, packet);
//    }
//
//    public static void removeEntities(Player player, int... entitiesId) {
//        Object[] packets = new Packet<?>[]{new ClientboundRemoveEntitiesPacket(entitiesId)};
//
//        TinyProtocol tinyProtocol = NillLibs.getTinyProtocol();
//        tinyProtocol.sendPackets(player, packets);
//    }
//}

class GlowingPackets {}