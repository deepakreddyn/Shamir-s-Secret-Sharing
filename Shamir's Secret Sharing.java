import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Parse JSON files for both test cases
            String[] files = {"testcase1.json", "testcase2.json"};
            for (String file : files) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                solveShamirSecret(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void solveShamirSecret(JSONObject jsonObject) {
        // Read keys
        JSONObject keys = (JSONObject) jsonObject.get("keys");
        long n = (long) keys.get("n");
        long k = (long) keys.get("k");

        // Read and decode points
        Map<Integer, Long> points = new HashMap<>();
        for (Object key : jsonObject.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt((String) key);
            JSONObject root = (JSONObject) jsonObject.get(key);
            int base = Integer.parseInt((String) root.get("base"));
            String value = (String) root.get("value");
            long y = Long.parseLong(value, base);
            points.put(x, y);
        }

        // Solve polynomial using Lagrange Interpolation
        long constantTerm = findConstant(points, k);
        System.out.println("Constant term (c): " + constantTerm);
    }

    private static long findConstant(Map<Integer, Long> points, long k) {
        double constant = 0;
        for (Map.Entry<Integer, Long> entryI : points.entrySet()) {
            int xi = entryI.getKey();
            long yi = entryI.getValue();
            double li = 1;

            for (Map.Entry<Integer, Long> entryJ : points.entrySet()) {
                int xj = entryJ.getKey();
                if (xi != xj) {
                    li *= (0.0 - xj) / (xi - xj); // Interpolation at x = 0
                }
            }

            constant += yi * li;
            if (--k == 0) break; // Stop after k terms
        }
        return Math.round(constant);
    }
}
