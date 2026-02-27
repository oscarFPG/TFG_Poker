package com.ucm.server.middleclasses;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;


public record HandInfo(Card[] cards, Player player) {}