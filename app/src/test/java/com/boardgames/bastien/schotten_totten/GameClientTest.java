package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.server.GameClient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GameClientTest {

	@Test
	public void testClient() {
		final GameClient client = new GameClient();

		System.out.println(client.ping());

		try {
			final Game game = new Game("P1", "P2");
			client.createGame("test1" + System.currentTimeMillis(), game);
			System.out.println(game.getPlayingPlayer().getName());
		} catch (final NoPlayerException | GameCreationException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			
			final String gameName = client.listGame().get().get(0);
			
			Game game = client.getGame(gameName).get();
			game = client.getGame(gameName).get();
			
			client.updateGame(gameName, game);
			
			game = client.getGame(gameName).get();
			
			final ArrayList<String> list = client.listGame().get();
			System.out.println(list.get(0));
			
			client.deleteGame(gameName);
			
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			client.getGame("test1").get();
			System.exit(-1);
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.out.println("test ok");
		}
		
	}
}
