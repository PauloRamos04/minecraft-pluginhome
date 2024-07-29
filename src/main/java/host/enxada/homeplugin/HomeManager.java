package host.enxada.homeplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class HomeManager {

    private final HomePlugin plugin;
    private Connection connection;

    public HomeManager(HomePlugin plugin) {
        this.plugin = plugin;
        connectDatabase();
    }

    private void connectDatabase() {
        FileConfiguration config = plugin.getConfig();
        String url = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        try {
            connection = DriverManager.getConnection(url, user, password);
            Bukkit.getLogger().info("Conexão com o banco de dados estabelecida com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Falha ao conectar ao banco de dados!");
        }
    }

    public void setHome(UUID playerUUID, String homeName, Location location) {
        String sql = "REPLACE INTO homes (player_uuid, home_name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, homeName);
            stmt.setString(3, location.getWorld().getName());
            stmt.setDouble(4, location.getX());
            stmt.setDouble(5, location.getY());
            stmt.setDouble(6, location.getZ());
            stmt.setFloat(7, location.getYaw());
            stmt.setFloat(8, location.getPitch());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getHome(UUID playerUUID, String homeName) {
        String sql = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, homeName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String world = rs.getString("world");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");
                return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
