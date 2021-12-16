package test;

import app.CarPricePrediction;
import exceptions.TestFailedException;
import swarm.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static app.GenericHelper.writeToCSV;

public class Test {

    private final Random random;
    private final CarPricePrediction trainingModel;
    private final CarPricePrediction validationModel;
    private final CarPricePrediction testingModel;

    public Test() {
        try {
            trainingModel = new CarPricePrediction("train");
            validationModel = new CarPricePrediction("validation");
            testingModel = new CarPricePrediction("test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        random = new Random();

        iterationTest();


        double[][] results = new double[30][2];

        int i = 0;
        for (; i < 30; i++) {
            SwarmParticle[] swarmParticles = SwarmHelper.generateRandomSwarm(trainingModel);
            long seed = random.nextLong();

            SwarmNetworkNovel swarmNetworkNovel = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            SwarmNetworkBaseline swarmNetworkBaseline = new SwarmNetworkBaseline(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            try {
                results[i][0] = TimeUnit.NANOSECONDS.toMillis(thresholdTest(swarmNetworkNovel, testingModel, 0.5D).getNano());

            } catch (TestFailedException e) {
                results[i][0] = -1;
            }
            try {
                results[i][1] = TimeUnit.NANOSECONDS.toMillis(thresholdTest(swarmNetworkBaseline, testingModel, 0.5D).getNano());
            } catch (TestFailedException e) {
                results[i][1] = -1;
            }
        }

        writeToCSV("C://test//output-time-4.csv", results);
    }

    private void iterationTest() {
        int steps = 1100;
        int numberOfIterations = 30;

        double[][] resultsNovel = new double[steps][numberOfIterations];
        double[][] resultsBaseline = new double[steps][numberOfIterations];

        for (int r = 0; r < numberOfIterations; r++) {
            SwarmParticle[] swarmParticles = SwarmHelper.generateRandomSwarm(trainingModel);
            long seed = random.nextLong();

            SwarmNetworkBaseline swarmNetworkBaseline = new SwarmNetworkBaseline(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            SwarmNetworkNovel swarmNetworkNovel = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));

            for (int i = 0; i < steps; i++) {
                resultsBaseline[i][r] = testingModel.evaluate(swarmNetworkBaseline.getGlobalBestPosition());
                resultsNovel[i][r] = testingModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());

                swarmNetworkBaseline.update();
                swarmNetworkNovel.update();

                swarmNetworkBaseline.update();
                swarmNetworkNovel.update();
            }
        }

        writeToCSV("C://test//output-baseline-3.csv", resultsBaseline);
        writeToCSV("C://test//output-novel-3.csv", resultsNovel);
    }

    private static LocalDateTime thresholdTest(SwarmNetwork swarmNetwork, CarPricePrediction carPricePrediction, double threshold) {
        double[] position = swarmNetwork.getGlobalBestPosition();
        LocalDateTime started = LocalDateTime.now();

        while (carPricePrediction.evaluate(position) > threshold) {
            if (!started.plusSeconds(30).isAfter(LocalDateTime.now())) {
                throw new TestFailedException("Took longer than 1 minute to execute.");
            }
            swarmNetwork.update();
            position = swarmNetwork.getGlobalBestPosition();
        }

        LocalDateTime ended = LocalDateTime.now();
        return ended.minusNanos(started.getNano());
    }
}
