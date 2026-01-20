package classe;

public enum UnitType {
    PCS("Pièces"),   // Pour les items comptés (laitue, œufs)
    KG("Kilogramme"), // Pour les solides vendus au poids (tomates, poulet)
    L("Litre");       // Pour les liquides (huile, lait)

    private final String label;

    UnitType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Méthode utilitaire pour convertir depuis String
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
