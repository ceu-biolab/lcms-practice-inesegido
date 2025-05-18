package main;

import lipid.*;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;

import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Crear unidad de datos
        LipidScoreUnit lipidScoreUnit = new LipidScoreUnit();
        RuleUnitInstance<LipidScoreUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(lipidScoreUnit);

        try {
            // -------- 1. Crear ejemplos de lípidos y peaks --------
            Lipid lipid1 = new Lipid(1, "TG 54:3", "C57H104O6", LipidType.TG, 54, 3);
            Lipid lipid2 = new Lipid(2, "TG 52:3", "C55H100O6", LipidType.TG, 52, 3);

            Peak peak1 = new Peak(700.500, 100000.0); // [M+H]+
            Peak peak2 = new Peak(722.482, 80000.0);  // [M+Na]+
            Peak peak3 = new Peak(350.754, 85000.0);  // [M+2H]2+

            // -------- 2. Crear anotaciones con grupos de señales --------
            Annotation annotation1 = new Annotation(lipid1, 700.500, 90000.0, 10.0, IoniationMode.POSITIVE, Set.of(peak1, peak2));
            Annotation annotation2 = new Annotation(lipid2, 350.754, 85000.0, 9.0, IoniationMode.POSITIVE, Set.of(peak1, peak3));

            // -------- 3. Detectar aducto usando lógica propia --------
            String adduct1 = annotation1.detectAdduct();
            annotation1.setAdduct(adduct1);
            System.out.println("Annotation 1 adduct detected: " + adduct1);

            String adduct2 = annotation2.detectAdduct();
            annotation2.setAdduct(adduct2);
            System.out.println("Annotation 2 adduct detected: " + adduct2);

            // -------- 4. Insertar anotaciones al motor de reglas --------
            lipidScoreUnit.getAnnotations().add(annotation1);
            lipidScoreUnit.getAnnotations().add(annotation2);

            // -------- 5. Disparar reglas --------
            instance.fire();

            // -------- 6. Mostrar puntuaciones --------
            System.out.println("Annotation 1 score: " + annotation1.getScore() + " | Normalized: " + annotation1.getNormalizedScore());
            System.out.println("Annotation 2 score: " + annotation2.getScore() + " | Normalized: " + annotation2.getNormalizedScore());

        } finally {
            instance.close();
        }
    }
}
