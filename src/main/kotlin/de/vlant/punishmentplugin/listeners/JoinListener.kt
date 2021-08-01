package de.vlant.punishmentplugin.listeners

import de.vlant.punishmentplugin.PunishmentPlugin
import de.vlant.punishmentplugin.applyPunishments
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(_plugin: PunishmentPlugin) : Listener {
    private val plugin = _plugin

    @EventHandler
    fun onPlayerJoin(playerJoinEvent: PlayerJoinEvent) {
        val pp = plugin.players[playerJoinEvent.player.uniqueId]
        if (pp != null)
            playerJoinEvent.player.applyPunishments(pp, plugin)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerPreLogin(asyncPlayerPreLoginEvent: AsyncPlayerPreLoginEvent) {
        plugin.loadForPlayer(uuid = asyncPlayerPreLoginEvent.uniqueId)
        if (plugin.players[asyncPlayerPreLoginEvent.uniqueId]?.contains(PunishmentPlugin.Punishment.CANT_JOIN) == true) {
            asyncPlayerPreLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "${ChatColor.DARK_RED}You are not allowed to join.")
        }
    }
}