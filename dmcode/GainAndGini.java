import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GainAndGini {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();
        
        String[][] data = readCSV(filePath);
        if (data == null) return;

        int totalInstances = data.length - 1;
        String targetClass = "Class";

        double overallEntropy = calculateEntropy(data, targetClass, totalInstances);
        System.out.printf("Overall Entropy: %.4f%n", overallEntropy);

        String[] attributes = {"Outlook", "Temperature", "Humidity", "Windy"};
        double[] gains = new double[attributes.length];
        double[] giniIndices = new double[attributes.length];

        for (int j = 0; j < attributes.length; j++) {
            String attribute = attributes[j];
            gains[j] = calculateInformationGain(data, attribute, targetClass, totalInstances, overallEntropy);
            giniIndices[j] = calculateGiniIndex(data, attribute, targetClass, totalInstances);
        }

        System.out.printf("%n%30s%20s%20s%n", "Attribute", "Information Gain", "Gini Index");
        System.out.println("---------------------------------------------------------");
        for (int j = 0; j < attributes.length; j++) {
            System.out.printf("%30s%20.4f%20.4f%n", attributes[j], gains[j], giniIndices[j]);
        }

        int bestGainIndex = 0;
        int bestGiniIndex = 0;
        for (int j = 1; j < attributes.length; j++) {
            if (gains[j] > gains[bestGainIndex]) {
                bestGainIndex = j;
            }
            if (giniIndices[j] < giniIndices[bestGiniIndex]) {
                bestGiniIndex = j;
            }
        }

        System.out.printf("%nSelected attribute for split based on Information Gain: %s (Gain: %.4f)%n", attributes[bestGainIndex], gains[bestGainIndex]);
        System.out.printf("Selected attribute for split based on Gini Index: %s (Gini: %.4f)%n", attributes[bestGiniIndex], giniIndices[bestGiniIndex]);
        
        scanner.close();
    }

    private static String[][] readCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[][] data = new String[15][5]; 
            int row = 0;
            while ((line = br.readLine()) != null) {
                data[row++] = line.split(",");
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double calculateEntropy(String[][] data, String targetClass, int totalInstances) {
        Map<String, Integer> classCounts = new HashMap<>();
        for (int i = 1; i < data.length; i++) {
            String cls = data[i][4];
            classCounts.put(cls, classCounts.getOrDefault(cls, 0) + 1);
        }

        double entropy = 0.0;
        for (int count : classCounts.values()) {
            double probability = (double) count / totalInstances;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }

    private static double calculateInformationGain(String[][] data, String attribute, String targetClass, int totalInstances, double overallEntropy) {
        Map<String, Map<String, Integer>> subsetCounts = new HashMap<>();
        Map<String, Integer> totalCounts = new HashMap<>();

        int attrIndex = getAttributeIndex(attribute);
        int classIndex = getAttributeIndex(targetClass);

        for (int i = 1; i < data.length; i++) {
            String attrValue = data[i][attrIndex];
            String cls = data[i][classIndex];
            subsetCounts.putIfAbsent(attrValue, new HashMap<>());
            subsetCounts.get(attrValue).put(cls, subsetCounts.get(attrValue).getOrDefault(cls, 0) + 1);
            totalCounts.put(attrValue, totalCounts.getOrDefault(attrValue, 0) + 1);
        }

        double weightedEntropy = 0.0;
        System.out.printf("Calculating Information Gain for %s:%n", attribute);
        for (String attrValue : subsetCounts.keySet()) {
            Map<String, Integer> counts = subsetCounts.get(attrValue);
            int subsetSize = totalCounts.get(attrValue);
            double subsetEntropy = 0.0;

            System.out.printf("  %s: ", attrValue);
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                String cls = entry.getKey();
                int count = entry.getValue();
                double probability = (double) count / subsetSize;
                subsetEntropy -= probability * (Math.log(probability) / Math.log(2));
                System.out.printf("%s: %d ", cls, count);
            }
            System.out.printf("=> Subset Entropy: %.4f%n", subsetEntropy);

            weightedEntropy += (double) subsetSize / totalInstances * subsetEntropy;
        }

        System.out.printf("Weighted Entropy for %s: %.4f%n", attribute, weightedEntropy);
        return overallEntropy - weightedEntropy;
    }

    private static double calculateGiniIndex(String[][] data, String attribute, String targetClass, int totalInstances) {
        Map<String, Map<String, Integer>> subsetCounts = new HashMap<>();
        Map<String, Integer> totalCounts = new HashMap<>();

        int attrIndex = getAttributeIndex(attribute);
        int classIndex = getAttributeIndex(targetClass);

        for (int i = 1; i < data.length; i++) {
            String attrValue = data[i][attrIndex];
            String cls = data[i][classIndex];
            subsetCounts.putIfAbsent(attrValue, new HashMap<>());
            subsetCounts.get(attrValue).put(cls, subsetCounts.get(attrValue).getOrDefault(cls, 0) + 1);
            totalCounts.put(attrValue, totalCounts.getOrDefault(attrValue, 0) + 1);
        }

        double giniIndex = 0.0;
        System.out.printf("Calculating Gini Index for %s:%n", attribute);
        for (String attrValue : subsetCounts.keySet()) {
            Map<String, Integer> counts = subsetCounts.get(attrValue);
            int subsetSize = totalCounts.get(attrValue);
            double sumOfSquares = 0.0;

            for (int count : counts.values()) {
                double probability = (double) count / subsetSize;
                sumOfSquares += probability * probability;
            }

            giniIndex += (double) subsetSize / totalInstances * (1 - sumOfSquares);
            System.out.printf("  %s: Gini Index Contribution: %.4f%n", attrValue, (1 - sumOfSquares));
        }

        return giniIndex;
    }

    private static int getAttributeIndex(String attribute) {
        switch (attribute) {
            case "Outlook": return 0;
            case "Temperature": return 1;
            case "Humidity": return 2;
            case "Windy": return 3;
            case "Class": return 4;
            default: return -1;
        }
    }
}
