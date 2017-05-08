package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.ByteArrayUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Bastien on 08/05/2017.
 */

public class MongoTest {

    private final MongoClientURI uri =
            new MongoClientURI("mongodb://player:player1234567890@ds133991.mlab.com:33991/schotten-totten");

    @Before
    public void before() {
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection =
                mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
            collection.deleteMany(new BasicDBObject());
        Assert.assertEquals(0, collection.count());
    }

    @Test
    public void basicTest(){
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection =
                mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        Assert.assertEquals(0, collection.count());
    }

    @Test
    public void insertTest() throws GameCreationException, IOException, NoPlayerException, ClassNotFoundException {
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection = mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        final Document element = new Document("game", ByteArrayUtils.gameToByteArray(new Game("P1", "P2")));
        collection.insertOne(element);
        Assert.assertEquals(1, collection.count());
        final Document res = collection.find().first();
        final Game g = ByteArrayUtils.byteArrayToGame((byte[]) res.get("game"));
    }
}
