package Commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.tntrain.Main;
import xyz.tntrain.Vars;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TNTRainCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Random random = new Random();

    private final ArrayList<BukkitTask> activeTNTRains = new ArrayList<>();


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;

        //Permission check
        if(!commandSender.hasPermission("tntrain.use")) {
            player.sendMessage(miniMessage.deserialize(Vars.Prefix+"<red>You do not have permission to use this command!"));
            return true;
        }

        if(args.length < 1) {
            player.sendMessage(miniMessage.deserialize((Vars.Prefix+"Invalid Command Arguments, /tntrain <start|stop>")));
            return true;
        }

        switch(args[0]) {
            case "start" -> {
                if(args.length < 5) {
                    player.sendMessage(miniMessage.deserialize((Vars.Prefix+"Invalid Command Arguments, /tntrain start <center-x> <center-z> <range> <num-tnt>")));
                    return true;
                }

                try {
                    final double x1 = Integer.parseInt(args[1]);
                    final double z1 = Integer.parseInt(args[2]);
                    final int range = Integer.parseInt(args[3]);
                    final int numtnt = Integer.parseInt(args[4]);
                    activeTNTRains.add((new BukkitRunnable() {
                        @Override
                        public void run() {


                            World world = player.getWorld();

                            for(int i = 0; i < numtnt; i++) {
                                int x = (int) (x1 + random.nextInt(range * 2) - range);
                                int z = (int) (z1 + random.nextInt(range * 2) - range);
                                int y = world.getHighestBlockYAt(x, z) + 20;

                                Location location = new Location(world, x, y, z);
                                world.spawn(location, TNTPrimed.class);

                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20)));

                    player.sendMessage(miniMessage.deserialize((Vars.Prefix+"You have a created a TNT Rain with the settings "+Vars.PC+x1+", "+z1 + " | " + range+" blocks | " + numtnt+" TNT/s")));
                } catch(Exception e) {
                    player.sendMessage(miniMessage.deserialize((Vars.Prefix+"One of the arguments are <red>invalid<reset>, /tntrain start <center-x> <center-z> <range> <num-tnt>")));
                    e.printStackTrace();
                    return true;
                }
            }
            case "stop" -> {
                if(activeTNTRains.size() == 0) {
                    player.sendMessage(miniMessage.deserialize((Vars.Prefix+"There are no active TNT Rains!")));
                    return true;
                }
                for(BukkitTask task : activeTNTRains) {
                    task.cancel();
                }

                player.sendMessage(miniMessage.deserialize((Vars.Prefix+"You have stopped "+Vars.PC+activeTNTRains.size()+" <gray>TNT Rain(s)")));
                activeTNTRains.clear();
            }

        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        //Auto Completer
        if(args.length == 1) {
            return new ArrayList<>(List.of("start", "stop"));
        } else if(args[0].equals("start")) {
            if(args.length == 2) {
                return new ArrayList<>(List.of(String.valueOf((player.getLocation().getBlockX()))));
            } else if(args.length == 3) {
                return new ArrayList<>(List.of(String.valueOf((player.getLocation().getBlockZ()))));
            } else if(args.length == 4) {
                return new ArrayList<>(List.of("1", "3", "8", "10"));
            } else if(args.length == 5) {
                return new ArrayList<>(List.of("1", "3", "8", "10"));
            }
        }
        return new ArrayList<>();
    }
}
