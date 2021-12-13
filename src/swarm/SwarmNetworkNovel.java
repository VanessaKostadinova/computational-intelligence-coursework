package swarm;

import app.CarPricePrediction;

import java.util.Arrays;
import java.util.Random;

import static swarm.VectorMaths.*;

public class SwarmNetworkNovel {
    private final SwarmParticle[] particles;
    private final CarPricePrediction carPricePrediction;
    private static final double coefficientFi = 1.1193D;
    private static final double coefficientEta = 0.721D;
    private final Random random;
    private double[] globalBestPosition;
    private Double globalBestValue;

    public enum Type {
        EXPLORATION, EXPLOITATION
    }

    private Type type;

    public SwarmNetworkNovel(CarPricePrediction carPricePrediction) {
        this.carPricePrediction = carPricePrediction;
        this.globalBestPosition = null;
        this.globalBestValue = null;
        this.particles = new SwarmParticle[20 + (int) Math.sqrt(CarPricePrediction.bounds().length)];
        this.random = new Random();
        this.type = Type.EXPLORATION;

        //generate all random particles
        for (int i = 0; i < particles.length; i++) {
            double[] generatedPosition;

            do {
                generatedPosition = generatePosition(CarPricePrediction.bounds());
            } while (!carPricePrediction.is_valid(generatedPosition));

            double generatedPositionValue = Math.abs(carPricePrediction.evaluate(generatedPosition));

            //update best
            if (globalBestValue == null || generatedPositionValue < globalBestValue) {
                globalBestPosition = generatedPosition;
                globalBestValue = generatedPositionValue;
            }

            particles[i] = new SwarmParticle(generatedPosition, new Vector(generatedPosition));
        }
    }

    public SwarmNetworkNovel(CarPricePrediction carPricePrediction, SwarmParticle[] swarmParticles, Random random) {
        this.carPricePrediction = carPricePrediction;
        this.globalBestPosition = null;
        this.globalBestValue = null;
        this.particles = swarmParticles;
        this.random = random;
        this.type = Type.EXPLORATION;

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

    public void changeType(Type type) {
        this.type = type;
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new SwarmParticle(generatePosition(CarPricePrediction.bounds()), new Vector(generatePosition(CarPricePrediction.bounds())));
            //s.setBestPosition(s.getCurrentPosition());
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

            double clamp;
            if (type == Type.EXPLORATION) {
                clamp = 0.2D;
            } else {
                clamp = 0.1D;
            }
            clamp(v, clamp);
            double[] newPos = addVectorToCoordinate(v, s.getCurrentPosition());

            if (type == Type.EXPLORATION) {
                if (calculateDistance(newPos, globalBestPosition) < 0.01) {
                    newPos = generatePosition(CarPricePrediction.bounds());
                    while (!carPricePrediction.is_valid(newPos)) {
                        newPos = generatePosition(CarPricePrediction.bounds());
                    }
                    s.setBestPosition(newPos);
                }
            }

            if (carPricePrediction.is_valid(newPos)) {
                s.update(v, newPos);

                //update best
                double newValue = Math.abs(carPricePrediction.evaluate(newPos));

                if (newValue < carPricePrediction.evaluate(s.getBestPosition()))
                    s.setBestPosition(newPos);
                if (newValue < globalBestValue) {
                    globalBestPosition = newPos;
                    globalBestValue = newValue;

                    //System.out.println(globalBestValue);
                    //System.out.println(Arrays.toString(globalBestPosition));
                    //System.out.println("-----");
                }
            }
        }
    }

    public double[] getGlobalBestPosition() {
        double[] returnPos = new double[globalBestPosition.length];
        System.arraycopy(globalBestPosition, 0, returnPos, 0, returnPos.length);
        return returnPos;
    }

    public boolean isExploitive(){
        return this.type == Type.EXPLOITATION;
    }

    public double getGlobalBestValue() {
        return globalBestValue;
    }

    public SwarmParticle[] getParticles() {
        return Arrays.copyOf(particles, particles.length);
    }
}
