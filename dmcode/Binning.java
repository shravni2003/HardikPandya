import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Binning {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path of the CSV file: ");
        String filePath = scanner.nextLine();

        List<Map<String, String>> data = readCSV(filePath);

        while (true) {
            System.out.print("Enter the column name for binning (or '/' to exit): ");
            String column = scanner.nextLine();
            if (column.equals("/")) {
                break;
            }

            System.out.print("Enter the number of bins: ");
            int numBins = Integer.parseInt(scanner.nextLine());

            System.out.println("Choose the type of binning:");
            System.out.println("1. Equal Width");
            System.out.println("2. Equal Frequency");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    equalWidthBinning(data, column, numBins);
                    break;
                case 2:
                    equalFrequencyBinning(data, column, numBins);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
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

    private static void equalWidthBinning(List<Map<String, String>> data, String column, int numBins) {
        List<Double> values = new ArrayList<>();
        for (Map<String, String> row : data) {
            values.add(Double.parseDouble(row.get(column)));
        }

        double min = Collections.min(values);
        double max = Collections.max(values);
        double binWidth = (max - min) / numBins;

        System.out.printf("Equal Width Binning:\nMin: %.2f, Max: %.2f, Bin Width: %.2f%n", min, max, binWidth);
        
        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            bins.add(new ArrayList<>());
            double lowerBound = min + i * binWidth;
            double upperBound = (i == numBins - 1) ? max : lowerBound + binWidth;
            System.out.printf("Bin %d: [%.2f, %.2f)%n", i + 1, lowerBound, upperBound);

            for (double value : values) {
                if (value >= lowerBound && value < upperBound) {
                    bins.get(i).add(value);
                }
            }
        }

        for (int i = 0; i < bins.size(); i++) {
            System.out.printf("Values in Bin %d: %s%n", i + 1, bins.get(i).toString());
        }
    }

    private static void equalFrequencyBinning(List<Map<String, String>> data, String column, int numBins) {
        List<Double> values = new ArrayList<>();
        for (Map<String, String> row : data) {
            values.add(Double.parseDouble(row.get(column)));
        }

        Collections.sort(values);
        int binSize = values.size() / numBins;

        System.out.println("Equal Frequency Binning:");
        
        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            bins.add(new ArrayList<>());
            int startIndex = i * binSize;
            int endIndex = (i == numBins - 1) ? values.size() : (i + 1) * binSize;
            double lowerBound = values.get(startIndex);
            double upperBound = values.get(endIndex - 1);
            System.out.printf("Bin %d: [%.2f, %.2f]%n", i + 1, lowerBound, upperBound);

            for (int j = startIndex; j < endIndex; j++) {
                bins.get(i).add(values.get(j));
            }
        }

        for (int i = 0; i < bins.size(); i++) {
            System.out.printf("Values in Bin %d: %s%n", i + 1, bins.get(i).toString());
        }
    }
}
