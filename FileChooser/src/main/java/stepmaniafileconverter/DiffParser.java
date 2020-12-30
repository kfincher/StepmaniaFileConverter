package stepmaniafileconverter;


import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
	
// To-do: handle .ssc files (Boss Battle)

public class DiffParser {
	private File stepFile;

	public static class Song{
		String title;
		String artist;
		String banner;
		int bpm=0;
		List<Diff> chart = new ArrayList<Diff>();
		String topText = "";
		public Song(){} // for default setting stuff
		public Song(int b){ // for setting everything manually
			bpm = b;
		}
	}
	public static class Diff{
		String difficulty = "";
		String mode = ""; 
		String rating = "";
		String topText = "";
		String Notes = "";
		int length = 0;
		int totalNotes = 0;
		public void Copy(Diff other){
			difficulty = other.difficulty;
			mode = other.mode;
			rating = other.rating;
			topText = other.topText;
			Notes = other.Notes;
			length = other.length;
			totalNotes = other.totalNotes;
		}
	}

	public static void overwrite(Scanner in){
		try{
			out.write(main.topText);
			for (int i = 0;i<main.chart.size();i++){
				out.write(main.chart.get(i).topText);
				out.write(main.chart.get(i).Notes);
				if(i!=main.chart.size()-1)
					out.write("\n");
			}
		}catch(Exception e){}
	}
	public static Song main = new Song();
	public static Scanner in;
	public static FileWriter out;
	public static String stepfile;

	public static void mainConvert(String[] args){	
		// need to be able to overwrite or replace song
		// ideally, step 1 would be open the program and being given the option to open a file
		// step 2, if the file is chosen successfully, it will display the song, as well as all of the charts and their relevant difficulties
		// Step 3, choose option to replace song or add song
		// Step 4, outut relevant choice to file
		try{
			stepfile = args[0];
			in = new Scanner(new FileReader(args[0]));
			
			int length = 0;
			int originalKeyAmount = 0;
			

			String status = "Scanning Top";
			
			String line = "";
			main.topText = "";
			int chartIndex = 0;
			int index = 0;
			while(in.hasNextLine()){
				line = in.nextLine();
				if(length>50000)
					break;
				
				if(line.length()>0&&status == "Scanning Top")
					if(line.charAt(0)=='/')
					status = "Starting Notes";
				
				
				if(status == "Scanning Top"){
					main.topText += line+"\n";
					if(line.contains("#TITLE:")){
						main.title = line.replace("#TITLE:","").replace(";","");
					}
					if(line.contains("#BANNER")){
						main.banner = line.replace("#BANNER:","").replace(";","");
					}
				}
				
				if(status == "Starting Notes"){
					main.chart.add(new Diff());
					status = "Getting Notes";
				}
				
				if(status == "Getting Notes"){
					main.chart.get(chartIndex).topText += line+"\n";
					if(index==0&&!line.contains("//")){
					}else{
						if(line.contains("dance-single")){
							main.chart.get(chartIndex).mode = "single";
						}else if(line.contains("dance-solo")){
							main.chart.get(chartIndex).mode = "solo";
						}else if(line.contains("kb7-single")){
							main.chart.get(chartIndex).mode = "kb7-single";
						}
						
						index += 1;
						
						if(index==5)
							main.chart.get(chartIndex).difficulty = line.replace(":","").replace(" ","");
						if(index==6)
							main.chart.get(chartIndex).rating = line.replace(":","").replace(" ","");
						
						if(index>=7){
							
							status = "Placing Notes";
							continue;
						}
					}
				}
				// Loops back to starting notes, unless the file is finished
				if(status == "Placing Notes"){
					main.chart.get(chartIndex).Notes += line;
					main.chart.get(chartIndex).length++;
					if(line.charAt(0)==';'){
						status = "Starting Notes";
						main.chart.get(chartIndex).totalNotes = main.chart.get(chartIndex).Notes.replace("\n","").replace("M","").replace("3","").replace("0","").replace(",","").replaceAll("\s","").replace(";","").length(); //fix this please. Just put it out of its misery
						chartIndex++;
						index = 0;
					}else{
						main.chart.get(chartIndex).Notes += "\n";
					}
				}
				
				length++;
			}
		}catch(Exception e){
			System.out.println(e+" big whoops");
		}
	}
	public static void sevenify(int choice){
		Diff temp = new Diff();
		temp.Copy(main.chart.get(choice));
		temp.mode = "kb7-single";
		temp.topText = temp.topText.replace("dance-single","kb7-single").replace("dance-solo","kb7-single");
		KeyConverter kc = new KeyConverter();
		temp.Notes = kc.Convert(main.chart.get(choice).Notes,7);
		main.chart.add(temp);
	}
	public static void soloify(int choice){
		Diff temp = new Diff();
		temp.Copy(main.chart.get(choice));
		temp.mode = "solo";
		temp.topText = temp.topText.replace("dance-single","dance-solo");
		KeyConverter kc = new KeyConverter();
		temp.Notes = kc.Convert(main.chart.get(choice).Notes,6);
		main.chart.add(temp);
	}
	public static void indexify(int choice){
		Diff temp = new Diff();
		temp.Copy(main.chart.get(choice));
		temp.mode = "single";
		temp.topText = temp.topText.replace("dance-solo","dance-single");
		Index ind = new Index();
		temp.Notes = ind.indexify(main.chart.get(choice).Notes);
		
		temp.totalNotes = temp.Notes.replace("\n","").replace("M","").replace("3","").replace("0","").replace(",","").replaceAll("\s","").replace(";","").length(); //fix this please. Just put it out of its misery
		main.chart.add(temp);
	}
	public static void mainConvertThree(String[] args){
		Diff temp = new Diff();
		int choice = 0;
		int option = 2;
		try{
			option = 2;
		}catch(Exception e){}
		
		if(option==1){
			try{
				System.out.println("Which chart would you like to replace?");
				choice = 1;
			}catch(Exception e){}
			main.chart.get(choice).Copy(temp);
		}else if(option==2){
			main.chart.add(temp);
		}

	}
	public static void saveFile(){
		try{
				out = new FileWriter(stepfile);
				overwrite(in);
				out.close();
		}catch(Exception e){}
	}
}