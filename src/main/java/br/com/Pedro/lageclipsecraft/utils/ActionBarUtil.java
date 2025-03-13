package br.com.Pedro.lageclipsecraft.utils;

import org.bukkit.entity.Player;
import java.util.logging.Logger;

public class ActionBarUtil {

    private static final Logger LOGGER = Logger.getLogger("LagEclipseCraft");

    public static void sendActionBar(Player player, String message) {
        if (player == null) {
            LOGGER.warning("Tentativa de enviar action bar para um jogador nulo!");
            return;
        }
        if (!player.isOnline()) {
            LOGGER.warning("Tentativa de enviar action bar para um jogador offline: " + player.getName());
            return;
        }
        LOGGER.info("Enviando action bar para " + player.getName() + ": " + message);
        player.sendActionBar(message); // Deve funcionar com o JAR correto
    }
}