package com.i5mc.recipe.module;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created on 17-1-28.
 */
public class MainCommand extends Command {

    protected MainCommand() {
        super("recipe-module");
    }

    enum Act {
        LIMIT(MainCommand::limit);

        private final CommandExecutor executor;

        Act(CommandExecutor executor) {
            this.executor = executor;
        }
    }

    private static boolean limit(CommandSender sender, Command command, String label, String[] i) {
        if (i.length < 4) {
            sender.sendMessage("/recipe-module limit <player> <recipe> <limit>");
        } else {
            int limit = Integer.valueOf(i[3]);
            if (limit < 1) {
                throw new IllegalArgumentException("limit(" + limit +
                        ") < 1");
            }
            Main.exec(() -> Main.set(i[1], i[2], limit));
            return true;
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] i) {
        if (!sender.hasPermission("recipe.admin")) return false;
        if (i.length == 0) {
            sender.sendMessage("/recipe-module limit <player> <recipe> <limit>");
        } else {
            try {
                Act act = Act.valueOf(i[0].toUpperCase());
                return act.executor.onCommand(sender, this, label, i);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.toString());
            }
        }
        return false;
    }

}
