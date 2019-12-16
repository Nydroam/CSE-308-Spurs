package util;

import java.util.Set;

import model.PrecinctCluster;
import model.State;

public class Measure {
	
	public static double calculateMeasureFairness(PrecinctCluster pc, Set<PrecinctCluster> pcs) {
		// Temporary section
		int totalVote = 0;
		int totalGOPvote = 0;
		int totalPrecinctClusters = 0;
		int totalGOPPrecinctClusters = 0;
		for (PrecinctCluster sd : pcs) {
			totalVote += sd.getRepVotes();
			totalVote += sd.getDemVotes();
			totalGOPvote += sd.getRepVotes();
			totalPrecinctClusters += 1;
			if (sd.getRepVotes() > sd.getDemVotes()) {
				totalGOPPrecinctClusters += 1;
			}
		}
		int idealPrecinctClusterChange = ((int) Math.round(totalPrecinctClusters * ((1.0 * totalGOPvote) / totalVote))) - totalGOPPrecinctClusters;
		// End temporary section
		if (idealPrecinctClusterChange == 0) {
			return 1.0;
		}
		int gv = pc.getRepVotes();
		int dv = pc.getDemVotes();
		int tv = gv + dv;
		int margin = gv - dv;
		if (tv == 0) {
			return 1.0;
		}
		int win_v = Math.max(gv, dv);
		int loss_v = Math.min(gv, dv);
		int inefficient_V;
		if (idealPrecinctClusterChange * margin > 0) {
			inefficient_V = win_v - loss_v;
		} else {
			inefficient_V = loss_v;
		}
		return 1.0 - ((inefficient_V * 1.0) / tv);
	}
	
	public static double calculateMeasureREOCK(PrecinctCluster pc) {
		
		double area = pc.getArea();
		double perimeter = pc.getPerimeter();
		double circleArea = Math.pow((perimeter / (2 * Math.PI)), 2) * Math.PI;
		return area / circleArea;
	}
	
	public static double calculateMeasureEFFGAP(PrecinctCluster pc, Set<PrecinctCluster> pcs) {
		int iv_g = 0;
		int iv_d = 0;
		int tv = 0;
		
		for (PrecinctCluster sd : pcs) {
			int gv = sd.getRepVotes();
			int dv = sd.getDemVotes();
			if (gv > dv) {
				iv_d += dv;
				iv_g += (gv - dv);
			} else if (dv > gv) {
				iv_g += gv;
				iv_d += (dv - gv);
			}
			tv += gv;
			tv += dv;
		}
		return 1.0 - ((Math.abs(iv_g - iv_d) * 1.0) / tv);
	}
	
	public static double calculateMeasureEQPOP(long totalPop, PrecinctCluster pc, Set<PrecinctCluster> pcs) {
		//we will square before we return--this gives lower measure values
		// for greater error
		int idealPopulation = (int)totalPop / pcs.size();
		int truePopulation = (int)pc.getPopulation();
		if (idealPopulation >= truePopulation) {
			return 1-Math.pow(
					Math.abs( idealPopulation-(double)truePopulation)/idealPopulation ,1.25);
		}
		return 1-Math.pow(
				Math.abs( truePopulation -(double)idealPopulation)
						/idealPopulation, 1.25);
	}
	
	public static double calculateMeasureComp(PrecinctCluster pc) {
		int gv = pc.getRepVotes();
		int dv = pc.getDemVotes();
		return 1.0 - (((double) Math.abs(gv - dv)) / (gv + dv));
	}
	
	public static double calculateMeasureEDGECOMP(PrecinctCluster pc) {
		double internalEdges = pc.getInteriorEdges().size();
		double totalEdges = internalEdges + pc.getExteriorEdges().size();
		return internalEdges / totalEdges;
	}
	
	public static double calculateMeasureGerryRep(PrecinctCluster pc) {
		int gv = pc.getRepVotes();
		int dv = pc.getDemVotes();
		int tv = gv + dv;
		int margin = gv - dv;
		if (tv == 0) {
			return 1.0;
		}
		int win_v = Math.max(gv, dv);
		int loss_v = Math.min(gv, dv);
		int inefficient_V;
		if (margin > 0) {
			inefficient_V = win_v - loss_v;
		} else {
			inefficient_V = loss_v;
		}
		return 1.0 - ((inefficient_V * 1.0) / tv);
	}

	public static double calculateMeasureGerryDem(PrecinctCluster pc) {
		int gv = pc.getRepVotes();
		int dv = pc.getDemVotes();
		int tv = gv + dv;
		int margin = dv - gv;
		if (tv == 0) {
			return 1.0;
		}
		int win_v = Math.max(gv, dv);
		int loss_v = Math.min(gv, dv);
		int inefficient_V;
		if (margin > 0) {
			inefficient_V = win_v - loss_v;
		} else {
			inefficient_V = loss_v;
		}
		return 1.0 - ((inefficient_V * 1.0) / tv);
	}
	
}
