package host.enxada.homeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final HomePlugin plugin;

    public SetHomeCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player jogador = (Player) sender;
            if (args.length > 0) {
                String nomeDaHome = args[0];

                // Validação do nome da home
                if (nomeDaHome.length() > 32) {
                    jogador.sendMessage("O nome da home não pode ter mais de 32 caracteres.");
                    return false;
                }

                try {
                    plugin.getHomeManager().setHome(jogador.getUniqueId(), nomeDaHome, jogador.getLocation());
                    jogador.sendMessage("Home " + nomeDaHome + " definida!");
                } catch (Exception e) {
                    jogador.sendMessage("Ocorreu um erro ao definir a home. Tente novamente.");
                    e.printStackTrace();
                }
                return true;
            } else {
                jogador.sendMessage("Uso: /sethome <nome>");
                return false;
            }
        } else {
            sender.sendMessage("Este comando só pode ser executado por um jogador.");
            return false;
        }
    }
}
