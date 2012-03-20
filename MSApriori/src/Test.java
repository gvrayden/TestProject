import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Test {
public static void main(String a[])
{
	FileReader data_fr;
	try {
		data_fr = new FileReader("/home/gvenkateswaran/workspace/MSApriori/data-5.txt");
		FileReader result_fr = new FileReader("/home/gvenkateswaran/workspace/MSApriori/result-5.txt");
		BufferedReader data_br = new BufferedReader(data_fr);
		BufferedReader para_br = new BufferedReader(result_fr);
		String s;
		int realcount =0;
		while ((s = data_br.readLine()) != null) {
			if (!s.equalsIgnoreCase("")) {
				//System.out.println("Line Read :" + s);
				String[] v = s.split(",");
				int lk =0;
				for(String m : v){

					if(m.trim().equalsIgnoreCase("8") )
					{
						if(lk == 1)
							realcount++;
						else
						lk=1;
					}
					if(m.trim().equalsIgnoreCase("12") )
					{
						if(lk == 1)
							realcount++;
						else
						lk=1;
					}
				}
			}
		}
		while ((s = para_br.readLine()) != null) {
			if (!s.equalsIgnoreCase("")) {
				//System.out.println("Line Read :" + s);
			}
		}
		System.out.println(realcount);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
}
