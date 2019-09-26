
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * 
 * @author yiwen zhong
 *
 */
public class Methods {
	/**
	 * simulated annealing algorithm
	 * 
	 * @param solution
	 * @return
	 */
	public static Solution SA(Solution solution) {
		Solution current = new Solution(solution);
		Solution best = new Solution(solution);
		final int MAX_G = Simulations.MAX_GENERATION; //MAXIMUM GENERATION
		final int SCHEDULE_LENGTH = Simulations.MARKOV_CHAIN_LENGTH;
        double t = 1000;
        double alpha = 0.99;
 		for (int q = 0; q < MAX_G; q++) {
			for (int k = 0; k < SCHEDULE_LENGTH; k++) {
				Solution neighbor = current.randNeighbor();
				double p = Methods.rand.nextDouble();
				double d = neighbor.getValue() - current.getValue();
				if ( d > 0 || p < 1.0/Math.exp(Math.abs(d)/t)) {
					//accept
					current = neighbor;
					if (current.getValue() > best.getValue()) {
						best.update(current);
						best.setLastImproving(q);
					} 
				}
			}
			//System.out.println(current.getValue() + "," + best.getValue());
			System.out.println(current.getValue());
            t *= alpha;
		}
		return best;
	}

	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs, double bestValue) {
		try {
			String f = Problems.fileName;
			File file = new File(f);
			f = (new File("")).getAbsolutePath() + "\\results\\Convergence\\" + file.getName();
			f += " " + Simulations.getParaSetting() + " for KP results.csv";
			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx] + "," + (vs[idx] - bestValue) + "," + ( (bs[idx] - bestValue)));
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static Random rand = new Random();
}
