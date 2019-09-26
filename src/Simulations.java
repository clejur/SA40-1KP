
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yiwen zhong
 *
 */
public class Simulations {
	
	public static void main(String[] args) {
		String filePath = (new File("")).getAbsolutePath() + "/../f1-10/"; 
		filePath = (new File("")).getAbsolutePath() + "/../f11-20/";  //Cohort Intelligence
		//filePath = (new File("")).getAbsolutePath() + "/../kp7Crow/"; //"/../kp2Monkey/"; //"/../kp7Crow/"; 
		filePath = (new File("")).getAbsolutePath() + "/../stronglycorrelated/";//"/../paras/";// "/../stronglycorrelated/";//"/../uncorrelated/";//"/../weaklycorrelated/"; 
		//filePath = (new File("")).getAbsolutePath() + "/../kp2Ant/";
		if (Simulations.TEST_TYPE == ETestType.SINGLE_INSTANCE) {
			String fileName = filePath+"05-KP800_uncorrelated.txt";
			fileName = filePath+"10-KP2000_stronglycorrelated.txt";
			testSingleInstance(fileName, Simulations.TIMES);
		} else if (Simulations.TEST_TYPE == ETestType.MULTIPLE_INSTANCE) {
			System.out.println("\n"+Simulations.getParaSetting());
			testPerformance(filePath,Simulations.TIMES);
		} else if (Simulations.TEST_TYPE == ETestType.PARAMETER_TUNING_DENSITY_PROBABILITY) {
			parametersTunningDensity(filePath);
		} else {
			System.out.println("Error test type, Cannot reach here!");
		}
	}
	
	/**
	 * This function is used to tune density-probability
	 * 
	 * @param filePath
	 */
	private static void parametersTunningDensity(String filePath) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
 		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		
		String fileName = (new File("")).getAbsolutePath() + "/results/Parameters/";
		fileName += pathName + "-" + Simulations.methodType + "-";
		fileName += "density para tunning results.csv";

		List<double[]> resultsList = new ArrayList<>();
		for (File file : files) {
			List<Double> scalesList = new ArrayList<>();
			for (int i = 0; i <= 10; i++) {
				scalesList.add( i * 0.1);
				Simulations.densityProb = i * 0.1;
				System.out.println("\n"+Simulations.getParaSetting());
				double[] results = testSingleInstance(file.getAbsolutePath(), Simulations.TIMES);
				resultsList.add(results);
                
                Simulations.saveParaTunningResults(fileName, scalesList, resultsList);
			}			
		}

	}
	
	
	private static void saveParaTunningResults(String fileName, List<Double> paras, List<double[]> results ) {
		if (!Simulations.SAVING_PARA_TUNNING) return;
		
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
			for (int idx = 0; idx < results.size(); idx++) {
				double[] rs = results.get(idx);
				printWriter.println();
				printWriter.print(paras.get(idx%paras.size()));
				for (int j = 0; j < rs.length; j++) {
					printWriter.print(","+rs[j]);
				}
			}

			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static double[] testSingleInstance(String fileName, final int TIMES) {
		double[] results = runSA(fileName, TIMES);
		for (double d : results) {
			System.out.print(d + "\t");
		}
		System.out.println();
		return results;
	}

	private static double[] testPerformance(String filePath, final int TIMES) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		System.out.println(pathName);
		String fileName = (new File("")).getAbsolutePath() + "/results/Performance/" + pathName + "-";
		fileName += Simulations.getParaSetting();
		fileName += " results.csv";

		List<double[]> results = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			double[] result = runSA(file.getAbsolutePath(), TIMES);
			results.add(result);
			System.out.println();
			System.out.print(file.getName()+"\t");
			for (double d : result) {
				System.out.print(d+"\t");
			}
			System.out.println();
		    Simulations.saveFinalResults(fileName, files, results);
		}
		
		//calculate statistics results
		double[] totals = new double[results.get(0).length];
		for (int i = 0; i < files.length; i++) {
			System.out.println();
			System.out.print(files[i].getName()+"\t");
			for (int j = 0; j < results.get(i).length; j++) {
				System.out.print(results.get(i)[j]+"\t");
				totals[j] += results.get(i)[j];
			}
		}
		System.out.println("\t");
		for (int j = 0; j < totals.length; j++) {
			totals[j] = Math.round(totals[j]/files.length*1000)/1000.0;
			System.out.print(totals[j]+"\t");
		}
		return totals; //average data for all files
	}
	
	
	private static void saveFinalResults(String fileName, File[] files, List<double[]> results) {
		if (!Simulations.SAVING_FINAL_RESULTS) return;
		
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
			for (int i = 0; i < results.size(); i++) {
				printWriter.println();
				printWriter.print(files[i].getName());
				for (int j = 0; j < results.get(i).length; j++) {
					printWriter.print(","+results.get(i)[j]);
				}
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static double[] runSA(String fileName, final int TIMES) {
		double duration = (new java.util.Date()).getTime();
		Problems.setFileName(fileName);
		double bValue= Problems.getProblem().getBestValue();
		Solution s;
		double[] makespans = new double[Simulations.TIMES];
		double[] constrains = new double[Simulations.TIMES];
		int[] iterations = new int[Simulations.TIMES]; //last improving iteration
		for (int i = 0; i < Simulations.TIMES; i++) {
			s = new Solution(true);
			if (Simulations.methodType == EMethodType.SA ) {
				s = Methods.SA(s); 
			} else {
				System.out.println("Cannot reach here!");;
			}
			makespans[i] = s.getValue();// - bValue;
			constrains[i] = s.getWeight();
			iterations[i] = s.getLastImproving();
			if (Simulations.OUT_INDIVIDUAL_RUNNING_DATA) {
				System.out.println( i + " -- " + bValue + "," + makespans[i] + "," + s.getWeight() + "," + iterations[i]);
			}
		}
		duration = (new java.util.Date()).getTime()-duration;
		duration /= TIMES;
		duration = Math.round(duration/1000*1000)/1000.0;

		double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
		double total = 0;
		double totalIterations = 0;
		for (int i = 0; i < Simulations.TIMES; i++) {
			double mk = makespans[i];
			total += mk;
			if ( Math.abs((mk-bValue)) * (1.0/bValue) *100 < 1) {
				count++;
			}
			if ( mk < min) {
				min = mk;
			}
			if (mk > max) {
				max = mk;
			}
			totalIterations += iterations[i];
		}
		
		double ave = total / Simulations.TIMES;
		double median = DataStatisticalUtils.getMedian(makespans);
		double STD = DataStatisticalUtils.getStandardDevition(makespans);
		double bpd = min-bValue;//Math.abs(Math.round((min-bValue)) * (1.0/bValue) *100*1000)/1000.0;
		double wpd = max-bValue;//Math.abs(Math.round((max-bValue)) * (1.0/bValue) *100*1000)/1000.0;
		double apd = ave-bValue;//Math.abs(Math.round((ave-bValue)) * (1.0/bValue) *100*1000)/1000.0;
		double itr = Math.round(totalIterations/iterations.length*10)/10; //average last improving iteration
		return new double[] {bValue, min, max, ave, median, STD, bpd, wpd, apd, count, itr, duration};
		//return makespans;
	}
	
	public static EMethodType getSaType() { return Simulations.methodType;}
	public static boolean isSavingFinalResults() { return Simulations.SAVING_FINAL_RESULTS;}
	public static boolean isSavingProcessData() { return Simulations.SAVING_PROCESS_DATA;}
	public static String getParaSetting() {
		String str = methodType + "-" ;
		str += "MCF=" + Simulations.MARKOV_CHAIN_LENGTH ;
		str += " DP=" + Simulations.densityProb + " G=" + Simulations.MAX_GENERATION;
		return str;
	}
	
	private static EMethodType methodType = EMethodType.SA;
	public static int MAX_GENERATION = 800; //400, 800
	public static int MARKOV_CHAIN_LENGTH = 50;	//100, 50
	public static final int TIMES = 1;
		
	public static final boolean OUT_INDIVIDUAL_RUNNING_DATA = true;
	public static final boolean SAVING_PROCESS_DATA = false;
	public static final boolean SAVING_FINAL_RESULTS = false;
	public static final boolean SAVING_PARA_TUNNING = true;
	public static final ETestType TEST_TYPE = ETestType.SINGLE_INSTANCE;
	
	//parameter for neighbor structure
	public static double densityProb = 0.1; //0.4
}
