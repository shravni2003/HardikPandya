import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {

    public static List<List<String>> readData(String fileName) {
        List<List<String>> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            Map<Integer, Set<String>> tempMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0].trim());
                String item = values[1].trim();
                tempMap.putIfAbsent(id, new HashSet<>());
                tempMap.get(id).add(item);
            }
            for (Set<String> items : tempMap.values()) {
                transactions.add(new ArrayList<>(items));
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return transactions;
    }

    public static Set<Set<String>> generateFrequentItemsets(List<List<String>> transactions, double minSupport) {
        Map<Set<String>, Integer> itemCountMap = new HashMap<>();
        Set<Set<String>> frequentItemsets = new HashSet<>();

        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                Set<String> itemSet = new HashSet<>(Collections.singleton(item));
                itemCountMap.put(itemSet, itemCountMap.getOrDefault(itemSet, 0) + 1);
            }
        }

        System.out.println("Item Counts:");
        for (Map.Entry<Set<String>, Integer> entry : itemCountMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        for (Map.Entry<Set<String>, Integer> entry : itemCountMap.entrySet()) {
            double support = (double) entry.getValue() / transactions.size();
            if (support >= minSupport) {
                frequentItemsets.add(entry.getKey());
                System.out.println("Frequent Itemset: " + entry.getKey() + " | Support: " + support);
            }
        }

        int k = 2;
        while (!itemCountMap.isEmpty()) {
            Map<Set<String>, Integer> newCountMap = new HashMap<>();
            List<Set<String>> currentFrequentItemsets = new ArrayList<>(frequentItemsets);

            for (int i = 0; i < currentFrequentItemsets.size(); i++) {
                for (int j = i + 1; j < currentFrequentItemsets.size(); j++) {
                    Set<String> unionSet = new HashSet<>(currentFrequentItemsets.get(i));
                    unionSet.addAll(currentFrequentItemsets.get(j));
                    if (unionSet.size() == k) {
                        int count = 0;
                        for (List<String> transaction : transactions) {
                            if (transaction.containsAll(unionSet)) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            newCountMap.put(unionSet, count);
                        }
                    }
                }
            }
            for (Map.Entry<Set<String>, Integer> entry : newCountMap.entrySet()) {
                double support = (double) entry.getValue() / transactions.size();
                if (support >= minSupport) {
                    frequentItemsets.add(entry.getKey());
                    System.out.println("Frequent Itemset: " + entry.getKey() + " | Support: " + support);
                }
            }
            itemCountMap = newCountMap;  
            k++;
        }

        return frequentItemsets;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file path: ");
        String filePath = scanner.nextLine();
        
        System.out.print("Enter minimum support (e.g., 0.4): ");
        double minSupport = scanner.nextDouble();

        List<List<String>> transactions = readData(filePath);
        System.out.println("Transactions: " + transactions);

        Set<Set<String>> frequentItemsets = generateFrequentItemsets(transactions, minSupport);

        System.out.println("\nFinal Frequent Itemsets:");
        for (Set<String> itemset : frequentItemsets) {
            System.out.println(itemset);
        }
        
        scanner.close();
    }
}
