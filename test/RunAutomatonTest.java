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
        RunAutomaton runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (b): " + runAutomaton.run("b"));
        System.out.println("Match (c): " + runAutomaton.run("c"));
        System.out.println("Match (ac): " + runAutomaton.run("ac"));
        System.out.println("Match (ab): " + runAutomaton.run("ab"));
        System.out.println("Match (abc): " + runAutomaton.run("abc"));
        System.out.println("Match (ababc): " + runAutomaton.run("ababc"));
        System.out.println("Match (abababc): " + runAutomaton.run("abababc"));
        System.out.println("Match (abf): " + runAutomaton.run("abf"));
        System.out.println("Match (ababf): " + runAutomaton.run("ababf"));
        System.out.println("Match (abababf): " + runAutomaton.run("abababf"));
        System.out.println("Match (ababababf): " + runAutomaton.run("ababababf"));
        System.out.println("Match (abababababf): " + runAutomaton.run("abababababf"));
        System.out.println("Match (ababababababf): " + runAutomaton.run("ababababababf"));
        System.out.println("Match (abababf): " + a.run("abababf"));
        System.out.println("Match (abababc): " + a.run("ababaabc"));

        r = new RegExp("^(a)?a");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (a): " + a.run("a"));

        r = new RegExp("^(aa(bb)?)+$");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (aabbaa): " + runAutomaton.run("aabbaa"));
        System.out.println("Match (aabbaa): " + a.run("aabbaa"));

        r = new RegExp("((a|b)?b)+");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (b): " + runAutomaton.run("b"));
        System.out.println("Match (b): " + a.run("b"));

        r = new RegExp("(aaa)?aaa");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (aaa): " + runAutomaton.run("aaa"));
        System.out.println("Match (aaa): " + a.run("aaa"));

        r = new RegExp("^(a(b)?)+$");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (aba): " + runAutomaton.run("aba"));
        System.out.println("Match (aba): " + a.run("aba"));

        r = new RegExp("^(a(b(c)?)?)?abc");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (abc): " + runAutomaton.run("abc"));
        System.out.println("Match (abc): " + a.run("abc"));

        r = new RegExp("(|f)?+");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (foo): " + runAutomaton.run("foo"));
        System.out.println("Match (foo): " + a.run("foo"));

    }

}
