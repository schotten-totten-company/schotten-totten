package com.boardgames.bastien.schotten_totten.server;


import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface GameClientInterface {

	Future<Boolean> createGame(final String gameName, final GameManagerInterface game);

	Future<GameManagerInterface> getGame(final String gameName);
	
	Future<ArrayList<String>> listGame();
	
	void updateGame(final String gameName, final GameManagerInterface game) throws ExecutionException, InterruptedException;
	
	void deleteGame(final String gameName) throws ExecutionException, InterruptedException;

}
