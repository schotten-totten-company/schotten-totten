package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.model.Game;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClients;

public interface GameClientInterface {

	Future<Boolean> createGame(final String gameName, final Game game);

	Future<Game> getGame(final String gameName);
	
	Future<ArrayList<String>> listGame();
	
	void updateGame(final String gameName, final Game game) throws ExecutionException, InterruptedException;
	
	void deleteGame(final String gameName) throws ExecutionException, InterruptedException;

}
