package SchemaExperiments.SchemaBased;

import DataStructures.AbstractBlock;
import BlockBuilding.MemoryBased.SchemaBased.CanopyClustering;
import BlockProcessing.ComparisonRefinement.ComparisonPropagation;
import SchemaExperiments.Utilities;
import Utilities.BlockStatistics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author G.A.P. II
 */
public class CanopyClusteringExperiments {

    public static void main(String[] args) throws IOException {
        int[] qs = {2, 3};
        double[] t1 = {0.8, 0.7};
        double[] t2 = {0.9, 0.8};
        int[][] blockingKeys = {{0, 1}, {2, 3}, {4, 5}};
        for (int datasetId = 0; datasetId < 4; datasetId++) {
            System.out.println("\n\nCurrent dataset id\t:\t" + (datasetId + 1));

            for (int blockKeyId = 0; blockKeyId < 3; blockKeyId++) {
                System.out.println("\n\nCurrent blocking key id\t:\t" + (blockKeyId + 1));

                List<Double> averageComparisons = new ArrayList<>();
                List<Double> averagePc = new ArrayList<>();
                List<Double> averagePq = new ArrayList<>();
                List<Double> averageRr = new ArrayList<>();
                for (int i = 0; i < t1.length; i++) {
                    for (int q : qs) {
                        CanopyClustering blockingMethod = new CanopyClustering(t1[i], t2[i], q, blockingKeys[blockKeyId], Utilities.getProfileType(datasetId), Utilities.getEntities(datasetId));
                        List<AbstractBlock> blocks = blockingMethod.buildBlocks();

                        ComparisonPropagation cp = new ComparisonPropagation();
                        cp.applyProcessing(blocks);

                        BlockStatistics bStats = new BlockStatistics(blocks, Utilities.getGroundTruth(datasetId));
                        double[] metrics = bStats.applyProcessing();

                        averageComparisons.add(metrics[2]);
                        averagePc.add(metrics[0]);
                        averagePq.add(metrics[1]);
                        double bfComparisons = blockingMethod.getBruteForceComparisons();
                        System.out.println("BF!!!" + bfComparisons);
                        double rr = 1 - metrics[2] / bfComparisons;
                        averageRr.add(rr);
                    }
                }
                Utilities.printOutcome(averageComparisons, "Comparisons");
                Utilities.printOutcome(averagePc, "PC");
                Utilities.printOutcome(averagePq, "PQ");
                Utilities.printOutcome(averageRr, "RR");
            }
        }
    }
}