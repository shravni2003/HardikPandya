import java.io.*;
import java.util.*;

public class hierarchicalComplete {

    public static List<List<String>> readCSV(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            data.add(Arrays.asList(values));
        }
        reader.close();
        return data;
    }

    public static void showData(List<List<String>> data) {
        for (List<String> row : data) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static double computeDist(double val1, double val2) {
        return Math.abs(val1 - val2);
    }

    public static double completeLinkage(double[][] distMatrix, List<Integer> cluster1, List<Integer> cluster2) {
        double maxDist = Double.MIN_VALUE;
        for (int i : cluster1) {
            for (int j : cluster2) {
                maxDist = Math.max(maxDist, distMatrix[i][j]);
            }
        }
        return maxDist;
    }

    public static int[] findNearestClusters(double[][] distMatrix, List<List<Integer>> clusters) {
        double minDist = Double.MAX_VALUE;
        int first = -1, second = -1;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double distance = completeLinkage(distMatrix, clusters.get(i), clusters.get(j));
                if (distance < minDist) {
                    minDist = distance;
                    first = i;
                    second = j;
                }
            }
        }
        return new int[]{first, second};
    }

    public static void showDistMatrix(double[][] distMatrix, List<List<Integer>> clusters) {
        System.out.println("\nDistance Matrix:");
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) {
                    System.out.printf("%8s", "inf");
                } else {
                    double distance = completeLinkage(distMatrix, clusters.get(i), clusters.get(j));
                    System.out.printf("%8.2f", distance);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void clusterAnalysis(List<Double> values, int targetClusters) {
        int count = values.size();
        double[][] distMatrix = new double[count][count];

        for (int i = 0; i < count; ++i) {
            for (int j = 0; j < count; ++j) {
                if (i == j) {
                    distMatrix[i][j] = Double.MAX_VALUE;
                } else {
                    distMatrix[i][j] = computeDist(values.get(i), values.get(j));
                }
            }
        }

        List<List<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            clusters.add(new ArrayList<>(Arrays.asList(i)));
        }

        System.out.println("Initial Distance Matrix:");
        showDistMatrix(distMatrix, clusters);

        while (clusters.size() > targetClusters) {
            int[] nearestClusters = findNearestClusters(distMatrix, clusters);
            int c1 = nearestClusters[0];
            int c2 = nearestClusters[1];
            System.out.println("\nCombining clusters: " + c1 + " and " + c2 
                    + ", distance: " + completeLinkage(distMatrix, clusters.get(c1), clusters.get(c2)) + "\n");

            clusters.get(c1).addAll(clusters.get(c2));
            clusters.remove(c2);

            showDistMatrix(distMatrix, clusters);
        }

        System.out.println("Final Clusters:");
        int clusterId = 0;
        for (List<Integer> cluster : clusters) {
            List<Double> clusterValues = new ArrayList<>();
            for (int index : cluster) {
                clusterValues.add(values.get(index));
            }
            System.out.println("Group " + clusterId++ + ": " + clusterValues);
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the file path: ");
        String filePath = input.nextLine();

        System.out.print("Enter the column name: ");
        String column = input.nextLine();

        System.out.print("Enter number of clusters: ");
        int targetClusters = input.nextInt();

        try {
            List<List<String>> data = readCSV(filePath);
            System.out.println("Data:");
            showData(data);

            int colIndex = -1;
            String[] columns = data.get(0).toArray(new String[0]);
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].trim().equalsIgnoreCase(column)) {
                    colIndex = i;
                    break;
                }
            }

            if (colIndex == -1) {
                System.out.println("Column not found!");
                return;
            }

            List<Double> colValues = new ArrayList<>();
            for (int i = 1; i < data.size(); ++i) {
                colValues.add(Double.parseDouble(data.get(i).get(colIndex)));
            }

            clusterAnalysis(colValues, targetClusters);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error reading number.");
        } finally {
            input.close();
        }
    }
}
