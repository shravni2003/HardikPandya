import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinearRegressionFromCSV {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();

        List<Double> ages = new ArrayList<>();
        List<Double> heights = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            System.out.println("\nCSV Data:");
            System.out.println("Age\tHeight");
            System.out.println("----------------");

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; 
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    double age = Double.parseDouble(values[0]);
                    double height = Double.parseDouble(values[1]);
                    ages.add(age);
                    heights.add(height);
                    System.out.printf("%.1f\t%.1f%n", age, height);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
            return;
        }

        if (ages.size() > 0 && heights.size() > 0) {
            LinearRegression lr = new LinearRegression(ages, heights);
            lr.performRegression();
            lr.displayResults();
        } else {
            System.out.println("No data available for regression.");
        }
    }
}

class LinearRegression {
    private final List<Double> x; 
    private final List<Double> y;
    private double slope;
    private double intercept;
    private double rSquared;

    public LinearRegression(List<Double> x, List<Double> y) {
        this.x = x;
        this.y = y;
    }

    public void performRegression() {
        int n = x.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i);
            sumY2 += y.get(i) * y.get(i);
        }

        slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        intercept = (sumY - slope * sumX) / n;

        double ssTotal = 0, ssResidual = 0;
        double meanY = sumY / n;
        for (int i = 0; i < n; i++) {
            double predictedY = slope * x.get(i) + intercept;
            ssTotal += (y.get(i) - meanY) * (y.get(i) - meanY);
            ssResidual += (y.get(i) - predictedY) * (y.get(i) - predictedY);
        }
        rSquared = 1 - (ssResidual / ssTotal);
    }

    public void displayResults() {
        System.out.printf("\nSlope (m): %.4f%n", slope);
        System.out.printf("Intercept (b): %.4f%n", intercept);
        System.out.printf("R-squared: %.4f%n", rSquared);
        System.out.println();

        System.out.println("Linear Regression Equation: y = " + slope + " * x + " + intercept);

        System.out.println("\nPredictions:");
        for (int i = 0; i < x.size(); i++) {
            double predictedY = slope * x.get(i) + intercept;
            System.out.printf("Age: %.1f, Actual Height: %.1f, Predicted Height: %.2f%n", x.get(i), y.get(i), predictedY);
        }
    }
}
