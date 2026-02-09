package dataaccess;

import chess.model.GameData;

class GameDao {
    void createGame(GameData g);
    GameData getGame(int gameId);
    void updateGame(GameData g);

}