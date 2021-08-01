@file:Suppress("unused")

package de.vlant.punishmentplugin

import de.vlant.punishmentplugin.commands.CommandPunishment
import de.vlant.punishmentplugin.listeners.JoinListener
import de.vlant.punishmentplugin.listeners.PunishmentsListener
import de.vlant.punishmentplugin.listeners.QuitListener
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.MemorySection
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val damageTasks = HashMap<Player, BukkitTask?>()

private var Player.damageTask: BukkitTask?
    get() {
        return damageTasks[this]
    }
    set(newVal) {
        damageTasks[this] = newVal
    }

class PunishmentPlugin : JavaPlugin() {

    val players: HashMap<UUID, ArrayList<Punishment>> = HashMap()

    val defaultPath: String = "players.default.punishments"

    enum class Punishment {
        CANT_WALK, CANT_JOIN, CANT_OPEN_STORAGE, CANT_PVP, CANT_TALK, CONSTANT_DAMAGE;

        fun toInfoString(): String {
            return when (this) {
                CANT_WALK -> "can't walk"
                CANT_JOIN -> "can't join"
                CANT_OPEN_STORAGE -> "can't open storage"
                CANT_PVP -> "can't pvp"
                CANT_TALK -> "can't talk"
                CONSTANT_DAMAGE -> "takes damage every second"
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        config.addDefault("$defaultPath.can-walk", true)
        config.addDefault("$defaultPath.can-pvp", true)
        config.addDefault("$defaultPath.can-open-storage", true)
        config.addDefault("$defaultPath.can-join", true)
        config.addDefault("$defaultPath.can-talk", true)
        config.addDefault("$defaultPath.constant-damage.enabled", false)
        config.addDefault("$defaultPath.constant-damage.damage-per-interval", 1)
        config.addDefault("$defaultPath.constant-damage.interval-in-seconds", 1)
        config.options().copyDefaults(true)
        saveConfig()
        val punishmentsCommand = getCommand("punishments")
        punishmentsCommand?.setExecutor(CommandPunishment(this))
        punishmentsCommand?.tabCompleter = TabCompleter { commandSender: CommandSender, _: Command, _: String, strings: Array<String> ->
            val completions = ArrayList<String>()
            if (strings.count() == 1) {
                if (commandSender.hasPermission(Permissions.manage)) {
                    completions.add("set")
                    completions.add("unset")
                    completions.add("reload")
                }
                if (commandSender.hasPermission(Permissions.getOwn) || commandSender.hasPermission(Permissions.getEveryone))
                    completions.add("list")
                completions.add("help")
                completions.add("version")
            } else if (strings.count() == 2) {
                when (strings[0]) {
                    "list" -> {
                        if (commandSender.hasPermission(Permissions.getEveryone)) {
                            for (player in Bukkit.getOnlinePlayers())
                                completions.add(player.name)
                            for ((key, _) in (config.get("players") as MemorySection).getValues(false)) {
                                var name: String? = "default"
                                if (key != "default") name = Bukkit.getOfflinePlayer(UUID.fromString(key)).name
                                if (name != null && !completions.contains(name)) completions.add(name)
                            }
                        }
                    }
                    "set", "unset" -> {
                        if (commandSender.hasPermission(Permissions.manage)) {
                            for (player in Bukkit.getOnlinePlayers())
                                completions.add(player.name)
                            for ((key, _) in (config.get("players") as MemorySection).getValues(false)) {
                                var name: String? = "default"
                                if (key != "default") name = Bukkit.getOfflinePlayer(UUID.fromString(key)).name
                                if (name != null && !completions.contains(name) && (if (strings[0] == "unset") name != "default" else true)) completions.add(name)
                            }
                        }
                    }
                }
            } else if (strings.count() == 3 && (strings[0] == "set" || strings[0] == "unset") && commandSender.hasPermission(Permissions.manage)) {
                completions.add("can-walk")
                completions.add("can-open-storage")
                completions.add("can-pvp")
                completions.add("can-join")
                completions.add("can-talk")
                completions.add("constant-damage")
            } else if (strings.count() == 4 && strings[0] == "set" && commandSender.hasPermission(Permissions.manage)) {
                completions.add("true")
                completions.add("false")
            }
            return@TabCompleter completions
        }
        for (player in Bukkit.getOnlinePlayers()) {
            loadForPlayer(player)
        }
        server.pluginManager.registerEvents(JoinListener(this), this)
        server.pluginManager.registerEvents(PunishmentsListener(this), this)
        server.pluginManager.registerEvents(QuitListener(this), this)
    }

    private fun getPlayerProp(player: Player? = null, id: String, uuid: UUID? = null): Boolean? {
        var uniqueID = "default"
        if (player == null && uuid != null) uniqueID = uuid.toString()
        if (player != null) uniqueID = player.uniqueId.toString()
        val prop = config.get("players.$uniqueID.punishments.$id")
        @Suppress("LiftReturnOrAssignment")
        if (prop == null) {
            val defaultProp = config.get("$defaultPath.$id" + (if (id == "constant-damage") ".enabled" else ""))
            return if (defaultProp != null) (defaultProp as Boolean) else null
        } else if (prop is MemorySection) {
            return prop.get("enabled") as Boolean
        } else return prop as Boolean
    }

    fun loadForPlayer(player: Player? = null, uuid: UUID? = null): List<Punishment> {
        val punishments = arrayListOf<Punishment>()

        val canWalk = getPlayerProp(player, "can-walk", uuid)
        val canPvp = getPlayerProp(player, "can-pvp", uuid)
        val canOpenStorage = getPlayerProp(player, "can-open-storage", uuid)
        val canJoin = getPlayerProp(player, "can-join", uuid)
        val constantDamage = getPlayerProp(player, "constant-damage", uuid)
        val canTalk = getPlayerProp(player, "can-talk", uuid)

        if (canWalk == false) punishments.add(Punishment.CANT_WALK)
        if (canPvp == false) punishments.add(Punishment.CANT_PVP)
        if (canOpenStorage == false) punishments.add(Punishment.CANT_OPEN_STORAGE)
        if (canJoin == false) punishments.add(Punishment.CANT_JOIN)
        if (canTalk == false) punishments.add(Punishment.CANT_TALK)
        if (constantDamage == true) punishments.add(Punishment.CONSTANT_DAMAGE)

//        for (p in punishments) Bukkit.broadcastMessage(p.toInfoString())
        player?.applyPunishments(punishments, this)
        val uniqueID = player?.uniqueId ?: uuid
        if (uniqueID != null)
            players[uniqueID] = punishments
        return punishments
    }
}

fun Player.applyPunishments(punishments: List<PunishmentPlugin.Punishment>, plugin: PunishmentPlugin) {
    this.damageTask?.cancel()
    this.damageTask = null
    for (punishment in punishments) {
        when (punishment) {
            PunishmentPlugin.Punishment.CANT_WALK -> {
            }
            PunishmentPlugin.Punishment.CANT_JOIN -> {
            }
            PunishmentPlugin.Punishment.CANT_OPEN_STORAGE -> {
            }
            PunishmentPlugin.Punishment.CANT_PVP -> {
            }
            PunishmentPlugin.Punishment.CANT_TALK -> {
            }
            PunishmentPlugin.Punishment.CONSTANT_DAMAGE -> {
                this.damageTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
                    this.damage((plugin.config.get(plugin.defaultPath + ".constant-damage.damage-per-interval")as Int).toDouble())
                }, 0, (plugin.config.get(plugin.defaultPath + ".constant-damage.interval-in-seconds") as Int).toLong() * 20L)
            }
        }
    }
}

class Permissions {
    companion object {
        const val manage = "punishments.manage"
        const val getOwn = "punishments.get.own"
        const val getEveryone = "punishments.get.everyone"
        const val punishments = "punishments.punishment"
    }
}
