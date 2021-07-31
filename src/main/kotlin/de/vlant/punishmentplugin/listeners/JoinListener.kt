package de.vlant.punishmentplugin.listeners

import de.vlant.punishmentplugin.PunishmentPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(_plugin: PunishmentPlugin) : Listener {
    private val plugin = _plugin

    @EventHandler
    fun onPlayerJoin(playerJoinEvent: PlayerJoinEvent) {
        val config = plugin.config
        val player = playerJoinEvent.player
        val punishmntsPath = "players." + player.uniqueId.toString() + ".punishments"
        val punishments = arrayListOf<PunishmentPlugin.Punishment>()

        var canWalk = config.get("$punishmntsPath.can-walk")
        if (canWalk == null) canWalk = config.get("${plugin.defaultPath}.can-walk")
        var canPvp = config.get("$punishmntsPath.can-pvp")
        if (canPvp == null) canPvp = config.get("${plugin.defaultPath}.can-pvp")
        var canOpenStorage = config.get("$punishmntsPath.can-open-storage")
        if (canOpenStorage == null) canOpenStorage = config.get("${plugin.defaultPath}.can-open-storage")
        var canJoin = config.get("$punishmntsPath.can-join")
        if (canJoin == null) canJoin = config.get("${plugin.defaultPath}.can-join")
        var constantDamage = config.get("$punishmntsPath.constant-damage")
        if (constantDamage == null) constantDamage = config.get("${plugin.defaultPath}.constant-damage.enabled")
        var canTalk = config.get("$punishmntsPath.can-talk")
        if (canTalk == null) canTalk = config.get("${plugin.defaultPath}.can-talk")

        if (canWalk == false) punishments.add(PunishmentPlugin.Punishment.CANT_WALK)
        if (canPvp == false) punishments.add(PunishmentPlugin.Punishment.CANT_PVP)
        if (canOpenStorage == false) punishments.add(PunishmentPlugin.Punishment.CANT_OPEN_STORAGE)
        if (canJoin == false) punishments.add(PunishmentPlugin.Punishment.CANT_JOIN)
        if (canTalk == false) punishments.add(PunishmentPlugin.Punishment.CANT_TALK)
        if (constantDamage == true) punishments.add(PunishmentPlugin.Punishment.CONSTANT_DAMAGE)
        plugin.players[player] = punishments
    }
}