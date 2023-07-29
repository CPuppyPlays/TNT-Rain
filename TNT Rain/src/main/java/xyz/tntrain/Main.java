package xyz.tntrain;

import Commands.TNTRainCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    @Override
    public void onEnable() {

        //Register tnt rain command
        TNTRainCommand TNTRainCommand = new TNTRainCommand();
        getCommand("tntrain").setExecutor(TNTRainCommand);
        getCommand("tntrain").setTabCompleter(TNTRainCommand);

        //Announcing plugin is online
        getLogger().info("TNT Rain has been enabled");
        Main.instance = this;
    }

    @Override
    public void onDisable() {

    }

    public static Main getInstance() {
        return Main.instance;
    }
}
