@file:Suppress("unused")

package de.vlant.punishmentplugin

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class PunishmentPlugin : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        Bukkit.broadcastMessage("Hi from PunishmentPlugin")
        config.addDefault("output-when-loaded", "Hi from PunishmentPlugin!")
        config.addDefault("players.default.punishments.can-move", true)
        config.addDefault("players.default.punishments.can-pvp", true)
        config.addDefault("players.default.punishments.can-open-chests", true)
        config.addDefault("players.default.punishments.can-join", true)
        config.addDefault("players.default.punishments.takes-damage.enabled", false)
        config.addDefault("players.default.punishments.takes-damage.damage-per-second", 0.5)
        config.options().copyDefaults(true)
        saveConfig()
        logger.info(config.getString("output-when-loaded"))
    }
}