package stream.flarebot.flarebot.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import io.github.binaryoverload.JSONConfig;
import stream.flarebot.flarebot.FlareBot;

import java.io.File;
import java.io.IOException;

public class CassandraController {

    private static Cluster cluster;
    // Cassandra sessions should be kept open, these handle the pooling per node internally.
    private static Session session;

    public void init() {
        JSONConfig config = null;
        try {
            File file = new File("config.json");
            if(!file.exists())
                file.createNewFile();
            config = new JSONConfig("config.json");
        } catch (IOException e) {
            FlareBot.LOGGER.error("Unable to create config.json!");
            System.exit(1);
        }
        Cluster.Builder builder = Cluster.builder().withClusterName("FlareBot Nodes")
                .withCredentials(config.getString("cassandra.username").get(), config.getString("cassandra.password").get())
                .withPoolingOptions(new PoolingOptions().setConnectionsPerHost(HostDistance.LOCAL, 2, 4).setConnectionsPerHost(HostDistance.REMOTE, 2, 4));
        config.getArray("cassandra.nodes").ifPresent(array -> array.forEach(ip -> builder.addContactPoint(ip.getAsString())));
        cluster = builder.build();
        session = cluster.connect();
    }

    public static void runTask(CassandraTask task) {
        task.execute(session);
    }

    public void close() {
        session.close();
        cluster.close();
    }
}
