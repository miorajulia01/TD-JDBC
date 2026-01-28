package classe;

public enum UnitType {
    PCS("Pièces"),
    KG("Kilogramme"),
    L("Litre");

    private final String label;

    UnitType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static UnitType fromString(String text) {
        if (text != null) {
            for (UnitType unit : UnitType.values()) {
                if (text.equalsIgnoreCase(unit.name())) {
                    return unit;
                }
            }
        }
        throw new IllegalArgumentException("Unité inconnue: " + text);
    }
}
