package kr.starly.libs.todo_glow;

//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import lombok.Getter;
//import net.md_5.bungee.api.ChatColor;
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
//import net.minecraft.world.scores.PlayerTeam;
//import net.minecraft.world.scores.Scoreboard;
//import net.minecraft.world.scores.Team;
//
//import java.util.concurrent.TimeUnit;
//
//@Getter
//class TeamData {
//
//    private final PlayerTeam team;
//    private final Packet<?> creationPacket;
//
//    private final Cache<String, Packet<?>> addPackets =
//            CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build();
//    private final Cache<String, Packet<?>> removePackets =
//            CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build();
//
//    public TeamData(ChatColor color) {
//        PlayerTeam team = new PlayerTeam(new Scoreboard(), "glow-" + color.getName());
//        team.setCollisionRule(Team.CollisionRule.NEVER);
//        team.setColor(ChatFormatting.getByName(color.getName().toUpperCase()));
//
//        this.team = team;
//        this.creationPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
//    }
//
//    public Packet<?> getEntityAddPacket(String teamId) {
//        Packet<?> packet = addPackets.getIfPresent(teamId);
//        if (packet == null) {
//            packet = ClientboundSetPlayerTeamPacket.createPlayerPacket(team, teamId, ClientboundSetPlayerTeamPacket.Action.ADD);
//            addPackets.put(teamId, packet);
//        }
//        return packet;
//    }
//
//    public Packet<?> getEntityRemovePacket(String teamId) {
//        Packet<?> packet = removePackets.getIfPresent(teamId);
//        if (packet == null) {
//            packet = ClientboundSetPlayerTeamPacket.createPlayerPacket(team, teamId, ClientboundSetPlayerTeamPacket.Action.REMOVE);
//            removePackets.put(teamId, packet);
//        }
//        return packet;
//    }
//}

class TeamData {}