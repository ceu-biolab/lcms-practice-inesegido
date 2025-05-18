package lipid;

import java.util.Objects;

public class Lipid {
    private final int compoundId;
    private final String name;
    private final String formula;
    private final LipidType lipidType;
    private final int carbonCount;
    private final int doubleBondsCount;

    /**
     * Constructor to define a lipid compound
     *
     * @param compoundId        unique identifier
     * @param name              human-readable name (e.g., "TG 54:3")
     * @param formula           chemical formula
     * @param lipidType         enum indicating lipid class (e.g., TG, PC)
     * @param carbonCount       number of carbon atoms
     * @param doubleBondsCount  number of double bonds
     */
    public Lipid(int compoundId, String name, String formula, LipidType lipidType, int carbonCount, int doubleBondsCount) {
        this.compoundId = compoundId;
        this.name = name;
        this.formula = formula;
        this.lipidType = lipidType;
        this.carbonCount = carbonCount;
        this.doubleBondsCount = doubleBondsCount;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public LipidType getLipidType() {
        return lipidType;
    }

    public int getCarbonCount() {
        return carbonCount;
    }

    public int getDoubleBondsCount() {
        return doubleBondsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Lipid)) return false;
        Lipid lipid = (Lipid) o;
        return compoundId == lipid.compoundId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(compoundId);
    }

    @Override
    public String toString() {
        return "Lipid{" +
                "compoundId=" + compoundId +
                ", name='" + name + '\'' +
                ", formula='" + formula + '\'' +
                ", lipidType=" + lipidType +
                ", carbonCount=" + carbonCount +
                ", doubleBondsCount=" + doubleBondsCount +
                '}';
    }
}

