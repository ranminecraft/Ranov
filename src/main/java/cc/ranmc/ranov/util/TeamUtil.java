package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.color;

public class TeamUtil {

    // 小队最大成员数量
    private static final int teamMax = Main.getInstance().getConfig().getInt("team-max", 3);

    /** key = 接收方玩家名, value = 发起请求的玩家名 */
    private static final Map<String, String> requestMap = new ConcurrentHashMap<>();

    /** 队伍 id 与队长、成员的映射 */
    private static final Map<UUID, Team> teamMap = new ConcurrentHashMap<>();

    /** 玩家名 → 所属队伍UUID */
    private static final Map<String, UUID> playerTeamMap = new ConcurrentHashMap<>();

    /** 队伍结构 */
    public static class Team {
        String leader;                 // 队长
        public Set<String> members = new HashSet<>(); // 所有成员（包含队长）
    }

    /* ============================================================
     * 队伍查询 API
     * ============================================================ */
    public static boolean inTeam(String player) {
        return playerTeamMap.containsKey(player);
    }

    public static Team getTeam(String player) {
        UUID id = playerTeamMap.get(player);
        return id == null ? null : teamMap.get(id);
    }

    public static boolean isLeader(String player) {
        Team t = getTeam(player);
        return t != null && t.leader.equals(player);
    }

    /* ============================================================
     * 队长转让
     * ============================================================ */
    public static void transfer(Player leader, Player target) {
        String leaderName = leader.getName();
        String targetName = target.getName();

        Team team = getTeam(leaderName);

        if (team == null) {
            leader.sendMessage(PREFIX + color("&c你当前没有队伍"));
            return;
        }

        if (!team.leader.equals(leaderName)) {
            leader.sendMessage(PREFIX + color("&c只有队长才能转让队伍"));
            return;
        }

        if (!team.members.contains(targetName)) {
            leader.sendMessage(PREFIX + color("&c该玩家不在你的队伍中"));
            return;
        }

        if (targetName.equals(leaderName)) {
            leader.sendMessage(PREFIX + color("&c不能把队长转让给自己"));
            return;
        }

        team.leader = targetName;

        for (String memberName : team.members) {
            Player p = leader.getServer().getPlayer(memberName);
            if (p != null) {
                p.sendMessage(PREFIX + color("&e队伍队长已由 &a" + leaderName +
                        " &e转让给 &a" + targetName));
            }
        }
    }

    /* ============================================================
     * 发送邀请：sender 邀请 target 进入 sender 的队伍
     * ============================================================ */
    public static void invite(Player sender, Player target) {
        String senderName = sender.getName();
        String targetName = target.getName();

        Team senderTeam = getTeam(senderName);

        // 若发送者已有队伍 → 检查人数是否已满
        if (senderTeam != null && senderTeam.members.size() >= teamMax) {
            sender.sendMessage(PREFIX + color("&c你的队伍人数已达上限 " + teamMax));
            return;
        }

        if (senderName.equals(requestMap.get(targetName))) {
            sender.sendMessage(PREFIX + color("&c你已经邀请过该玩家"));
            return;
        }

        requestMap.put(targetName, senderName);

        sender.sendMessage(PREFIX + color("&a邀请已发送"));
        target.sendMessage(PREFIX + color("&e你收到来自 &a" + senderName +
                " &e的组队邀请，输入 &b/team accept " + senderName + " &e加入队伍"));
    }

    /* ============================================================
     * 申请加入：sender 申请加入 target 的队伍
     * ============================================================ */
    public static void join(Player sender, Player target) {
        String senderName = sender.getName();
        String targetName = target.getName();

        Team targetTeam = getTeam(targetName);

        // 若目标已有队伍 → 检查人数上限
        if (targetTeam != null && targetTeam.members.size() >= teamMax) {
            sender.sendMessage(PREFIX + color("&c对方队伍人数已达上限 " + teamMax));
            return;
        }

        if (senderName.equals(requestMap.get(targetName))) {
            sender.sendMessage(PREFIX + color("&c你已发送过加入申请"));
            return;
        }

        requestMap.put(targetName, senderName);

        sender.sendMessage(PREFIX + color("&a入队申请已发送"));
        target.sendMessage(PREFIX + color("&e你收到来自 &a" + senderName +
                " &e的入队申请，输入 &b/team accept " + senderName));
    }

    /* ============================================================
     * 接受请求
     * ============================================================ */
    public static void accept(Player target, Player sender) {
        String senderName = sender.getName();
        String targetName = target.getName();

        String requester = requestMap.get(targetName);
        if (requester == null || !requester.equals(senderName)) {
            target.sendMessage(PREFIX + color("&c没有来自该玩家的组队请求"));
            return;
        }
        requestMap.remove(targetName);

        Team requesterTeam = getTeam(senderName);
        Team targetTeam = getTeam(targetName);

        // 情况1：双方都没队伍 → 创建队伍
        if (requesterTeam == null && targetTeam == null) {
            createTeam(senderName, targetName);
            return;
        }

        // 情况2：目标无队伍 → 加入邀请方队伍
        if (requesterTeam != null && targetTeam == null) {

            if (requesterTeam.members.size() >= teamMax) {
                target.sendMessage(PREFIX + color("&c对方队伍人数已达上限 " + teamMax));
                return;
            }

            addPlayerToTeam(requesterTeam, targetName);
            return;
        }

        // 情况3：sender 无队伍 → sender 加入 target 队伍
        if (requesterTeam == null && targetTeam != null) {

            if (targetTeam.members.size() >= teamMax) {
                target.sendMessage(PREFIX + color("&c你的队伍人数已达上限 " + teamMax));
                return;
            }

            addPlayerToTeam(targetTeam, senderName);
            return;
        }

        target.sendMessage(PREFIX + color("&c双方都有队伍，无法加入"));
    }

    /* ============================================================
     * 退出队伍
     * ============================================================ */
    public static void quit(Player player) {
        String name = player.getName();
        Team t = getTeam(name);

        if (t == null) {
            player.sendMessage(PREFIX + color("&c你没有在队伍中"));
            return;
        }

        if (t.leader.equals(name)) {
            t.members.remove(name);
            if (t.members.isEmpty()) {
                disbandTeam(t, name);
                player.sendMessage(PREFIX + color("&c你退出并解散了队伍"));
            } else {
                String newLeader = t.members.iterator().next();
                t.leader = newLeader;
                playerTeamMap.remove(name);
                player.sendMessage(PREFIX + color("&e你已退出队伍，新队长为 " + newLeader));
            }
        } else {
            t.members.remove(name);
            playerTeamMap.remove(name);
            player.sendMessage(PREFIX + color("&c你已退出队伍"));
        }
    }

    /* ============================================================
     * 队伍管理
     * ============================================================ */
    private static void createTeam(String leader, String other) {
        UUID id = UUID.randomUUID();
        Team t = new Team();
        t.leader = leader;
        t.members.add(leader);
        t.members.add(other);

        teamMap.put(id, t);
        playerTeamMap.put(leader, id);
        playerTeamMap.put(other, id);
    }

    private static void addPlayerToTeam(Team t, String player) {
        t.members.add(player);

        UUID id = null;
        for (Map.Entry<UUID, Team> e : teamMap.entrySet()) {
            if (e.getValue() == t) {
                id = e.getKey();
                break;
            }
        }

        if (id != null) {
            playerTeamMap.put(player, id);
        }
    }

    private static void disbandTeam(Team t, String leader) {
        UUID removeId = null;

        for (Map.Entry<UUID, Team> e : teamMap.entrySet()) {
            if (e.getValue() == t) {
                removeId = e.getKey();
                break;
            }
        }

        if (removeId != null) {
            for (String m : t.members) {
                playerTeamMap.remove(m);
            }
            playerTeamMap.remove(leader);
            teamMap.remove(removeId);
        }
    }
}
