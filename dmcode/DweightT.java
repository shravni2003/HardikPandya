import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DweightT {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CSV file path: ");
        String filePath = scanner.nextLine();

        Map<String, Integer> totalMap = new HashMap<>();
        totalMap.put("Mobile", 0);
        totalMap.put("Laptop", 0);
        totalMap.put("Total", 0);

        Map<String, Map<String, Integer>> locationData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",");
                if (values.length < 4) {
                    continue;
                }

                String location = values[0];
                int mobile = Integer.parseInt(values[1].trim());
                int laptop = Integer.parseInt(values[2].trim());
                int total = Integer.parseInt(values[3].trim());

                totalMap.put("Mobile", totalMap.get("Mobile") + mobile);
                totalMap.put("Laptop", totalMap.get("Laptop") + laptop);
                totalMap.put("Total", totalMap.get("Total") + total);

                locationData.putIfAbsent(location, new HashMap<>());
                locationData.get(location).put("Mobile", mobile);
                locationData.get(location).put("Laptop", laptop);
                locationData.get(location).put("Total", total);
            }

            System.out.printf("%-15s %-20s %-20s %-20s%n", "Location", "T weight (Mobile)", "T weight (Laptop)", "Total");
            System.out.println("---------------------------------------------------------------------------------");

            for (Map.Entry<String, Map<String, Integer>> entry : locationData.entrySet()) {
                String location = entry.getKey();
                int mobile = entry.getValue().get("Mobile");
                int laptop = entry.getValue().get("Laptop");
                int total = entry.getValue().get("Total");

                double tWeightMobile = (double) mobile / total * 100;
                double tWeightLaptop = (double) laptop / total * 100;
                double tot=tWeightLaptop+tWeightMobile;

                System.out.printf("%-15s %-20.2f %-20.2f %-20f%n",
                        location, tWeightMobile, tWeightLaptop, tot);
            }

            System.out.printf("%-15s %-20s %-20s%n", "Location", "D weight (Mobile)", "D weight (Laptop)");
            System.out.println("---------------------------------------------------------------------------------");

            for (Map.Entry<String, Map<String, Integer>> entry : locationData.entrySet()) {
                String location = entry.getKey();
                int mobile = entry.getValue().get("Mobile");
                int laptop = entry.getValue().get("Laptop");

                double dWeightMobile = (double) mobile / totalMap.get("Mobile") * 100;
                double dWeightLaptop = (double) laptop / totalMap.get("Laptop") * 100;

                double totalDWeight = dWeightMobile + dWeightLaptop;
                dWeightMobile = (dWeightMobile / totalDWeight) * 100;
                dWeightLaptop = (dWeightLaptop / totalDWeight) * 100;

                System.out.printf("%-15s %-20.2f %-20.2f%n",
                        location, dWeightMobile, dWeightLaptop);
            }

            System.out.printf("%-15s %-20.2f %-20.2f%n", "Total", 
                    (double) totalMap.get("Mobile") / totalMap.get("Mobile") * 100,
                    (double) totalMap.get("Laptop") / totalMap.get("Laptop") * 100);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
