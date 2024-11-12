import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class kmeans2d {

    public static double computeDistance(double[] a, double[] b) {
        if (a.length == 1 && b.length == 1) {
            return Math.abs(a[0] - b[0]);
        } else if (a.length == 2 && b.length == 2) {
            return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
        }
        throw new IllegalArgumentException("Unsupported dimensions for distance computation.");
    }

    public static List<double[]> selectRandomCentroids(List<double[]> dataset, int numClusters) {
        Random rand = new Random();
        List<double[]> centroidList = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            centroidList.add(dataset.get(rand.nextInt(dataset.size())));
        }
        return centroidList;
    }

    public static List<Integer> groupDataPoints(List<double[]> dataset, List<double[]> centroidList) {
        List<Integer> assignments = new ArrayList<>();
        for (double[] value : dataset) {
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

    public static List<double[]> calculateNewCentroids(List<double[]> dataset, List<Integer> assignments, int numClusters) {
        List<double[]> updatedCentroids = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            double sumX = 0, sumY = 0;
            int count = 0;
            for (int j = 0; j < dataset.size(); j++) {
                if (assignments.get(j) == i) {
                    sumX += dataset.get(j)[0];
                    if (dataset.get(j).length > 1) {
                        sumY += dataset.get(j)[1];
                    }
                    count++;
                }
            }
            updatedCentroids.add(count == 0 ? new double[]{0} : new double[]{sumX / count, sumY / (count > 0 ? count : 1)});
        }
        return updatedCentroids;
    }

    public static List<double[]> readData(String fileName, String[] columnHeaders) {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            String[] headers = line.split(",");
            int[] indices = new int[columnHeaders.length];

            // Find the indices of the specified columns
            for (int i = 0; i < columnHeaders.length; i++) {
                for (int j = 0; j < headers.length; j++) {
                    if (headers[j].equalsIgnoreCase(columnHeaders[i])) {
                        indices[i] = j;
                        break;
                    }
                }
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] point = new double[columnHeaders.length];
                boolean valid = true;

                for (int i = 0; i < columnHeaders.length; i++) {
                    try {
                        point[i] = Double.parseDouble(values[indices[i]]);
                    } catch (NumberFormatException e) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    data.add(point);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return data;
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter the path to the CSV file: ");
        String filePath = inputScanner.nextLine();

        System.out.print("Enter the number of clusters (K): ");
        int numClusters = inputScanner.nextInt();
        inputScanner.nextLine(); 

        System.out.print("Enter the column headers for clustering (comma-separated): ");
        String[] columnHeaders = inputScanner.nextLine().split(",");

        List<double[]> dataset = readData(filePath, columnHeaders);

        if (dataset.isEmpty()) {
            System.out.println("No data found for the specified columns.");
            return;
        }

        List<double[]> centroidList = selectRandomCentroids(dataset, numClusters);
        System.out.println("Initial randomly selected centroids:");
        for (int i = 0; i < centroidList.size(); i++) {
            System.out.printf("Centroid %d: %.2f%s%n", i, centroidList.get(i)[0], (centroidList.get(i).length > 1 ? ", " + centroidList.get(i)[1] : ""));
        }
        System.out.println();

        List<Integer> assignments = new ArrayList<>();
        boolean hasConverged = false;
        int iteration = 0;
        int maxIterations = 10;

        while (!hasConverged && iteration < maxIterations) {
            assignments = groupDataPoints(dataset, centroidList);
            List<double[]> newCentroids = calculateNewCentroids(dataset, assignments, numClusters);

            System.out.printf("Iteration %d:\n", iteration + 1);
            for (int i = 0; i < dataset.size(); i++) {
                System.out.printf("Data point (%.2f%s) is in cluster %d%n", dataset.get(i)[0], (dataset.get(i).length > 1 ? ", " + dataset.get(i)[1] : ""), assignments.get(i));
            }
            System.out.println("Current centroids:");
            for (int i = 0; i < centroidList.size(); i++) {
                System.out.printf("Centroid %d: %.2f%s%n", i, centroidList.get(i)[0], (centroidList.get(i).length > 1 ? ", " + centroidList.get(i)[1] : ""));
            }
            System.out.println();

            hasConverged = newCentroids.equals(centroidList);
            centroidList = newCentroids;
            iteration++;
        }

        System.out.println("Final cluster assignments:");
        List<List<double[]>> clusters = new ArrayList<>();
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
            System.out.printf("Centroid %d: %.2f%s%n", i, centroidList.get(i)[0], (centroidList.get(i).length > 1 ? ", " + centroidList.get(i)[1] : ""));
        }

        inputScanner.close();
    }
}
