import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Normalization {

    public static List<Double> minMaxNormalization(List<Double> data, double newMin, double newMax) {
        double oldMin = data.stream().min(Double::compare).get();
        double oldMax = data.stream().max(Double::compare).get();

        List<Double> normalizedData = new ArrayList<>();
        for (double value : data) {
            double normalizedValue = ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
            normalizedData.add(normalizedValue);
        }
        return normalizedData;
    }

    public static List<Double> zScoreNormalization(List<Double> data) {
        double mean = data.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double stdDev = Math.sqrt(data.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().getAsDouble());

        List<Double> normalizedData = new ArrayList<>();
        for (double value : data) {
            double zScore = (value - mean) / stdDev;
            normalizedData.add(zScore);
        }
        return normalizedData;
    }

    public static List<Double> loadData(String filename, String columnName) {
        List<Double> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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
                System.err.println("Column '" + columnName + "' not found in the dataset.");
                return data;
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    data.add(Double.parseDouble(values[columnIndex]));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in the data.");
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return data;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the file name (with extension): ");
        String filename = scanner.nextLine();

        System.out.print("Enter the column name for normalization: ");
        String columnName = scanner.nextLine();

        List<Double> data = loadData(filename, columnName);

        if (data.isEmpty()) {
            System.out.println("No data found for column '" + columnName + "'.");
            return;
        }

        System.out.print("Enter new minimum value for Min-Max normalization: ");
        double newMin = scanner.nextDouble();

        System.out.print("Enter new maximum value for Min-Max normalization: ");
        double newMax = scanner.nextDouble();

        List<Double> minMaxNormalizedData = minMaxNormalization(data, newMin, newMax);

        List<Double> zScoreNormalizedData = zScoreNormalization(data);

        System.out.printf("%-15s %-20s %-20s%n", "Original", "Min-Max Normalized", "Z-Score Normalized");
        for (int i = 0; i < data.size(); i++) {
            System.out.printf("%-15.2f %-20.2f %-20.2f%n", data.get(i), minMaxNormalizedData.get(i), zScoreNormalizedData.get(i));
        }

        scanner.close();
    }
}
