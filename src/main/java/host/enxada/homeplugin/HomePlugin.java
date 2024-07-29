package host.enxada.homeplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class HomePlugin extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Inicializa o gerenciador de homes
        homeManager = new HomeManager(this);

        // Registra os comandos
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("home").setExecutor(new HomeCommand(this));
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
}
