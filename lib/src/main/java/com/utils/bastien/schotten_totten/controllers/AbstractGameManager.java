package com.utils.bastien.schotten_totten.controllers;

import com.utils.bastien.schotten_totten.model.Card;

/**
 * Created by Bastien on 19/10/2017.
 */

public abstract class AbstractGameManager implements GameManagerInterface {

    public abstract Card getLastPlayedCard();

}
