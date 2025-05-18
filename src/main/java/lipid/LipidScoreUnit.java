package lipid;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

import java.util.HashSet;
import java.util.Set;
public class LipidScoreUnit implements RuleUnitData {

    // !TODO insert here the code to store the data structures containing the facts where the rules will be applied

    private final DataStore<Annotation> annotations;
    private final Set<Annotation> annotationsScore;

    public LipidScoreUnit() {
        this(DataSource.createStore());
    }

    public LipidScoreUnit(DataStore<Annotation> annotations) {
        this.annotations = annotations;
        this.annotationsScore = new HashSet<>();

    }

    public DataStore<Annotation> getAnnotations() {
        return annotations;
    }

    public Set<Annotation> getAnnotationsScore() {
        return annotationsScore;
    }
}
