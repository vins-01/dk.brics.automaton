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
        runAutomaton.run("abc");
        String s = "abc";
        System.out.println("Match: " + a.run(s));
    }

}
