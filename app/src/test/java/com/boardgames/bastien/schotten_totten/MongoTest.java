package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.ByteArrayUtils;
import com.boardgames.bastien.schotten_totten.server.MongoDbGameClient;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.bson.types.Binary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Bastien on 08/05/2017.
 */

public class MongoTest {

    private static final MongoClientURI uri =
            new MongoClientURI("mongodb://player:player1234567890@ds133991.mlab.com:33991/schotten-totten");

    @Before
    public void before() {
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection =
                mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        collection.deleteMany(new BasicDBObject());
        Assert.assertEquals(0, collection.count());
    }

    @AfterClass
    public static void cleanAll() {
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
        final Document element = new Document("name", "game01").append("game", ByteArrayUtils.gameToByteArray(new Game("P1", "P2")));
        collection.insertOne(element);
        Assert.assertEquals(1, collection.count());
        final Document res = collection.find(new Document("name", "game01")).first();
        final Game g = ByteArrayUtils.byteArrayToGame(((Binary) res.get("game")).getData());
        Assert.assertEquals("P1", g.getPlayer(PlayerType.ONE).getName());
    }

    @Test
    public void listTest() throws GameCreationException, IOException, NoPlayerException, ClassNotFoundException {
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection = mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        for (int i = 0; i <10; i++) {
            final Document element = new Document("name", "game" + i).append("game", ByteArrayUtils.gameToByteArray(new Game("P1", "P2")));
            collection.insertOne(element);
        }
        Assert.assertEquals(10, collection.count());
        final MongoCursor<Document> iterator = collection.find().iterator();
        while(iterator.hasNext()) {
           System.out.println(iterator.next().get("name"));
        }
    }

    @Test
    public void deleteTest() throws GameCreationException, IOException, NoPlayerException, ClassNotFoundException {
        insertTest();
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection = mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        collection.deleteOne(new Document("name", "game01"));
        Assert.assertEquals(0, collection.count());
    }

    @Test
    public void updateTest() throws GameCreationException, IOException, NoPlayerException, ClassNotFoundException, EmptyDeckException {
        insertTest();
        final MongoClient mongoClient = new MongoClient(uri);
        final MongoCollection<Document> collection = mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
        final Document res = collection.find(new Document("name", "game01")).first();
        final Game g = ByteArrayUtils.byteArrayToGame(((Binary) res.get("game")).getData());
        Assert.assertEquals(PlayerType.ONE, g.getPlayingPlayer().getPlayerType());
        Assert.assertEquals(42, g.getGameBoard().getDeck().getDeck().size());
        g.getGameBoard().getDeck().drawCard();
        g.swapPlayingPlayerType();
        Assert.assertEquals(41, g.getGameBoard().getDeck().getDeck().size());
        Assert.assertEquals(PlayerType.TWO, g.getPlayingPlayer().getPlayerType());
        // update
        final Document element = new Document("game", ByteArrayUtils.gameToByteArray(g));
        collection.updateOne(new Document("name", "game01"), new Document("$set", element));
        // get it again
        final Document res2 = collection.find(new Document("name", "game01")).first();
        final Game g2 = ByteArrayUtils.byteArrayToGame(((Binary) res2.get("game")).getData());
        Assert.assertEquals(41, g2.getGameBoard().getDeck().getDeck().size());
        Assert.assertEquals(PlayerType.TWO, g2.getPlayingPlayer().getPlayerType());
    }

    @Test
    public void testWithHomeMadeClient() throws GameCreationException, ExecutionException, InterruptedException {
        final MongoDbGameClient client = new MongoDbGameClient();
        client.createGame("toto", new Game("P1", "P2")).get();
        Assert.assertEquals(1, client.listGame().get().size());
        client.deleteGame("toto");
        Assert.assertEquals(0, client.listGame().get().size());
    }
}
