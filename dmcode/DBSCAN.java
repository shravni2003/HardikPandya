import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class DBSCAN {

    static class Point {
        double value;
        boolean isVisited;
        boolean isNoise;
        boolean isCore;
        List<Point> neighbors;

        Point(double value) {
            this.value = value;
            this.isVisited = false;
            this.isNoise = false;
            this.isCore = false;
            this.neighbors = new ArrayList<>();
        }
    }

    public static List<Point> readDataFromCSV(String fileName, String columnName) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            String[] headers = line.split(",");
            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                System.err.println("Column '" + columnName + "' not found in the file.");
                return points;
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    double value = Double.parseDouble(values[columnIndex]);
                    points.add(new Point(value));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid data: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return points;
    }

    public static List<List<Point>> performDBSCAN(List<Point> dataset, double epsilon, int minPts) {
        List<List<Point>> clusters = new ArrayList<>();
        for (Point point : dataset) {
            if (!point.isVisited) {
                point.isVisited = true;
                List<Point> neighbors = findNeighbors(dataset, point, epsilon);
                if (neighbors.size() < minPts) {
                    point.isNoise = true;
                } else {
                    List<Point> cluster = new ArrayList<>();
                    clusters.add(cluster);
                    expandCluster(cluster, point, neighbors, dataset, epsilon, minPts);
                }
            }
        }
        return clusters;
    }

    private static void expandCluster(List<Point> cluster, Point point, List<Point> neighbors, List<Point> dataset, double epsilon, int minPts) {
        cluster.add(point);
        point.isCore = true;
        HashSet<Point> neighborsSet = new HashSet<>(neighbors);
        
        while (!neighborsSet.isEmpty()) {
            Point current = neighborsSet.iterator().next();
            neighborsSet.remove(current);
            
            if (!current.isVisited) {
                current.isVisited = true;
                List<Point> currentNeighbors = findNeighbors(dataset, current, epsilon);
                if (currentNeighbors.size() >= minPts) {
                    neighborsSet.addAll(currentNeighbors);
                }
            }
            if (!cluster.contains(current)) {
                cluster.add(current);
            }
        }
    }

    private static List<Point> findNeighbors(List<Point> dataset, Point point, double epsilon) {
        List<Point> neighbors = new ArrayList<>();
        for (Point p : dataset) {
            if (Math.abs(p.value - point.value) <= epsilon) {
                neighbors.add(p);
            }
        }
        return neighbors;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CSV file path: ");
        String filePath = scanner.nextLine();
        System.out.print("Enter the column name for clustering: ");
        String columnName = scanner.nextLine();

        List<Point> dataset = readDataFromCSV(filePath, columnName);

        if (dataset.isEmpty()) {
            System.out.println("No data to process.");
            return;
        }

        double epsilon = 2.0; 
        int minPts = 3; 

        List<List<Point>> clusters = performDBSCAN(dataset, epsilon, minPts);

        System.out.println("\nClustering Results:");
        for (int i = 0; i < clusters.size(); i++) {
            System.out.print("Cluster " + (i + 1) + ": ");
            for (Point p : clusters.get(i)) {
                System.out.print(p.value + " ");
            }
            System.out.println();
        }

        System.out.print("Noise Points: ");
        for (Point p : dataset) {
            if (p.isNoise) {
                System.out.print(p.value + " ");
            }
        }
        System.out.println();
        scanner.close();
    }
}
