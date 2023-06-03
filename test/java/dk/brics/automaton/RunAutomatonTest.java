package dk.brics.automaton;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunAutomatonTest {

    private static final Map<RegExp.Operators, String> operatorsMap = Stream.of(
            new SimpleEntry<>(RegExp.Operators.CHAR_CLASS_START, "//"),
            new SimpleEntry<>(RegExp.Operators.CHAR_CLASS_SEPARATOR, "//"),
            new SimpleEntry<>(RegExp.Operators.CHAR_CLASS_END, "//"),
            new SimpleEntry<>(RegExp.Operators.CHAR_CLASS_NEGATION, "//"),
            new SimpleEntry<>(RegExp.Operators.REPEAT_MIN, "//"),
            new SimpleEntry<>(RegExp.Operators.INTERSECTION, "//"),
            new SimpleEntry<>(RegExp.Operators.REPEAT_RANGE_START, "["),
            new SimpleEntry<>(RegExp.Operators.REPEAT_RANGE_SEPARATOR, ".."),
            new SimpleEntry<>(RegExp.Operators.REPEAT_RANGE_END, "]"),
            new SimpleEntry<>(RegExp.Operators.REPEAT_RANGE_UNBOUND, "*"),
            new SimpleEntry<>(RegExp.Operators.CONCATENATION, "."),
            new SimpleEntry<>(RegExp.Operators.UNION, "+"),
            new SimpleEntry<>(RegExp.Operators.EMPTY_STRING, "$"),
            new SimpleEntry<>(RegExp.Operators.INTERLEAVING, "&")
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
        RegExp r = new RegExp("(ab{2,3}c){2,4}");
        Automaton a = r.toAutomaton();
        Assert.assertFalse("Match (a): ", a.run("a"));
        Assert.assertFalse("Match (ab): ", a.run("ab"));
        Assert.assertFalse("Match (abb): ", a.run("abb"));
        Assert.assertFalse("Match (abbb): ", a.run("abbb"));
        Assert.assertFalse("Match (abbc): ", a.run("abbc"));
        Assert.assertFalse("Match (abbbc): ", a.run("abbbc"));
        Assert.assertFalse("Match (abbbca): ", a.run("abbbca"));
        Assert.assertFalse("Match (abbbcab): ", a.run("abbbcab"));
        Assert.assertFalse("Match (abbbcabb): ", a.run("abbbcabb"));
        Assert.assertFalse("Match (abbbcabbb): ", a.run("abbbcabbb"));
        Assert.assertTrue("Match (abbbcabbc): ", a.run("abbbcabbc"));
        Assert.assertTrue("Match (abbbcabbbc): ", a.run("abbbcabbbc"));
        Assert.assertTrue("Match (abbbcabbcabbbc): ", a.run("abbbcabbcabbbc"));
        Assert.assertTrue("Match (abbbcabbbcabbbc): ", a.run("abbbcabbbcabbbc"));
        Assert.assertTrue("Match (abbbcabbcabbbcabbbc): ", a.run("abbbcabbcabbbcabbbc"));
        Assert.assertTrue("Match (abbbcabbbcabbbcabbbc): ", a.run("abbbcabbbcabbbcabbbc"));
        Assert.assertTrue("Match (abbcabbcabbc): ", a.run("abbcabbcabbc"));
        Assert.assertTrue("Match (abbcabbcabbc): ", a.run("abbcabbbcabbc"));
        Assert.assertTrue("Match (abbcabbcabbcabbc): ", a.run("abbcabbcabbcabbc"));
        Assert.assertTrue("Match (abbcabbcabbcabbc): ", a.run("abbcabbcabbcabbc"));
        Assert.assertFalse("Match (abbcabbcabbcabbcabbc): ", a.run("abbcabbcabbcabbcabbc"));
    }

    @Test
    public void testComplex() throws IOException {
        File file = new File("./test/resources/complex/regex-valid.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = null;
        String regex = reader.readLine();
        RegExp r = new RegExp(regex);
        Automaton a = r.toAutomaton();
        System.out.println(a.toDot());
        StringBuilder valid = new StringBuilder(reader.readLine());
        while ((line = reader.readLine()) != null) {
            valid.append("\n");
            valid.append(line);
        }
        System.out.println("Match " + valid.toString() + " Result: " + a.run(valid.toString()));
    }

    @Test
    public void testCount() throws IOException {
        Files.list(Paths.get("./test/resources/bench"))
                .filter(file ->  !Files.isDirectory(file))
                .map(Path::toFile)
                .map(file -> {
                    try {
                        return new BufferedReader(new FileReader(file));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(reader -> {
                    String line = null;
                    RegExp r = null;
                    Automaton a = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            String regex =
                                line.substring(0, line.lastIndexOf(';'));
                            System.out.println(regex);
                            r = new RegExp(regex);
                            a = r.toAutomaton();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testComplex20() throws IOException {
        testComplex("./test/resources/complex/random_20_100.txt");
    }

    @Test
    public void testComplexAccepting20() throws IOException {
        testComplexStrings("./test/resources/complex/random_20_100.txt");
    }

    @Test
    public void testComplex20EvaluateStrings() throws IOException {
        testAcceptStrings("./test/resources/complex/random_20_100.txt");
    }

    @Test
    public void testComplex15() throws IOException {
        testComplex("./test/resources/complex/random_15_100.txt");
    }
    @Test
    public void testComplex10() throws IOException {
        testComplex("./test/resources/complex/random_10_100.txt");
    }

    @Test
    public void testFailure() throws IOException {
        testComplex("./test/resources/complex/failing-regex.txt");
    }

    @Test
    public void testEmptyStrings() throws IOException {
        testComplex("./test/resources/complex/empty-generated.txt");
    }

    @Test
    public void testAcceptingEmptyStrings() throws IOException {
        testComplexStrings("./test/resources/complex/empty-generated.txt");
    }

    @Test
    public void testInterleaving() {
        RegExp r = new RegExp(
                "a{1,2}&b{2,3}",
                Stream.of(
                        new SimpleEntry<>(RegExp.Operators.INTERSECTION, "//"),
                        new SimpleEntry<>(RegExp.Operators.INTERLEAVING, "&")
                ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))
        );
        Automaton a = r.toAutomaton();
        System.out.println(a.run("ababb"));
    }

    public void fixEmptyStrings(String pathname) throws IOException {
        File file = new File(pathname);
        final int extensionIdx = pathname.lastIndexOf('.');
        String fixedStringsFile = pathname.substring(0, extensionIdx) + "-fixed" + pathname.substring(extensionIdx);
        File fileStrings = new File(fixedStringsFile);
        if (fileStrings.exists()) {
            fileStrings.delete();
        }
        if (!fileStrings.createNewFile()) {
            throw new RuntimeException("Cannot create fixed strings file: " + fixedStringsFile);
        }
        String regex;
        try (
                FileReader fileReader = new FileReader(file);
                FileWriter fileStringsWriter = new FileWriter(fileStrings);
        ) {
            try (
                    BufferedReader reader = new BufferedReader(fileReader);
                    BufferedWriter writer = new BufferedWriter(fileStringsWriter);
            ) {
                while ((regex = reader.readLine()) != null) {
                    System.out.println(regex);
                    RegExp r = new RegExp(regex, operatorsMap);
                    writeStringTo(writer).accept(r.getString());
                    Automaton a = r.toAutomaton(false);
                }
            }
        }
    }

    private static void testComplex(String pathname) throws IOException {
        File file = new File(pathname);
        final int extensionIdx = pathname.lastIndexOf('.');
        String pathStrings = pathname.substring(0, extensionIdx) + "-strings" + pathname.substring(extensionIdx);
        File fileStrings = new File(pathStrings);
        if (fileStrings.exists()) {
            fileStrings.delete();
        }
        if (!fileStrings.createNewFile()) {
            throw new RuntimeException("Cannot create accepting strings file: " + pathStrings);
        }
        String regex;
        Map<Integer, String> regexSizes = new HashMap<>();
        try (
                FileReader fileReader = new FileReader(file);
                FileWriter fileStringsWriter = new FileWriter(fileStrings);
            ) {
            try (
                    BufferedReader reader = new BufferedReader(fileReader);
                    BufferedWriter writer = new BufferedWriter(fileStringsWriter);
                ) {
                while ((regex = reader.readLine()) != null) {
                    System.out.println(regex);
                    RegExp r = new RegExp(regex, operatorsMap);
                    writeStringTo(writer).accept(r.getString());
                    Automaton a = r.toAutomaton(false);
                    regexSizes.put(a.getStates().size(), regex);
//                    generateAcceptingString(writer, a);
//                    System.out.println(a.toDot());
                }
            }
        }
        regexSizes
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .forEachOrdered(entry -> System.out.println("Regex: " + entry.getValue() + " #States: " + entry.getKey()));
    }

    private static void testComplexStrings(String pathname) throws IOException {
        File file = new File(pathname);
        final int extensionIdx = pathname.lastIndexOf('.');
        final String fileName = pathname.substring(0, extensionIdx);
        final String fileExtension = pathname.substring(extensionIdx);
        String pathStrings = fileName + "-strings" + fileExtension;
        File fileStrings = new File(pathStrings);
        String pathResults = fileName + "-results" + fileExtension;
        File fileResults = new File(pathResults);
        if (fileResults.exists()) {
            fileResults.delete();
        }
        fileResults.createNewFile();
        try (
                FileReader fileReader = new FileReader(file);
                FileReader fileStringsReader = new FileReader(fileStrings);
                FileWriter fileResultsWriter = new FileWriter(fileResults);
        ) {
            try (
                    BufferedReader reader = new BufferedReader(fileReader);
                    BufferedReader stringsReader = new BufferedReader(fileStringsReader);
                    BufferedWriter resultsWriter = new BufferedWriter(fileResultsWriter);
            ) {
                evaluateAcceptingStrings(reader, stringsReader, resultsWriter);
            }
        }
    }

    private static void testAcceptStrings(String pathname) throws IOException {
        File file = new File(pathname);
        final int extensionIdx = pathname.lastIndexOf('.');
        final String fileName = pathname.substring(0, extensionIdx);
        final String fileExtension = pathname.substring(extensionIdx);
        String pathStrings = fileName + "-strings" + fileExtension;
        File fileStrings = new File(pathStrings);
        try (
                FileReader fileReader = new FileReader(file);
                FileReader fileStringsReader = new FileReader(fileStrings);
        ) {
            try (
                    BufferedReader reader = new BufferedReader(fileReader);
                    BufferedReader stringsReader = new BufferedReader(fileStringsReader);
            ) {
                evaluateAcceptingStrings(reader, stringsReader, null);
            }
        }
    }

    private static void evaluateAcceptingStrings(BufferedReader reader, BufferedReader stringsReader, BufferedWriter resultsWriter) throws IOException {
            writeStringTo(resultsWriter).accept("Regex,String,Result");
            String regex = null;
            String s = null;
            while ((regex = reader.readLine()) != null && (s = stringsReader.readLine()) != null) {
                System.out.println(regex);
                RegExp r = new RegExp(regex, operatorsMap);
                Automaton a = r.toAutomaton(false);
                boolean result = a.run(s);
                writeStringTo(resultsWriter)
                        .accept(
                                String.join(",", regex, s, String.valueOf(result))
                        );
            }
    }

    private static void generateAcceptingString(BufferedWriter writer, Automaton a) {
        int size = 10;
        int retried = 0;
        Set<String> strings = null;
        while (retried <= 3) {
            strings = SpecialOperations.getStrings(a, size);
            retried++;
            if (strings.isEmpty()) {
                size *= 2;
            }
         }
        if (strings.isEmpty()) {
            writeStringTo(writer).accept("\000");
        } else {
            strings.forEach(writeStringTo(writer));
        }
    }

    private static Consumer<String> writeStringTo(BufferedWriter writer) {
        return s -> {
            if (writer == null) {
                return;
            }
            try {
                writer.write(s);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Cannot write " + s + ": " + e.getMessage());
            }
        };
    }
}
