package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.model.Game;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Bastien on 08/05/2017.
 */

public class MongoDbGameClient implements GameClientInterface {

    private final MongoClientURI uri;

    public MongoDbGameClient() {
        uri = new MongoClientURI("mongodb://player:player1234567890@ds133991.mlab.com:33991/schotten-totten");
    }

    public Future<Boolean> createGame(final String gameName, final Game game) {

        final Callable createCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    final MongoCursor<Document> iterator = collection.find().iterator();
                    while (iterator.hasNext()) {
                        if (gameName.equals(iterator.next().get("name"))) {
                            throw new GameAlreadyExistsException();
                        }
                    }
                }
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    final Document element = new Document("name", gameName).
                            append("game", ByteArrayUtils.gameToByteArray(game));
                    collection.insertOne(element);
                    return true;
                }
            }
        };
        return Executors.newSingleThreadExecutor().submit(createCallable);
    }

    public Future<Game> getGame(final String gameName) {

        final Callable getCallable = new Callable<Game>() {
            @Override
            public Game call() throws Exception {
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    final Document res = collection.find(new Document("name", gameName)).first();
                    if (res != null) {
                        return ByteArrayUtils.byteArrayToGame(((Binary) res.get("game")).getData());
                    } else {
                        // game does not exist
                        throw new GameDoNotExistException();
                    }
                }
            }
        };
        return Executors.newSingleThreadExecutor().submit(getCallable);
    }

    public Future<ArrayList<String>> listGame() {

        final Callable listCallable = new Callable<ArrayList<String>>() {
            @Override
            public ArrayList<String> call() throws Exception {
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    final MongoCursor<Document> iterator = collection.find().iterator();
                    final ArrayList<String> list = new ArrayList<>();
                    while (iterator.hasNext()) {
                        list.add((String) iterator.next().get("name"));
                    }
                    return list;
                }
            }
        };
        return Executors.newSingleThreadExecutor().submit(listCallable);
    }

    public void updateGame(final String gameName, final Game game) throws ExecutionException, InterruptedException {

        final Callable updateCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    // update
                    final Document element = new Document("game", ByteArrayUtils.gameToByteArray(game));
                    collection.updateOne(new Document("name", gameName), new Document("$set", element));
                    return true;
                }
            }
        };
        Executors.newSingleThreadExecutor().submit(updateCallable).get();
    }

    public void deleteGame(final String gameName) throws ExecutionException, InterruptedException {

        final Callable updateCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (final MongoClient mongoClient = new MongoClient(uri)) {
                    final MongoCollection<Document> collection =
                            mongoClient.getDatabase(uri.getDatabase()).getCollection("games");
                    collection.deleteOne(new Document("name", gameName));
                    return true;
                }
            }
        };
        Executors.newSingleThreadExecutor().submit(updateCallable).get();
    }
}
