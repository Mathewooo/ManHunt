package gg.matthew.core.players.model;

import java.util.UUID;

public class Hunter {
    private final UUID playerId;
    private int lives;

    private Hunter(UUID playerId, int lives) {
        this.playerId = playerId;
        this.lives = lives;
    }

    public void updateLives(Integer live) {
        this.lives = live;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getLives() {
        return lives;
    }

    public static class Builder {
        private UUID playerId;
        private int lives;

        public Builder setPlayerId(UUID playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder setLives(int lives) {
            this.lives = lives;
            return this;
        }

        public Hunter build() {
            return new Hunter(playerId, lives);
        }
    }
}