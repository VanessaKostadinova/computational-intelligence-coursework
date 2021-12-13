package swarm;

public class SwarmParticle {

    private double[] currentPosition;
    private double[] bestPosition;
    private Vector velocity;


    public SwarmParticle(double[] startingPosition, Vector vector) {
        this.currentPosition = startingPosition;
        this.bestPosition = startingPosition;
        this.velocity = vector;
    }

    public double[] getCurrentPosition() {
        return currentPosition;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setBestPosition(double[] newPos) {
        bestPosition = newPos;
    }

    public void update(Vector v, double[] c) {
        this.velocity = v;
        this.currentPosition = c;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }
}
