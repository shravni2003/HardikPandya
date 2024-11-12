import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Association {

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

    public static Map<String, Integer> countItems(List<List<String>> transactions) {
        Map<String, Integer> itemCountMap = new HashMap<>();
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + 1);
            }
        }
        return itemCountMap;
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
    
        System.out.println("\n1-itemsets:");
        for (Map.Entry<Set<String>, Integer> entry : itemCountMap.entrySet()) {
            double support = (double) entry.getValue() / transactions.size();
            if (support >= minSupport) {
                frequentItemsets.add(entry.getKey());
                System.out.println("Frequent Itemset: " + entry.getKey() + " | Support: " + support);
            }
        }
    
        int k = 2;
        while (!itemCountMap.isEmpty()) {
            itemCountMap.clear();
            List<Set<String>> currentFrequentItemsets = new ArrayList<>(frequentItemsets);
            System.out.println("\n" + k + "-itemsets:");
    
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
                        itemCountMap.put(unionSet, count); 
                    }
                }
            }
    
            for (Map.Entry<Set<String>, Integer> entry : itemCountMap.entrySet()) {
                double support = (double) entry.getValue() / transactions.size(); 
                if (support >= minSupport) {
                    frequentItemsets.add(entry.getKey());
                    System.out.println("Frequent Itemset: " + entry.getKey() + " | Support: " + support);
                }
            }
            k++;
        }
    
        return frequentItemsets;
    }
    
    public static void generateAssociationRules(Set<Set<String>> frequentItemsets, List<List<String>> transactions, double minConfidence) {
        for (Set<String> itemset : frequentItemsets) {
            List<String> itemList = new ArrayList<>(itemset);
            for (int i = 1; i < (1 << itemList.size()); i++) {
                Set<String> prev = new HashSet<>();
                Set<String> fur = new HashSet<>(itemset);
                
                for (int j = 0; j < itemList.size(); j++) {
                    if ((i & (1 << j)) > 0) {
                        prev.add(itemList.get(j));
                    }
                }
                
                fur.removeAll(prev);
                if (!prev.isEmpty() && !fur.isEmpty()) {
                    int countAntecedent = 0;
                    int countItemset = 0;
                    
                    for (List<String> transaction : transactions) {
                        if (transaction.containsAll(prev)) {
                            countAntecedent++;
                        }
                        if (transaction.containsAll(itemset)) {
                            countItemset++;
                        }
                    }
                    
                    double confidence = (double) countItemset / countAntecedent;
                    if (confidence >= minConfidence) {
                        System.out.println("Rule: " + prev + " => " + fur + " | Confidence: " + confidence);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CSV file path: ");
        String filePath = scanner.nextLine();
        
        double minSupport = 0.4; 
        double minConfidence = 0.7; 

        List<List<String>> transactions = readData(filePath);
        System.out.println("Transactions: " + transactions);

        Map<String, Integer> itemCountMap = countItems(transactions);
        System.out.println("\nItem Counts:");
        for (Map.Entry<String, Integer> entry : itemCountMap.entrySet()) {
            System.out.println("Item: " + entry.getKey() + " | Count: " + entry.getValue());
        }

        Set<Set<String>> frequentItemsets = generateFrequentItemsets(transactions, minSupport);

        System.out.println("\nFinal Frequent Itemsets:");
        for (Set<String> itemset : frequentItemsets) {
            System.out.println(itemset);
        }

        generateAssociationRules(frequentItemsets, transactions, minConfidence);
        
        scanner.close();
    }
}
