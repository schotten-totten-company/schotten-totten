package com.boardgames.bastien.schotten_totten.server;

import com.utils.bastien.schotten_totten.controllers.AbstractGameManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface GameClientInterface {

	Future<Boolean> createGame(final String gameName, final AbstractGameManager game);

	Future<AbstractGameManager> getGame(final String gameName);
	
	Future<ArrayList<String>> listGame();
	
	void updateGame(final String gameName, final AbstractGameManager game) throws ExecutionException, InterruptedException;
	
	void deleteGame(final String gameName) throws ExecutionException, InterruptedException;

}
