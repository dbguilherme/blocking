package SchemaExperiments;

import DataStructures.EntityProfile;
import DataStructures.SchemaBasedProfiles.AbstractProfile;
import DataStructures.SchemaBasedProfiles.CddbProfile;
import DataStructures.SchemaBasedProfiles.CensusProfile;
import DataStructures.SchemaBasedProfiles.CoraProfile;
import DataStructures.SchemaBasedProfiles.RestaurantProfile;
import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.UnilateralDuplicatePropagation;
import DataStructures.SchemaBasedProfiles.ProfileType;
import Utilities.SerializationUtilities;
import Utilities.StatisticsUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author G.A.P. II
 */
public class Utilities {

    private final static String mainDirectory = "E:\\Data\\profiles\\";
//    private final static String mainDirectory = "/media/gap2/Data/Data/profiles/";
    private final static String[] entitiesPathDER = {
        mainDirectory + "censusProfiles",
        mainDirectory + "restaurantProfiles",
        mainDirectory + "coraProfiles",
        mainDirectory + "cddbProfilesNEW"
    };

    private final static String mainGTDirectory = "E:\\Data\\groundTruth\\";
//    private final static String mainGTDirectory = "/media/gap2/Data/Data/groundtruth/";
    private final static AbstractDuplicatePropagation[] adp = {
        new UnilateralDuplicatePropagation(mainGTDirectory + "censusIdDuplicates"),
        new UnilateralDuplicatePropagation(mainGTDirectory + "restaurantIdDuplicates"),
        new UnilateralDuplicatePropagation(mainGTDirectory + "coraIdDuplicates"),
        new UnilateralDuplicatePropagation(mainGTDirectory + "cddbIdDuplicates")
    };

    public static List<EntityProfile>[] getEntities(int datasetId) {
        List<EntityProfile>[] profiles = new List[1];
        profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(entitiesPathDER[datasetId]);

        return profiles;
    }

    public static String[] getEntitiesPath(int datasetId) {
        String[] paths = {entitiesPathDER[datasetId]};
        return paths;
    }

    public static List<AbstractProfile> getEntityCollection(int datasetId) {
        List<AbstractProfile> profiles = new ArrayList<>();
        List<EntityProfile> entityProfiles = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(entitiesPathDER[datasetId]);
        switch (datasetId) {
            case 0:
                for (EntityProfile eProfile : entityProfiles) {
                    profiles.add(new CensusProfile(eProfile));
                }
                break;
            case 1:
                for (EntityProfile eProfile : entityProfiles) {
                    profiles.add(new RestaurantProfile(eProfile));
                }
                break;
            case 2:
                for (EntityProfile eProfile : entityProfiles) {
                    profiles.add(new CoraProfile(eProfile));
                }
                break;
            case 3:
                for (EntityProfile eProfile : entityProfiles) {
                    profiles.add(new CddbProfile(eProfile));
                }
                break;
        }
        return profiles;
    }

    public static AbstractDuplicatePropagation getGroundTruth(int datasetId) {
        return adp[datasetId];
    }

    public static ProfileType getProfileType(int datasetId) {
        switch (datasetId) {
            case 0:
                return ProfileType.CENSUS_PROFILE;
            case 1:
                return ProfileType.RESTAURANT_PROFILE;
            case 2:
                return ProfileType.CORA_PROFILE;
            case 3:
                return ProfileType.CDDB_PROFILE;
        }
        return null;
    }

    public static void printOutcome(List<Double> instances, String measure) {
        double meanValue = StatisticsUtilities.getMeanValue(instances);
        System.out.println("Average " + measure + "\t:\t" + meanValue);
        System.out.println("Standard Deviation " + measure + "\t:\t" + StatisticsUtilities.getStandardDeviation(meanValue, instances));
    }
}
