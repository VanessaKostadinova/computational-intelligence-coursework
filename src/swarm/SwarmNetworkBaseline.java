package swarm;

import app.CarPricePrediction;

import java.util.Random;

import static swarm.VectorMaths.*;
import static swarm.VectorMaths.generatePosition;

public class SwarmNetworkBaseline implements SwarmNetwork{
    private final SwarmParticle[] particles;
    private final CarPricePrediction carPricePrediction;
    private static final double coefficientFi = 1.1193D;
    private static final double coefficientEta = 0.721D;
    private final Random random;
    private double[] globalBestPosition;
    private Double globalBestValue;

    public SwarmNetworkBaseline(CarPricePrediction carPricePrediction, SwarmParticle[] swarmParticles, Random random) {
        this.carPricePrediction = carPricePrediction;
        this.globalBestPosition = null;
        this.globalBestValue = null;
        this.particles = swarmParticles;
        this.random = random;

        //generate all random particles
        for (SwarmParticle swarmParticle : swarmParticles) {
            double[] position = swarmParticle.getCurrentPosition();
            double generatedPositionValue = Math.abs(carPricePrediction.evaluate(position));

            //update best
            if (globalBestValue == null || generatedPositionValue < globalBestValue) {
                globalBestPosition = position;
                globalBestValue = generatedPositionValue;
            }
        }
    }

    public void update() {
        for (SwarmParticle s : particles) {
            Vector v = generateNewVelocityVector(
                    s.getVelocity(),
                    s.getBestPosition(),
                    globalBestPosition,
                    s.getCurrentPosition(),
                    coefficientFi,
                    coefficientFi,
                    coefficientEta,
                    random.nextDouble(0, 1),
                    random.nextDouble(0, 1));


            clamp(v, 0.1D);
            double[] newPos = addVectorToCoordinate(v, s.getCurrentPosition());

            if (carPricePrediction.is_valid(newPos)) {
                s.update(v, newPos);

                //update best
                double newValue = Math.abs(carPricePrediction.evaluate(newPos));

                if (newValue < carPricePrediction.evaluate(s.getBestPosition()))
                    s.setBestPosition(newPos);
                if (newValue < globalBestValue) {
                    globalBestPosition = newPos;
                    globalBestValue = newValue;
                }
            }
        }
    }

    public double[] getGlobalBestPosition() {
        return globalBestPosition;
    }
}
