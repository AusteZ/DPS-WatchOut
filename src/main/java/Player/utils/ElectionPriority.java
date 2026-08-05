package Player.utils;

public record ElectionPriority(double playerDistance, int playerId) {
    public boolean isHigherPriority(ElectionPriority otherElectionPriority) {

        return isFartherAway(otherElectionPriority) || winsTieBreaker(otherElectionPriority);
    }

    private boolean isFartherAway(ElectionPriority otherElectionPriority) {
        return this.playerDistance < otherElectionPriority.playerDistance;
    }

    private boolean winsTieBreaker(ElectionPriority otherElectionPriority) {
        return this.playerDistance == otherElectionPriority.playerDistance && this.playerId > otherElectionPriority.playerId;
    }
}
