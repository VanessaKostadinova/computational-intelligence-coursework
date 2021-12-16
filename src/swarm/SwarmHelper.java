package swarm;

import app.CarPricePrediction;

import java.util.Arrays;

import static swarm.VectorMaths.clamp;
import static swarm.VectorMaths.generatePosition;


public class SwarmHelper {
    public static SwarmParticle[] generateRandomSwarm(CarPricePrediction carPricePrediction) {
        SwarmParticle[] particles = new SwarmParticle[20 + (int) Math.sqrt(CarPricePrediction.bounds().length)];
        //generate all random particles
        for (int i = 0; i < particles.length; i++) {
            double[] generatedPosition;

            do {
                generatedPosition = generatePosition(CarPricePrediction.bounds());
            } while (!carPricePrediction.is_valid(generatedPosition));

            particles[i] = new SwarmParticle(generatedPosition, clamp(new Vector(generatePosition(CarPricePrediction.bounds())), 0.1D));
        }
        return particles;
    }

    public static SwarmParticle[] createCopyOfParticles(SwarmParticle[] swarmParticles) {
        SwarmParticle[] newParticles = new SwarmParticle[swarmParticles.length];
        for (int i = 0; i < swarmParticles.length; i++) {
            double[] position = swarmParticles[i].getCurrentPosition();
            double[] vectorPoints = swarmParticles[i].getVelocity().getVectorPoints();
            Vector vector = new Vector(Arrays.copyOf(vectorPoints, vectorPoints.length));
            newParticles[i] = new SwarmParticle(Arrays.copyOf(position, position.length), vector);
        }
        return newParticles;
    }
}
