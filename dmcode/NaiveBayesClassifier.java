import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NaiveBayesClassifier {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();

        String[][] data = readCSV(filePath);
        if (data == null || data.length <= 1) {
            System.out.println("Failed to read data from CSV.");
            return;
        }

        printCSVData(data);
        
        System.out.print("Enter the Age: ");
        String ageInput = scanner.nextLine();
        System.out.print("Enter the Income (Low/Medium/High): ");
        String incomeInput = scanner.nextLine();
        System.out.print("Are you a student? (Yes/No): ");
        String studentInput = scanner.nextLine();
        System.out.print("Enter the Credit Rating (Fair/Average/Good/Excellent): ");
        String creditInput = scanner.nextLine();

        String predictedClass = classify(data, ageInput, incomeInput, studentInput, creditInput);
        System.out.println("\nPredicted Buys_comp: " + predictedClass);
    }

    private static String[][] readCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int rowCount = 0;
            int colCount = 0;

            while ((line = br.readLine()) != null) {
                if (rowCount == 0) {
                    colCount = line.split(",").length;
                }
                rowCount++;
            }

            String[][] data = new String[rowCount][colCount];
            br.close(); 
            BufferedReader br2 = new BufferedReader(new FileReader(filePath));
            int currentRow = 0;
            while ((line = br2.readLine()) != null) {
                String[] values = line.split(",");
                System.arraycopy(values, 0, data[currentRow], 0, values.length);
                currentRow++;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void printCSVData(String[][] data) {
        for (String[] row : data) {
            for (String value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private static String classify(String[][] data, String age, String income, String student, String credit) {
        Map<String, Double> priorProbabilities = calculatePriorProbabilities(data);
        Map<String, Map<String, Double>> ageProbabilities = calculateLikelihoods(data, "Age");
        Map<String, Map<String, Double>> incomeProbabilities = calculateLikelihoods(data, "Income");
        Map<String, Map<String, Double>> studentProbabilities = calculateLikelihoods(data, "Student");
        Map<String, Map<String, Double>> creditProbabilities = calculateLikelihoods(data, "Credit rating");

        Map<String, Double> finalProbabilities = new HashMap<>();

        for (String cls : priorProbabilities.keySet()) {
            double prior = priorProbabilities.get(cls);
            double ageProb = ageProbabilities.getOrDefault(cls, new HashMap<>()).getOrDefault(age, 0.0);
            double incomeProb = incomeProbabilities.getOrDefault(cls, new HashMap<>()).getOrDefault(income, 0.0);
            double studentProb = studentProbabilities.getOrDefault(cls, new HashMap<>()).getOrDefault(student, 0.0);
            double creditProb = creditProbabilities.getOrDefault(cls, new HashMap<>()).getOrDefault(credit, 0.0);

            double totalProb = prior * ageProb * incomeProb * studentProb * creditProb;
            finalProbabilities.put(cls, totalProb);

            System.out.printf("\nClass: %s%n", cls);
            System.out.printf("Prior: P(%s) = %.4f%n", cls, prior);
            System.out.printf("P(Age=%s | Class=%s) = %.4f%n", age, cls, ageProb);
            System.out.printf("P(Income=%s | Class=%s) = %.4f%n", income, cls, incomeProb);
            System.out.printf("P(Student=%s | Class=%s) = %.4f%n", student, cls, studentProb);
            System.out.printf("P(Credit Rating=%s | Class=%s) = %.4f%n", credit, cls, creditProb);
            System.out.printf("Total Probability: P(%s | Age=%s, Income=%s, Student=%s, Credit Rating=%s) = %.4f%n",
                    cls, age, income, student, credit, totalProb);
        }

        return finalProbabilities.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    private static Map<String, Double> calculatePriorProbabilities(String[][] data) {
        Map<String, Integer> classCounts = new HashMap<>();
        int totalRows = data.length - 1; 

        for (int i = 1; i < data.length; i++) {
            String cls = data[i][data[0].length - 1];
            classCounts.put(cls, classCounts.getOrDefault(cls, 0) + 1);
        }

        Map<String, Double> priors = new HashMap<>();
        for (Map.Entry<String, Integer> entry : classCounts.entrySet()) {
            priors.put(entry.getKey(), (double) entry.getValue() / totalRows);
        }
        return priors;
    }

    private static Map<String, Map<String, Double>> calculateLikelihoods(String[][] data, String attribute) {
        int attrIndex = -1;
        for (int i = 0; i < data[0].length; i++) {
            if (data[0][i].equalsIgnoreCase(attribute)) {
                attrIndex = i;
                break;
            }
        }
        if (attrIndex == -1) {
            throw new IllegalArgumentException("Attribute not found: " + attribute);
        }

        Map<String, Map<String, Integer>> counts = new HashMap<>();
        Map<String, Integer> classCounts = new HashMap<>();

        for (int i = 1; i < data.length; i++) {
            String cls = data[i][data[0].length - 1];
            String value = data[i][attrIndex];

            counts.putIfAbsent(cls, new HashMap<>());
            Map<String, Integer> valueCounts = counts.get(cls);
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
            classCounts.put(cls, classCounts.getOrDefault(cls, 0) + 1);
        }

        Map<String, Map<String, Double>> likelihoods = new HashMap<>();
        for (String cls : counts.keySet()) {
            Map<String, Integer> valueCounts = counts.get(cls);
            Map<String, Double> classProbabilities = new HashMap<>();
            int totalClassCount = classCounts.get(cls);
            int uniqueValuesCount = valueCounts.size(); 
            for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
                classProbabilities.put(entry.getKey(), (entry.getValue() + 1.0) / (totalClassCount + uniqueValuesCount));
            }

            for (String value : valueCounts.keySet()) {
                if (!classProbabilities.containsKey(value)) {
                    classProbabilities.put(value, 1.0 / (totalClassCount + uniqueValuesCount));
                }
            }

            likelihoods.put(cls, classProbabilities);
        }
        return likelihoods;
    }
}