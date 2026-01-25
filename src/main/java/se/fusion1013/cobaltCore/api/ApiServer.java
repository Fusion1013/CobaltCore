package se.fusion1013.cobaltCore.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.item.CustomItemManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ApiServer {

    private final CobaltPlugin plugin;
    private HttpServer server;

    public ApiServer(CobaltPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/items/", new ItemsApiHandler());
            server.setExecutor(null);
            server.start();

            plugin.getLogger().info("REST API started on port 8080");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start REST API: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("REST API stopped");
        }
    }

    static class ItemsApiHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                JSONArray jsonObject = CustomItemManager.getCustomItemJson();
                String jsonString = jsonObject.toJSONString();
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                byte[] response = jsonString.getBytes(StandardCharsets.UTF_8);

                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (Exception e) {
                CobaltCore.getInstance().getLogger().warning("Error in api server: " + e.getMessage());
            }
        }
    }

}
