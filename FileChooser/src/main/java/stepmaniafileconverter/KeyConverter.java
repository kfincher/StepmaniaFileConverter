//////////////////////////////////
// KeyConverter.Java
// Takes (long) String of notes, and outputs them randomly organized as the appropriate keyamount
// To-do: Add "forgiveness" rule: rule that disregards one of the rules for cancelling placement if a set snap has been passed (I.E. if it's been an 8th notes since the last rule)
//        improve randomness to decrease mini-jacks
// Version 1.2 
//////////////////////////////////
package stepmaniafileconverter;


import java.io.*;
import java.util.*;


public class KeyConverter{
	public static char[][] Notes = {};
	public static char[][] NotesCopy = {};
	public static char[] lastRow = {};
	public static char[] anyLastRow = {};
	public static char[] upcomingRow = {};
	public static FileWriter out;
	public static int keyAmount;
	public static int mode = 1;
	
	public static double[] notePossibilities; //= {2,2,2,2};

	public static void changeProb(int index){
		double amt = 0.6; // this seems to work pretty well in preventing it from being to anchory or spreaded
		for(int i = 0;i<notePossibilities.length;i++){
			if(i==index)
				notePossibilities[i] -= amt;
			else
				notePossibilities[i] += amt/(keyAmount-1); // Could potentially change the weighting to decrease hand bias
			
			// catches probabilities that may go negative;
			if(notePossibilities[i]<=0){
				notePossibilities[i] = 0.1;
			}
		}
	}
	
	public static int probability(ArrayList<Integer> in){
		double sum = 0;
		double[] temp = new double[in.size()];
		for(int i = 0;i< in.size();i++){
			sum+=notePossibilities[in.get(i)];
			temp[i] = sum;
		}
		double randProb = Math.random()*sum; // normalizing max to sum;
		for(int i = 0;i< in.size();i++){
			if(randProb<temp[i]){
				return in.get(i);
			}
		}
		return '1';
	}
	
	public static void printLine(char[]Nt){
		for(int y = 0; y<Nt.length;y++){
			System.out.print(Nt[y]);
		}
		System.out.println();
	}
	public static void printNotes(char[][] Nt){
		for(int x = 0; x<Nt.length;x++){
			//System.out.print(x+": ");
			for(int y = 0; y<Nt[0].length;y++){
				System.out.print(Nt[x][y]);
			}
			System.out.println();
		}
	}	
	public static void outputNotes(){
		for(int x = 0; x<NotesCopy.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				try{
					out.write(NotesCopy[x][y]);
					if(NotesCopy[x][y]==',')
						y=keyAmount;
				}catch(Exception e){System.out.println(e);}
			}
			try{
				out.write("\n");
			}catch(Exception e){System.out.println(e);}
		}
	}
	public static String outputNotesString(){
		String temp = "";
		for(int x = 0; x<NotesCopy.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				
					temp+=NotesCopy[x][y];
					if(NotesCopy[x][y]==',')
						y=keyAmount;
			}
				temp += ("\n");			
		}
		temp+=';';
		return temp;
	}
	// Finds and randomly places all 2's and 3's
	// places K's between 2's and 3's	
	public static void holdHandler(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='2'||Notes[x][y]=='4'){
					int place = -1;
					// places 2 in a valid position
					while(true){
						place = (int)(Math.random()*keyAmount); // PLACEMENT HANDLED HERE
						if(NotesCopy[x][place]=='0')
							break;
					}
					NotesCopy[x][place]=Notes[x][y];
					int tempX = x+1;
					while(true&&tempX<=Notes.length){
						if(Notes[tempX][y]=='3'){
							NotesCopy[tempX][place]='3';
							break;
						}else{
							if(Notes[tempX][0]!=',')
							NotesCopy[tempX][place]='K';
							tempX++;
						}
					}
				}
			}
		}
	}
	
	public static void KRemover(){
		for(int x = 0; x<NotesCopy.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				if(NotesCopy[x][y]=='K')
					NotesCopy[x][y]='0';
			}
		}
	}
	
	public static int count(char[] arr){
		int amt = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]=='1'||arr[i]=='K') amt++;
		}
		return amt;
	}
	public static int countOnes(char[] arr){
		int amt = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]=='1') amt++;
		}
		return amt;
	}
	public static int countTwos(char[] arr){
		int amt = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]=='2'||arr[i]=='4') amt++;
		}
		return amt;
	}
	public static int countThrees(char[] arr){
		int amt = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]=='3'||arr[i]=='K') amt++;
		}
		return amt;
	}
	// copys row at index "row" to output array
	public static void copyRow(int row,char[] arr){
		for (int y=0;y<NotesCopy[0].length;y++){
			arr[y] = NotesCopy[row][y];
		}
	}
	public static int findNextNoteRow(int row){
		for (int x=row;x<NotesCopy.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				if(NotesCopy[x][y]=='2'){ 
					//System.out.println("Found one on "+ x);
					return x;
				}
			}
		}
		return 0;
	}
	public static void randomPlacer(){
		boolean oneDetected = false;
		boolean noteDetected = false;
		int lastNoteRow = 0;
		int lastRowX = 0;
		int nextNoteRow = 0;
		if(NotesCopy.length>0)
			nextNoteRow = findNextNoteRow(1);
		for(int x = 0; x<NotesCopy.length;x++){			
			for(int y = 0; y<NotesCopy[0].length;y++){
				if(Notes[x][y]!='0'&&Notes[x][y]!='K')
					noteDetected = true;
				if(Notes[x][y]=='1'||Notes[x][y]=='M'){
					int place = -1;
					// places 1s and Ms in a valid position
					if(Notes[x][y]=='1'||Notes[x][y]=='2')
						oneDetected = true;
					if(mode==1){
						while(true){
							place = (int)(Math.random()*keyAmount); // PLACEMENT HANDLED HERE
							if(NotesCopy[x][place]=='0')
								break;
						}
					}else{
						int cnt = 0;
						ArrayList<Integer> possiblePositions = new ArrayList<Integer>();
						for(int i=0;i<keyAmount;i++){
							possiblePositions.add(i);
						}
						while(true){
							cnt++; // just a debugging variable
							//int placement = (int)(Math.random()*(possiblePositions.size())); // Old placement technique
							//place = possiblePositions.get((placement)); // PLACEMENT HANDLED HERE
							place = probability(possiblePositions); // PLACEMENT HANDLED HERE
							try{
								possiblePositions.remove(possiblePositions.indexOf(place)); // PLACEMENT HANDLED HERE
							}catch(Exception e){
								System.out.println("At "+x+" "+e);
								System.out.println(Arrays.toString(lastRow));
								System.out.println(Arrays.toString(NotesCopy[lastNoteRow]));
								System.out.println(Arrays.toString(Notes[x]));
								System.out.println(Arrays.toString(upcomingRow));
								System.out.println(Arrays.toString(NotesCopy[nextNoteRow]));
							}
							
							// This whole section needs cleaning
							// Maybe I should incorporate that distance finder to give priority to what should be handled, to avoid clutter
							if(NotesCopy[x][place]=='0'){
								if(Notes[x][y]=='M')
									break;
								else{ 
									if(count(lastRow)+count(Notes[x])+countThrees(Notes[x])>keyAmount){ // if there are more than X single notes, hold notes, or mid holds in the current row and the last row with notes combined, then accept placement
										//System.out.println("Break type 1 at: "+x);
										break;
									}else if(lastRow[place]!='1'&&upcomingRow[place]!='2'){ // if last row had a note in this spot, the upcomingRow has a hold in this spot, and the next row with notes does not have a hold in this spot, accept placement (last 2 kind of redudant)
										if(nextNoteRow-x<5||x-lastNoteRow<2){
											if(count(Notes[x])+countTwos(upcomingRow)+count(lastRow)+countThrees(NotesCopy[lastNoteRow])>keyAmount){ // if the ones & threes above combined with the twos and fours below is greater than keyamount, break
												int maximumDistance = 0;
												if(nextNoteRow-x>maximumDistance){
													maximumDistance = nextNoteRow-x;
													for(int wk = 0; wk<NotesCopy[nextNoteRow].length;wk++){
														if(NotesCopy[nextNoteRow][wk]=='2')
															place = wk;
													}
												}
												if(x-lastNoteRow>maximumDistance){
													maximumDistance = x-lastNoteRow;
													for(int wk = 0; wk<NotesCopy[lastNoteRow].length;wk++){
														if(NotesCopy[lastNoteRow][wk]!='0'&&NotesCopy[lastNoteRow][wk]!='K')
															place = wk;
													}
												}
												if(x-lastRowX>maximumDistance){
													maximumDistance = x-lastRowX;
													for(int wk = 0; wk<lastRow.length;wk++){
														if(lastRow[wk]=='1')
															place = wk;
													}
												}
												break;
											}
											
											boolean breakThis = false;
											if(nextNoteRow-x<5){
												if(NotesCopy[nextNoteRow][place]!='2'){
													breakThis = true; 
												}
											}
											if(x-lastNoteRow<2){
												if(NotesCopy[lastNoteRow][place]!='3'){
													breakThis = true; 
												}else
													breakThis = false;
											}
											if(breakThis)
												break;
										}else{
											break;
										}
										// for below: Total notes in lastrow, total notes in currRow, total Holds in row, holds in next valid row (row without ','), total holds in curr row
									}else if(countOnes(lastRow)+count(Notes[x])+countThrees(NotesCopy[x])+countTwos(upcomingRow)+countTwos(NotesCopy[x])>keyAmount){ // last catch all for combined amount of notes
										//System.out.println("Break at: "+x);
										break;
									}
								}
							}
							if(cnt>1000){
								System.out.println("broke");
								System.exit(0);
							}	
						}
						changeProb(place);
					}
					NotesCopy[x][place] = Notes[x][y];
				}
			}
			int nextRow = x+2;
			if((nextRow)<NotesCopy.length&&x>=nextNoteRow-1){
				nextNoteRow = findNextNoteRow(x+2); // Next row with hold note
			}
			while(true){
				if((nextRow)<NotesCopy.length){
					copyRow(nextRow,upcomingRow); //gets next valid row
					if(upcomingRow[0]==','){
						nextRow++;
						continue;
					}else
						break;
				}else{
					upcomingRow = new char[keyAmount];
					break;
				}
			}				
									
			if (noteDetected){
				lastNoteRow = x;
				//copyRow(x,anyLastRow); // Not used. Keeping just in case it's needed (or it somehow breaks something)
				noteDetected = false;
			}
			if (oneDetected){
				copyRow(x,lastRow);
				lastRowX = x;
				oneDetected = false;
			}
		}
	}
	
	
	public static String Convert (String chart,int amt){
		mode = 2;
		keyAmount = amt;
		notePossibilities = new double[keyAmount];
		for(int i = 0;i<notePossibilities.length;i++){
			notePossibilities[i] = 2;
		}
		int length = 0;
		int originalKeyAmount = 0;
		int count = 0;
		String[] tempy = chart.split("\n");
		length = tempy.length;
		lastRow = new char[keyAmount];
		anyLastRow = new char[keyAmount];
		upcomingRow = new char[keyAmount];
		Notes = new char[length][keyAmount];
		NotesCopy = new char[length][keyAmount];
		int line = 0;

		for (int j=0;j<tempy.length;j++){
			String temp = tempy[j];
			if(temp.charAt(0)==','){
				Notes[line][0] = temp.charAt(0);
				NotesCopy[line][0] = temp.charAt(0);
			}else{
				for(int i = 0;i<keyAmount;i++){
					if(i<temp.length())
						Notes[line][i] = temp.charAt(i);
					else
						Notes[line][i] = '0';
					NotesCopy[line][i] = '0';
				}
			}
			
			line++;
		}

		holdHandler();
		copyRow(1,upcomingRow);
		randomPlacer();
		KRemover();
		
		String temp = "";
		temp = outputNotesString();
		return temp;
	}
}