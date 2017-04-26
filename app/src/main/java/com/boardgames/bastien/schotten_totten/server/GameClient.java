package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.model.Game;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
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

public class GameClient {

	private final String serverUrl;

	public GameClient() {
		serverUrl = "https://schotten-totten.herokuapp.com";
	}

	public String ping() throws ExecutionException, InterruptedException {

		final Callable pingCallable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				final HttpGet requestGet = new HttpGet(serverUrl + "/");
				final HttpResponse responseGet = client.execute(requestGet);
				if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return IOUtils.toString(responseGet.getEntity().getContent());
				} else {
					// other error
					throw new HttpException(responseGet.getStatusLine().getStatusCode() + "");
				}
			}
		};
		final Future<String> future = Executors.newSingleThreadExecutor().submit(pingCallable);
		return future.get();
	}

	public void createGame(final String gameName, final Game game) throws ExecutionException, InterruptedException {

		final Callable createCallable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				// post
				final HttpPost post = new HttpPost(serverUrl + "/createGame");
				post.addHeader("gameName", gameName);
				post.setEntity(ByteArrayUtils.gameToByteArrayEntity(game));
				final HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					// game already exist
					throw new GameAlreadyExistsException();
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_METHOD_FAILURE) {
					// other error
					throw new HttpException(response.getStatusLine().getStatusCode() + "");
				}
				return true;
			}
		};
		Executors.newSingleThreadExecutor().submit(createCallable).get();
	}

	public Game getGame(final String gameName) throws ExecutionException, InterruptedException {

		final Callable getCallable = new Callable<Game>() {
			@Override
			public Game call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				final HttpGet requestGet = new HttpGet(serverUrl + "/getGame");
				requestGet.addHeader("gameName", gameName);
				final HttpResponse responseGet = client.execute(requestGet);
				if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return ByteArrayUtils.inputStreamToGame(responseGet.getEntity().getContent());
				} else if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
					// game does not exist
					throw new GameDoNotExistException();
				} else {
					// other error
					throw new HttpException(responseGet.getStatusLine().getStatusCode() + "");
				}
			}
		};
		final Future<Game> future = Executors.newSingleThreadExecutor().submit(getCallable);
		return future.get();
	}
	
	public ArrayList<String> listGame() throws ExecutionException, InterruptedException {

		final Callable listCallable = new Callable<ArrayList<String>>() {
			@Override
			public ArrayList<String> call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				final HttpGet requestGet = new HttpGet(serverUrl + "/listGame");
				final HttpResponse responseGet = client.execute(requestGet);
				if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return ByteArrayUtils.inputStreamToSet(responseGet.getEntity().getContent());
				} else {
					// other error
					throw new HttpException();
				}
			}
		};
		final Future<ArrayList<String>> future = Executors.newSingleThreadExecutor().submit(listCallable);
		return future.get();
	}
	
	public void updateGame(final String gameName, final Game game) throws ExecutionException, InterruptedException {

		final Callable updateCallable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				// post
				final HttpPost post = new HttpPost(serverUrl + "/updateGame");
				post.addHeader("gameName", gameName);
				post.setEntity(ByteArrayUtils.gameToByteArrayEntity(game));
				final HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					// error
					throw new HttpException(response.getStatusLine().getStatusCode() + "");
				}
				return true;
			}
		};
		Executors.newSingleThreadExecutor().submit(updateCallable).get();
	}
	
	public void deleteGame(final String gameName) throws ExecutionException, InterruptedException {

		final Callable updateCallable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final HttpClient client = HttpClients.createDefault();
				final HttpGet request = new HttpGet(serverUrl + "/deleteGame");
				request.addHeader("gameName", gameName);
				final HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					// other error
					throw new HttpException(response.getStatusLine().getStatusCode() + "");
				}
				return true;
			}
		};
		Executors.newSingleThreadExecutor().submit(updateCallable).get();
	}
		

}
