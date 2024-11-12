import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CustomKMeans {

    public static double computeDistance(double a, double b) {
        return Math.abs(a - b);
    }

    public static List<Double> selectRandomCentroids(List<Double> dataset, int numClusters) {
        Random rand = new Random();
        List<Double> centroidList = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            centroidList.add(dataset.get(rand.nextInt(dataset.size())));
        }
        return centroidList;
    }

    public static List<Integer> groupDataPoints(List<Double> dataset, List<Double> centroidList) {
        List<Integer> assignments = new ArrayList<>();
        for (double value : dataset) {
            double minDist = Double.MAX_VALUE;
            int nearestCluster = -1;
            for (int i = 0; i < centroidList.size(); i++) {
                double distance = computeDistance(value, centroidList.get(i));
                if (distance < minDist) {
                    minDist = distance;
                    nearestCluster = i;
                }
            }
            assignments.add(nearestCluster);
        }
        return assignments;
    }

    public static List<Double> calculateNewCentroids(List<Double> dataset, List<Integer> assignments, int numClusters) {
        List<Double> updatedCentroids = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            double sum = 0;
            int count = 0;
            for (int j = 0; j < dataset.size(); j++) {
                if (assignments.get(j) == i) {
                    sum += dataset.get(j);
                    count++;
                }
            }
            updatedCentroids.add(count == 0 ? 0 : sum / count);
        }
        return updatedCentroids;
    }
    
    public static List<Double> readQuantityData(String fileName, String columnHeader) {
        List<Double> quantityData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            String[] headers = line.split(",");
            int index = -1;

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(columnHeader)) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                System.err.println("Column '" + columnHeader + "' not found in the dataset.");
                return quantityData;
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    quantityData.add(Double.parseDouble(values[index]));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in the data.");
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return quantityData;
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter the file path (e.g., OnlineRetail.csv): ");
        String filePath = inputScanner.nextLine();
        
        System.out.print("Enter the column name to be used for clustering (e.g., Quantity): ");
        String targetColumn = inputScanner.nextLine();

        List<Double> dataset = readQuantityData(filePath, targetColumn);

        if (dataset.isEmpty()) {
            System.out.println("No data found for column '" + targetColumn + "'.");
            return;
        }

        System.out.print("Enter the number of clusters (K): ");
        int numClusters = inputScanner.nextInt();

        System.out.print("Enter the maximum number of iterations: ");
        int maxIterations = inputScanner.nextInt(); 

        List<Double> centroidList = selectRandomCentroids(dataset, numClusters);
        System.out.println("Initial randomly selected centroids:");
        for (int i = 0; i < centroidList.size(); i++) {
            System.out.printf("Centroid %d: %.2f%n", i, centroidList.get(i));
        }
        System.out.println();

        List<Integer> assignments = new ArrayList<>();
        boolean hasConverged = false;
        int iteration = 0;

        while (!hasConverged && iteration < maxIterations) {
            assignments = groupDataPoints(dataset, centroidList);
            List<Double> newCentroids = calculateNewCentroids(dataset, assignments, numClusters);

            System.out.printf("Iteration %d:\n", iteration + 1);
            for (int i = 0; i < dataset.size(); i++) {
                System.out.printf("Data point %.2f is in cluster %d%n", dataset.get(i), assignments.get(i));
            }
            System.out.println("Current centroids:");
            for (int i = 0; i < centroidList.size(); i++) {
                System.out.printf("Centroid %d: %.2f%n", i, centroidList.get(i));
            }
            System.out.println();

            hasConverged = newCentroids.equals(centroidList);
            centroidList = newCentroids;
            iteration++;
        }

        System.out.println("Final cluster assignments:");
        List<List<Double>> clusters = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<>());
        }
        
        for (int i = 0; i < dataset.size(); i++) {
            clusters.get(assignments.get(i)).add(dataset.get(i));
        }

        for (int i = 0; i < clusters.size(); i++) {
            System.out.printf("Cluster %d: %s%n", i, clusters.get(i).toString());
        }

        System.out.println("\nFinal centroids:");
        for (int i = 0; i < centroidList.size(); i++) {
            System.out.printf("Centroid %d: %.2f%n", i, centroidList.get(i));
        }

        inputScanner.close();
    }
}
