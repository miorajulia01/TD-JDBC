package classe;

import java.util.HashMap;
import java.util.Map;

public class Convertion {
    private static final Map<String, Map<UnitType, Double>> valeurConverti = new HashMap<>();

    static {
        Map<UnitType, Double> tomateMap = new HashMap<>();
        tomateMap.put(UnitType.PCS, 10.0);
        valeurConverti.put("Tomate", tomateMap);

        Map<UnitType, Double> laitueMap = new HashMap<>();
        laitueMap.put(UnitType.PCS, 2.0);
        valeurConverti.put("Laitue", laitueMap);


        Map<UnitType, Double> chocolatMap = new HashMap<>();
        chocolatMap.put(UnitType.PCS, 10.0);
        chocolatMap.put(UnitType.L, 2.5);
        valeurConverti.put("Chocolat", chocolatMap);


        Map<UnitType, Double> pouletMap = new HashMap<>();
        pouletMap.put(UnitType.PCS, 8.0);
        valeurConverti.put("Poulet", pouletMap);


        Map<UnitType, Double> beurreMap = new HashMap<>();
        beurreMap.put(UnitType.PCS, 4.0);
        beurreMap.put(UnitType.L, 5.0);
        valeurConverti.put("Beurre", beurreMap);
    }

    public static double convertToKG(String ingredient, double quantity, UnitType unit) {
        if (unit == UnitType.KG) return quantity;
        Map<UnitType, Double> rates = valeurConverti.get(ingredient);

        if (rates == null || !rates.containsKey(unit)) {
            throw new RuntimeException("Conversion impossible pour " + ingredient + " en " + unit);
        }
        return quantity / rates.get(unit);
    }
}
