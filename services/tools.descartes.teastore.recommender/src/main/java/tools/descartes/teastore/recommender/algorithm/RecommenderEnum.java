package tools.descartes.teastore.recommender.algorithm;

import java.util.Random;

public enum RecommenderEnum {	
	POPULARITY, SLOPE_ONE, PREPROC_SLOPE_ONE, ORDER_BASED;
	
	private static final Random R = new Random();
	
	public static RecommenderEnum random() {
		return RecommenderEnum.values()[R.nextInt(RecommenderEnum.values().length)];
	}
}
