package org.deidentifier.arx;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class PrivacyModel {
	private final BenchmarkCriterion criterion;
	private final Integer k;
	private       Double  c;
	private       Integer l;
	private       Double  t;
	private       Double  d;
	private       Double  b;
	private       Double  dim2Val;
	
	/**
	 * @param criterion
	 * @param dim1Val
	 * @param dim2Val
	 */
	public PrivacyModel(BenchmarkCriterion criterion, Integer dim1Val, Double dim2Val) {
		super();
		this.k = dim1Val;
		this.dim2Val = dim2Val;
		this.criterion = criterion;	

		this.c = null;
		this.l = null;
		this.t = null;
		this.d = null;
		this.b = null;
		
		switch(criterion) {
		case BASIC_BETA_LIKENESS:
			this.b = dim2Val;
			break;
		case D_DISCLOSURE_PRIVACY:
			this.d = dim2Val;
			break;
		case L_DIVERSITY_DISTINCT:
			this.l = (int) Math.ceil(dim2Val);
			break;
		case L_DIVERSITY_ENTROPY:
			this.l = (int) Math.ceil(dim2Val);
			break;
		case L_DIVERSITY_RECURSIVE:
			this.l = (int) Math.ceil(dim2Val);
			this.c = 4d;
			break;
		case T_CLOSENESS_ED:
			this.t= dim2Val;
			break;
		case T_CLOSENESS_HD:
			break;
		default:
			throw new IllegalArgumentException("Unsupported criterion: " + criterion);
		}
	}
	
	/**
	 * @param dim2Qualifier
	 * @param dim1Val
	 * @param dim2Val
	 */
	public PrivacyModel(String dim2Qualifier, Integer dim1Val, Double dim2Val) {
		
		this(toCrit(dim2Qualifier), dim1Val, dim2Val);
		
	}
	
	/**
	 * @param criterion
	 * @param k
	 * @param c
	 * @param l
	 * @param t
	 * @param d TODO
	 * @param b TODO
	 */
	public PrivacyModel(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, Double d, Double b) {
		super();
		this.criterion = criterion;
		this.k = k;
		this.c = c;
		this.l = l;
		this.t = t;
		this.d = d;
		this.b = b;
		switch (criterion) {
		case BASIC_BETA_LIKENESS:
			dim2Val=b;
			break;
		case D_DISCLOSURE_PRIVACY:
			dim2Val=d;
			break;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			dim2Val=Double.valueOf(l);
			break;
		case T_CLOSENESS_ED:
			dim2Val=t;
			break;
		default:
			throw new IllegalArgumentException("Invalid criterion: " + criterion);
		}
	}

	public static Double getDefaultParam2(BenchmarkCriterion criterion2) {
		double param2;
		switch (criterion2) {
		case BASIC_BETA_LIKENESS:
			param2=Double.MAX_VALUE;
			break;
		case D_DISCLOSURE_PRIVACY:
			param2=1d;
			break;
		case L_DIVERSITY_DISTINCT:
			param2=3d;
			break;
		case L_DIVERSITY_ENTROPY:
			param2=3d;
			break;
		case L_DIVERSITY_RECURSIVE:
			param2=3d;
			break;
		case T_CLOSENESS_ED:
			param2=0.2;
			break;
		default:
			throw new IllegalArgumentException("Invalid criterion: " + criterion2);
		}
		
		return param2;
	}

	private static BenchmarkCriterion toCrit(String critString) {
		if ("t".equals(critString)) {
			return BenchmarkCriterion.T_CLOSENESS_ED;
		} else if ("ld".equals(critString)) {
			return BenchmarkCriterion.L_DIVERSITY_DISTINCT;
		} else if ("lr".equals(critString)) {
			return BenchmarkCriterion.L_DIVERSITY_RECURSIVE;
		} else if ("le".equals(critString)) {
			return BenchmarkCriterion.L_DIVERSITY_ENTROPY;
		} else if ("d".equals(critString)) {
			return BenchmarkCriterion.D_DISCLOSURE_PRIVACY;
		} else if ("b".equals(critString)) {
			return BenchmarkCriterion.BASIC_BETA_LIKENESS;
		} else {
			throw new IllegalArgumentException("Illegal crtiString: " + critString);
		}
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

	public Double getB() {
		return b;
	}

	public Double getDim2Val() {
		return dim2Val;
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
		case BASIC_BETA_LIKENESS:
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
		case BASIC_BETA_LIKENESS:
			theString = "basic-" + b + "-likeness";
			break;
		default:
			throw new RuntimeException("Invalid criterion");
		}
		return theString;
	}
}
