package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;

/**
 * Created by Bastien on 19/10/2017.
 */

public abstract class AbstractGameManager implements GameManagerInterface {

    public abstract Card getLastPlayedCard();

}
