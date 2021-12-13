package app;

import swarm.SwarmHelper;
import swarm.SwarmNetworkNovel;
import swarm.SwarmNetworkSimple;
import swarm.SwarmParticle;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class SwarmMain {
    private static final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    private static final Random random = new Random();
    private static CarPricePrediction trainingModel;
    private static CarPricePrediction validationModel;

    private static SwarmNetworkNovel swarmNetworkNovel;
    private static SwarmNetworkNovel swarmNetworkNovelWithoutStates;
    private static SwarmNetworkSimple swarmNetworkSimple;

    public static void main(String[] args) throws IOException {
        trainingModel = new CarPricePrediction("train");
        validationModel = new CarPricePrediction("validation");

        double novelTestErrorTotal = 0;
        double simpleTestErrorTotal = 0;
        double simpleNovelTestErrorTotal = 0;
        double startingWeightTestErrorTotal = 0;

        int loopCount = 2;
        int iterations = 10000;

        timeTest();
/*
        for (int r = 0; r < loopCount; r++) {
            long seed = random.nextLong();
            SwarmParticle[] swarmParticles = SwarmHelper.generateRandomSwarm(trainingModel);
            swarmNetworkNovel = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            swarmNetworkNovelWithoutStates = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            swarmNetworkSimple = new SwarmNetworkSimple(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));

            Double startingWeightValue = null;

            for(SwarmParticle s : SwarmHelper.createCopyOfParticles(swarmParticles)){
                if(startingWeightValue == null || startingWeightValue > trainingModel.evaluate(s.getCurrentPosition())){
                    startingWeightValue = trainingModel.evaluate(s.getCurrentPosition());
                }
            }

            boolean switched = false;
            LocalDateTime time = LocalDateTime.now();
            while (LocalDateTime.now().isBefore(time.plusMinutes(1))) {
                swarmNetworkNovel.update();
                swarmNetworkSimple.update();
                swarmNetworkNovelWithoutStates.update();


                if (!switched && LocalDateTime.now().isAfter(time.plusSeconds(30))) {
                    System.out.println("hi");
                    switched = true;
                    swarmNetworkNovel.changeType(SwarmNetworkNovel.Type.EXPLOITATION);
                }
            }

            novelTestErrorTotal += validationModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());
            simpleTestErrorTotal += validationModel.evaluate(swarmNetworkSimple.getGlobalBestPosition());
            simpleNovelTestErrorTotal += validationModel.evaluate(swarmNetworkNovelWithoutStates.getGlobalBestPosition());
            startingWeightTestErrorTotal += startingWeightValue;
        }

        System.out.println("Novel: " + novelTestErrorTotal / loopCount);
        System.out.println("Simple: " + simpleTestErrorTotal / loopCount);
        System.out.println("Simple Novel: " + simpleNovelTestErrorTotal / loopCount);
        System.out.println("Starting: " + startingWeightTestErrorTotal / loopCount);

        /*
        double validationErrorNovel = validationModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());

        System.out.printf("Validation error of best novel solution " +
                "found while training: %f%n", validationErrorNovel);

        CarPricePrediction testingModel = new CarPricePrediction("test");
        double testingErrorNovel = testingModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());

        System.out.printf("Testing error of best novel solution " +
                "found while training: %f%n", testingErrorNovel);

        double validationErrorSimple = validationModel.evaluate(swarmNetworkSimple.getGlobalBestPosition());

        System.out.printf("Validation error of best simple solution " +
                "found while training: %f%n", validationErrorSimple);

        double testingErrorSimple = testingModel.evaluate(swarmNetworkSimple.getGlobalBestPosition());

        System.out.printf("Testing error of best simple solution " +
                "found while training: %f%n", testingErrorSimple);

        //System.out.println(training_problem.is_valid(VectorMaths.generatePosition(CarPricePrediction.bounds())));
         */
    }

    /*
        This test will run both versions for different lengths of time, i number of times.
     */

    private static void timeTest(){
        int[] intervals = new int[]{0,100,100,100,100,100,1000,1000,10000};
        int switchThreshold = 5000;
        int numberOfIterations = 1;
        double[][] results = new double[intervals.length][numberOfIterations];


        for(int r = 0; r < numberOfIterations; r++) {
            SwarmParticle[] swarmParticles = SwarmHelper.generateRandomSwarm(trainingModel);
            long seed = random.nextLong();
            swarmNetworkNovel = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
            //LocalDateTime finishTime = LocalDateTime.now();
            int finishIterations = intervals[0];
            int counter = 0;
            for (int i = 0; i < intervals.length; i++) {
                //finishTime = finishTime.plusSeconds(intervals[i]);
                finishIterations += intervals[i];

                while (counter <= finishIterations) {
                    counter++;
                    if(switchThreshold <= counter && !swarmNetworkNovel.isExploitive()){
                        swarmNetworkNovel.changeType(SwarmNetworkNovel.Type.EXPLOITATION);
                    }
                    swarmNetworkNovel.update();
                    //swarmNetworkNovelWithoutStates = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
                    //swarmNetworkSimple = new SwarmNetworkSimple(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));

                }
                System.out.println(validationModel.evaluate(swarmNetworkNovel.getGlobalBestPosition()));
                results[i][r] = validationModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());
            }
        }
        writeToCSV("C://test//output.csv", results);
    }

    private static void writeToCSV(String outputFile, double[][] results){

        try(FileOutputStream fw = new FileOutputStream(outputFile, false)){
            for (double[] x : results) {
                for (double y : x) {
                    //System.out.println(y);
                    fw.write(String.valueOf(y).getBytes(StandardCharsets.UTF_8));
                    fw.write(",".getBytes(StandardCharsets.UTF_8));
                    //fw.flush();
                }
                fw.write("\n".getBytes(StandardCharsets.UTF_8));
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    /*
    private static void iterationTest(int iterations){
        long seed = random.nextLong();
        SwarmParticle[] swarmParticles = SwarmHelper.generateRandomSwarm(trainingModel);
        SwarmNetworkNovel swarmNetworkNovel = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
        SwarmNetworkNovel swarmNetworkNovelWithoutStates = new SwarmNetworkNovel(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));
        SwarmNetworkSimple swarmNetworkSimple = new SwarmNetworkSimple(trainingModel, SwarmHelper.createCopyOfParticles(swarmParticles), new Random(seed));

        int i = 0;
        while (i < iterations) {
            swarmNetworkNovel.update();

            swarmNetworkSimple.update();

            swarmNetworkNovelWithoutStates.update();

            if (i == iterations / 2) {
                swarmNetworkNovel.changeType(SwarmNetworkNovel.Type.EXPLOITATION);
            }

            i++;
        }

        novelTestErrorTotal += validationModel.evaluate(swarmNetworkNovel.getGlobalBestPosition());
        simpleTestErrorTotal += validationModel.evaluate(swarmNetworkSimple.getGlobalBestPosition());
        simpleNovelTestErrorTotal += validationModel.evaluate(swarmNetworkNovelWithoutStates.getGlobalBestPosition());
    }*/

    public static File loadFile(String name) {
        URL path = classloader.getResource(name);
        if (path != null) {
            return new File(path.getPath());
        } else {
            throw new RuntimeException("idk");
        }
    }
}
