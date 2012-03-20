import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import com.google.common.collect.Sets;

/**
 * Class having a main method that implements the MSApriori algorithm.
 * @author gvenkateswaran <vganes3@uic.edu>
 *
 */
/**
 * Sorting code taken from
 * http://stackoverflow.com/questions/109383/how-to-sort-
 * a-mapkey-value-on-the-values-in-java/3420912#3420912+
 * 
 * @author gvenkateswaran
 * 
 */
public class MSApp2 {

	private static HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
	private static ArrayList<Integer> l = new ArrayList<Integer>();
	private static HashSet<Integer> hs = new HashSet<Integer>();
	private static HashMap<Integer, Float> MISValues = new HashMap<Integer, Float>();
	private static HashMap<Integer, Float> sortedMISValues = new HashMap<Integer, Float>();
	
	// used for generating f1,f2,f3...
	private static HashMap<ArrayList, Integer> prevFreqItemCount = new HashMap<ArrayList, Integer>();
	// fk
	private static HashMap<ArrayList, Integer> fk = new HashMap<ArrayList, Integer>();
	private static HashMap<ArrayList, Integer> fkprev = new HashMap<ArrayList, Integer>();
	private static ArrayList<ArrayList> ck = new ArrayList<ArrayList>();
	private static HashMap<Integer, ArrayList> transData = new HashMap<Integer, ArrayList>();
	private static int transCount = 0;
	private static float SDC = (float) 0.0;

	public static void main(String a[]) {

		initialize();
		// code to generate f2,f3...
		 prevFreqItemCount.clear();
		fkprev.put(null,null);
		for (int k = 2; !fkprev.isEmpty(); k++) {

			if (k == 2) {
				ck = level2CandidateGen();
			} else {
				// for other ck's
				ck = msCandidateGen();
			}

			for (int c = 0; c < ck.size(); c++) {
				int ccount = 0;
				for (Iterator it = transData.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					if (((ArrayList) entry.getValue()).containsAll(ck.get(c))) // c
																				// is
																				// a
																				// subset
																				// of
																				// transaction t
					{
						ccount++;
					}
				}
				prevFreqItemCount.put(ck.get(c), ccount);
			}
			//System.out.println("size" + ck.size());
			System.out.println(sortByValue(prevFreqItemCount));
			for (Iterator it = prevFreqItemCount.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				int ccount = (Integer) entry.getValue();
				float csupport = (float) ccount / (float) transCount;
				ArrayList al = (ArrayList) entry.getKey();

				if (csupport >= MISValues.get(al.get(0))) {

					// condition met for entry to be added into fk
					
					fk.put((ArrayList) entry.getKey(),
							(Integer) entry.getValue());
				}
			}

			printFreqItemset(k);
			//storing fk for next iteration
			fkprev.clear();
			fkprev.putAll(fk);
			fk.clear();
			prevFreqItemCount.clear();
		}
	}

	/**
	 * generate all possible candidates for f2
	 * 
	 * @return
	 */
	private static ArrayList<ArrayList> level2CandidateGen() {
		ArrayList<ArrayList> c2 = new ArrayList<ArrayList>();
		for (int i = 0; i < l.size(); i++) {
			// item in l ,i here corresponds to l in the actual algorithm
			Integer item = l.get(i);
			float isupport = (float) hm.get(item) / (float) transCount;
			if (isupport >= MISValues.get(item)) {
				for (int j = i + 1; j < l.size(); j++) {
					Integer hitem = l.get(j);
					float hsupport = (float) hm.get(hitem) / (float) transCount;
					float supportDiff = Math.abs(hsupport - isupport);

					if (hsupport >= MISValues.get(item) && supportDiff <= SDC) {
						// condition met for being part of C2
						/*
						 * System.out.println(hsupport);
						 * System.out.println(isupport);
						 * System.out.println(item+ ","+hitem);
						 * System.out.println(supportDiff);
						 */
						ArrayList candidate = new ArrayList();
						// adding l,h
						candidate.add(item);
						candidate.add(hitem);
						c2.add(candidate);
					}
				}
			}
		}
		System.out.println("c2: " + c2);
		return c2;
	}

	/**
	 * Performs the initial loading of File reads for inputs and parameters.
	 * Calls {@link initPass} to perform initial pass that eventually gives us L
	 * and F1.
	 */
	private static void initialize() {
		try {
			/*FileReader data_fr = new FileReader("C:\\mining\\data-5.txt");
			FileReader para_fr = new FileReader("C:\\mining\\para-5.txt");*/
			FileReader data_fr = new FileReader("/home/gvenkateswaran/workspace/MSApriori/data-5.txt");
			FileReader para_fr = new FileReader("/home/gvenkateswaran/workspace/MSApriori/para-5.txt");
			BufferedReader data_br = new BufferedReader(data_fr);
			BufferedReader para_br = new BufferedReader(para_fr);
			String s;
			while ((s = data_br.readLine()) != null) {
				if (!s.equalsIgnoreCase("")) {
					//System.out.println("Line Read :" + s);
					transCount++;
					initPass(s);

				}
			}
			while ((s = para_br.readLine()) != null) {
				if (!s.equalsIgnoreCase("")) {
					//System.out.println("Line Read :" + s);
					if (s.startsWith("SDC =", 0))
						storeSDC(s);
					else
						storeMIS(s);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*System.out.println("HM : " + hm);
		System.out.println("HS : " + hs);*/
		System.out.println("MISValues : " + MISValues);
		sortedMISValues = (HashMap<Integer, Float>) sortByValue(MISValues);
		System.out.println(sortedMISValues);
		// second step of init_pass
		l = initialpass(sortedMISValues);
		System.out.println("l value:" + l);
		frequent1itemsets();

	}

	/*
	 * find first item in M that meets MIS(i) is inserted into L for subsequent
	 * item j is added to L if j.count/n >= MIS(i)
	 */
	private static ArrayList<Integer> initialpass(HashMap<Integer, Float> M) {
		Integer i = 0;
		float isupport = 0;
		for (Iterator it = M.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			// finding support count;

			float support = (float) hm.get(entry.getKey()) / (float) transCount;
			// System.out.println("entry: " + entry.getKey()+support );

			if (support >= (Float) entry.getValue() && l.isEmpty()) {
				// found i,add i to L
				l.add((Integer) entry.getKey());
				isupport = (Float) entry.getValue();
				continue;
			}
			if (!l.isEmpty() && support >= isupport) {
				// adding remaining j in M whose support
				l.add((Integer) entry.getKey());

			}
		}
		return l;
	}

	/**
	 * Method that stores SDC value.
	 * 
	 * @param s
	 */
	private static void storeSDC(String s) {
		String[] sdc = s.split("=");
		SDC = Float.parseFloat(sdc[1].trim());
		System.out.println("SDC : " + SDC);
	}

	/**
	 * Method that stores MIS values from initialize.
	 * 
	 * @param s
	 * 
	 */
	private static void storeMIS(String s) {
		String key = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
		//System.out.println(key);
		String[] val = s.split("=");
		MISValues.put(Integer.decode(key.trim()),
				Float.parseFloat(val[1].trim()));
	}

	/**
	 * Performs init pass. Does 4 things: 1. Save the transaction data to a
	 * hashmap for later use 2. Traverses each transaction and adds each element
	 * to a HashSet var hs. 3. If currentval is already present in hs, the count
	 * in hm is incremented. 4. If currentval is not present in hs, adds
	 * currentval to hs and sets the count of current element to 1 in hm.
	 * 
	 * @param s
	 */
	static void initPass(String s) {
		String[] vals = s.split(",");
		ArrayList temp = new ArrayList();
		for (int i = 0; i < vals.length; i++) {
			Integer current_val = Integer.parseInt(vals[i].trim());
			if (hs.add(current_val)) {
				hm.put(current_val, (Integer) 1);
			} else {
				Integer count = hm.get(current_val);
				// System.out.println(count);
				hm.put(current_val, ++count);
			}
			temp.add(current_val);
		}
		transData.put(transCount, temp);
	}

	static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	

	/*
	 * Performs the generation frequent1itemsets using the number of transaction
	 * count and L
	 */
	private static void frequent1itemsets() {
		for (Iterator it = l.iterator(); it.hasNext();) {
			Integer b = (Integer) it.next();
			Integer lcount = (Integer) hm.get(b);
			// individual item frequency;
			float itemsetMisValue = (float) lcount / (float) transCount;

			/*
			 * System.out.println(entry.getKey());
			 * System.out.println(itemsetMisValue);
			 * System.out.println(sortedMISValues.get(entry.getKey()));
			 */

			// condition for adding item to a frequent itemset
			if (itemsetMisValue >= sortedMISValues.get(b)) {
				// condition meet adding itemset to last known frequent itemset
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add((Integer) b);
				prevFreqItemCount.put(temp, lcount);
			}

		}

		printFreqItemset(1);
	}

	/*
	 * Generates the formatted output required for frequent itemsets
	 * 
	 * @param seqNumber
	 */
	static void printFreqItemset(int seqNumber) {
		// sorting the previous itemset according to item MIS
		if (seqNumber == 1) {
			HashMap<ArrayList, Integer> sortedhm = (HashMap<ArrayList, Integer>) sortByValue(prevFreqItemCount);
			System.out.println("No. of length " + seqNumber
					+ " frequent itemsets:" + prevFreqItemCount.size());
			for (Iterator it = sortedhm.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				ArrayList<Integer> itemset = (ArrayList<Integer>) entry
						.getKey();

				System.out.println(itemset + "support-count ="
						+ entry.getValue());
			}
		}
		if (seqNumber >= 2) {
			System.out.println("FK ---->"+fk);
			HashMap<ArrayList, Integer> fvalue = (HashMap<ArrayList, Integer>) sortByValue(fk);
			System.out.println("No. of length " + seqNumber
					+ " frequent itemsets:" + fk.size());
			for (Iterator it = fvalue.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				ArrayList<Integer> itemset = (ArrayList<Integer>) entry
						.getKey();

				System.out.println(itemset + ": support-count = "
						+ entry.getValue());

			}
		}
	}
	public static ArrayList<ArrayList> msCandidateGen()
	{
		//System.out.println(fkprev);
		ArrayList<ArrayList> Ck = new ArrayList<ArrayList>();
		
		ArrayList fkList = new ArrayList();
		fkList.addAll(fkprev.keySet());
		//System.out.println("fkList : "+fkList);
		for(int set1Count = 0; set1Count < fkList.size(); set1Count++)
		{
			ArrayList f1 = (ArrayList) fkList.get(set1Count);
			for(int set2Count = set1Count+1; set2Count < fkList.size(); set2Count++){
					ArrayList f2 = (ArrayList) fkList.get(set2Count);
					int val1 = (Integer) f1.get(f1.size()-1);
					int val2 = (Integer) f2.get(f2.size()-1);
					Float mis1 = MISValues.get(val1);
					Float mis2 = MISValues.get(val2);
					Float sup1 = (float) (hm.get(val1)/transCount);
					Float sup2 = (float) (hm.get(val2)/transCount);
//					System.out.println(mis1 + "<" + mis2 + " && " + Math.abs(sup1 - sup2) + "<" + SDC);
					if( Math.abs(sup1 - sup2) <= SDC)
					{
	//					TreeSet hsc = new TreeSet();
						//hsc.addAll(f1);
						//hsc.addAll(f2);
						ArrayList newCandidate = new ArrayList();
						ArrayList f11 = new ArrayList();
						f11.addAll(f1);
						f11.remove(f11.size()-1);
						ArrayList f22 = new ArrayList();
						f22.addAll(f2);
						f22.remove(f22.size()-1);
						HashSet hs1 = new HashSet(f11);
						HashSet hs2 = new HashSet(f22);
						if(!hs1.retainAll(hs2))
						{
							if(MISValues.get(val1) < MISValues.get(val2) )
							{
								f11.add(val1);
								f11.add(val2);
							}
							else{
								f11.add(val2);
								f11.add(val1);								
							}
							Ck.add(f11);
						}
					}
					
				}
		}
		System.out.println("CK==>"+Ck);
		ArrayList<ArrayList> modCk = new ArrayList<ArrayList>();
		modCk.addAll(Ck);
		for(Iterator ir = Ck.iterator();ir.hasNext();){
			ArrayList c = (ArrayList)ir.next();
			//System.out.println("c.get"+c.get(0));
			ArrayList<String> subSets = genPowerSet(c);
			Boolean notFound = null;
			for(String subSet : subSets){
				ArrayList subSetList = convertToColl(subSet);
				/*System.out.println("=============");
				System.out.println("subSetList : "+ subSetList);
				System.out.println("c.get(0) : "+MISValues.get(c.get(0)));
				System.out.println("c.get(1) : "+MISValues.get(c.get(1)));*/
				Float mis1 = MISValues.get(c.get(0));
				Float mis2 = MISValues.get(c.get(1));
				if(subSetList.contains(c.get(0)) || mis1.equals(mis2)){
					notFound = true;
					//System.out.println("in");
					for(ArrayList summa : fkprev.keySet()){
						//System.out.println("Compare : "+summa);
						if(summa.containsAll(subSetList))
						{
							//System.out.println(summa);
							notFound = false;
							//System.out.println("Match Found");
							break;
						}
					}
					if(notFound){
						modCk.remove(c);
						//System.out.println("Not Found : "+subSet);
					}
				}
				/*System.out.println("=============");*/
				
			}
			/*for(Set subSet : newSet){
				if(true)
				{
					String subSetString = convertToString((HashSet)subSet);
					
					//System.out.println("subset : "+subSet.toString().equalsIgnoreCase("279"));
					//System.out.println("c0 : " + c.get(0) + "||" + MISValues.get(c.get(1))  + "=="+ (MISValues.get(c.get(2))));
					if ( subSet.contains(c.get(0)) || ( MISValues.get(c.get(1)) == (MISValues.get(c.get(2)))))
					{
						 notFound = true;
						//System.out.println("Subset to be compared : "+subSet);
						for(ArrayList summa : fkprev.keySet()){
							if(summa.containsAll(new ArrayList(subSet)))
							{
								//System.out.println(summa);
								notFound = false;
								break;
							}
						}
						if(notFound){
							System.out.println("Not Found : "+subSet);
						}
					}
				}
			}*/
			/*if(notFound)
			System.out.println("candidate not found : "+c);*/
		}
		
		//System.out.println(modCk);
		return modCk;
		
	}

	private static ArrayList convertToColl(String subSet) {
		char[] charArray = subSet.toCharArray();
		int count = 0;
		ArrayList retArrayList = new ArrayList();
		while(count < charArray.length)
		{
			Integer temp = Integer.parseInt(""+charArray[count]);
			retArrayList.add(temp);
			count++;
		}
		return retArrayList;
	}

	private static ArrayList genPowerSet(ArrayList hsCandidate) {
		int len = hsCandidate.size()-1;
		ArrayList returnSubSetList = new ArrayList();
		String strInput = convertToString(hsCandidate);
		//System.out.println("strInput"+strInput);
		char[] inpChar = strInput.toCharArray();
		String byteArr="";
		for(int i=0;i<=len;i++){
			byteArr+="1";
		}
		//System.out.println("byteArr : "+byteArr);
		char[] c = byteArr.toCharArray();
		int count = 0;
		int clen = c.length;
		while(count < clen)
		{
			c[count]='0';
			//System.out.println(c);
			int subCount = 0;
			String intString = "";
			while(subCount < clen){
			String temp1 = ""+c[subCount];
			String temp2 = ""+inpChar[subCount];
			//System.out.println("subC : "+inpChar[subCount]+" * "+temp1);
			Integer finalVal = Integer.parseInt(temp2)*Integer.parseInt(temp1);
			intString += finalVal.toString();
			subCount++;
			}
			//System.out.println("intString : "+intString.replace("0", "").trim());
			intString = intString.replace("0", "").trim();
			returnSubSetList.add(intString);
			c[count]='1';
			count++;
		}
		return returnSubSetList;
	}

	private static String convertToString(ArrayList col) {
		String str="";
		//System.out.println("col:" +col);
		for(Iterator ir = col.iterator();ir.hasNext();){
			Integer i = (Integer)ir.next();
			//System.out.println("obj : "+i);
			str+=i.toString();
		}
		return str;
		
	}
}
