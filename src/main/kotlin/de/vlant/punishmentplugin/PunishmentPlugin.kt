@file:Suppress("unused")

package de.vlant.punishmentplugin

import de.vlant.punishmentplugin.commands.CommandPunishment
import de.vlant.punishmentplugin.listeners.JoinListener
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PunishmentPlugin : JavaPlugin() {
    val players: HashMap<Player, List<Punishment>> = HashMap()

    val defaultPath: String = "players.default.punishments"

    enum class Punishment {
        CANT_WALK, CANT_JOIN, CANT_OPEN_STORAGE, CANT_PVP, CANT_TALK, CONSTANT_DAMAGE
    }

    override fun onEnable() {
        super.onEnable()
        config.addDefault("$defaultPath.can-walk", true)
        config.addDefault("$defaultPath.can-pvp", true)
        config.addDefault("$defaultPath.can-open-storage", true)
        config.addDefault("$defaultPath.can-join", true)
        config.addDefault("$defaultPath.can-talk", true)
        config.addDefault("$defaultPath.constant-damage.enabled", false)
        config.addDefault("$defaultPath.constant-damage.damage-per-second", 1)
        config.options().copyDefaults(true)
        saveConfig()
        val punishmentsCommand = getCommand("punishments")
        punishmentsCommand?.setExecutor(CommandPunishment(this))
        punishmentsCommand?.tabCompleter = TabCompleter { commandSender: CommandSender, _: Command, _: String, strings: Array<String> ->
            val completions = ArrayList<String>()
            if (strings.count() == 1) {
                if (commandSender.hasPermission("punishments.manage")) {
                    completions.add("set")
                    completions.add("unset")
                }
                completions.add("list")
                completions.add("help")
                completions.add("version")
            } else if (strings.count() == 2) {
                when (strings[0]) {
                    "list" -> {
                        if (commandSender.hasPermission("punishments.get.everyone"))
                            for (player in Bukkit.getOnlinePlayers())
                                completions.add(player.name)
                    }
                    "set", "unset" -> {
                        if (commandSender.hasPermission("punishments.manage"))
                            for (player in Bukkit.getOnlinePlayers())
                                completions.add(player.name)
                    }
                }
            } else if (strings.count() == 3 && (strings[0] == "set" || strings[0] == "unset")) {
                completions.add("can-walk")
                completions.add("can-open-storage")
                completions.add("can-pvp")
                completions.add("can-join")
                completions.add("constant-damage")
            } else if (strings.count() == 4 && strings[0] == "set") {
                completions.add("true")
                completions.add("false")
            }
            return@TabCompleter completions
        }
        for (player in Bukkit.getOnlinePlayers()) {
            val punishmntsPath = "players." + player.uniqueId.toString() + ".punishments"
            val punishments = arrayListOf<Punishment>()
            if (config.get("$punishmntsPath.can-walk") == true) punishments.add(Punishment.CANT_WALK)
            if (config.get("$punishmntsPath.can-pvp") == true) punishments.add(Punishment.CANT_PVP)
            if (config.get("$punishmntsPath.can-open-storage") == true) punishments.add(Punishment.CANT_OPEN_STORAGE)
            if (config.get("$punishmntsPath.can-join") == true) punishments.add(Punishment.CANT_JOIN)
            if (config.get("$punishmntsPath.constant-damage") == true) punishments.add(Punishment.CONSTANT_DAMAGE)
            players[player] = punishments
        }
        server.pluginManager.registerEvents(JoinListener(this), this)
    }
}