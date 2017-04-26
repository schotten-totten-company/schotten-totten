package com.boardgames.bastien.schotten_totten;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.server.GameAlreadyExistsException;
import com.boardgames.bastien.schotten_totten.server.GameClient;
import com.boardgames.bastien.schotten_totten.server.GameDoNotExistException;

import cz.msebera.android.httpclient.HttpException;

public class GameClientTest {

	@Test
	public void testClient() {
		final GameClient client = new GameClient();

		try {
			System.out.println(client.ping());
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			final Game game = new Game("P1", "P2");
			client.createGame("test1" + System.currentTimeMillis(), game);
			System.out.println(game.getPlayingPlayer().getName());
		} catch (final ExecutionException | InterruptedException | NoPlayerException | GameCreationException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			
			final String gameName = client.listGame().get(0);
			
			Game game = client.getGame(gameName);
			game = client.getGame(gameName);
			
			client.updateGame(gameName, game);
			
			game = client.getGame(gameName);
			
			final ArrayList<String> list = client.listGame();
			System.out.println(list.get(0));
			
			client.deleteGame(gameName);
			
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			client.getGame("test1");
			System.exit(-1);
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.out.println("test ok");
		}
		
	}
}
