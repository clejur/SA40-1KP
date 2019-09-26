

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author yiwen zhong
 *
 */
public class Solution implements Comparable<Solution> {

	public Solution(boolean initial) {
		problem = Problems.getProblem();
		match = new int[problem.getItemNumber()];
		if ( initial ) { 
			densityList = problem.getDensityList();
			valueList = problem.getValueList();
			this.randPick(); 
		}
	}

	/**
	 * Use parameter to clone a new Solution
	 * 
	 * @param solution
	 */
	public Solution(Solution solution) {
		problem = Problems.getProblem();
		match = solution.match.clone(); 
		lastImproving = solution.lastImproving;
		isValid = solution.isValid;
		value = solution.value;
		weight = solution.weight;
	}
	
	
	/**
	 * Use parameter to update this object
	 * 
	 * @param s
	 */
	public void update(Solution s) {
		for (int i = 0; i <match.length; i++) {
			match[i] = s.match[i];
		}
		lastImproving = s.lastImproving;
		isValid = s.isValid;
		value = s.value;
		weight = s.weight;
	}

	/*
	 * This method randomly selects items into knapsack
	 */
	private void randPick() {
		for (int i = 0; i < match.length; i++) {
			if (Solution.rand.nextDouble() < 0.5)  {
				match[i] = 1;
			} else {
				match[i] = 0;
			}
		}
		evalWithGreedyRepair();
		optimization(Solution.densityList);
	}
	
	public void evalWithGreedyRepair() {
		int[] items = problem.getDensityOrder();
		double capacity = problem.getCapacity();
		value = 0;
		weight = 0;;
		for (int i = 0; i < items.length; i++) {
			int item = items[i];
			if ( match[item] == 1 &&
				(weight + problem.getItemWeight(item) <= capacity)) {
			    value += problem.getItemValue(item);
			    weight += problem.getItemWeight(item);
			} else {
				match[item] = 0;
			}
		}
		isValid = weight <= problem.getCapacity();
	}

	/**
	 * repair the solution and calculate the value
	 * 
	 * @return
	 */
	public void repair(List<Integer> itemList) {
		//repair invalid solution
		for(int i = itemList.size() - 1; i >= 0 && !isValid; i--) {
			int item = itemList.get(i);
			if (match[item] == 1) {
 			    match[item] = 0;
			    value -= problem.getItemValue(item);
			    weight -= problem.getItemWeight(item);
			    isValid = weight <= problem.getCapacity();
			}
		}
	}
	

	/**
	 * try to add items into knapsack
	 */
	public void optimization(List<Integer> itemList) {
		for (int i = 0; i < itemList.size(); i++) {
			int item = itemList.get(i);
			if (match[item] == 0  && weight + problem.getItemWeight(item) <= problem.getCapacity()) {
				match[item] = 1;
				value += problem.getItemValue(item);
				weight += problem.getItemWeight(item);
			}
		}
	}


	/**
	 * Produce a random neighbor
	 * 
	 * @return
	 */
	public Solution randNeighbor() {
		Solution s = new Solution(this);
		int idx = Solution.rand.nextInt(match.length);
		if (s.match[idx] == 1) { 
			s.match[idx] = 0;

		} else  {
			s.match[idx] = 1;
		} 
		s.evalWithGreedyRepair();
		if (Solution.rand.nextDouble() <= Simulations.densityProb) {
			s.optimization(Solution.densityList);
		} else {
		    s.optimization(Solution.valueList);
		}
		return s;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Solution s) {
		if ( value > s.value) {
			return 1;
		} else if ( value == s.value) {
			return 0;
		} else {
			return -1;
		}
	}

	public String toString() {
		String str = "";
        str += value + "," + weight + "\r\n";
        for (int i = 0; i < match.length; i++) {
        	str += match[i] + ",";
        }
        str += "\r\n";
        for (int i = 0; i < match.length; i++) {
        	str +=problem.getItemValue(i) + ",";
        }
		return str;
	}
	

	public int getItemNumber() { return match.length; }
	public List<Integer> getValueList() { return valueList;}
	public double getValue() {return value;}
	public double getWeight() { return weight;}
	public void setLastImproving(int n) { this.lastImproving = n; }
	public int getLastImproving() { return lastImproving;}

	private Problems problem;
	private int[] match;
	private double value;
	private boolean isValid;
	private double weight;
	private int lastImproving = 0; //
	
    private static List<Integer> densityList;
    private static List<Integer> valueList;
 
	public static void main(String[] args) {
		String filePath = (new File("")).getAbsolutePath() + "/../f1-10/"; 
		String fileName = filePath+"f1.txt";
		Problems.setFileName(fileName);
		Solution s = new Solution(true);
		double d = 0;
		for (int i = 0; i < 10; i++) {
			s.randPick();
			System.out.println(s.value);
			d += s.value;
		}
		System.out.println("Best known value:" + Problems.getProblem().getBestValue() + ", Random solution:" + d / 10);
	}

	private static Random rand = new Random();
}

//02-KP150_stronglycorrelated.txt	2.147483647E9	7173.0	7173.0	7173.0	99.99966598115846	99.99966598115846	99.99966598115846	0.0	3.0	0.12	
//03-KP200_stronglycorrelated.txt	2.147483647E9	9716.0	9716.0	9716.0	99.99954756349304	99.99954756349304	99.99954756349304	0.0	1.0	0.135	
//06-KP800_stronglycorrelated.txt	2.147483647E9	40167.0	40167.0	40167.0	99.9981295783064	99.9981295783064	99.9981295783064	0.0	1.0	1.878	
//07-KP1000_stronglycorrelated.txt	2.147483647E9	49443.0	49443.0	49443.0	99.99769763089608	99.99769763089608	99.99769763089608	0.0	6.0	3.067	
//08-KP1200_stronglycorrelated.txt	2.147483647E9	60640.0	60640.0	60640.0	99.99717622995246	99.99717622995246	99.99717622995246	0.0	1.0	4.421	
//09-KP1500_stronglycorrelated.txt	2.147483647E9	74932.0	74932.0	74932.0	99.99651070684033	99.99651070684033	99.99651070684033	0.0	4.0	6.846		
//2.147483647E9	40345.167	40345.167	40345.167	99.998	99.998	99.998	0.0	2.667	2.745	
