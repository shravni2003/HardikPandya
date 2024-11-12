import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClusterAndDistance {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();

        List<Double[]> dataPoints = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 4) {
                    double sepalLength = Double.parseDouble(values[0]);
                    double sepalWidth = Double.parseDouble(values[1]);
                    dataPoints.add(new Double[]{sepalLength, sepalWidth});
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
            return;
        }

        if (!dataPoints.isEmpty()) {
            Double[] centroid = computeCentroid(dataPoints);
            System.out.printf("Centroid: (%.2f, %.2f)%n", centroid[0], centroid[1]);
            System.out.println("Distance matrix:");
            displayDistanceMatrix(dataPoints, centroid);
        } else {
            System.out.println("No data available for clustering.");
        }
    }

    private static Double[] computeCentroid(List<Double[]> points) {
        double sumX = 0, sumY = 0;
        for (Double[] point : points) {
            sumX += point[0];
            sumY += point[1];
        }
        return new Double[]{sumX / points.size(), sumY / points.size()};
    }

    private static void displayDistanceMatrix(List<Double[]> points, Double[] centroid) {
        int n = points.size();
        double[][] distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i >= j) {
                    double distance = calculateDistance(points.get(i), points.get(j));
                    distanceMatrix[i][j] = distance;
                    System.out.printf("Distance from point %d to point %d: %.4f%n", i + 1, j + 1, distance);
                }
            }
        }

        System.out.println("\nLower Triangular Distance Matrix:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i >= j) {
                    System.out.printf("%.4f ", distanceMatrix[i][j]);
                } else {
                    System.out.print("0.0000 ");
                }
            }
            System.out.println();
        }
    }

    private static double calculateDistance(Double[] point1, Double[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }
}
