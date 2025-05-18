package adduct;

import java.util.regex.*;

public class Adduct {

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param mz     mz
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return the monoisotopic mass of the experimental mass mz with the adduct
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        Double massToSearch;

        // DONE---!! TODO METHOD
        // DONE---!! TODO Create the necessary regex to obtain the multimer (number before the M)
        // and the charge (number before the + or - (if no number, the charge is 1).

        // Determinar el multímero (ej. [2M+H]+ → multimer = 2)
        int numberOfMultimer = 1; // por defecto
        Pattern multimerPattern = Pattern.compile("\\[(\\d+)M"); // patrón de si hay un número antes de la M
        Matcher multimerMatcher = multimerPattern.matcher(adduct); // hace match con el patrón
        if (multimerMatcher.find()) {
            numberOfMultimer = Integer.parseInt(multimerMatcher.group(1)); // asigna el número del multímero
        }

        // Determinar la carga (ej. 2+ → charge = 2)
        int charge = 1;
        Pattern chargePattern = Pattern.compile("(\\d+)?([\\+−])\\]"); // busca la carga al final del aducto
        Matcher chargeMatcher = chargePattern.matcher(adduct);
        char polarity = '+';
        if (chargeMatcher.find()) {
            String chargeStr = chargeMatcher.group(1);
            polarity = chargeMatcher.group(2).charAt(0); // '+' o '−'
            if (chargeStr != null) {
                charge = Integer.parseInt(chargeStr);
            }
        }

        // Buscar la masa del aducto en las listas
        Double adductMass = AdductList.MAPMZPOSITIVEADDUCTS.get(adduct);
        if (adductMass == null) {
            adductMass = AdductList.MAPMZNEGATIVEADDUCTS.get(adduct);
        }

        // Calcular masa ajustada según fórmula
        double intermediateMass;
        if (polarity == '+') {
            intermediateMass = (mz + adductMass) * charge;
        } else {
            intermediateMass = (mz - adductMass) * charge;
        }

        // Ajustar por multímero
        double monoisotopicMass = intermediateMass / numberOfMultimer; // si multimer = 1, no cambia

        return monoisotopicMass;

        /*
         * if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
         * if Adduct is double or triple charged the formula is M = ( mz +- adductMass ) * charge
         * if adduct is a dimer or multimer the formula is M =  (mz +- adductMass) / numberOfMultimer
         */
    }

    /**
     * Este method calcula la relación m/z a partir de la masa monoisotópica y el aducto.
     */
    public static Double getMZFromMonoisotopicMass(Double monoisotopicMass, String adduct) {
        Double massToSearch;

        // DONE---!! TODO METHOD
        // DONE---!! TODO Create the necessary regex to obtain the multimer (number before the M)
        // and the charge (number before the + or - (if no number, the charge is 1).

        // Determinar el multímero (ej. [2M+H]+ → multimer = 2)
        int numberOfMultimer = 1; // por defecto
        Pattern multimerPattern = Pattern.compile("\\[(\\d+)M"); // patrón de si hay un número antes de la M
        Matcher multimerMatcher = multimerPattern.matcher(adduct);
        if (multimerMatcher.find()) {
            numberOfMultimer = Integer.parseInt(multimerMatcher.group(1)); // asigna el número del multímero
        }

        // Determinar la carga (ej. 2+ → charge = 2)
        int charge = 1;
        Pattern chargePattern = Pattern.compile("(\\d+)?([\\+−])\\]"); // busca la carga al final del aducto
        Matcher chargeMatcher = chargePattern.matcher(adduct);
        char polarity = '+';
        if (chargeMatcher.find()) {
            String chargeStr = chargeMatcher.group(1);
            polarity = chargeMatcher.group(2).charAt(0); // '+' o '−'
            if (chargeStr != null) {
                charge = Integer.parseInt(chargeStr);
            }
        }

        // Buscar la masa del aducto en las listas
        Double adductMass = AdductList.MAPMZPOSITIVEADDUCTS.get(adduct);
        if (adductMass == null) {
            adductMass = AdductList.MAPMZNEGATIVEADDUCTS.get(adduct);
        }

        // Calcular la masa ajustada (según multímero y carga)
        double adjustedMass = monoisotopicMass * numberOfMultimer;

        // Aplicar fórmula según polaridad
        double mz;
        if (polarity == '+') {
            mz = (adjustedMass - adductMass) / charge;
        } else {
            mz = (adjustedMass + adductMass) / charge;
        }

        return mz;

        /*
         * if Adduct is single charge the formula is m/z = M +- adductMass. Charge is 1 so it does not affect
         * if Adduct is double or triple charged the formula is mz = M/charge +- adductMass
         * if adduct is a dimer or multimer the formula is mz = M * numberOfMultimer +- adductMass
         */
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass    Mass measured by MS
     * @param theoreticalMass     Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double experimentalMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((experimentalMass - theoreticalMass) * 1_000_000
                / theoreticalMass));
        return ppmIncrement;
    }


}
