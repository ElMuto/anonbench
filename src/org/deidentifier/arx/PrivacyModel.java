package org.deidentifier.arx;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class PrivacyModel {
	private final BenchmarkCriterion criterion;
	private final Integer k;
	private final Double  c;
	private final Integer l;
	private final Double  t;
	private final Double  d;
	
	public PrivacyModel(String dim2Qualifier, Integer dim1Val, Double dim2Val) {
		super();
		this.k = dim1Val;
		if ("t".equals(dim2Qualifier)) {
			this.criterion = BenchmarkCriterion.T_CLOSENESS_ED;
			this.c = null;
			this.l = null;
			this.t = dim2Val;
			this.d = null;
		} else {
			throw new RuntimeException("invalid parameter for constructor: '" + dim2Qualifier + "'");
		}
	}

	/**
	 * @param criterion
	 * @param k
	 * @param c
	 * @param l
	 * @param t
	 * @param d TODO
	 */
	public PrivacyModel(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, Double d) {
		super();
		this.criterion = criterion;
		this.k = k;
		this.c = c;
		this.l = l;
		this.t = t;
		this.d = d;
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

	public Double getD() {
		return d;
	}

	public boolean isSaBased() {
		switch (criterion) {
		case K_ANONYMITY:
			return false;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
		case T_CLOSENESS_ED:
		case T_CLOSENESS_HD:
		case D_DISCLOSURE_PRIVACY:
			return true;
		default:
			throw new RuntimeException("Invalid criterion");
		}
	}

	@Override
	public String toString() {
		String theString;
		switch (criterion) {
		case K_ANONYMITY:
			theString = k + "-anonymity";
			break;
		case L_DIVERSITY_DISTINCT:
			theString = "dist.-" + l + "-diversity";
			break;
		case L_DIVERSITY_ENTROPY:
			theString = "ent.-" + l + "-diversity";
			break;
		case L_DIVERSITY_RECURSIVE:
			theString = "rec.-(" + c + ", " + l + ")-diversity";
			break;
		case T_CLOSENESS_ED:
			theString = "equ.-dist.-" + t + "-closeness";
			break;
		case T_CLOSENESS_HD:
			theString = "hier.-dist.-" + t + "-closeness";
			break;
		case D_DISCLOSURE_PRIVACY:
			theString = d + "-disclosure privacy";
			break;
		default:
			throw new RuntimeException("Invalid criterion");
		}
		return theString;
	}
}
