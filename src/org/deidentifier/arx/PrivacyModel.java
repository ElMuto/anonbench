package org.deidentifier.arx;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class PrivacyModel {
	private final BenchmarkCriterion criterion;
	private final Integer k;
	private final Double  c;
	private final Integer l;
	private final Double  t;
	private final boolean isSaBased;

	/**
	 * @param criterion
	 * @param k
	 * @param c
	 * @param l
	 * @param t
	 */
	public PrivacyModel(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, boolean isSaBased) {
		super();
		this.criterion = criterion;
		this.k = k;
		this.c = c;
		this.l = l;
		this.t = t;
		this.isSaBased = isSaBased;
	}

	public BenchmarkCriterion getCriterion() {
		return criterion;
	}

	public Integer getK() {
		return k;
	}

	public Double getC() {
		return c;
	}

	public Integer getL() {
		return l;
	}

	public Double getT() {
		return t;
	}

	public boolean isSaBased() {
		return isSaBased;
	}

	@Override
	public String toString() {
		String theString;
		switch (criterion) {
		case K_ANONYMITY:
			theString = k + "-anonymity";
			break;
		case L_DIVERSITY_DISTINCT:
			theString = "distinct-" + l + "-diversity";
			break;
		case L_DIVERSITY_ENTROPY:
			theString = "entropy-" + l + "-diversity";
			break;
		case L_DIVERSITY_RECURSIVE:
			theString = "recursive-(" + c + ", " + l + ")-diversity";
			break;
		case T_CLOSENESS_ED:
			theString = "equal-distance-" + t + "-closeness";
			break;
		case T_CLOSENESS_HD:
			theString = "hierarchical-distance-" + t + "-closeness";
			break;
		default:
			throw new RuntimeException("Invalid criterion");
		}
		return theString;
	}
}
