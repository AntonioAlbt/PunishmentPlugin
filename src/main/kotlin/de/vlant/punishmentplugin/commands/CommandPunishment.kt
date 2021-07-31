package de.vlant.punishmentplugin.commands

import de.vlant.punishmentplugin.PunishmentPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPunishment(_plugin: PunishmentPlugin) : CommandExecutor {
    private val plugin: PunishmentPlugin = _plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "punishments") {
            if (args.isNotEmpty()) {
                when (args[0]) {
                    "list" -> {
                        if (args.count() == 1) {
                            if (sender is Player) {
                                val punishments = plugin.players[sender]
                                if (punishments != null) {
                                    sender.sendMessage("${ChatColor.AQUA}Punishments for ${ChatColor.GOLD}" + sender.name + ":"
                                            + (if (punishments.isEmpty()) "${ChatColor.RESET}\nnone" else ""))
                                    for (punishment in punishments) {
                                        sender.sendMessage(punishment.toString())
                                    }
                                }
                            } else {
                                sender.sendMessage("This can only be executed as a player!")
                            }
                        } else {
                            val player = Bukkit.getOnlinePlayers().find {value -> value.name == args[1] }
                            if (player != null) {
                                val punishments = plugin.players[player]
                                if (punishments != null) {
                                    sender.sendMessage("${ChatColor.AQUA}Punishments for ${ChatColor.GOLD}" + player.name + ":"
                                            + (if (punishments.isEmpty()) "${ChatColor.RESET}\nnone" else ""))
                                    for (punishment in punishments) {
                                        sender.sendMessage(punishment.toString())
                                    }
                                }
                            }
                        }
                    }
                    "help" -> {
                        val help = ArrayList<String>()
                        help.add("Help for PunishmentPlugin:")
                        if (sender.hasPermission("punishments.manage")) {
                            help.add("/${command.name} set <player> <punishment> <true|false>: Set punishment for player.")
                            help.add("/${command.name} unset <player> <punishment>: Reset punishment for player to default.")
                        }
                        if (sender.hasPermission("punishments.get.everyone")) {
                            help.add("/${command.name} list [player]: List punishments for player.")
                        } else if (sender.hasPermission("punishments.get.own")) {
                            help.add("/${command.name} list: Display own punishments.")
                        }
                        help.add("/${command.name} version: Display plugin version.")
                        help.add("/${command.name} help: Display this help.")
                        for (msg in help) sender.sendMessage(msg)
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