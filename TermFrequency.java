import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

class Main extends Frequency{
	public Main(String word, float value) {
		super(word, value);	
	}

	static TreeMap<String , Float> map = new TreeMap<String , Float>();
	static TreeMap<String , Float> map1 = new TreeMap<String , Float>();
	static List<Frequency> temp = new ArrayList<Frequency>();
	private static PrintWriter pw;
	
	public static TreeMap<String , Float> getMap(){ return map; }
	

	public static TreeMap<String , Float> termFreq(File node,int i) 
	{
		map1.clear();
		int y;
		BufferedReader br;
		String arr[];
		String line;
		try
		{
			br = new BufferedReader(new FileReader(node));
			while ((line = br.readLine()) != null) 
			{
				char a[];
				float f=1;
				arr = line.split(" "); // To split the string in one line w.r.t " "
				for (String str : arr) 
				{
					a = str.toCharArray(); // Convert each String in each line to char[]
					for (y = 0; y < a.length; y++) // Loop to Eliminate '.' ,'!' , etc....
					{
						if ((a[y] >= 'a' && a[y] <= 'z') || (a[y] >= 'A' && a[y] <= 'Z'))
							continue;
						else
							break;
					}
					if (y == 0)
						continue;
					str = String.valueOf(a, 0, y); // Convert each char[] to String
					if(i==2)						//For Term per Document Frequency
					{
						if (map1.get(str) == null)
						{
							map1.put(str, f);
						}
						else
						{
							float value1 = map1.get(str);
							map1.replace(str,++value1);
						}
					}
					else							//For Termed Frequency
					{
						if (map.get(str) == null)
						{
							map.put(str, f);
						}
						else
						{
							float value = map.get(str);
							map.replace(str, ++value);
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			System.out.printf("Exception Occurred at Location : %s\n", node);
		}
		if(i==2)
			return map1;
		else 
			return map;
	}
	
	public static void TermDocFreq(File node)
	{
		try {
			temp.clear();
	        pw = new PrintWriter(new BufferedWriter(new FileWriter("TermDocumentFrequency.txt",true)));  //For appending into text file
	        pw.println("   File Name           Path of the File  ");
	        pw.printf("   %s         %s",node.getName(),node.getAbsolutePath());
			pw.println();
			pw.println();
			pw.println("    Word " + "\t\tTerm per Document Frequency");
			for (String key : map1.keySet()) 						//Calculating Term frequency per Document
			{
				float value = map.get(key);							//Occurrences from all files
				float value1 = map1.get(key);						//Occurrences from current file
				temp.add(new Frequency(key,value1/value));			//Inserting into ArrayList
			}
			ValueComparator vc = new ValueComparator();				//instance necessary to invoke compare function
			Collections.sort(temp,vc);								//compare function invoked
			int i=10;												//To get Top 10 occurrences of each File 
			for(int j=temp.size()-1;j>=0&&i>0;j--,i--)				//To write into file the top 5 important words
			{
				Frequency obj = temp.get(j);
				pw.printf("   %-20s  \t%f\n", obj.getWord(), obj.getOccur());			//Writing into file after sorting in ascending order
				pw.println();
			}
			pw.println();
			pw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.printf("Exception Ocurred in TermDocFreq at location : %s\n",node.getAbsolutePath());
		}
	}

	public static void FindFile(File node,int i)
	{
		if((node.listFiles()==null)&&node.isFile())				//instead of Folder , we get only one file to calculate term frequency
		{
			if(node.isFile())
			{
				if(i==1)
					map = termFreq(node,1);
				else if(i==2)
				{
					map1 = termFreq(node,2);
					TermDocFreq(node);
				}
			}
		}
		else
		{
			File[] FileArr = node.listFiles();
			for(File fl : FileArr)
			{
				if(fl.isFile())
				{
					if(i==1)
						map = termFreq(fl,1);
					else if(i==2)
					{
						map1 = termFreq(fl,2);
						TermDocFreq(fl);
					}
				}
				else
				{
					FindFile(fl,i);
				}
			}
		}
	}	

	
	public static void Display(TreeMap<String , Float> temp)
	{
		System.out.println("Word " + "\t\t       Occurs\n");
		for (String key : temp.keySet()) 
		{
			float value = temp.get(key);
			System.out.printf("%-20s  %d\n", key, (int)value);
		}
	}
	
}

class ValueComparator implements Comparator<Frequency>
{
	public int compare(Frequency a, Frequency b) 
	{
		if (a.getOccur() > b.getOccur()) 
		{
			return 1;
		} else 
		{
			return -1;
		}
	}
}
 class Frequency 
{
	private String word;
	private float occur;
	public Frequency(String word, float value)
	{
		this.word = word;
		this.occur = value;
	}
	public String getWord() { return word; }
	public float getOccur() { return occur; }
}

public class TermFrequency
{
	public static void main(String[] args) 
	{
		int i;
		File node = new File("F:\\Studies\\java videos\\bbc");					//Given Folder Location
		System.out.println("1. Termed Frequency");
		System.out.println("2. Termed Document Frequency");
		System.out.println("Enter your choice : ");
		Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
		i = sc.nextInt();
		switch(i)
		{
			case 1: Main.FindFile(node,1);
					TreeMap<String , Float> temp = Main.getMap();
					Main.Display(temp);
					break;
			case 2: try {
						File file = new File("TermDocumentFrequency.txt");
						if(!file.exists())
							file.createNewFile();
						Main.FindFile(node,1);								//To calculate all the term occurrences in file
						Main.FindFile(node,2);
						System.out.println("Written in File");
						break;
						} catch (Exception e) {
							System.out.println("Exception Occured in main");
						}
			default : System.out.println("Wrong Choice");
			
		}
	}
}
