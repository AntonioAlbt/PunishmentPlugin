package de.vlant.punishmentplugin.commands

import de.vlant.punishmentplugin.Permissions
import de.vlant.punishmentplugin.PunishmentPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPunishment(_plugin: PunishmentPlugin) : CommandExecutor {
    private val plugin: PunishmentPlugin = _plugin

    private val possiblePunishments = arrayListOf("can-walk", "can-join", "can-pvp", "can-open-storage", "constant-damage", "can-talk")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "punishments") {
            if (!sender.hasPermission(Permissions.punishments)) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                return true
            }
            if (args.isNotEmpty()) {
                when (args[0]) {
                    "set" -> {
                        if (!sender.hasPermission(Permissions.manage)) {
                            sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                            return true
                        }
                        if (args.count() == 4) {
                            var player: OfflinePlayer? = null
                            if (args[1] != "default") player = Bukkit.getOfflinePlayer(args[1])
                            if ((player != null && player.name != null) || args[1] == "default") {
                                var punishmntsPath = "players.default.punishments"
                                if (player != null) punishmntsPath = "players." + player.uniqueId.toString() + ".punishments"
                                if (possiblePunishments.contains(args[2])) {
                                    plugin.config.set(
                                        "$punishmntsPath.${args[2]}", when (args[3]) {
                                            "true" -> true
                                            "false" -> false
                                            else -> {
                                                sender.sendMessage(
                                                    "${ChatColor.RED}Not a valid value! Has to be ${ChatColor.BOLD}${ChatColor.WHITE}true " +
                                                            "${ChatColor.RESET}${ChatColor.RED}or ${ChatColor.BOLD}${ChatColor.WHITE}false${ChatColor.RESET}${ChatColor.RED}."
                                                )
                                                return true
                                            }
                                        }
                                    )
                                } else {
                                    sender.sendMessage("${ChatColor.RED}Punishment not found!")
                                    return true
                                }
                                plugin.saveConfig()
                                val onlinePlayer = player?.player
                                if (onlinePlayer != null) plugin.loadForPlayer(onlinePlayer)
                                sender.sendMessage("${ChatColor.AQUA}Changed punishment ${ChatColor.WHITE}\"${args[2]}\" " +
                                        "${ChatColor.AQUA}for ${ChatColor.DARK_AQUA}${player?.name ?: "default"}" +
                                        "${ChatColor.AQUA} to ${ChatColor.WHITE}${args[3]}${ChatColor.AQUA}.")
                            } else {
                                sender.sendMessage("${ChatColor.RED}Player not found!")
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Not enough or too much arguments!")
                        }
                    }
                    "unset" -> {
                        if (!sender.hasPermission(Permissions.manage)) {
                            sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                            return true
                        }
                        if (args.count() == 3) {
                            if (args[1] == "default") {
                                sender.sendMessage("${ChatColor.RED}You can't unset the default values!")
                                return true
                            }
                            val player = Bukkit.getOfflinePlayer(args[1])
                            if (player.name != null) {
                                val punishmntsPath = "players." + player.uniqueId.toString() + ".punishments"
                                if (possiblePunishments.contains(args[2])) {
                                    plugin.config.set("$punishmntsPath.${args[2]}", null)
                                } else {
                                    sender.sendMessage("${ChatColor.RED}Punishment not found!")
                                    return true
                                }
                                plugin.saveConfig()
                                val onlinePlayer = player.player
                                if (onlinePlayer != null) plugin.loadForPlayer(onlinePlayer)
                                sender.sendMessage("${ChatColor.AQUA}Reset punishment ${ChatColor.WHITE}\"${args[2]}\" " +
                                        "${ChatColor.AQUA}for ${ChatColor.DARK_AQUA}${player.name ?: "default"}${ChatColor.AQUA}.")
                            } else {
                                sender.sendMessage("${ChatColor.RED}Player not found!")
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Not enough or too much arguments!")
                        }
                    }
                    "list" -> {
                        if (args.count() == 1) {
                            if (sender.hasPermission(Permissions.getOwn) || sender.hasPermission(Permissions.getEveryone)) {
                                if (sender is Player) {
                                    val punishments = plugin.players[sender.uniqueId]
                                    if (punishments != null) {
                                        sender.sendMessage(
                                            "${ChatColor.AQUA}Punishments for ${ChatColor.GOLD}" + sender.name + ":"
                                                    + (if (punishments.isEmpty()) "${ChatColor.RESET} none" else "")
                                        )
                                        for (punishment in punishments) {
                                            sender.sendMessage("- " + punishment.toInfoString())
                                        }
                                    }
                                } else {
                                    sender.sendMessage("This can only be executed as a player!")
                                }
                            } else {
                                sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                            }
                        } else if (args.count() == 2) {
                            if (sender.hasPermission(Permissions.getEveryone)) {
                                if (args[1] != "default") {
                                    val player = Bukkit.getOfflinePlayer(args[1])
                                    if (player.name != null) {
                                        val punishments = plugin.loadForPlayer(uuid = player.uniqueId)
                                        sender.sendMessage(
                                            "${ChatColor.AQUA}Punishments for ${ChatColor.GOLD}" + player.name + ":"
                                                    + (if (punishments.isEmpty()) "${ChatColor.RESET} none" else "")
                                        )
                                        for (punishment in punishments) {
                                            sender.sendMessage("- " + punishment.toInfoString())
                                        }
                                    } else {
                                        sender.sendMessage("${ChatColor.RED}Player not found!")
                                    }
                                } else {
                                    val default = plugin.loadForPlayer()
                                    sender.sendMessage(
                                        "${ChatColor.AQUA}Punishments for ${ChatColor.GOLD}default:"
                                                + (if (default.isEmpty()) "${ChatColor.RESET} none" else "")
                                    )
                                    for (punishment in default) {
                                        sender.sendMessage("- " + punishment.toInfoString())
                                    }
                                }
                            } else {
                                sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Not enough or too much arguments!")
                        }
                    }
                    "help" -> {
                        val help = ArrayList<String>()
                        help.add("${ChatColor.UNDERLINE}Help for PunishmentPlugin:")
                        val manage = sender.hasPermission(Permissions.manage)
                        if (manage) {
                            help.add("/${command.name} set <player> <punishment> <true|false>: Set punishment for player.")
                            help.add("/${command.name} unset <player> <punishment>: Reset punishment for player to default.")
                        }
                        if (sender.hasPermission(Permissions.getEveryone)) {
                            help.add("/${command.name} list [player]: List punishments for player.")
                        } else if (sender.hasPermission(Permissions.getOwn)) {
                            help.add("/${command.name} list: Display own punishments.")
                        }
                        if (manage) help.add("/${command.name} reload: Reloads the config.")
                        help.add("/${command.name} version: Display plugin version.")
                        help.add("/${command.name} help: Display this help.")
                        for (msg in help) sender.sendMessage(msg)
                    }
                    "reload" -> {
                        if (sender.hasPermission(Permissions.manage)) {
                            plugin.reloadConfig()
                            for (player in Bukkit.getOnlinePlayers())
                                plugin.loadForPlayer(player)
                            sender.sendMessage("${ChatColor.GREEN}Player punishments reloaded.")
                        } else {
                            sender.sendMessage("${ChatColor.RED}You don't have the permission to do this.")
                        }
                    }
                    "version" -> sender.sendMessage(plugin.description.fullName)
                    else -> sender.sendMessage("" + ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Unknown argument!")
                }
            } else sender.sendMessage("" + ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Not enough arguments!")
            return true
        }
        return false
    }
}