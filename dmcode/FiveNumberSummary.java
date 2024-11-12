import java.io.*;
import java.util.*;

public class FiveNumberSummary {

    public static double calculateMedian(List<Double> data) {
        int n = data.size();
        Collections.sort(data);
        if (n % 2 == 0) {
            return (data.get(n / 2 - 1) + data.get(n / 2)) / 2.0;
        } else {
            return data.get(n / 2);
        }
    }

    public static double[] calculateQuartiles(List<Double> data) {
        Collections.sort(data);
        int n = data.size();

        List<Double> lowerHalf, upperHalf;
        if (n % 2 == 0) {
            lowerHalf = data.subList(0, n / 2);
            upperHalf = data.subList(n / 2, n);
        } else {
            lowerHalf = data.subList(0, n / 2);
            upperHalf = data.subList(n / 2 + 1, n);
        }

        double q1 = calculateMedian(lowerHalf);
        double q3 = calculateMedian(upperHalf);
        return new double[]{q1, q3};
    }

    public static double[] fiveNumberSummary(List<Double> data) {
        Collections.sort(data);
        double min = data.get(0);
        double max = data.get(data.size() - 1);
        double median = calculateMedian(data);
        double[] quartiles = calculateQuartiles(data);

        return new double[]{min, quartiles[0], median, quartiles[1], max};
    }

    public static double[] calculateWhiskers(List<Double> data, double q1, double q3) {
        double iqr = q3 - q1;
        double lowerWhisker = Math.max(data.get(0), q1 - 1.5 * iqr);
        double upperWhisker = Math.min(data.get(data.size() - 1), q3 + 1.5 * iqr);

        return new double[]{lowerWhisker, upperWhisker};
    }
    public static List<Double> loadData(String filename, String columnName) {
        List<Double> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); 
            if (line == null) {
                System.err.println("Error: Empty file.");
                return data;
            }

            String[] headers = line.split(",");
            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                System.err.println("Error: Column '" + columnName + "' not found in the dataset.");
                return data;
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    data.add(Double.parseDouble(values[columnIndex]));
                } catch (NumberFormatException e) {
                    System.err.println("Error: Invalid data in the column.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not open file " + filename);
        }

        return data;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the filename (CSV format): ");
        String filename = scanner.nextLine();

        System.out.print("Enter the column name for analysis: ");
        String columnName = scanner.nextLine();

        List<Double> data = loadData(filename, columnName);
        if (data.isEmpty()) {
            System.out.println("No data found for column '" + columnName + "'.");
            return;
        }

        double[] summary = fiveNumberSummary(data);
        System.out.println("Five-number summary for '" + columnName + "':");
        System.out.println("Minimum: " + summary[0]);
        System.out.println("Q1 (25th percentile): " + summary[1]);
        System.out.println("Median (Q2): " + summary[2]);
        System.out.println("Q3 (75th percentile): " + summary[3]);
        System.out.println("Maximum: " + summary[4]);

        double[] whiskers = calculateWhiskers(data, summary[1], summary[3]);
        System.out.println("Lower Whisker: " + whiskers[0]);
        System.out.println("Upper Whisker: " + whiskers[1]);
    }
}
