package lipid;
import adduct.Adduct;
import adduct.AdductList;

import java.util.*;

public class Annotation {
    private final Lipid lipid;
    private final double mz;
    private final double intensity;
    private final double rtMin;
    private final IoniationMode ioniationMode;
    private String adduct;
    private final Set<Peak> groupedSignals;
    private int score;
    private int totalScoresApplied;

    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IoniationMode ionizationMode) {
        this(lipid, mz, intensity, retentionTime, ionizationMode, Collections.emptySet());
    }


    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IoniationMode ionizationMode, Set<Peak> groupedSignals) {
        this.lipid = lipid;
        this.mz = mz;
        this.rtMin = retentionTime;
        this.intensity = intensity;
        this.ioniationMode = ionizationMode;
        // DONE---!!TODO This set should be sorted according to help the program to deisotope the signals plus detect the adduct
        this.groupedSignals = new TreeSet<>(groupedSignals);
        this.score = 0;
        this.totalScoresApplied = 0;
    }

    public Lipid getLipid() {
        return lipid;
    }

    public double getMz() {
        return mz;
    }

    public double getRtMin() {
        return rtMin;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public double getIntensity() {
        return intensity;
    }

    public IoniationMode getIonizationMode() {
        return ioniationMode;
    }

    public Set<Peak> getGroupedSignals() {
        return Collections.unmodifiableSet(groupedSignals);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int delta) {
        this.score += delta;
        this.totalScoresApplied++;
    }

    public double getNormalizedScore() {
        return totalScoresApplied == 0 ? 0.0 : (double) this.score / this.totalScoresApplied;
    }

    //Resumen lógico de la función detectAduct()
    //1. Tomo el mz observado y pruebo con cada aducto posible (según modo de ionización).
    //2. Convierto ese mz en una masa monoisotópica.
    //3. Para esa masa, busco si algún otro pico del grupo tiene una m/z que podría derivar de ella usando otro aducto válido.
    //4. Si lo encuentro → detecto el aducto del mz original.
    //5. Si no encuentro coincidencias → devuelvo "Unknown".

    public String detectAdduct() {
        double mzTolerance = 0.2;

        //Si no hay al menos dos señales, no se puede hacer inferencia de aducto
        if (groupedSignals == null || groupedSignals.size() < 2) {
            System.out.println("detectAdduct: Not enough signals (" +
                    (groupedSignals == null ? 0 : groupedSignals.size()) + ")");
            return "Unknown";
        }

        //Para la selección del mapa de aductos según el modo de ionización
        Map<String, Double> adductMap = (getIonizationMode() == IoniationMode.POSITIVE)
                ? AdductList.MAPMZPOSITIVEADDUCTS
                : AdductList.MAPMZNEGATIVEADDUCTS;

        //mz de la annotaion
        //double observedMz = this.getMz();
        // Usar el peak con menor m/z como punto de referencia para detectar el aducto
        double observedMz = groupedSignals.stream()
                .mapToDouble(Peak::getMz)
                .min()
                .orElse(this.getMz()); // fallback de seguridad

        System.out.println("detectAdduct: observedMz = " + observedMz + ", mode = " + getIonizationMode());

        //probar cada aducto posible como hipótesis para el observedMz
        //Para cada aducto candidato que podría explicar observedMz:
        for (String candidateAdduct : adductMap.keySet()) {
            System.out.println("  Probando candidateAdduct: " + candidateAdduct);
            try {
                // Masa monoisotópica según el aducto candidato para observedMz
                double monoisotopicMass = Adduct.getMonoisotopicMassFromMZ(observedMz, candidateAdduct);
                System.out.println("    monoisotopicMass = " + monoisotopicMass);

                // Buscamos que alguno de los otros peaks se corresponda con esa misma masa
                for (Peak otherPeak : groupedSignals) {
                    System.out.println("    Comparando con otherPeak: " + otherPeak);
                    if (Math.abs(otherPeak.getMz() - observedMz) <= mzTolerance) {
                        // Es el mismo pico objetivo, lo saltamos
                        System.out.println("      Skip: same as observedMz");
                        continue;
                    }
                    // Probamos todos los aductos posibles para ese otherPeak
                    for (String secondAdduct : adductMap.keySet()) {
                        double expectedMz = Adduct.getMZFromMonoisotopicMass(monoisotopicMass, secondAdduct);
                        double diff = Math.abs(expectedMz - otherPeak.getMz());
                        System.out.println("      secondAdduct=" + secondAdduct + ", expectedMz=" + expectedMz + ", observed=" + otherPeak.getMz() + ", diff=" + diff);
                        if (diff <= mzTolerance) {
                            System.out.println("    DETECTED adduct: " + candidateAdduct + " (via " + secondAdduct + ")");
                            return candidateAdduct;
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                // Si el aducto no se puede parsear, simplemente lo ignoramos
                System.out.println("    Ignorado candidateAdduct (parse error): " + candidateAdduct);
            }
        }

        System.out.println("detectAdduct: Ningún aducto reconocido");
        return "Unknown";
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return Double.compare(that.mz, mz) == 0 &&
                Double.compare(that.rtMin, rtMin) == 0 &&
                Objects.equals(lipid, that.lipid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lipid, mz, rtMin);
    }

    @Override
    public String toString() {
        return String.format("Annotation(%s, mz=%.4f, RT=%.2f, adduct=%s, intensity=%.1f, score=%d)",
                lipid.getName(), mz, rtMin, adduct, intensity, score);
    }
}
