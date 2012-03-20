import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Class having a main method that implements the MSApriori algorithm.
 * @author gvenkateswaran <vganes3@uic.edu>
 *
 */
/**
 * Sorting code taken from http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java/3420912#3420912+
 * @author gvenkateswaran
 *
 */
public class MSApp {
	
	private static HashMap<Byte,Byte> hm = new HashMap<Byte,Byte>();
	private static HashSet<Byte> hs = new HashSet<Byte>();
	private static HashMap<Byte,Float> MISValues = new HashMap<Byte, Float>();
	private static HashMap<Byte,Float> sortedMISValues = new HashMap<Byte, Float>();
	private static HashMap<Byte,Float> prevFreqItemset = new HashMap<Byte,Float>();
	private static int transCount=0;
	private static float SDC = (float) 0.0;
	public static void main(String a[])
	{
		initialize();	
	}
	/**
	 * Performs the initial loading of File reads for inputs and parameters.
	 * Calls {@link initPass} to perform initial pass that eventually gives us L and F1.
	 */
	private static void initialize() {
		try {
			/*FileReader data_fr = new FileReader("C:\\mining\\data-1.txt");
			FileReader para_fr = new FileReader("C:\\mining\\para-1.txt");*/
			FileReader data_fr = new FileReader("/media/4458AD7058AD6202/Documents and Settings/Data Mining/data-1.txt");
			FileReader para_fr = new FileReader("/media/4458AD7058AD6202/Documents and Settings/Data Mining/para-1.txt");
			BufferedReader data_br = new BufferedReader(data_fr);
			BufferedReader para_br = new BufferedReader(para_fr);
			String s;
			while( (s=data_br.readLine()) != null)
			{
				if(!s.equalsIgnoreCase(""))
				{
					System.out.println("Line Read :"+ s);
					initPass(s);
					transCount++;
				}
			}
			while( (s=para_br.readLine()) != null)
			{
				if(!s.equalsIgnoreCase(""))
				{
					System.out.println("Line Read :"+ s);
					if(s.startsWith("SDC =", 0))
						storeSDC(s);
					else
						storeMIS(s);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HM : "+hm);
		System.out.println("HS : "+hs);
		System.out.println("MISValues : "+MISValues);
		sortedMISValues = (HashMap<Byte, Float>) sortByValue(MISValues);
		System.out.println(sortedMISValues);
		frequent1itemsets();
		
		
	}
	
	/**
	 * Method that stores SDC value.
	 * @param s
	 */
	private static void storeSDC(String s) {
		String[] sdc = s.split("=");
		SDC = Float.parseFloat(sdc[1].trim());
		System.out.println("SDC : "+SDC);
	}
	
	/**
	 * Method that stores MIS values from initialize.
	 * @param s
	 */
	private static void storeMIS(String s) {
		String key = s.substring(s.indexOf('(')+1,s.indexOf(')')) ;
		System.out.println(key);
		String[] val = s.split("=");
		MISValues.put(Byte.decode(key.trim()), Float.parseFloat(val[1].trim()));
	}
	
	/**
	 * Performs init pass. Does 2 things:
	 * 1. Traverses each transaction and adds each element to a HashSet var hs.
	 * 2. If currentval is already present in hs, the count in hm is incremented.
	 * 3. If currentval is not presenet in hs, adds currentval to hs and sets the count of current element to 1 in hm.
	 * @param s
	 */
	static void initPass(String s)
	{
		String[] vals = s.split(",");
		for(int i=0; i < vals.length; i++)
		{
			Byte current_val = Byte.parseByte(vals[i].trim());
			if(hs.add(current_val))
			{
				hm.put(current_val, (byte)1);
			}
			else
			{
				byte count = hm.get(current_val);
				hm.put(current_val, ++count);
			}
		}
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
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	} 
	
	/*
	 * Performs the generation frequent1itemsets using the number of transaction count 
	 * and L
	 */
	private static void frequent1itemsets()
	{
		 for (Iterator it = hm.entrySet().iterator(); it.hasNext();) {
		        Map.Entry entry = (Map.Entry)it.next();
		        Byte lcount=(Byte) entry.getValue();
		        //individual item frequency;
		        float itemsetMisValue=(float)lcount/(float)transCount;
		    
		        /*System.out.println(entry.getKey());
		        System.out.println(itemsetMisValue);
		        System.out.println(sortedMISValues.get(entry.getKey()));
		        */
		        
		        //condition for adding item to a frequent itemset
		        if(itemsetMisValue >= sortedMISValues.get(entry.getKey()))
		        		{
		        			//condition meet adding itemset to last known frequent itemset
		        			prevFreqItemset.put((Byte)entry.getKey(),itemsetMisValue);
		        		}
		        
		    }
		 printFreqItemset(1);
	}
	/*
	 * Generates the formatted output required for frequent itemsets
	 * @param seqNumber
	 */
	static void printFreqItemset(int seqNumber)
	{
		//sorting the previous itemset according to item MIS
		HashMap<Byte,Byte> sortedhm = (HashMap<Byte,Byte>) sortByValue(prevFreqItemset);
		System.out.println("No. of length "+seqNumber +" frequent itemsets:" + prevFreqItemset.size());
		for (Iterator it = sortedhm.entrySet().iterator(); it.hasNext();) {
			 Map.Entry entry = (Map.Entry)it.next();
			 System.out.println("("+ entry.getKey()+") : support-count =" +hm.get(entry.getKey()));
		 }
	}
}
