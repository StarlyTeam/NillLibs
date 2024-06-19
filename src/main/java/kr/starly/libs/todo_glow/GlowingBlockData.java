package kr.starly.libs.todo_glow;

//import kr.starly.libs.NillLibs;
//import net.md_5.bungee.api.ChatColor;
//import net.minecraft.world.entity.EntityType;
//import org.bukkit.Location;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.UUID;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.atomic.AtomicInteger;
//
//class GlowingBlockData {
//
//    private static final byte FLAGS = 1 << 5;
//    private static final AtomicInteger ENTITY_ID_COUNTER = new AtomicInteger(ThreadLocalRandom.current().nextInt(1_000_000, 2_000_000_000));
//
//    private final @NotNull Player player;
//    private final @NotNull Location location;
//
//    private @NotNull ChatColor color;
//    private int entityId;
//    private UUID entityUuid;
//
//    public GlowingBlockData(@NotNull Player player, @NotNull Location location, @NotNull ChatColor color) {
//        this.player = player;
//        this.location = location;
//        this.color = color;
//    }
//
//    public void setColor(@NotNull ChatColor color) {
//        this.color = color;
//
//        GlowApi glowApi = NillLibs.getGlowApi();
//        if (entityUuid != null) glowApi.setGlowing(entityId, entityUuid.toString(), player, color, FLAGS);
//    }
//
//    public void spawn() {
//        init();
//
//        GlowingPackets.createEntity(player, entityId, entityUuid, EntityType.SHULKER, location);
//        GlowingPackets.setMetadata(player, entityId, FLAGS, false);
//    }
//
//    public void remove() {
//        if (entityUuid == null) return;
//
//        GlowingPackets.removeEntities(player, entityId);
//
//        GlowApi glowApi = NillLibs.getGlowApi();
//        glowApi.unsetGlowing(entityId, player);
//    }
//
//    private void init() {
//        if (entityUuid == null) {
//            entityId = ENTITY_ID_COUNTER.getAndIncrement();
//            entityUuid = UUID.randomUUID();
//
//            setColor(color);
//        }
//    }
//}

class GlowingBlockData {}