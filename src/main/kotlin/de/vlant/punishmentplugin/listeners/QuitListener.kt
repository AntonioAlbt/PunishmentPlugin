package de.vlant.punishmentplugin.listeners

import de.vlant.punishmentplugin.PunishmentPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitListener(_plugin: PunishmentPlugin) : Listener {
    private val plugin = _plugin

    @EventHandler
    fun onQuit(playerQuitEvent: PlayerQuitEvent) {
        plugin.players.remove(playerQuitEvent.player.uniqueId)
    }
}