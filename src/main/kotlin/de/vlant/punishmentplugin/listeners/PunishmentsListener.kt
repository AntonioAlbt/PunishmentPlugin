package de.vlant.punishmentplugin.listeners

import de.vlant.punishmentplugin.PunishmentPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.block.DoubleChest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*

class PunishmentsListener(_plugin: PunishmentPlugin) : Listener {
    private val plugin = _plugin

    @EventHandler
    fun onPlayerMove(playerMoveEvent: PlayerMoveEvent) {
        if (plugin.players[playerMoveEvent.player.uniqueId]?.contains(PunishmentPlugin.Punishment.CANT_WALK) == true) {
            if (playerMoveEvent.to.posEquals(playerMoveEvent.from)) return
            else playerMoveEvent.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerChat(playerChatEvent: AsyncPlayerChatEvent) {
        if (plugin.players[playerChatEvent.player.uniqueId]?.contains(PunishmentPlugin.Punishment.CANT_TALK) == true) {
            playerChatEvent.isCancelled = true
            playerChatEvent.player.sendMessage("${ChatColor.DARK_RED}You can't talk right now.")
        }
    }

    @EventHandler
    fun onEntityDamage(entityDamageByEntityEvent: EntityDamageByEntityEvent) {
        val damager = entityDamageByEntityEvent.damager
        if (damager is Player && entityDamageByEntityEvent.entity is Player) {
            if (plugin.players[damager.uniqueId]?.contains(PunishmentPlugin.Punishment.CANT_PVP) == true) {
                entityDamageByEntityEvent.isCancelled = true
                damager.sendMessage("${ChatColor.DARK_RED}You can't pvp right now.")
            }
        }
    }

    @EventHandler
    fun onStorageOpen(inventoryOpenEvent: InventoryOpenEvent) {
        val holder = inventoryOpenEvent.inventory.holder
        if (holder is Container) {
            if (plugin.players[inventoryOpenEvent.player.uniqueId]?.contains(PunishmentPlugin.Punishment.CANT_OPEN_STORAGE) == true) {
                inventoryOpenEvent.isCancelled = true
                inventoryOpenEvent.player.sendMessage("${ChatColor.DARK_RED}You can't open storage right now.")
            }
        }
    }
}

private fun Location?.posEquals(from: Location): Boolean {
    if (this == null) return false
    return (from.x == this.x) && (from.z == this.z)
}
