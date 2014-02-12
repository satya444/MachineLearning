//id3 project
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class PartitionExample {
	public static void main(String args[]){
		//System.out.println("Enter names of dataset and partitionset inputs");
		int a[]= new int[2];
		Pattern p = Pattern.compile("-*");
		File datasetf= new File("C:\\Users\\Dilip\\Desktop\\ML\\input.txt");
		try {
			Scanner readdataset= new Scanner(datasetf);
			//readdataset.useDelimiter(p);
			if(readdataset.findInLine("-") != null){
				readdataset.next();
			}
			//else{
			a[0]= readdataset.nextInt();
			a[1]=readdataset.nextInt();
			//}
			System.out.println(a[0] +"     "+a[1]);
			int input[][]= new int[a[0]][a[1]];
			int j =0;
			int k =0;
			while(readdataset.hasNext()){
				if(readdataset.findInLine("-") != null){
					readdataset.next();
				}
				else{
					input[j][k]= readdataset.nextInt();
					if(k<a[1]-1){
						k++;
					}
					else{
						j++;
						k=0;
					}
				}
			}
			for(int m =0; m<a[0]; m++){
				for(int n=0; n<a[1]; n++){
					System.out.print(input[m][n]);
				}
				System.out.println();
			}


			File partitionset = new File("C:\\Users\\Dilip\\Desktop\\ML\\partition.txt");
			Scanner readpartition = new Scanner(partitionset);
			//readpartition.useDelimiter(p);
			//List<ArrayList> l = new ArrayList<>();
			HashMap<String, ArrayList<Integer>> h = new HashMap();
			//readpartition.skip(p);
//			Scanner readpartition = new Scanner(partitionset);
			//List<ArrayList> l = new ArrayList<>();
	//		HashMap<String, ArrayList<Integer>> h = new HashMap();
			while(readpartition.hasNext()){
				//readpartition.skip(p);
				String line = readpartition.nextLine();
				StringTokenizer str = new StringTokenizer(line);
				int i =0;
				ArrayList<Integer> al = new ArrayList();
				String id =str.nextToken();
				while(str.hasMoreTokens()){
					al.add(Integer.parseInt(str.nextToken()));
				}
				h.put(id,al);
			}
			for(String s : h.keySet()){
				System.out.println(s+" "+h.get(s));
			}
			double totent= entropy(input);

			System.out.println("target entropy"+ totent);
			individualentropies(input,h);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void individualentropies(int[][] input,
			HashMap<String, ArrayList<Integer>> h) {
		HashMap<String, Map<String,Double>> gains = new HashMap();
		String id = h.keySet().iterator().next();
		for(String s : h.keySet()){
			System.out.println("id isssssss"+s);
			ArrayList<Integer> l = new ArrayList();
			l= h.get(s);
			Iterator<Integer> itr = l.iterator();
			int b[][]= new int[l.size()][];
			int i=0;
			int j=0;
			while(itr.hasNext()){
				b[i]=input[itr.next()-1];
				i++;
			}
			Map<String,Double> groupent=groupone(b,input.length);
			gains.put(s, groupent);


		}
		double maxgain =0;
		String divided =null;
		String nextpartition = null;
		for(String s: gains.keySet()){
			Map<String,Double> temp= gains.get(s);
			String attr= temp.keySet().iterator().next();
			System.out.println("attr is "+ attr);
			double tempgain = temp.get(attr);
			if(tempgain>=maxgain){
				maxgain = tempgain;
				nextpartition = s;
				divided = attr;
			}
		}

		System.out.println("gain is"+maxgain);
		System.out.println("next part is"+nextpartition);
		System.out.println("division is" + divided);
		ArrayList<Integer> l= h.get(nextpartition);
		int c[][]=new int[l.size()][];
		Iterator<Integer> it= l.iterator();
		int m=0;
		List<Integer> tracking= new ArrayList();
		while(it.hasNext()){
			int no= it.next();
			c[m]=input[no-1];
			m++;
			tracking.add(no);
		}
		int coldiv= Integer.parseInt(divided);
		ArrayList<Integer> zeros= new ArrayList();
		ArrayList<Integer> ones= new ArrayList();
		ArrayList<Integer> twos= new ArrayList();
		for(int k=0; k<c.length; k++){
			if(c[k][coldiv]==0){
				zeros.add(tracking.get(k));

			}
			if(c[k][coldiv]==1){
				ones.add(tracking.get(k));

			}
			if(c[k][coldiv]==2){
				twos.add(tracking.get(k));
			}
		}
		h.remove(nextpartition);
		if(!zeros.isEmpty()){
			String s= nextpartition+"0";
			h.put(s, zeros);
		}
		if(!ones.isEmpty()){
			String s= nextpartition+"1";
			h.put(s, ones);
			
		}
		if(!twos.isEmpty()){
			String s= nextpartition+"2";
			h.put(s, twos);
		}
		for(String s : h.keySet()){
			System.out.println(s+" "+h.get(s));
		}
		File file = new File("C:\\Users\\Dilip\\Desktop\\ML\\output.txt");
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	try{
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(String s : h.keySet()){
			bw.write(s);
			bw.write(" ");
			ArrayList<Integer> li = h.get(s);
			Iterator<Integer> itr = li.iterator();
			while(itr.hasNext()){
				
				bw.write(String.valueOf(itr.next()));
				bw.write(" ");
			}
			bw.newLine();
		}
		bw.close();
	}
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}




	private static Map<String, Double> groupone(int[][] b,int totlen) {
		double totent = entropy(b);
		int verlen= b.length;
		int horlen= b[0].length;
		Map<String,Double> subarr= new HashMap();
		int t[]= new int[verlen];
		double maxgain =0;
		String maxindex= null;
		for(int k=0;k<verlen;k++){
			t[k]=b[k][horlen-1];
		}
		for(int j =0; j< horlen-1; j++){
			int a[]= new int[verlen];
			for(int i =0; i< verlen; i++){
				a[i]=b[i][j];
			}
			double gaincol= gaincolumn(a,t,totent);
			gaincol=(double)(b.length)*gaincol/(double)totlen;
			if(gaincol>=maxgain){
				maxgain=gaincol;
				maxindex = String.valueOf(j); 
			}
		}	
		//double partialgain= (double)(b.length)*maxgain/(double)totlen;
		subarr.put(maxindex,maxgain);
		return subarr;
	}

	private static double gaincolumn(int[] a, int[] t,double totent) {
		double gain=0;
		double entgiva=0;
		double entropyzero=0;
		double entropyone=0;
		double entropytwo=0;
		double entgivzero=0;
		double entgivone=0;
		double entgivtwo=0;
		int zerosina=0;
		int onesina=0;
		int twosina=0;
		for(int j=0;j<2;j++){
			int countzeroina=0;
			int countoneina=0;
			int counttwoina=0;
			int countzero=0;
			int countone=0;
			double partialentzer=0;
			double partialentone=0;
			int flag0=0;
			int flag1=0;
			int flag2=0;
			
			for(int i=0; i<a.length;i++){
				if(a[i]== j){
					if(a[i]==0){
						flag0=1;
						countzeroina++;
					}
					if(a[i]==1){
						flag1=1;
						countoneina++;
					}
					if(a[i]==2){
						flag2=1;
						counttwoina++;
					}
					if(t[i]==0){
						countzero++;
					}
					else if(t[i]==1){
						countone++;
					}
				}
			}
			if(countzero==0){
				partialentzer=0;
				System.out.println("partialentz"+partialentzer);
			}
			else if(flag0==1){
			//	if(a[i]==0){
				double p =((double )countzero)/((double)countzeroina);
				partialentzer= p*(Math.log(1/p))/(Math.log(2));
				System.out.println("partialentz"+partialentzer);
			}
			else if(flag1==1){
				//	if(a[i]==0){
					double p =((double )countzero)/((double)countoneina);
					partialentzer= p*(Math.log(1/p))/(Math.log(2));
					System.out.println("partialentz"+partialentzer);
				}
			else if(flag2==1){
				//	if(a[i]==0){
					double p =((double )countzero)/((double)counttwoina);
					partialentzer= p*(Math.log(1/p))/(Math.log(2));
					System.out.println("partialentz"+partialentzer);
				}
			if(countone==0){
				partialentone=0;
				System.out.println("partialone"+partialentone);
			}
			else if(flag0==1){
				double p =((double )countone)/((double)countzeroina);
				partialentone= p*(Math.log(1/p))/(Math.log(2));
				System.out.println("partialone"+partialentone);
			}
			else if(flag1==1){
				double p =((double )countone)/((double)countoneina);
				partialentone= p*(Math.log(1/p))/(Math.log(2));
				System.out.println("partialone"+partialentone);
			}
			else if(flag2==1){
				double p =((double )countone)/((double)counttwoina);
				partialentone= p*(Math.log(1/p))/(Math.log(2));
				System.out.println("partialone"+partialentone);
			}
			if(j==0){
				entgivzero=partialentone+partialentzer;
				zerosina= countzeroina;
				System.out.println("zerogain"+entgivzero);
			}
			if(j==1){
				entgivone=partialentone+partialentzer;
				onesina=countoneina;
				System.out.println("onegain"+entgivone);
			}
			if(j==2){
				entgivtwo=partialentone+partialentzer;
				twosina=counttwoina;
				System.out.println("twogain"+entgivtwo);
			}
		}
		entropyzero=((double)(zerosina)/(double)a.length)*entgivzero;
		entropyone=((double)(onesina)/(double)a.length)*entgivone;
		entropytwo=((double)(twosina)/(double)a.length)*entgivtwo;
		entgiva=(entropyzero+entropyone+entropytwo);
		gain=totent-entgiva;
		System.out.println("totent is "+ totent);
		System.out.println("gain plzzzzzzzzzzzzzzzzzz"+ gain);
		return gain;
	}

	private static double entropy(int[][] input) {
		System.out.println("length is"+input.length);
		int horlength= input[0].length;
		int countzero=0;
		int countone=0;
		for(int i =0; i<input.length;i++){
			if(input[i][horlength-1]==0){
				countzero++;
			}
			else if(input[i][horlength-1]==1){
				countone++;
			}
		}
		System.out.println("zero countis"+ countzero);
		double targetentropy= 0;
		double zeroent= (double)countzero/(input.length);
		System.out.println("zeroent is "+zeroent);
		double zerocont=0;
		if(zeroent==0){
			zerocont=0;
		}
		else{
			zerocont=(zeroent*Math.log(1/zeroent))/(Math.log(2));
		}double oneent = (double)countone/(input.length);
		double onecont=0;
		if(oneent==0){
			onecont=0;
		}
		else{
		 onecont= (oneent*Math.log(1/oneent))/(Math.log(2));
		}
		targetentropy= zerocont+onecont;

		return targetentropy;
	}

}



