import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import org.junit.Assert;
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
        System.out.println("RegExp (foo): " + a.toDot());
        System.out.println("Match (foo): " + runAutomaton.run("foo"));
        System.out.println("Match (foo): " + a.run("foo"));

        r = new RegExp("\0061");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (a): " + a.run("a"));

        r = new RegExp("0-9");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (4): " + runAutomaton.run("4"));
        System.out.println("Match (4): " + a.run("4"));

        r = new RegExp("@");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (Any string): " + runAutomaton.run("any string"));
        System.out.println("Match (Any string): " + a.run("any string"));

        r = new RegExp("a-d");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (a): " + a.run("a"));
        System.out.println("Match (b): " + runAutomaton.run("b"));
        System.out.println("Match (b): " + a.run("b"));
        System.out.println("Match (c): " + runAutomaton.run("c"));
        System.out.println("Match (c): " + a.run("c"));
        System.out.println("Match (d): " + runAutomaton.run("d"));
        System.out.println("Match (d): " + a.run("d"));

        r = new RegExp("a-d");
        a = r.toAutomaton();
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (a): " + a.run("a"));
        System.out.println("Match (b): " + runAutomaton.run("b"));
        System.out.println("Match (b): " + a.run("b"));
        System.out.println("Match (c): " + runAutomaton.run("c"));
        System.out.println("Match (c): " + a.run("c"));
        System.out.println("Match (d): " + runAutomaton.run("d"));
        System.out.println("Match (d): " + a.run("d"));

        r = new RegExp("a");
        a = BasicOperations.repeat(r.toAutomaton(), 3);
        runAutomaton = new RunAutomaton(a);
        System.out.println("Match (a): " + runAutomaton.run("a"));
        System.out.println("Match (a): " + a.run("a"));
        System.out.println("Match (aa): " + runAutomaton.run("aa"));
        System.out.println("Match (aa): " + a.run("aa"));
        System.out.println("Match (aaa): " + runAutomaton.run("aaa"));
        System.out.println("Match (aaa): " + a.run("aaa"));
        System.out.println("Match (aaaa): " + runAutomaton.run("aaaa"));
        System.out.println("Match (aaaa): " + a.run("aaaa"));

    }

    @Test
    public void testRepeat() {
        RegExp r = new RegExp("a");
        Automaton a = BasicOperations.repeat(r.toAutomaton(), 3);
        String regex = a.toDot();
        RunAutomaton runAutomaton = new RunAutomaton(a);
        /*
        System.out.println("Regex: " + regex);
        Assert.assertFalse("Match a", runAutomaton.run("a"));
        Assert.assertFalse("Match (a): ", a.run("a"));
        Assert.assertFalse("Match (aa): ", runAutomaton.run("aa"));
        Assert.assertFalse("Match (aa): ", a.run("aa"));
        Assert.assertTrue("Match (aaa): ", runAutomaton.run("aaa"));
        Assert.assertTrue("Match (aaa): ", a.run("aaa"));
        Assert.assertTrue("Match (aaaa): ", runAutomaton.run("aaaa"));
        Assert.assertTrue("Match (aaaa): ", a.run("aaaa"));

         */

        r = new RegExp("a");
        a = BasicOperations.repeat(r.toAutomaton());
        runAutomaton = new RunAutomaton(a);
        regex = a.toDot();
        System.out.println("Regex: " + regex);
        Assert.assertTrue("Match a", runAutomaton.run("a"));
        Assert.assertTrue("Match (a): ", a.run("a"));
        Assert.assertTrue("Match (aa): ", runAutomaton.run("aa"));
        Assert.assertTrue("Match (aa): ", a.run("aa"));
        Assert.assertTrue("Match (aaa): ", runAutomaton.run("aaa"));
        Assert.assertTrue("Match (aaa): ", a.run("aaa"));
        Assert.assertTrue("Match (aaaa): ", runAutomaton.run("aaaa"));
        Assert.assertTrue("Match (aaaa): ", a.run("aaaa"));

        r = new RegExp("a");
        a = BasicOperations.repeat(r.toAutomaton(), 2, 3);
        runAutomaton = new RunAutomaton(a);
        regex = a.toDot();
        System.out.println("Regex: " + regex);
        Assert.assertFalse("Match a", runAutomaton.run("a"));
        Assert.assertFalse("Match (a): ", a.run("a"));
        Assert.assertTrue("Match (aa): ", runAutomaton.run("aa"));
        Assert.assertTrue("Match (aa): ", a.run("aa"));
        Assert.assertTrue("Match (aaa): ", runAutomaton.run("aaa"));
        Assert.assertTrue("Match (aaa): ", a.run("aaa"));
        Assert.assertFalse("Match (aaaa): ", runAutomaton.run("aaaa"));
        Assert.assertFalse("Match (aaaa): ", a.run("aaaa"));
    }

    @Test
    public void testComplement() {
        RegExp r = new RegExp("(ab){1,2}c|(ab){3,5}f");
        //RegExp r = new RegExp("a{3,6}");
        Automaton a = r.toAutomaton();
        a = BasicOperations.complement(a);
        String regex = a.toDot();
        RunAutomaton runAutomaton = new RunAutomaton(a);
        System.out.println("Regex: " + regex);
        Assert.assertTrue("Match (ab): ", a.run("ab"));
        Assert.assertTrue("Match (ab): ", runAutomaton.run("ab"));
        Assert.assertFalse("Match (abc): ", a.run("abc"));
        Assert.assertFalse("Match (abc): ", runAutomaton.run("abc"));
        Assert.assertTrue("Match (abab): ", a.run("abab"));
        Assert.assertTrue("Match (abab): ", runAutomaton.run("abab"));
        Assert.assertFalse("Match (ababc): ", a.run("ababc"));
        Assert.assertFalse("Match (ababc): ", runAutomaton.run("ababc"));
        Assert.assertFalse("Match (abababf): ", a.run("abababf"));
        Assert.assertFalse("Match (abababf): ", runAutomaton.run("abababf"));
        Assert.assertFalse("Match (ababababf): ", a.run("ababababf"));
        Assert.assertFalse("Match (ababababf): ", runAutomaton.run("ababababf"));
        Assert.assertFalse("Match (abababababf): ", a.run("abababababf"));
        Assert.assertFalse("Match (abababababf): ", runAutomaton.run("abababababf"));
        Assert.assertTrue("Match (ababababababf): ", a.run("ababababababf"));
        Assert.assertTrue("Match (ababababababf): ", runAutomaton.run("ababababababf"));
        Assert.assertTrue("Match a", runAutomaton.run("a"));
        Assert.assertTrue("Match (a): ", a.run("a"));
        Assert.assertTrue("Match (aa): ", runAutomaton.run("aa"));
        Assert.assertTrue("Match (aa): ", a.run("aa"));
        Assert.assertTrue("Match (aaa): ", runAutomaton.run("aaa"));
        Assert.assertTrue("Match (aaa): ", a.run("aaa"));
        Assert.assertTrue("Match (aaaa): ", runAutomaton.run("aaaa"));
        Assert.assertTrue("Match (aaaa): ", a.run("aaaa"));
    }

    @Test
    public void testNested() {
        RegExp r = new RegExp("(ab{2,3}c){2,4}ab{1,2}c");
        //RegExp r = new RegExp("a{3,6}");
        Automaton a = r.toAutomaton();
    }
}
