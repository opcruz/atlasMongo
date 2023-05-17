package mx.atlas.games.db;

import mx.atlas.games.config.Config;
import mx.atlas.games.dtos.GameSale;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Connection {

    public volatile static MongoClient mongoClient;

    public static void load() {
    }

    public static <E> MongoCollection<E> getCollection(String dbName, String collectionName, Class<E> type) {
        // MongoDatabase defines a connection to a specific MongoDB database
        MongoDatabase database = mongoClient.getDatabase(dbName);
        // MongoCollection defines a connection to a specific collection of documents in a specific database
        return database.getCollection(collectionName, type);
    }

    public static MongoCollection<GameSale> getDefaultCollection() {
        return
                getCollection(
                        Config.properties.getProperty("database"),
                        Config.properties.getProperty("collection"),
                        GameSale.class
                );
    }


    static {
        String mongoUri = String.format(
                "mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority",
                Config.properties.getProperty("user"),
                Config.properties.getProperty("password"),
                Config.properties.getProperty("host")
        );
        System.out.println(mongoUri);

        ConnectionString connectionUri = new ConnectionString(mongoUri);

        // a CodecRegistry tells the Driver how to move data between Java POJOs (Plain Old Java Objects) and MongoDB documents
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // The MongoClient defines the connection to our MongoDB datastore instance (Atlas) using MongoClientSettings
        // You can create a MongoClientSettings with a Builder to configure codecRegistries, connection strings, and more
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(connectionUri)
                .build();

        mongoClient = MongoClients.create(settings);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (mongoClient != null) {
                    System.out.println("Off");
                    mongoClient.close();
                }
            }
        });

    }

}
