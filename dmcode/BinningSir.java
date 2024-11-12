import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BinningSir {

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
            System.out.println("1. Binning by Depth with Median Replacement");
            System.out.println("2. Binning by Bin Boundaries with Boundary Replacement");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    binningByDepthWithMedianReplacement(data, column, numBins);
                    break;
                case 2:
                    binningByBinBoundaries(data, column, numBins);
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

    private static void binningByDepthWithMedianReplacement(List<Map<String, String>> data, String column, int numBins) {
        List<Double> values = new ArrayList<>();
        for (Map<String, String> row : data) {
            values.add(Double.parseDouble(row.get(column)));
        }

        Collections.sort(values);
        int binSize = values.size() / numBins;

        System.out.println("Binning by Depth with Median Replacement:");

        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            int startIndex = i * binSize;
            int endIndex = (i == numBins - 1) ? values.size() : (i + 1) * binSize;
            List<Double> bin = values.subList(startIndex, endIndex);

            // Calculate the median of the bin
            double median = bin.size() % 2 == 0 ?
                    (bin.get(bin.size() / 2 - 1) + bin.get(bin.size() / 2)) / 2.0 :
                    bin.get(bin.size() / 2);

            // Replace each value in the bin by the median
            List<Double> replacedBin = new ArrayList<>(Collections.nCopies(bin.size(), median));
            bins.add(replacedBin);

            System.out.printf("Bin %d (Median %.2f): %s%n", i + 1, median, replacedBin);
        }
    }

    private static void binningByBinBoundaries(List<Map<String, String>> data, String column, int numBins) {
        List<Double> values = new ArrayList<>();
        for (Map<String, String> row : data) {
            values.add(Double.parseDouble(row.get(column)));
        }

        double min = Collections.min(values);
        double max = Collections.max(values);
        double binWidth = (max - min) / numBins;

        System.out.println("Binning by Bin Boundaries with Boundary Replacement:");

        List<List<Double>> bins = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            double lowerBound = min + i * binWidth;
            double upperBound = (i == numBins - 1) ? max : lowerBound + binWidth;
            List<Double> bin = new ArrayList<>();

            for (double value : values) {
                if (value >= lowerBound && value < upperBound || (i == numBins - 1 && value == max)) {
                    double replacementValue = Math.abs(value - lowerBound) < Math.abs(value - upperBound) ? lowerBound : upperBound;
                    bin.add(replacementValue);
                }
            }
            bins.add(bin);

            System.out.printf("Bin %d (Boundaries %.2f - %.2f): %s%n", i + 1, lowerBound, upperBound, bin);
        }
    }
}
