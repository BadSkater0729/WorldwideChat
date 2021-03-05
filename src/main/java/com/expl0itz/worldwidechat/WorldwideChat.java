package com.expl0itz.worldwidechat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.expl0itz.worldwidechat.commands.WWCGlobal;
import com.expl0itz.worldwidechat.commands.WWCReload;
import com.expl0itz.worldwidechat.commands.WWCTranslate;
import com.expl0itz.worldwidechat.configuration.WWCConfigurationHandler;
import com.expl0itz.worldwidechat.listeners.WWCChatListener;
import com.expl0itz.worldwidechat.misc.WWCActiveTranslator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class WorldwideChat extends JavaPlugin {
    /* Managers */
    private Set < BukkitTask > backgroundTasks = new HashSet < BukkitTask > ();
    private WWCConfigurationHandler configurationManager;

    /* Vars */
    private double pluginVersion = 1.0;

    private boolean enablebStats = true;

    private String pluginPrefixString = "WWC";
    private String pluginLang = "en";
    private String translatorName = "Watson";

    private ArrayList < WWCActiveTranslator > activeTranslators = new ArrayList < WWCActiveTranslator > ();

    /*Little bug about text components as of adventure 4.5.1:
     * If you do not use a NamedTextColor as your first color (ex: hex), the output will
     * be garbled with some annoying variables. We used the MC dark red to "get around"
     * this. Even though it's more of a good alternative solution now, keep this in mind if
     * this is still not patched + you start with a hex color.
     * */
    private TextComponent pluginPrefix = Component.text()
        .content("[").color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
        .append(Component.text().content(pluginPrefixString).color(TextColor.color(0x5757c4)))
        .append(Component.text().content("]").color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true))
        .build();

    /* Methods */
    @Override
    public void onEnable() {
        boolean settingsSetSuccessfully;
        try {
            //Load main config + other configs
            configurationManager = new WWCConfigurationHandler(this);
            configurationManager.initMainConfig(); //this loads our language; load messages.yml immediately after this
            configurationManager.initMessagesConfig(); //messages.yml, other configs
            settingsSetSuccessfully = configurationManager.loadMainSettings(); //main config.yml settings
        } catch (Exception exception) {
            //Probably bad credentials
            getLogger().severe(ChatColor.RED + getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConnectionFailed").replace("%o", translatorName));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (settingsSetSuccessfully) { //If all settings don't error out
            getLogger().info(ChatColor.LIGHT_PURPLE + getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConnectionSuccess").replace("%o", translatorName));
            
            //EventHandlers
            getServer().getPluginManager().registerEvents(new WWCChatListener(this), this);
            getLogger().info(ChatColor.LIGHT_PURPLE + getConfigManager().getMessagesConfig().getString("Messages.wwcListenersInitialized"));

            //Check for Updates
            //TODO

            //We made it!
             getLogger().info(ChatColor.GREEN + "Enabled WorldwideChat version " + pluginVersion + ".");
        }
    }

    @Override
    public void onDisable() {
        //Cleanly cancel/reset all background tasks (runnables, timers, vars, etc.)
        cancelBackgroundTasks();

        //Null static vars

        //All done.
        getLogger().info("Disabled WorldwideChat version " + pluginVersion + ".");
    }

    public boolean reloadWWC() {
        //Cancel all background tasks
        cancelBackgroundTasks();

        //Reload main config + other configs
        boolean settingsSetSuccessfully;
        try {
            configurationManager = new WWCConfigurationHandler(this);
            configurationManager.initMainConfig();
            configurationManager.initMessagesConfig();
            settingsSetSuccessfully = configurationManager.loadMainSettings();
        } catch (Exception exception) { //Connection failed, probably; specify this if config gets more complex
            getLogger().severe(ChatColor.RED + getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConnectionFailed").replace("%o", translatorName));
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        if (settingsSetSuccessfully) { //If all settings don't error out
            getLogger().info(ChatColor.LIGHT_PURPLE + getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConnectionSuccess").replace("%o", translatorName));
            
            //Check for Updates
            //TODO

            //Done
            return true;
        }
        return false;
    }

    public void cancelBackgroundTasks() {
        //Clear all active translators
        activeTranslators.clear();

        for (BukkitTask task: backgroundTasks) {
            //ask active tasks if they are active; let them finish + cancel?
            //TODO
            //task.cancel();
        }
    }

    //Init all commands
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wwc")) {
            //Me fucking with adventure for the first time, cool
            final TextComponent versionNotice = Component.text()
                .append(pluginPrefix.asComponent())
                .append(Component.text().content(getConfigManager().getMessagesConfig().getString("Messages.wwcVersion")).color(NamedTextColor.RED))
                .append(Component.text().content(pluginVersion + "").color(NamedTextColor.LIGHT_PURPLE))
                .build();
            sender.sendMessage((versionNotice));
        } else if (command.getName().equalsIgnoreCase("wwcr")) {
            //Reload command
            WWCReload wwcr = new WWCReload(sender, command, label, args, this);
            return wwcr.processCommand();
        } else if (command.getName().equalsIgnoreCase("wwcg")) {
            //TODO: Global Translation
            if (checkSenderIdentity(sender)) {
                WWCGlobal wwcg = new WWCGlobal(sender, command, label, args, this);
                return wwcg.processCommand();
            }
        } else if (command.getName().equalsIgnoreCase("wwct")) {
            //Translate to a specific language for one player
            if (checkSenderIdentity(sender)) {
                WWCTranslate wwct = new WWCTranslate(sender, command, label, args, this);
                return wwct.processCommand(false);
            }
        }
        return true;
    }

    /* Setters */
    public void addActiveTranslator(WWCActiveTranslator i) {
        activeTranslators.add(i);
    }

    public void removeActiveTranslator(WWCActiveTranslator i) {
        for (Iterator < WWCActiveTranslator > aList = activeTranslators.iterator(); aList.hasNext();) {
            WWCActiveTranslator activeTranslators = aList.next();
            if (activeTranslators == i) {
                aList.remove();
            }
        }
    }

    public void setPrefixName(String i) {
        pluginPrefixString = i;
    }

    public void setPluginLang(String i) {
        pluginLang = i;
    }

    public void setbStats(boolean i) {
        enablebStats = i;
    }

    public void setTranslatorName(String i) {
        translatorName = i;
    }

    /* Getters */
    public WWCActiveTranslator getActiveTranslator(String uuid) {
        if (activeTranslators.size() > 0) //just return false if there are no active translators, less code to run
        {
            for (WWCActiveTranslator eaTranslator: activeTranslators) {
                if (eaTranslator.getUUID().equals(uuid)) //if uuid matches up with one in ArrayList, we chillin'
                {
                    return eaTranslator;
                }
            }
        }
        return null;
    }

    public ArrayList < WWCActiveTranslator > getActiveTranslators() {
        return activeTranslators;
    }

    public TextComponent getPluginPrefix() {
        return pluginPrefix;
    }

    public String getPluginLang() {
        return pluginLang;
    }

    public String getPrefixName() {
        return pluginPrefixString;
    }

    public String getTranslatorName() {
        return translatorName;
    }

    public boolean getbStats() {
        return enablebStats;
    }

    public WWCConfigurationHandler getConfigManager() {
        return configurationManager;
    }

    /* Common Methods */
    public boolean checkSenderIdentity(CommandSender sender) {
        if (!(sender instanceof Player)) {
            final TextComponent consoleNotice = Component.text()
                .append(pluginPrefix.asComponent())
                .append(Component.text().content(getConfigManager().getMessagesConfig().getString("Messages.wwcNoConsole")).color(NamedTextColor.RED))
                .build();
            sender.sendMessage(consoleNotice);
            return false;
        }
        return true;
    }
}