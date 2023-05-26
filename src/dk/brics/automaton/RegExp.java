/*
 * dk.brics.automaton
 * 
 * Copyright (c) 2001-2017 Anders Moeller
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dk.brics.automaton;

import java.io.IOException;
import java.util.*;

/**
 * Regular Expression extension to <code>Automaton</code>.
 * <p>
 * Regular expressions are built from the following abstract syntax:
 * <table border="0">
 * <tr><td><i>regexp</i></td><td>::=</td><td><i>unionexp</i></td><td></td><td></td></tr>
 * <tr><td></td><td>|</td><td></td><td></td><td></td></tr>
 *
 * <tr><td><i>unionexp</i></td><td>::=</td><td><i>interexp</i>&nbsp;<code><b>|</b></code>&nbsp;<i>unionexp</i></td><td>(union)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>interexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>interexp</i></td><td>::=</td><td><i>concatexp</i>&nbsp;<code><b>&amp;</b></code>&nbsp;<i>interexp</i></td><td>(intersection)</td><td><small>[OPTIONAL]</small></td></tr>
 * <tr><td></td><td>|</td><td><i>concatexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>concatexp</i></td><td>::=</td><td><i>repeatexp</i>&nbsp;<i>concatexp</i></td><td>(concatenation)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>repeatexp</i></td><td>::=</td><td><i>repeatexp</i>&nbsp;<code><b>?</b></code></td><td>(zero or one occurrence)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i>&nbsp;<code><b>*</b></code></td><td>(zero or more occurrences)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i>&nbsp;<code><b>+</b></code></td><td>(one or more occurrences)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i>&nbsp;<code><b>{</b><i>n</i><b>}</b></code></td><td>(<code><i>n</i></code> occurrences)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i>&nbsp;<code><b>{</b><i>n</i><b>,}</b></code></td><td>(<code><i>n</i></code> or more occurrences)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>repeatexp</i>&nbsp;<code><b>{</b><i>n</i><b>,</b><i>m</i><b>}</b></code></td><td>(<code><i>n</i></code> to <code><i>m</i></code> occurrences, including both)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>complexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>complexp</i></td><td>::=</td><td><code><b>~</b></code>&nbsp;<i>complexp</i></td><td>(complement)</td><td><small>[OPTIONAL]</small></td></tr>
 * <tr><td></td><td>|</td><td><i>charclassexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>charclassexp</i></td><td>::=</td><td><code><b>[</b></code>&nbsp;<i>charclasses</i>&nbsp;<code><b>]</b></code></td><td>(character class)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>[^</b></code>&nbsp;<i>charclasses</i>&nbsp;<code><b>]</b></code></td><td>(negated character class)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>simpleexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>charclasses</i></td><td>::=</td><td><i>charclass</i>&nbsp;<i>charclasses</i></td><td></td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>charclass</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>charclass</i></td><td>::=</td><td><i>charexp</i>&nbsp;<code><b>-</b></code>&nbsp;<i>charexp</i></td><td>(character range, including end-points)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><i>charexp</i></td><td></td><td></td></tr>
 *
 * <tr><td><i>simpleexp</i></td><td>::=</td><td><i>charexp</i></td><td></td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>.</b></code></td><td>(any single character)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>#</b></code></td><td>(the empty language)</td><td><small>[OPTIONAL]</small></td></tr>
 * <tr><td></td><td>|</td><td><code><b>@</b></code></td><td>(any string)</td><td><small>[OPTIONAL]</small></td></tr>
 * <tr><td></td><td>|</td><td><code><b>"</b></code>&nbsp;&lt;Unicode string without double-quotes&gt;&nbsp;<code><b>"</b></code></td><td>(a string)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>(</b></code>&nbsp;<code><b>)</b></code></td><td>(the empty string)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>(</b></code>&nbsp;<i>unionexp</i>&nbsp;<code><b>)</b></code></td><td>(precedence override)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>&lt;</b></code>&nbsp;&lt;identifier&gt;&nbsp;<code><b>&gt;</b></code></td><td>(named automaton)</td><td><small>[OPTIONAL]</small></td></tr>
 * <tr><td></td><td>|</td><td><code><b>&lt;</b><i>n</i>-<i>m</i><b>&gt;</b></code></td><td>(numerical interval)</td><td><small>[OPTIONAL]</small></td></tr>
 *
 * <tr><td><i>charexp</i></td><td>::=</td><td>&lt;Unicode character&gt;</td><td>(a single non-reserved character)</td><td></td></tr>
 * <tr><td></td><td>|</td><td><code><b>\</b></code>&nbsp;&lt;Unicode character&gt;&nbsp;</td><td>(a single character)</td><td></td></tr>
 * </table>
 * <p>
 * The productions marked <small>[OPTIONAL]</small> are only allowed
 * if specified by the syntax flags passed to the <code>RegExp</code>
 * constructor.  The reserved characters used in the (enabled) syntax
 * must be escaped with backslash (<code><b>\</b></code>) or double-quotes
 * (<code><b>"..."</b></code>). (In contrast to other regexp syntaxes,
 * this is required also in character classes.)  Be aware that
 * dash (<code><b>-</b></code>) has a special meaning in <i>charclass</i> expressions.
 * An identifier is a string not containing right angle bracket
 * (<code><b>&gt;</b></code>) or dash (<code><b>-</b></code>).  Numerical intervals are
 * specified by non-negative decimal integers and include both end
 * points, and if <code><i>n</i></code> and <code><i>m</i></code> have the
 * same number of digits, then the conforming strings must have that
 * length (i.e. prefixed by 0's).
 * @author Anders M&oslash;ller &lt;<a href="mailto:amoeller@cs.au.dk">amoeller@cs.au.dk</a>&gt; 
 * */
public class RegExp {

	enum Kind {
		REGEXP_UNION,
		REGEXP_CONCATENATION,
		REGEXP_INTERSECTION,
		REGEXP_OPTIONAL,
		REGEXP_REPEAT,
		REGEXP_REPEAT_MIN,
		REGEXP_REPEAT_MINMAX,
		REGEXP_COMPLEMENT,
		REGEXP_CHAR,
		REGEXP_CHAR_RANGE,
		REGEXP_ANYCHAR,
		REGEXP_EMPTY,
		REGEXP_STRING,
		REGEXP_ANYSTRING,
		REGEXP_AUTOMATON,
		REGEXP_INTERVAL
	}

	enum ComplexOperators {
		REPEAT_RANGE,
		CHAR_CLASS,
		GROUP,
		INTERVAL
	}

	enum Operators {
		UNION, // '|'
		INTERSECTION, // '&'
		CONCATENATION, // ''
		OPTIONAL, // '?'
		REPEAT, // '*'
		REPEAT_MIN, // '+'
		REPEAT_RANGE, // array ['{',',','}']
		REPEAT_RANGE_START, // '{'
		REPEAT_RANGE_SEPARATOR, // ','
		REPEAT_RANGE_END, // '}'
		REPEAT_RANGE_UNBOUND, // '' (i.e. {1,}, void second argument or some special character)
		COMPLEMENT, // '~'
		CHAR_CLASS,
		CHAR_CLASS_START, // '['
		CHAR_CLASS_SEPARATOR, // '-'
		CHAR_CLASS_END, // ']'
		CHAR_CLASS_NEGATION, // '^'
		ANY_CHAR, // '.'
		EMPTY_STRING, // '#'
		ANY_STRING, // '@'
		STRING_START, // '"'
		STRING_END, // '"'
		GROUP,
		GROUP_START, // '('
		GROUP_END, // ')'
		INTERVAL,
		INTERVAL_START, // '<'
		INTERVAL_SEPARATOR, // '-'
		INTERVAL_END // '>'
	}

	/**
	 * Syntax flag, enables intersection (<code>&amp;</code>).
	 */
	public static final int INTERSECTION = 0x0001;

	/**
	 * Syntax flag, enables complement (<code>~</code>).
	 */
	public static final int COMPLEMENT = 0x0002;

	/**
	 * Syntax flag, enables empty language (<code>#</code>).
	 */
	public static final int EMPTY = 0x0004;

	/**
	 * Syntax flag, enables anystring (<code>@</code>).
	 */
	public static final int ANYSTRING = 0x0008;

	/**
	 * Syntax flag, enables named automata (<code>&lt;</code>identifier<code>&gt;</code>).
	 */
	public static final int AUTOMATON = 0x0010;

	/**
	 * Syntax flag, enables numerical intervals (<code>&lt;<i>n</i>-<i>m</i>&gt;</code>).
	 */
	public static final int INTERVAL = 0x0020;

	/**
	 * Syntax flag, enables all optional regexp syntax.
	 */
	public static final int ALL = 0xffff;

	/**
	 * Syntax flag, enables no optional regexp syntax.
	 */
	public static final int NONE = 0x0000;

	private static boolean allow_mutation = false;

	Kind kind;
	RegExp exp1, exp2;
	String s;
	char c;
	int min, max, digits;
	char from, to;

	String b;
	int flags;
	int pos;

	private Map<Operators, String> operatorsMap;

	RegExp() {}

	/**
	 * Constructs new <code>RegExp</code> from a string.
	 * Same as <code>RegExp(s, ALL)</code>.
	 * @param s regexp string
	 * @exception IllegalArgumentException if an error occured while parsing the regular expression
	 */
	public RegExp(String s) throws IllegalArgumentException {
		this(s, ALL);
	}

	public RegExp(String s, Map<Operators, String> operatorsMap) throws IllegalArgumentException {
		this(s, ALL, operatorsMap);
	}

	/**
	 * Constructs new <code>RegExp</code> from a string.
	 * @param s regexp string
	 * @param syntax_flags boolean 'or' of optional syntax constructs to be enabled
	 * @exception IllegalArgumentException if an error occured while parsing the regular expression
	 */
	public RegExp(String s, int syntax_flags) throws IllegalArgumentException {
		this(s, syntax_flags, null);
	}

	public RegExp(String s, int syntax_flags, Map<Operators, String> operatorsMap) throws IllegalArgumentException {
		b = s;
		flags = syntax_flags;
		this.operatorsMap = operatorsMap;
		RegExp e;
		if (s.length() == 0)
			e = makeString("");
		else {
			e = parseUnionExp();
			if (pos < b.length())
				throw new IllegalArgumentException("end-of-string expected at position " + pos);
		}
		kind = e.kind;
		exp1 = e.exp1;
		exp2 = e.exp2;
		this.s = e.s;
		c = e.c;
		min = e.min;
		max = e.max;
		digits = e.digits;
		from = e.from;
		to = e.to;
		b = null;
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * Same as <code>toAutomaton(null)</code> (empty automaton map).
	 */
	public Automaton toAutomaton() {
		return toAutomatonAllowMutate(null, null, true);
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * Same as <code>toAutomaton(null,minimize)</code> (empty automaton map).
	 */
	public Automaton toAutomaton(boolean minimize) {
		return toAutomatonAllowMutate(null, null, minimize);
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * The constructed automaton is minimal and deterministic and has no
	 * transitions to dead states.
	 * @param automaton_provider provider of automata for named identifiers
	 * @exception IllegalArgumentException if this regular expression uses
	 *   a named identifier that is not available from the automaton provider
	 */
	public Automaton toAutomaton(AutomatonProvider automaton_provider) throws IllegalArgumentException {
		return toAutomatonAllowMutate(null, automaton_provider, true);
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * The constructed automaton has no transitions to dead states.
	 * @param automaton_provider provider of automata for named identifiers
	 * @param minimize if set, the automaton is minimized and determinized
	 * @exception IllegalArgumentException if this regular expression uses
	 *   a named identifier that is not available from the automaton provider
	 */
	public Automaton toAutomaton(AutomatonProvider automaton_provider, boolean minimize) throws IllegalArgumentException {
		return toAutomatonAllowMutate(null, automaton_provider, minimize);
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * The constructed automaton is minimal and deterministic and has no
	 * transitions to dead states.
	 * @param automata a map from automaton identifiers to automata
	 *   (of type <code>Automaton</code>).
	 * @exception IllegalArgumentException if this regular expression uses
	 *   a named identifier that does not occur in the automaton map
	 */
	public Automaton toAutomaton(Map<String, Automaton> automata) throws IllegalArgumentException {
		return toAutomatonAllowMutate(automata, null, true);
	}

	/**
	 * Constructs new <code>Automaton</code> from this <code>RegExp</code>.
	 * The constructed automaton has no transitions to dead states.
	 * @param automata a map from automaton identifiers to automata
	 *   (of type <code>Automaton</code>).
	 * @param minimize if set, the automaton is minimized and determinized
	 * @exception IllegalArgumentException if this regular expression uses
	 *   a named identifier that does not occur in the automaton map
	 */
	public Automaton toAutomaton(Map<String, Automaton> automata, boolean minimize) throws IllegalArgumentException {
		return toAutomatonAllowMutate(automata, null, minimize);
	}

	/**
	 * Sets or resets allow mutate flag.
	 * If this flag is set, then automata construction uses mutable automata,
	 * which is slightly faster but not thread safe.
	 * By default, the flag is not set.
	 * @param flag if true, the flag is set
	 * @return previous value of the flag
	 */
	public boolean setAllowMutate(boolean flag) {
		boolean b = allow_mutation;
		allow_mutation = flag;
		return b;
	}

	private Automaton toAutomatonAllowMutate(Map<String, Automaton> automata,
											 AutomatonProvider automaton_provider,
											 boolean minimize) throws IllegalArgumentException {
		boolean b = false;
		if (allow_mutation)
			b = Automaton.setAllowMutate(true); // thread unsafe
		Automaton a = toAutomaton(automata, automaton_provider, minimize);
		if (allow_mutation)
			Automaton.setAllowMutate(b);
		return a;
	}

	private Automaton toAutomaton(Map<String, Automaton> automata,
								  AutomatonProvider automaton_provider,
								  boolean minimize) throws IllegalArgumentException {
		List<Automaton> list;
		Automaton a = null;
		switch (kind) {
			case REGEXP_UNION:
				list = new ArrayList<Automaton>();
				findLeaves(exp1, Kind.REGEXP_UNION, list, automata, automaton_provider, minimize);
				findLeaves(exp2, Kind.REGEXP_UNION, list, automata, automaton_provider, minimize);
				a = BasicOperations.union(list);
				if (minimize)
					a.minimize();
				break;
			case REGEXP_CONCATENATION:
				list = new ArrayList<Automaton>();
				findLeaves(exp1, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider, minimize);
				findLeaves(exp2, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider, minimize);
				a = BasicOperations.concatenate(list);
				if (minimize)
					a.minimize();
				break;
			case REGEXP_INTERSECTION:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).intersection(exp2.toAutomaton(automata, automaton_provider, minimize));
				if (minimize)
					a.minimize();
				break;
			case REGEXP_OPTIONAL:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).optional();
				if (minimize)
					a.minimize();
				break;
			case REGEXP_REPEAT:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).repeat();
				if (minimize)
					a.minimize();
				break;
			case REGEXP_REPEAT_MIN:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).repeat(min);
				if (minimize)
					a.minimize();
				break;
			case REGEXP_REPEAT_MINMAX:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).repeat(min, max);
				if (minimize)
					a.minimize();
				break;
			case REGEXP_COMPLEMENT:
				a = exp1.toAutomaton(automata, automaton_provider, minimize).complement();
				if (minimize)
					a.minimize();
				break;
			case REGEXP_CHAR:
				a = BasicAutomata.makeChar(c);
				break;
			case REGEXP_CHAR_RANGE:
				a = BasicAutomata.makeCharRange(from, to);
				break;
			case REGEXP_ANYCHAR:
				a = BasicAutomata.makeAnyChar();
				break;
			case REGEXP_EMPTY:
				a = BasicAutomata.makeEmpty();
				break;
			case REGEXP_STRING:
				a = BasicAutomata.makeString(s);
				break;
			case REGEXP_ANYSTRING:
				a = BasicAutomata.makeAnyString();
				break;
			case REGEXP_AUTOMATON:
				Automaton aa = null;
				if (automata != null)
					aa = automata.get(s);
				if (aa == null && automaton_provider != null)
					try {
						aa = automaton_provider.getAutomaton(s);
					} catch (IOException e) {
						throw new IllegalArgumentException(e);
					}
				if (aa == null)
					throw new IllegalArgumentException("'" + s + "' not found");
				a = aa.clone(); // always clone here (ignore allow_mutate)
				break;
			case REGEXP_INTERVAL:
				a = BasicAutomata.makeInterval(min, max, digits);
				break;
		}
		return a;
	}

	private void findLeaves(RegExp exp, Kind kind, List<Automaton> list, Map<String, Automaton> automata,
							AutomatonProvider automaton_provider,
							boolean minimize) {
		if (exp.kind == kind) {
			findLeaves(exp.exp1, kind, list, automata, automaton_provider, minimize);
			findLeaves(exp.exp2, kind, list, automata, automaton_provider, minimize);
		} else
			list.add(exp.toAutomaton(automata, automaton_provider, minimize));
	}

	/**
	 * Constructs string from parsed regular expression.
	 */
	@Override
	public String toString() {
		return toStringBuilder(new StringBuilder()).toString();
	}

	public String getString() {
		StringBuilder sb = new StringBuilder();
		switch (kind) {
			case REGEXP_UNION:
				if (new Random().nextBoolean()) {
					sb.append(exp1.getString());
				} else {
					sb.append(exp2.getString());
				}
				break;
			case REGEXP_CONCATENATION:
				sb.append(exp1.getString());
				sb.append(exp2.getString());
				break;
			case REGEXP_INTERSECTION:
				String r1 = exp1.getString();
				String r2 = exp2.getString();
				StringBuilder intersection = new StringBuilder();
				final int min = Integer.min(r1.length(), r2.length());
				for (int i = 0; i < min; i++) {
					Character choosen = null;
					if (new Random().nextBoolean()) {
						choosen = r1.charAt(i);
					} else {
						choosen = r2.charAt(i);
					}
					intersection.append(choosen);
				}
				if (r1.length() == r2.length()) {
					String suffix = null;
					if (new Random().nextBoolean()) {
						suffix = r2.substring(min);
					} else {
						suffix = r1.substring(min);
					}
					intersection.append(suffix);
				} else if (r1.length() > min) {
					intersection.append(r1.substring(min));
				} else {
					intersection.append(r2.substring(min));
				}
				sb.append(intersection);
				break;
			case REGEXP_OPTIONAL:
				if (new Random().nextBoolean()) {
					sb.append(exp1.getString());
				}
				break;
			case REGEXP_REPEAT:
				repeat(new Random().nextInt(5), sb);
				break;
			case REGEXP_REPEAT_MIN:
				repeat(this.min, sb);
				break;
			case REGEXP_REPEAT_MINMAX:
				repeat(new Random().nextInt(max - this.min + 1) + this.min, sb);
				break;
			case REGEXP_COMPLEMENT:
				sb.append("~(");
				exp1.toStringBuilder(sb);
				sb.append(")");
				break;
			case REGEXP_CHAR:
				appendChar(c, sb);
				break;
			case REGEXP_CHAR_RANGE:
				appendChar(((char)(new Random().nextInt(to - from + 1) + from)), sb);
				break;
			case REGEXP_ANYCHAR:
				appendChar(((char)(new Random().nextInt(Character.MAX_VALUE - Character.MIN_VALUE + 1) + Character.MIN_VALUE)), sb);
				break;
			case REGEXP_EMPTY:
			case REGEXP_STRING:
				break;
			case REGEXP_ANYSTRING:
				int maxChars = new Random().nextInt(5);
				for (; maxChars > 0; maxChars--) {
					appendChar(((char)(new Random().nextInt(Character.MAX_VALUE - Character.MIN_VALUE + 1) + Character.MIN_VALUE)), sb);
				}
				break;
			case REGEXP_AUTOMATON:
				sb.append("<").append(s).append(">");
				break;
			case REGEXP_INTERVAL:
				String s1 = Integer.toString(this.min);
				String s2 = Integer.toString(max);
				sb.append("<");
				if (digits > 0)
					for (int i = s1.length(); i < digits; i++)
						sb.append('0');
				sb.append(s1).append("-");
				if (digits > 0)
					for (int i = s2.length(); i < digits; i++)
						sb.append('0');
				sb.append(s2).append(">");
				break;
		}
		return sb.toString();
	}

	private void repeat(int MAX_VALUE, StringBuilder s) {
		int maxRepeat = MAX_VALUE;
		final String repeatable = exp1.getString();
		for (; maxRepeat > 0; maxRepeat--) {
			s.append(repeatable);
		}
	}

	StringBuilder toStringBuilder(StringBuilder b) {
		switch (kind) {
			case REGEXP_UNION:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append("|");
				exp2.toStringBuilder(b);
				b.append(")");
				break;
			case REGEXP_CONCATENATION:
				exp1.toStringBuilder(b);
				exp2.toStringBuilder(b);
				break;
			case REGEXP_INTERSECTION:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append("&");
				exp2.toStringBuilder(b);
				b.append(")");
				break;
			case REGEXP_OPTIONAL:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append(")?");
				break;
			case REGEXP_REPEAT:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append(")*");
				break;
			case REGEXP_REPEAT_MIN:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append("){").append(min).append(",}");
				break;
			case REGEXP_REPEAT_MINMAX:
				b.append("(");
				exp1.toStringBuilder(b);
				b.append("){").append(min).append(",").append(max).append("}");
				break;
			case REGEXP_COMPLEMENT:
				b.append("~(");
				exp1.toStringBuilder(b);
				b.append(")");
				break;
			case REGEXP_CHAR:
				appendChar(c, b);
				break;
			case REGEXP_CHAR_RANGE:
				b.append("[\\").append(from).append("-\\").append(to).append("]");
				break;
			case REGEXP_ANYCHAR:
				b.append(".");
				break;
			case REGEXP_EMPTY:
				b.append("#");
				break;
			case REGEXP_STRING:
				if (s.indexOf('"') == -1) {
					b.append("\"").append(s).append("\"");
				} else {
					for (int i = 0; i < s.length(); i++) {
						appendChar(s.charAt(i), b);
					}
				}
				break;
			case REGEXP_ANYSTRING:
				b.append("@");
				break;
			case REGEXP_AUTOMATON:
				b.append("<").append(s).append(">");
				break;
			case REGEXP_INTERVAL:
				String s1 = Integer.toString(min);
				String s2 = Integer.toString(max);
				b.append("<");
				if (digits > 0)
					for (int i = s1.length(); i < digits; i++)
						b.append('0');
				b.append(s1).append("-");
				if (digits > 0)
					for (int i = s2.length(); i < digits; i++)
						b.append('0');
				b.append(s2).append(">");
				break;
		}
		return b;
	}

	private void appendChar(char c, StringBuilder b) {
		if ("|&?*+{},![]^-.#@\"()<>\\".indexOf(c) != -1) {
			b.append("\\");
		}
		b.append(c);
	}

	/**
	 * Returns set of automaton identifiers that occur in this regular expression.
	 */
	public Set<String> getIdentifiers() {
		HashSet<String> set = new HashSet<String>();
		getIdentifiers(set);
		return set;
	}

	void getIdentifiers(Set<String> set) {
		switch (kind) {
			case REGEXP_UNION:
			case REGEXP_CONCATENATION:
			case REGEXP_INTERSECTION:
				exp1.getIdentifiers(set);
				exp2.getIdentifiers(set);
				break;
			case REGEXP_OPTIONAL:
			case REGEXP_REPEAT:
			case REGEXP_REPEAT_MIN:
			case REGEXP_REPEAT_MINMAX:
			case REGEXP_COMPLEMENT:
				exp1.getIdentifiers(set);
				break;
			case REGEXP_AUTOMATON:
				set.add(s);
				break;
			default:
		}
	}

	static RegExp makeUnion(RegExp exp1, RegExp exp2) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_UNION;
		r.exp1 = exp1;
		r.exp2 = exp2;
		return r;
	}

	static RegExp makeConcatenation(RegExp exp1, RegExp exp2) {
		if ((exp1.kind == Kind.REGEXP_CHAR || exp1.kind == Kind.REGEXP_STRING) &&
				(exp2.kind == Kind.REGEXP_CHAR || exp2.kind == Kind.REGEXP_STRING))
			return makeString(exp1, exp2);
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_CONCATENATION;
		if (exp1.kind == Kind.REGEXP_CONCATENATION &&
				(exp1.exp2.kind == Kind.REGEXP_CHAR || exp1.exp2.kind == Kind.REGEXP_STRING) &&
				(exp2.kind == Kind.REGEXP_CHAR || exp2.kind == Kind.REGEXP_STRING)) {
			r.exp1 = exp1.exp1;
			r.exp2 = makeString(exp1.exp2, exp2);
		} else if ((exp1.kind == Kind.REGEXP_CHAR || exp1.kind == Kind.REGEXP_STRING) &&
				exp2.kind == Kind.REGEXP_CONCATENATION &&
				(exp2.exp1.kind == Kind.REGEXP_CHAR || exp2.exp1.kind == Kind.REGEXP_STRING)) {
			r.exp1 = makeString(exp1, exp2.exp1);
			r.exp2 = exp2.exp2;
		} else {
			r.exp1 = exp1;
			r.exp2 = exp2;
		}
		return r;
	}

	static private RegExp makeString(RegExp exp1, RegExp exp2) {
		StringBuilder b = new StringBuilder();
		if (exp1.kind == Kind.REGEXP_STRING)
			b.append(exp1.s);
		else
			b.append(exp1.c);
		if (exp2.kind == Kind.REGEXP_STRING)
			b.append(exp2.s);
		else
			b.append(exp2.c);
		return makeString(b.toString());
	}

	static RegExp makeIntersection(RegExp exp1, RegExp exp2) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_INTERSECTION;
		r.exp1 = exp1;
		r.exp2 = exp2;
		return r;
	}

	static RegExp makeOptional(RegExp exp) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_OPTIONAL;
		r.exp1 = exp;
		return r;
	}

	static RegExp makeRepeat(RegExp exp) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_REPEAT;
		r.exp1 = exp;
		return r;
	}

	static RegExp makeRepeat(RegExp exp, int min) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_REPEAT_MIN;
		r.exp1 = exp;
		r.min = min;
		return r;
	}

	static RegExp makeRepeat(RegExp exp, int min, int max) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_REPEAT_MINMAX;
		r.exp1 = exp;
		r.min = min;
		r.max = max;
		return r;
	}

	static RegExp makeComplement(RegExp exp) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_COMPLEMENT;
		r.exp1 = exp;
		return r;
	}

	static RegExp makeChar(char c) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_CHAR;
		r.c = c;
		return r;
	}

	static RegExp makeCharRange(char from, char to) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_CHAR_RANGE;
		r.from = from;
		r.to = to;
		return r;
	}

	static RegExp makeAnyChar() {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_ANYCHAR;
		return r;
	}

	static RegExp makeEmpty() {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_EMPTY;
		return r;
	}

	static RegExp makeString(String s) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_STRING;
		r.s = s;
		return r;
	}

	static RegExp makeAnyString() {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_ANYSTRING;
		return r;
	}

	static RegExp makeAutomaton(String s) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_AUTOMATON;
		r.s = s;
		return r;
	}

	static RegExp makeInterval(int min, int max, int digits) {
		RegExp r = new RegExp();
		r.kind = Kind.REGEXP_INTERVAL;
		r.min = min;
		r.max = max;
		r.digits = digits;
		return r;
	}

	private boolean peek(String s) {
		return more() && s.indexOf(b.charAt(pos)) != -1;
	}

	private boolean match(char c) {
		if (pos >= b.length())
			return false;
		if (b.charAt(pos) == c) {
			pos++;
			return true;
		}
		return false;
	}

	private boolean match(String s) {
		if (pos >= b.length())
			return false;
		if (s != null && s.equals(b.substring(pos, pos + s.length()))) {
			pos += s.length();
			return true;
		}
		return false;
	}

	private boolean more() {
		return pos < b.length();
	}

	private char next() throws IllegalArgumentException {
		if (!more())
			throw new IllegalArgumentException("unexpected end-of-string");
		return b.charAt(pos++);
	}

	private boolean check(int flag) {
		return (flags & flag) != 0;
	}

	private String convert(Operators o) {
		Optional<String> converted =
				Optional.ofNullable(this.operatorsMap)
						.map(m -> m.get(o));
		switch (o) {
			case UNION:
				return converted.orElse("|");
			case INTERSECTION:
				return converted.orElse("&");
			case REPEAT:
				return converted.orElse("*");
			case CONCATENATION:
				return converted.orElse("");
			case OPTIONAL:
				return converted.orElse("?");
			case REPEAT_MIN:
				return converted.orElse("+");
			case REPEAT_RANGE_START:
				return converted.orElse("{");
			case REPEAT_RANGE_SEPARATOR:
				return converted.orElse(",");
			case REPEAT_RANGE_END:
				return converted.orElse("}");
			case REPEAT_RANGE_UNBOUND:
				return converted.orElse("");
			case COMPLEMENT:
				return converted.orElse("~");
			case GROUP_START:
				return converted.orElse("(");
			case GROUP_END:
				return converted.orElse(")");
			case ANY_CHAR:
				return converted.orElse(".");
			case CHAR_CLASS_START:
				return converted.orElse("[");
			case CHAR_CLASS_END:
				return converted.orElse("]");
			case CHAR_CLASS_SEPARATOR:
				return converted.orElse("-");
			case CHAR_CLASS_NEGATION:
				return converted.orElse("^");
			case ANY_STRING:
				return converted.orElse("@");
			case INTERVAL_START:
				return converted.orElse("<");
			case INTERVAL_SEPARATOR:
				return converted.orElse("-");
			case INTERVAL_END:
				return converted.orElse(">");
			default:
				return converted.orElse(null);
		}
	}

	final RegExp parseUnionExp() throws IllegalArgumentException {
		RegExp e = parseInterExp();
		if (match(convert(Operators.UNION)))
			e = makeUnion(e, parseUnionExp());
		return e;
	}

	final RegExp parseInterExp() throws IllegalArgumentException {
		RegExp e = parseConcatExp();
		if (check(INTERSECTION) && match(convert(Operators.INTERSECTION)))
			e = makeIntersection(e, parseInterExp());
		return e;
	}

	final RegExp parseConcatExp() throws IllegalArgumentException {
		RegExp e = parseRepeatExp();
		if (
				more() && !peek(convert(Operators.GROUP_END) + convert(Operators.UNION)) &&
						(!check(INTERSECTION) || !peek(convert(Operators.INTERSECTION))) &&
						match(convert(Operators.CONCATENATION))
		)
			e = makeConcatenation(e, parseConcatExp());
		return e;
	}

	final RegExp parseRepeatExp() throws IllegalArgumentException {
		RegExp e = parseComplExp();
		while (
				peek(
						convert(Operators.OPTIONAL) +
								convert(Operators.REPEAT) +
								convert(Operators.REPEAT_MIN) +
								convert(Operators.REPEAT_RANGE_START)
				)
		) {
			if (match(convert(Operators.OPTIONAL)))
				e = makeOptional(e);
			else if (match(convert(Operators.REPEAT)))
				e = makeRepeat(e);
			else if (match(convert(Operators.REPEAT_MIN)))
				e = makeRepeat(e, 1);
			else if (match(convert(Operators.REPEAT_RANGE_START))) {
				int start = pos;
				while (peek("0123456789"))
					next();
				if (start == pos)
					throw new IllegalArgumentException("integer expected at position " + pos);
				int n = Integer.parseInt(b.substring(start, pos));
				int m = -1;
				if (match(convert(Operators.REPEAT_RANGE_SEPARATOR))) {
					if (peek("0123456789")) {
						start = pos;
						while(peek("0123456789")) {
							next();
						}
						if (start != pos) {
							m = Integer.parseInt(b.substring(start, pos));
						}
					} else if (!match(convert(Operators.REPEAT_RANGE_UNBOUND))) {
						throw new IllegalArgumentException(
								"expected " + convert(Operators.REPEAT_RANGE_UNBOUND) + " at position " + pos
						);
					}
				} else
					m = n;
				if (!match(convert(Operators.REPEAT_RANGE_END)))
					throw new IllegalArgumentException(
							"expected " + convert(Operators.REPEAT_RANGE_END) + " at position " + pos
					);
				if (m == -1)
					e = makeRepeat(e, n);
				else
					e = makeRepeat(e, n, m);
			}
		}
		return e;
	}

	final RegExp parseComplExp() throws IllegalArgumentException {
		if (check(COMPLEMENT) && match(convert(Operators.COMPLEMENT)))
			return makeComplement(parseComplExp());
		else
			return parseCharClassExp();
	}

	final RegExp parseCharClassExp() throws IllegalArgumentException {
		if (match(convert(Operators.CHAR_CLASS_START))) {
			boolean negate = false;
			if (match(convert(Operators.CHAR_CLASS_NEGATION)))
				negate = true;
			RegExp e = parseCharClasses();
			if (negate)
				e = makeIntersection(makeAnyChar(), makeComplement(e));
			if (!match(convert(Operators.CHAR_CLASS_END)))
				throw new IllegalArgumentException("expected ']' at position " + pos);
			return e;
		} else
			return parseSimpleExp();
	}

	final RegExp parseCharClasses() throws IllegalArgumentException {
		RegExp e = parseCharClass();
		while (more() && !peek(String.valueOf(convert(Operators.CHAR_CLASS_END))))
			e = makeUnion(e, parseCharClass());
		return e;
	}

	final RegExp parseCharClass() throws IllegalArgumentException {
		char c = parseCharExp();
		if (match(convert(Operators.CHAR_CLASS_SEPARATOR)))
			if (peek(String.valueOf(convert(Operators.CHAR_CLASS_END))))
                return makeUnion(makeChar(c), makeChar(convert(Operators.CHAR_CLASS_SEPARATOR).charAt(0)));
            else
                return makeCharRange(c, parseCharExp());
		else
			return makeChar(c);
	}

	final RegExp parseSimpleExp() throws IllegalArgumentException {
		if (match(convert(Operators.ANY_CHAR)))
			return makeAnyChar();
		else if (check(EMPTY) && match(convert(Operators.EMPTY_STRING)))
			return makeEmpty();
		else if (check(ANYSTRING) && match(convert(Operators.ANY_STRING)))
			return makeAnyString();
		else if (match(convert(Operators.STRING_START))) {
			int start = pos;
			while (more() && !peek(String.valueOf(convert(Operators.STRING_END))))
				next();
			if (!match(convert(Operators.STRING_END)))
				throw new IllegalArgumentException("expected '\"' at position " + pos);
			return makeString(b.substring(start, pos - 1));
		} else if (match(convert(Operators.GROUP_START))) {
			if (match(convert(Operators.GROUP_END)))
				return makeString("");
			RegExp e = parseUnionExp();
			if (!match(convert(Operators.GROUP_END)))
				throw new IllegalArgumentException("expected ')' at position " + pos);
			return e;
		} else if ((check(AUTOMATON) || check(INTERVAL)) && match(convert(Operators.INTERVAL_START))) {
			int start = pos;
			while (more() && !peek(String.valueOf(convert(Operators.INTERVAL_END))))
				next();
			if (!match(convert(Operators.INTERVAL_END)))
				throw new IllegalArgumentException("expected '>' at position " + pos);
			String s = b.substring(start, pos - 1);
			int i = s.indexOf(convert(Operators.INTERVAL_SEPARATOR));
			if (i == -1) {
				if (!check(AUTOMATON))
					throw new IllegalArgumentException("interval syntax error at position " + (pos - 1));
				return makeAutomaton(s);
			} else {
				if (!check(INTERVAL))
					throw new IllegalArgumentException("illegal identifier at position " + (pos - 1));
				try {
					if (i == 0 || i == s.length() - 1 || i != s.lastIndexOf('-'))
						throw new NumberFormatException();
					String smin = s.substring(0, i);
					String smax = s.substring(i + 1, s.length());
					int imin = Integer.parseInt(smin);
					int imax = Integer.parseInt(smax);
					int digits;
					if (smin.length() == smax.length())
						digits = smin.length();
					else
						digits = 0;
					if (imin > imax) {
						int t = imin;
						imin = imax;
						imax = t;
					}
					return makeInterval(imin, imax, digits);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("interval syntax error at position " + (pos - 1));
				}
			}
		} else if (peek("0123456789")) {
			return makeDecimalStringExp();
		} else
			return makeChar(parseCharExp());
	}

	final RegExp makeDecimalStringExp() {
		int start = pos;
		while (peek("0123456789"))
			next();
		final String s = b.substring(start, pos);
		if (start != pos && s.length() > 1) {
			return makeString(String.valueOf(Integer.parseInt(s)));
		}
		return makeChar(s.charAt(0));
	}

	final char parseCharExp() throws IllegalArgumentException {
		match('\\');
		return next();
	}
}
