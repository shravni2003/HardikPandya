import java.io.*;
import java.util.*;

public class hierarchicalSingle {

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

    public static int[] findNearestClusters(double[][] distMatrix, List<Integer> clusters) {
        double minDist = Double.MAX_VALUE;
        int first = -1, second = -1;

        for (int i : clusters) {
            for (int j : clusters) {
                if (i != j && distMatrix[i][j] < minDist) {
                    minDist = distMatrix[i][j];
                    first = i;
                    second = j;
                }
            }
        }
        return new int[] { first, second };
    }

    public static void showDistMatrix(double[][] distMatrix, List<Integer> clusters) {
        System.out.println("\nDistance Matrix:");
        for (int i : clusters) {
            for (int j : clusters) {
                if (i == j) {
                    System.out.printf("%8s", "inf");
                } else {
                    System.out.printf("%8.2f", distMatrix[i][j]);
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

        List<Integer> activeClusters = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            activeClusters.add(i);
        }
        showDistMatrix(distMatrix, activeClusters);

        int[] clusterLabels = new int[count];
        for (int i = 0; i < count; ++i) {
            clusterLabels[i] = i;
        }

        while (activeClusters.size() > targetClusters) {
            int[] nearestClusters = findNearestClusters(distMatrix, activeClusters);
            int c1 = nearestClusters[0];
            int c2 = nearestClusters[1];
            System.out.println("\nCombining clusters: " + c1 + " and " + c2 + ", distance: " 
                    + distMatrix[c1][c2] + "\n");

            for (int i : activeClusters) {
                if (i != c1 && i != c2) {
                    distMatrix[c1][i] = distMatrix[i][c1] = 
                        Math.min(distMatrix[c1][i], distMatrix[c2][i]);
                }
            }

            for (int i = 0; i < count; ++i) {
                if (clusterLabels[i] == c2) {
                    clusterLabels[i] = c1;
                }
            }

            activeClusters.remove(Integer.valueOf(c2));
            showDistMatrix(distMatrix, activeClusters);
        }

        Map<Integer, List<Double>> groups = new HashMap<>();
        for (int i = 0; i < count; ++i) {
            int groupId = clusterLabels[i];
            groups.computeIfAbsent(groupId, k -> new ArrayList<>()).add(values.get(i));
        }

        Map<Integer, List<Double>> finalClusters = new HashMap<>();
        int newLabel = 0;
        Map<Integer, Integer> labelMap = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> entry : groups.entrySet()) {
            labelMap.put(entry.getKey(), newLabel);
            finalClusters.put(newLabel++, entry.getValue());
        }

        System.out.println("Clusters:");
        for (Map.Entry<Integer, List<Double>> entry : finalClusters.entrySet()) {
            System.out.println("Group " + entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the CSV file path: ");
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
