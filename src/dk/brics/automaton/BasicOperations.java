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

import java.util.*;

import static dk.brics.automaton.Automaton.setStateNumbers;

/**
 * Basic automata operations.
 */
final public class BasicOperations {
	
	private BasicOperations() {}

	/** 
	 * Returns an automaton that accepts the concatenation of the languages of 
	 * the given automata. 
	 * <p>
	 * Complexity: linear in number of states. 
	 */
	static public Automaton concatenate(Automaton a1, Automaton a2) {
		if (a1.isSingleton() && a2.isSingleton())
			return BasicAutomata.makeString(a1.singleton + a2.singleton);
		if (isEmpty(a1) || isEmpty(a2))
			return BasicAutomata.makeEmpty();
		boolean deterministic = a1.isSingleton() && a2.isDeterministic();
		if (a1 == a2) {
			a1 = a1.cloneExpanded();
			a2 = a2.cloneExpanded();
		} else {
			a1 = a1.cloneExpandedIfRequired();
			a2 = a2.cloneExpandedIfRequired();
		}
		for (AbstractState s : a1.getAcceptStates()) {
			s.accept = false;
			s.addEpsilon(a2.initial);
		}
		a1.deterministic = deterministic;
		a1.clearHashCode();
		a1.checkMinimizeAlways();
		return a1;
	}
	
	/**
	 * Returns an automaton that accepts the concatenation of the languages of
	 * the given automata.
	 * <p>
	 * Complexity: linear in total number of states.
	 */
	static public Automaton concatenate(List<Automaton> l) {
		if (l.isEmpty())
			return BasicAutomata.makeEmptyString();
		boolean all_singleton = true;
		for (Automaton a : l)
			if (!a.isSingleton()) {
				all_singleton = false;
				break;
			}
		if (all_singleton) {
			StringBuilder b = new StringBuilder();
			for (Automaton a : l)
				b.append(a.singleton);
			return BasicAutomata.makeString(b.toString());
		} else {
			for (Automaton a : l)
				if (a.isEmpty())
					return BasicAutomata.makeEmpty();
			Set<Integer> ids = new HashSet<>();
			for (Automaton a : l)
				ids.add(System.identityHashCode(a));
			boolean has_aliases = ids.size() != l.size();
			Automaton b = l.get(0);
			if (has_aliases)
				b = b.cloneExpanded();
			else
				b = b.cloneExpandedIfRequired();
			Set<AbstractState> ac = b.getAcceptStates();
			boolean first = true;
			for (Automaton a : l)
				if (first)
					first = false;
				else {
					if (a.isEmptyString())
						continue;
					Automaton aa = a;
					if (has_aliases)
						aa = aa.cloneExpanded();
					else
						aa = aa.cloneExpandedIfRequired();
					Set<AbstractState> ns = aa.getAcceptStates();
					for (AbstractState s : ac) {
						s.accept = false;
						s.addEpsilon(aa.initial);
						if (s.accept)
							ns.add(s);
					}
					ac = ns;
				}
			b.deterministic = false;
			b.clearHashCode();
			b.checkMinimizeAlways();
			return b;
		}
	}

	/**
	 * Returns an automaton that accepts the union of the empty string and the
	 * language of the given automaton.
	 * <p>
	 * Complexity: linear in number of states.
	 */
	static public Automaton optional(Automaton a) {
		a = a.cloneExpandedIfRequired();
		State s = new State();
		s.addEpsilon(a.initial);
		s.accept = true;
		a.initial = s;
		a.deterministic = false;
		a.clearHashCode();
		a.checkMinimizeAlways();
		return a;
	}
	
	/**
	 * Returns an automaton that accepts the Kleene star (zero or more
	 * concatenated repetitions) of the language of the given automaton.
	 * Never modifies the input automaton language.
	 * <p>
	 * Complexity: linear in number of states.
	 */
	static public Automaton repeat(Automaton a) {
		a = a.cloneExpanded();
		AbstractState s = new CountState(0, Long.MAX_VALUE);
		s.accept = true;
		s.addEpsilon(a.initial);
		for (AbstractState p : a.getAcceptStates())
			p.addEpsilon(s);
		a.initial = s;
		a.deterministic = false;
		a.clearHashCode();
		a.checkMinimizeAlways();
		return a;
	}

	/**
	 * Returns an automaton that accepts <code>min</code> or more
	 * concatenated repetitions of the language of the given automaton.
	 * <p>
	 * Complexity: linear in number of states and in <code>min</code>.
	 */
	static public Automaton repeat(Automaton a, int min) {
		if (min == 0)
			return repeat(a);
		List<Automaton> as = new ArrayList<>();
		while (min-- > 0)
			as.add(a);
		as.add(repeat(a));
		return concatenate(as);
	}
	
	/**
	 * Returns an automaton that accepts between <code>min</code> and
	 * <code>max</code> (including both) concatenated repetitions of the
	 * language of the given automaton.
	 * <p>
	 * Complexity: linear in number of states and in <code>min</code> and
	 * <code>max</code>.
	 */
	static public Automaton repeat(Automaton a, int min, int max) {
		AbstractState c = new CountState(min, max);
		a.expandSingleton();
		c.addEpsilon(a.initial);
		for (AbstractState p : a.getAcceptStates()) {
			p.addEpsilon(c, false);
		}
		return a;
		/*if (min > max)
			return BasicAutomata.makeEmpty();
		max -= min;
		a.expandSingleton();
		Automaton b;
		if (min == 0)
			b = BasicAutomata.makeEmptyString();
		else if (min == 1)
			b = a.clone();
		else {
			List<Automaton> as = new ArrayList<>();
			while (min-- > 0)
				as.add(a);
			b = concatenate(as);
		}
		if (max > 0) {
			Automaton d = a.clone();
			while (--max > 0) {
				Automaton c = a.clone();
				for (AbstractState p : c.getAcceptStates())
					p.addEpsilon(d.initial);
				d = c;
			}
			for (AbstractState p : b.getAcceptStates())
				p.addEpsilon(d.initial);
			b.deterministic = false;
			b.clearHashCode();
			b.checkMinimizeAlways();
		}
		return b;*/
	}

	/**
	 * Returns a (deterministic) automaton that accepts the complement of the
	 * language of the given automaton.
	 * <p>
	 * Complexity: linear in number of states (if already deterministic).
	 */
	static public Automaton complement(Automaton a) {
		a = a.cloneExpandedIfRequired();
		// final Iterator<AbstractTransition> iterator = a.initial.transitions.iterator();
		final Iterator<AbstractState> states = a.getStates().iterator();
		AbstractTransition t = null;
		Map<AbstractState, AbstractTransition> mappedStates = new HashMap<>();
		AbstractState s = null;
		mapTransitionStates(mappedStates, a.getInitialState().transitions.iterator());
		while (states.hasNext() && (s = states.next()) != null) {
			final Iterator<AbstractTransition> iterator = s.transitions.iterator();
			mapTransitionStates(mappedStates, iterator);
		}
		mappedStates.forEach(BasicOperations::splitState);
		a.determinize();
		a.totalize();
		for (AbstractState p : a.getStates()) {
			p.accept = !p.accept;
		}
		a.removeDeadTransitions();
		return a;
	}

	private static void mapTransitionStates(Map<AbstractState, AbstractTransition> mappedStates, Iterator<AbstractTransition> iterator) {
		AbstractTransition t;
		while (iterator.hasNext() && (t = iterator.next()) != null) {
			if (t.to != null && t.to.internalState != null && mappedStates.get(t.to) == null) {
				mappedStates.put(t.to, t);
			}
		}
	}

	static public void splitState(AbstractState state, AbstractTransition t) {
		if (state.internalState != null) {
  			List<AbstractState> splittedStates = state.internalState.splitState(t.min, t.max, state.accept);
			if (splittedStates != null && splittedStates.size() > 0) {
				for (AbstractTransition transition : state.transitions) {
					if (transition.max != t.max && transition.min != t.min) {
						for (AbstractState s : splittedStates) {
							s.addTransition(transition);
						}
					}
				}
				t.to = splittedStates.get(0);
			}
		}
	}

	/**
	 * Returns a (deterministic) automaton that accepts the intersection of
	 * the language of <code>a1</code> and the complement of the language of 
	 * <code>a2</code>. As a side-effect, the automata may be determinized, if not
	 * already deterministic.
	 * <p>
	 * Complexity: quadratic in number of states (if already deterministic).
	 */
	static public Automaton minus(Automaton a1, Automaton a2) {
		if (a1.isEmpty() || a1 == a2)
			return BasicAutomata.makeEmpty();
		if (a2.isEmpty())
			return a1.cloneIfRequired();
		if (a1.isSingleton()) {
			if (a2.run(a1.singleton))
				return BasicAutomata.makeEmpty();
			else
				return a1.cloneIfRequired();
		}
		return intersection(a1, a2.complement());
	}

	/**
	 * Returns an automaton that accepts the intersection of
	 * the languages of the given automata. 
	 * Never modifies the input automata languages.
	 * <p>
	 * Complexity: quadratic in number of states.
	 */
	static public Automaton intersection(Automaton a1, Automaton a2) {
		if (a1.isSingleton()) {
			if (a2.run(a1.singleton))
				return a1.cloneIfRequired();
			else
				return BasicAutomata.makeEmpty();
		}
		if (a2.isSingleton()) {
			if (a1.run(a2.singleton))
				return a2.cloneIfRequired();
			else
				return BasicAutomata.makeEmpty();
		}
		if (a1 == a2)
			return a1.cloneIfRequired();
		AbstractTransition[][] transitions1 = Automaton.getSortedTransitions(a1.getStates());
		AbstractTransition[][] transitions2 = Automaton.getSortedTransitions(a2.getStates());
		Automaton c = new Automaton();
		LinkedList<StatePair> worklist = new LinkedList<>();
		HashMap<StatePair, StatePair> newstates = new HashMap<>();
		StatePair p = new StatePair(c.initial, a1.initial, a2.initial);
		worklist.add(p);
		newstates.put(p, p);
		while (worklist.size() > 0) {
			p = worklist.removeFirst();
			p.s.accept = p.s1.accept && p.s2.accept;
			AbstractTransition[] t1 = transitions1[p.s1.number];
			AbstractTransition[] t2 = transitions2[p.s2.number];
			for (int n1 = 0, b2 = 0; n1 < t1.length; n1++) {
				while (b2 < t2.length && t2[b2].max < t1[n1].min)
					b2++;
				for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; n2++) 
					if (t2[n2].max >= t1[n1].min) {
						StatePair q = new StatePair(t1[n1].to, t2[n2].to);
						StatePair r = newstates.get(q);
						if (r == null) {
							q.s = new State();
							worklist.add(q);
							newstates.put(q, q);
							r = q;
						}
						char min = t1[n1].min > t2[n2].min ? t1[n1].min : t2[n2].min;
						char max = t1[n1].max < t2[n2].max ? t1[n1].max : t2[n2].max;
						p.s.transitions.add(new Transition(min, max, r.s));
					}
			}
		}
		c.deterministic = a1.deterministic && a2.deterministic;
		c.removeDeadTransitions();
		c.checkMinimizeAlways();
		return c;
	}
		
	/**
	 * Returns true if the language of <code>a1</code> is a subset of the
	 * language of <code>a2</code>. 
	 * As a side-effect, <code>a2</code> is determinized if not already marked as
	 * deterministic.
	 * <p>
	 * Complexity: quadratic in number of states.
	 */
	public static boolean subsetOf(Automaton a1, Automaton a2) {
		if (a1 == a2)
			return true;
		if (a1.isSingleton()) {
			if (a2.isSingleton())
				return a1.singleton.equals(a2.singleton);
			return a2.run(a1.singleton);
		}
		a2.determinize();
		AbstractTransition[][] transitions1 = Automaton.getSortedTransitions(a1.getStates());
		AbstractTransition[][] transitions2 = Automaton.getSortedTransitions(a2.getStates());
		LinkedList<StatePair> worklist = new LinkedList<>();
		HashSet<StatePair> visited = new HashSet<>();
		StatePair p = new StatePair(a1.initial, a2.initial);
		worklist.add(p);
		visited.add(p);
		while (worklist.size() > 0) {
			p = worklist.removeFirst();
			if (p.s1.accept && !p.s2.accept)
				return false;
			AbstractTransition[] t1 = transitions1[p.s1.number];
			AbstractTransition[] t2 = transitions2[p.s2.number];
			for (int n1 = 0, b2 = 0; n1 < t1.length; n1++) {
				while (b2 < t2.length && t2[b2].max < t1[n1].min)
					b2++;
				int min1 = t1[n1].min, max1 = t1[n1].max;
				for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; n2++) {
					if (t2[n2].min > min1)
						return false;
					if (t2[n2].max < Character.MAX_VALUE) 
						min1 = t2[n2].max + 1;
					else {
						min1 = Character.MAX_VALUE;
						max1 = Character.MIN_VALUE;
					}
					StatePair q = new StatePair(t1[n1].to, t2[n2].to);
					if (!visited.contains(q)) {
						worklist.add(q);
						visited.add(q);
					}
				}
				if (min1 <= max1)
					return false;
			}		
		}
		return true;
	}
	
	/**
	 * Returns an automaton that accepts the union of the languages of the given automata.
	 * <p>
	 * Complexity: linear in number of states.
	 */
	public static Automaton union(Automaton a1, Automaton a2) {
		if ((a1.isSingleton() && a2.isSingleton() && a1.singleton.equals(a2.singleton)) || a1 == a2)
			return a1.cloneIfRequired();
		a1 = a1.cloneExpandedIfRequired();
		a2 = a2.cloneExpandedIfRequired();
		State s = new State();
		s.addEpsilon(a1.initial);
		s.addEpsilon(a2.initial);
		a1.initial = s;
		a1.deterministic = false;
		a1.clearHashCode();
		a1.checkMinimizeAlways();
		return a1;
	}
	
	/**
	 * Returns an automaton that accepts the union of the languages of the given automata.
	 * <p>
	 * Complexity: linear in number of states.
	 */
	public static Automaton union(Collection<Automaton> l) {
		Set<Integer> ids = new HashSet<>();
		for (Automaton a : l)
			ids.add(System.identityHashCode(a));
		boolean has_aliases = ids.size() != l.size();
		State s = new State();
		for (Automaton b : l) {
			if (b.isEmpty())
				continue;
			Automaton bb = b;
			if (has_aliases)
				bb = bb.cloneExpanded();
			else
				bb = bb.cloneExpandedIfRequired();
			s.addEpsilon(bb.initial);
		}
		Automaton a = new Automaton();
		a.initial = s;
		a.deterministic = false;
		a.clearHashCode();
		a.checkMinimizeAlways();
		return a;
	}

	/**
	 * Determinizes the given automaton.
	 * <p>
	 * Complexity: exponential in number of states.
	 */
	public static void determinize(Automaton a) {
		if (a.deterministic || a.isSingleton())
			return;
		Set<AbstractState> initialset = new HashSet<>();
		initialset.add(a.initial);
		determinize(a, initialset);
	}

	/** 
	 * Determinizes the given automaton using the given set of initial states. 
	 */
	static void determinize(Automaton a, Set<AbstractState> initialset) {
		char[] points = a.getStartPoints();
		// subset construction
		LinkedList<Set<AbstractState>> worklist = new LinkedList<>();
		Map<Set<AbstractState>, AbstractState> newstate = new HashMap<>();
		worklist.add(initialset);
		a.initial = new State();
		newstate.put(initialset, a.initial);
		while (worklist.size() > 0) {
			Set<AbstractState> s = worklist.removeFirst();
			AbstractState r = newstate.get(s);
			for (AbstractState q : s) {
				if (q.accept) {
					r.internalState = q.internalState;
					r.accept = true;
					break;
				}
			}
			for (int n = 0; n < points.length; n++) {
				Set<AbstractState> p = new HashSet<>();
				Set<AbstractState> internalStartingStates = new HashSet<>();
				boolean resetStatus = false;
				for (AbstractState q : s)
					for (AbstractTransition t : q.transitions)
						if (t.min <= points[n] && points[n] <= t.max) {
							p.add(t.to);
							resetStatus = resetStatus || t.reset;
							if (q.internalState != null) {
								internalStartingStates.add(q);
							}
						}
				if (!p.isEmpty()) {
					AbstractState q = newstate.get(p);
                    if (q == null) {
                        worklist.add(p);
						q = new State(mergeInternalStates(p));
                        newstate.put(p, q);
                    }
                    char min = points[n];
                    char max;
                    if (n + 1 < points.length)
                        max = (char) (points[n + 1] - 1);
                    else
                        max = Character.MAX_VALUE;
					if (resetStatus) {
						final AbstractInternalState internalState =
								Optional.of(internalStartingStates)
										.filter(states -> !states.isEmpty())
										.map(BasicOperations::mergeInternalStates)
										.orElse(null);
						r.transitions.add(new Transition(min, max, q, internalState, resetStatus));
					} else {
						r.transitions.add(new Transition(min, max, q, resetStatus));
					}
                }
			}
		}
		a.deterministic = true;
		a.removeDeadTransitions();
	}

	private static AbstractInternalState mergeInternalStates(Collection<AbstractState> p) {
		if (p == null || p.isEmpty()) {
			return null;
		}
		AbstractInternalState internalState = null;
		for (AbstractState s : p) {
			if (s.internalState != null) {
				if (internalState == null) {
					internalState = s.internalState;
				} else {
					internalState = internalState.mergeWith(s.internalState);
				}
			}
		}
		return internalState;
	}


	/** 
	 * Adds epsilon transitions to the given automaton.
	 * This method adds extra character interval transitions that are equivalent to the given
	 * set of epsilon transitions. 
	 * @param pairs collection of {@link StatePair} objects representing pairs of source/destination states 
	 *        where epsilon transitions should be added
	 */
	public static void addEpsilons(Automaton a, Collection<StatePair> pairs) {
		a.expandSingleton();
		HashMap<AbstractState, HashSet<AbstractState>> forward = new HashMap<>();
		HashMap<AbstractState, HashSet<AbstractState>> back = new HashMap<>();
		for (StatePair p : pairs) {
			HashSet<AbstractState> to = forward.get(p.s1);
			if (to == null) {
				to = new HashSet<>();
				forward.put(p.s1, to);
			}
			to.add(p.s2);
			HashSet<AbstractState> from = back.get(p.s2);
			if (from == null) {
				from = new HashSet<>();
				back.put(p.s2, from);
			}
			from.add(p.s1);
		}
		// calculate epsilon closure
		LinkedList<StatePair> worklist = new LinkedList<>(pairs);
		HashSet<StatePair> workset = new HashSet<>(pairs);
		while (!worklist.isEmpty()) {
			StatePair p = worklist.removeFirst();
			workset.remove(p);
			HashSet<AbstractState> to = forward.get(p.s2);
			HashSet<AbstractState> from = back.get(p.s1);
			if (to != null) {
				for (AbstractState s : to) {
					StatePair pp = new StatePair(p.s1, s);
					if (!pairs.contains(pp)) {
						pairs.add(pp);
						forward.get(p.s1).add(s);
						back.get(s).add(p.s1);
						worklist.add(pp);
						workset.add(pp);
						if (from != null) {
							for (AbstractState q : from) {
								StatePair qq = new StatePair(q, p.s1);
								if (!workset.contains(qq)) {
									worklist.add(qq);
									workset.add(qq);
								}
							}
						}
					}
				}
			}
		}
		// add transitions
		for (StatePair p : pairs)
			p.s1.addEpsilon(p.s2);
		a.deterministic = false;
		a.clearHashCode();
		a.checkMinimizeAlways();
	}
	
	/**
	 * Returns true if the given automaton accepts the empty string and nothing else.
	 */
	public static boolean isEmptyString(Automaton a) {
		if (a.isSingleton())
			return a.singleton.length() == 0;
		else
			return a.initial.accept && a.initial.transitions.isEmpty();
	}

	/**
	 * Returns true if the given automaton accepts no strings.
	 */
	public static boolean isEmpty(Automaton a) {
		if (a.isSingleton())
			return false;
		return !a.initial.accept && a.initial.transitions.isEmpty();
	}
	
	/**
	 * Returns true if the given automaton accepts all strings.
	 */
	public static boolean isTotal(Automaton a) {
		if (a.isSingleton())
			return false;
		if (a.initial.accept && a.initial.transitions.size() == 1) {
			AbstractTransition t = a.initial.transitions.iterator().next();
			return t.to == a.initial && t.min == Character.MIN_VALUE && t.max == Character.MAX_VALUE;
		}
		return false;
	}
	
	/**
	 * Returns a shortest accepted/rejected string. 
	 * If more than one shortest string is found, the lexicographically first of the shortest strings is returned.
	 * @param accepted if true, look for accepted strings; otherwise, look for rejected strings
	 * @return the string, null if none found
	 */
	public static String getShortestExample(Automaton a, boolean accepted) {
		if (a.isSingleton()) {
			if (accepted)
				return a.singleton;
			else if (a.singleton.length() > 0)
				return "";
			else
				return "\u0000";

		}
		return getShortestExample(a.getInitialState(), accepted);
	}

	static String getShortestExample(AbstractState s, boolean accepted) {
		Map<AbstractState,String> path = new HashMap<>();
		LinkedList<AbstractState> queue = new LinkedList<>();
		path.put(s, "");
		queue.add(s);
		String best = null;
		while (!queue.isEmpty()) {
			AbstractState q = queue.removeFirst();
			String p = path.get(q);
			if (q.accept == accepted) {
				if (best == null || p.length() < best.length() || (p.length() == best.length() && p.compareTo(best) < 0))
					best = p;
			} else 
				for (AbstractTransition t : q.getTransitions()) {
					String tp = path.get(t.to);
					String np = p + t.min;
					if (tp == null || (tp.length() == np.length() && np.compareTo(tp) < 0)) {
						if (tp == null)
							queue.addLast(t.to);
						path.put(t.to, np);
					}
				}
		}
		return best;
	}
	
	/**
	 * Returns true if the given string is accepted by the automaton. 
	 * <p>
	 * Complexity: linear in the length of the string.
	 * <p>
	 * <b>Note:</b> for full performance, use the {@link RunAutomaton} class.
	 */
	public static boolean run(Automaton a, String s) {
		if (a.isSingleton())
			return s.equals(a.singleton);
		if (a.deterministic) {
			a.reset();
			AbstractState p = a.initial;
			for (int i = 0; i < s.length(); i++) {
				AbstractState q = p.countingStep(s.charAt(i));
				if (q == null)
					return false;
				p = q;
			}
			return p.accept && (p.internalState == null || p.internalState.isAccept());
		} else {
			Set<AbstractState> states = a.getStates();
			setStateNumbers(states);
			LinkedList<AbstractState> pp = new LinkedList<>();
			LinkedList<AbstractState> pp_other = new LinkedList<>();
			BitSet bb = new BitSet(states.size());
			BitSet bb_other = new BitSet(states.size());
			pp.add(a.initial);
			ArrayList<AbstractState> dest = new ArrayList<>();
			boolean accept = a.initial.accept;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				accept = false;
				pp_other.clear();
				bb_other.clear();
				for (AbstractState p : pp) {
					dest.clear();
					p.step(c, dest);
					for (AbstractState q : dest) {
						if (q.accept)
							accept = true;
						if (!bb_other.get(q.number)) {
							bb_other.set(q.number);
							pp_other.add(q);
						}
					}
				}
				LinkedList<AbstractState> tp = pp;
				pp = pp_other;
				pp_other = tp;
				BitSet tb = bb;
				bb = bb_other;
				bb_other = tb;
			}
			return accept;
		}
	}
}
