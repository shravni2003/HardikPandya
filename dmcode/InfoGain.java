import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InfoGain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of the CSV file: ");
        String filePath = scanner.nextLine();
        List<Map<String, String>> data = readCSV(filePath);
        System.out.print("Enter the target column (e.g., Class): ");
        String targetColumn = scanner.nextLine();

        System.out.println("Choose output format:");
        System.out.println("1. Original Output");
        System.out.println("2. Formatted Table Output");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                originalOutput(data, targetColumn);
                break;
            case 2:
                formattedTableOutput(data, targetColumn);
                break;
            default:
                System.out.println("Invalid choice! Please select either 1 or 2.");
        }
        
        scanner.close();
    }

    private static List<Map<String, String>> readCSV(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = br.readLine().split(",");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static void originalOutput(List<Map<String, String>> data, String targetColumn) {
        Map<String, Double> infoGains = new HashMap<>();
        double totalEntropy = calculateEntropy(data, targetColumn);
        System.out.printf("\nTotal Entropy for '%s': %.4f%n", targetColumn, totalEntropy);

        for (String column : data.get(0).keySet()) {
            if (!column.equals(targetColumn)) {
                System.out.println("\nCalculating for column: " + column);
                double infoGain = calculateInformationGainOriginal(data, column, targetColumn);
                infoGains.put(column, infoGain);
            }
        }

        String bestColumn = Collections.max(infoGains.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("\nThe column with the highest information gain is: '%s'%n", bestColumn);
    }

    private static void formattedTableOutput(List<Map<String, String>> data, String targetColumn) {
        Map<String, Double> infoGains = new HashMap<>();
        double totalEntropy = calculateEntropy(data, targetColumn);
        System.out.printf("\nTotal Entropy for '%s': %.4f%n", targetColumn, totalEntropy);

        System.out.printf("\n%-15s %-10s %-20s %-15s %-15s\n", "Column", "Subset", "Counts (Play/NoPlay)", "Subset Entropy", "Information Gain");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (String column : data.get(0).keySet()) {
            if (!column.equals(targetColumn)) {
                double infoGain = calculateInformationGainFormatted(data, column, targetColumn);
                infoGains.put(column, infoGain);
            }
        }

        String bestColumn = Collections.max(infoGains.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("\nThe column with the highest information gain is: '%s'%n", bestColumn);
    }

    private static double calculateEntropy(List<Map<String, String>> data, String targetColumn) {
        Map<String, Integer> frequency = new HashMap<>();
        for (Map<String, String> row : data) {
            String key = row.get(targetColumn);
            frequency.put(key, frequency.getOrDefault(key, 0) + 1);
        }

        double entropy = 0.0;
        int total = data.size();
        for (int count : frequency.values()) {
            double probability = (double) count / total;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }

    private static double calculateInformationGainOriginal(List<Map<String, String>> data, String column, String targetColumn) {
        double totalEntropy = calculateEntropy(data, targetColumn);
        Map<String, List<Map<String, String>>> subsets = new HashMap<>();
        for (Map<String, String> row : data) {
            String key = row.get(column);
            subsets.putIfAbsent(key, new ArrayList<>());
            subsets.get(key).add(row);
        }

        double weightedEntropy = 0.0;
        for (Map.Entry<String, List<Map<String, String>>> subsetEntry : subsets.entrySet()) {
            String subsetKey = subsetEntry.getKey();
            List<Map<String, String>> subset = subsetEntry.getValue();
            double subsetEntropy = calculateEntropy(subset, targetColumn);
            weightedEntropy += ((double) subset.size() / data.size()) * subsetEntropy;

            Map<String, Integer> subsetTargetCount = new HashMap<>();
            for (Map<String, String> row : subset) {
                String key = row.get(targetColumn);
                subsetTargetCount.put(key, subsetTargetCount.getOrDefault(key, 0) + 1);
            }

            System.out.printf("Subset '%s' counts:\n", subsetKey);
            for (Map.Entry<String, Integer> entry : subsetTargetCount.entrySet()) {
                System.out.printf("%s: %d\n", entry.getKey(), entry.getValue());
            }
            System.out.printf("Entropy for subset '%s': %.4f%n", subsetKey, subsetEntropy);
        }

        double infoGain = totalEntropy - weightedEntropy;
        System.out.printf("Information Gain for '%s': %.4f%n", column, infoGain);
        return infoGain;
    }

    private static double calculateInformationGainFormatted(List<Map<String, String>> data, String column, String targetColumn) {
        double totalEntropy = calculateEntropy(data, targetColumn);
        Map<String, List<Map<String, String>>> subsets = new HashMap<>();
        for (Map<String, String> row : data) {
            String key = row.get(column);
            subsets.putIfAbsent(key, new ArrayList<>());
            subsets.get(key).add(row);
        }

        double weightedEntropy = 0.0;
        for (Map.Entry<String, List<Map<String, String>>> subsetEntry : subsets.entrySet()) {
            String subsetKey = subsetEntry.getKey();
            List<Map<String, String>> subset = subsetEntry.getValue();
            double subsetEntropy = calculateEntropy(subset, targetColumn);
            weightedEntropy += ((double) subset.size() / data.size()) * subsetEntropy;

            Map<String, Integer> subsetTargetCount = new HashMap<>();
            for (Map<String, String> row : subset) {
                String key = row.get(targetColumn);
                subsetTargetCount.put(key, subsetTargetCount.getOrDefault(key, 0) + 1);
            }

            String counts = String.format("%d/%d", subsetTargetCount.getOrDefault("Play", 0), subsetTargetCount.getOrDefault("NoPlay", 0));
            System.out.printf("%-15s %-10s %-20s %-15.4f%n", column, subsetKey, counts, subsetEntropy);
        }

        double infoGain = totalEntropy - weightedEntropy;
        System.out.printf("%-15s %-10s %-20s %-15s %-15.4f\n", column, "-", "-", "-", infoGain);
        return infoGain;
    }
}
