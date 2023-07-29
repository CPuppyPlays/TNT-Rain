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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TNTRainCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Random random = new Random();

    private World world;
    private double x1;
    private double z1;
    private int range;
    private int numtnt;


    private BukkitTask runnable;


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;

        //Permission check
        if(!commandSender.hasPermission("tntrain.use")) {
            player.sendMessage(miniMessage.deserialize("<red>You do not have permission to use this command!</red>"));
            return true;
        }

        if(args.length < 1) {
            player.sendMessage(miniMessage.deserialize(("<red>Invalid Command Arguments, /tntrain <start|stop>")));
            return true;
        }

        switch(args[0]) {
            case "start" -> {
                if(runnable != null) {
                    player.sendMessage(miniMessage.deserialize(("<red>TNT Rain is already happening at "+ x1+", " +z1)));
                    return true;
                }
                if(args.length < 5) {
                    player.sendMessage(miniMessage.deserialize(("<red>Invalid Command Arguments, /tntrain start <center-x> <center-z> <range> <num-tnt>")));
                    return true;
                }

                try {
                    x1 = Integer.parseInt(args[1]);
                    z1 = Integer.parseInt(args[2]);
                    range = Integer.parseInt(args[3]);
                    numtnt = Integer.parseInt(args[4]);
                    world = player.getWorld();

                    runnable = new BukkitRunnable() {
                        @Override
                        public void run() {

                            for(int i = 0; i < numtnt; i++) {
                                int x = (int) (x1 + random.nextInt(range * 2) - range);
                                int z = (int) (z1 + random.nextInt(range * 2) - range);
                                int y = world.getHighestBlockYAt(x, z) + 20;

                                Location location = new Location(world, x, y, z);
                                world.spawn(location, TNTPrimed.class);

                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20);

                    player.sendMessage(miniMessage.deserialize(("<green>TNT rain has successfully started")));
                } catch(Exception e) {
                    player.sendMessage(miniMessage.deserialize(("<red>One of the arguments are invalid, /tntrain start <center-x> <center-z> <range> <num-tnt>")));
                    //e.printStackTrace(); debugger for testing if args are correct
                    return true;
                }
            }
            case "stop" -> {
                if(runnable == null) {
                    player.sendMessage(miniMessage.deserialize(("<red>TNT Rain is already stopped")));
                    return true;
                }
                runnable.cancel();
                player.sendMessage(miniMessage.deserialize(("<green>You have stopped the TNT Rain")));
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
                return new ArrayList<>(List.of("1", "8", "32", "64"));
            } else if(args.length == 5) {
                return new ArrayList<>(List.of("1", "8", "32", "64"));
            }
        }
        return new ArrayList<>();
    }
}
