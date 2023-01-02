import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import org.junit.Test;

public class RunAutomatonTest {

    @Test
    public void testRunAutomaton() {
        RegExp r = new RegExp("(ab){1,2}c|(ab){3,5}f");
        Automaton a = r.toAutomaton();
        System.out.println("Match: " + a.toDot());
        final RunAutomaton runAutomaton = new RunAutomaton(a);
        System.out.println("Match (abc): " + runAutomaton.run("abc"));
        System.out.println("Match (abababf): " + runAutomaton.run("abababf"));
        System.out.println("Match (abababc): " + runAutomaton.run("abababc"));
        System.out.println("Match (abababf): " + a.run("abababf"));
        System.out.println("Match (abababc): " + a.run("ababaabc"));
    }

}
