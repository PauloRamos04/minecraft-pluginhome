package host.enxada.homeplugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final HomePlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HomeCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player jogador = (Player) sender;
            if (args.length > 0) {
                String nomeDaHome = args[0];

                long cooldown = plugin.getConfig().getInt("teleportCooldown") * 1000;
                long ultimaUso = cooldowns.getOrDefault(jogador.getUniqueId(), 0L);
                long tempoDesdeUltimoUso = System.currentTimeMillis() - ultimaUso;

                if (tempoDesdeUltimoUso < cooldown) {
                    jogador.sendMessage("Você deve esperar " + (cooldown - tempoDesdeUltimoUso) / 1000 + " segundos para usar este comando novamente.");
                    return true;
                }

                cooldowns.put(jogador.getUniqueId(), System.currentTimeMillis());

                BukkitRunnable tarefa = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location home = plugin.getHomeManager().getHome(jogador.getUniqueId(), nomeDaHome);
                        if (home != null) {
                            if (plugin.getConfig().getBoolean("showParticles", true)) {
                                home.getWorld().spawnParticle(Particle.PORTAL, home, 50);
                            }
                            jogador.teleport(home);
                            jogador.sendMessage("Teleportado para a home " + nomeDaHome + "!");
                        } else {
                            jogador.sendMessage("Home " + nomeDaHome + " não encontrada.");
                        }
                    }
                };

                tarefa.runTaskLater(plugin, 20L);  // Atraso para simular o efeito de teleportação
                return true;
            } else {
                jogador.sendMessage("Uso: /home <nome>");
                return false;
            }
        } else {
            sender.sendMessage("Este comando só pode ser executado por um jogador.");
            return false;
        }
    }
}
