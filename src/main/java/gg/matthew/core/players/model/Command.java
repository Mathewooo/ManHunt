package gg.matthew.core.players.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Command {
    private final UUID playerId;
    private List<Hunter> hunters;
    private List<UUID> runners;

    private Command(UUID playerId, List<Hunter> hunters, List<UUID> runners) {
        this.playerId = playerId;
        this.runners = runners;
        this.hunters = hunters;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public List<Hunter> getHunters() {
        return Collections.unmodifiableList(hunters);
    }

    public void setHunters(List<Hunter> hunters) {
        this.hunters = hunters;
    }

    public List<UUID> getRunners() {
        return Collections.unmodifiableList(runners);
    }

    public void setRunners(List<UUID> runners) {
        this.runners = runners;
    }

    public static class Builder {
        private final List<Hunter> hunters = new ArrayList<>();
        private final List<UUID> runners = new ArrayList<>();
        private UUID playerId;

        public Builder setPlayerId(UUID playerId) {
            this.playerId = playerId;
            return this;
        }

        public Command build() {
            return new Command(playerId, hunters, runners);
        }
    }
}