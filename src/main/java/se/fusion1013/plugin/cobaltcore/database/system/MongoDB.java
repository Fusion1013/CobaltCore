package se.fusion1013.plugin.cobaltcore.database.system;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoDB {

    private static MongoDB instance = null;

    /**
     * Creates a new <code>MongoDB</code> object and connects it to the database.
     */
    private MongoDB() {
        // morphia = new Morphia();
    }

    /**
     * Returns the object representing this <code>MongoDB</code>.
     *
     * @return The object of this class.
     */
    public static MongoDB getInstance() {
        if (instance == null) {
            instance = new MongoDB();
        }
        return instance;
    }

    /**
     * Connects to the database and prepares classes.
     */
    public void connect() {

        /*
        ConnectionString connectionString = new ConnectionString("mongodb+srv://fusion1013:<w1V!lepa1>@fusioncluster1.l8quj.mongodb.net/cobalt-test?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test");

         */

        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createCredential("fusion1013", "cobalt-test", "Qp8)cvcrnGFP#8sn".toCharArray()));

        // MongoClient mc = new MongoClient(addr, credentials);

        /*

        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createCredential("username", "database", "password".toCharArray()));

        mc = new MongoClient(addr, credentials);
        morphia = new Morphia();

        // Point the object factory to the right class loader
        // morphia.getMapper().getOptions().setObjectFactory(new HackyCreator());

        // Prepare the MongoClient
        String ip = "fusioncluster1-shard-00-02.l8quj.mongodb.net";
        int port = 27017;
        ServerAddress serverAddress = new ServerAddress(ip, port);

        List<MongoCredential> credentialList = new ArrayList<>();
        String username = "fusion1013";
        String dbname = "cobalt-test";
        char[] password = "Qp8)cvcrnGFP#8sn".toCharArray();
        MongoCredential auth = MongoCredential.createCredential(username, dbname, password);
        credentialList.add(auth);

        MongoClient client = new MongoClient(serverAddress, credentialList, MongoClientOptions.builder().sslEnabled(true).maxConnectionIdleTime(60000).build());

        // Create a Datastore to hold the database's data
        // datastore = morphia.createDatastore(client, dbname);
        // datastore.ensureIndexes();

         */
    }

}
