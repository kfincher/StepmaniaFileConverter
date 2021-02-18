
package stepmaniafileconverter;
// 1: regular note
// 2: hold start
// 3: hold end
// M: mine
// K: placeholder hold mid

import java.io.*;
import java.util.*;
public class Index{
	// chart to index chart
	// read in a chart from a file
	// load in every note into a 2D array as chars
	// go into placer function and set array equal to return value
	// return array file

	public static char[] notesPossible = {'1','2','3','4'};
	public static double[] notePossibilities = {2,2,2,2};
	// find relevant index given char
	public static int findProbability(char in){
		for(int i = 0;i<notesPossible.length;i++){
			if(in == notesPossible[i])
				return i;
		}
		return -1;
	}
	public static void changeProb(int index){
		double amt = 0.6; // this seems to work pretty well in preventing it from being to anchory or spreaded
		for(int i = 0;i<notePossibilities.length;i++){
			if(i==index)
				notePossibilities[i] -= amt;
			else
				notePossibilities[i] += amt/3;
			
			// catches probabilities that may go negative;
			if(notePossibilities[i]<=0){
				notePossibilities[i] = 0.1;
			}
		}
	}
	public static char probability(char[] in, double[] prob){
		double sum = 0;
		double[] temp = new double[prob.length];
		for(int i = 0;i< in.length;i++){
			sum+=prob[findProbability(in[i])];
			temp[i] = sum;
		}
		double randProb = Math.random()*sum; // normalizing max to sum;
		for(int i = 0;i< in.length;i++){
			if(randProb<temp[i]){
				changeProb(findProbability(in[i]));
				return in[i];
			}
		}
		return '1';
	}
	
	// checkNextAvail
	// based on hand and currLane, return available options
	// To-Do: Make 1&4 twice as likely to increase hand swapped
	// return as array
	public static char checkNextAvail(String Hand, int currLane){
		// left hand
		// 1:2,3,4
		// 2:3,4
		// 3:2,4
		int random = (int) Math.floor(Math.random() * 2) ;
		char[] ready = new char[3];
		if(Hand=="left"){
			if(currLane==1){
				random = (int) Math.floor(Math.random() * 3) ;
				ready = new char[]{'2','3','4'};
			}
			if(currLane==2){
				ready = new char[]{'3','4'};
			}
			if(currLane==3){
				ready = new char[]{'2','4'};
			}
		}
		// right hand
		// 2:1,3
		// 3:1,2
		// 4:1,2,3
		if(Hand=="right"){
			if(currLane==2){
				ready = new char[]{'1','3'};
			}
			if(currLane==3){
				ready = new char[]{'1','2'};
			}
			if(currLane==4){
				random = (int) Math.floor(Math.random() * 3) ;
				ready = new char[]{'1','2','3'};
			}
		}		
		return probability(ready,notePossibilities);
	}
	
	public static void printNotes(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				System.out.print(Notes[x][y]);
			}
			System.out.println();
		}
	}
	
	// Debugging function that outputs notes to a file without surrounding details.
	public static void outputNotes(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				try{
					out.write(NotesCopy[x][y]);
					if(NotesCopy[x][y]==',')
						y=4;
				}catch(Exception e){System.out.println(e);}
			}
			try{
				out.write("\n");
			}catch(Exception e){System.out.println(e);}
		}
	}
	
	public static void holdClear(int lineNum){ //unused
		for(int x = lineNum+1; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='3'){
					Notes[x][y]='0';
					x+=Notes.length;
					y+=Notes[0].length;
				}
			}
		}
	}
	
	public static void holdSingularize(){ //unused
		boolean holdInProgress = false;
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(holdInProgress==false){
					if(Notes[x][y]=='2'){
						holdInProgress = true;
						
						continue;
					}
				}	
				if(holdInProgress==true){
					
					if(Notes[x][y]=='3'){
						holdInProgress = false;
						while(y<Notes[0].length){
							if(Notes[x][y]!='3'){
								Notes[x][y]='0';
								holdClear(x);
							}
							y++;
						}
						continue;
						
					}
					if(Notes[x][y]!='3'){
						if(Notes[x][y]=='2')
							holdClear(x);
						Notes[x][y]='0';
					}
				}
			}
		}
	}
	
	public static void holdClean(){ 
		boolean holdStarted = false;
		for(int x = 0; x<Notes.length;x++){
			if(holdStarted==false){
				if(Notes[x][0]=='3'){
					Notes[x][0]='0';
				}
				if(Notes[x][0]=='2'){
					holdStarted=true;
					continue;
				}
			}
			if(holdStarted==true){
				if(Notes[x][0]!='3'){
					Notes[x][0]='0';
				}
				if(Notes[x][0]=='3'){
					holdStarted=false;
				}
			}
		}
	}
	
	public static void holdFinal(){
		boolean going = false;
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				if(Notes[x][y]=='3'){
					going=false;
					for(int w=0;w<Notes[0].length;w++){
						if(w==y)
							continue;
						else if(Notes[x][w]!='M')
							Notes[x][w]='0';
					}
				}
				if(Notes[x][y]=='2'){
					going=true;
					continue;
				}
				if(going){
					if(Notes[x][y]!='M')
						Notes[x][y]= '0';
				}	
			}
		}
	}
	
	public static void holdFinish(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(NotesCopy[x][y]=='2'){
					int row = x+1;
					while(NotesCopy[row][y]!='3'){
						NotesCopy[row][y]='0';
						row++;
					}
				}
			}
		}
	}
	
	// A debugging function that prints out all of the notes
	public static void printCopyNotes(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
				System.out.print(NotesCopy[x][y]);
			}
			System.out.println();
		}
	}

	// singularize Note()
	// for lanes 0-N, check for a 1 or 2 or K or 3
	// if 1 is detected, place a 1 in slot 0; 2 in slot 1;  K in slot 1; 3 in slot 1
	// return fullArray
	public static void singularizeNotes(){
		for(int x = 0; x<Notes.length;x++){
			boolean oneFound = false;
			int mineFound = 0;
			boolean holdStartFound = false;
			boolean holdEndFound = false;
			boolean KFound = false;
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='1')
					oneFound = true;
				if(Notes[x][y]=='M')
					mineFound++;
				if(Notes[x][y]=='2')
					holdStartFound = true;
				if(Notes[x][y]=='3')
					holdEndFound = true;
				if(Notes[x][y]=='K')
					KFound = true;
			}
			if(Notes[x][0]!=','){
				for(int y = 0; y<Notes[0].length;y++){
					Notes[x][y] = '0';
				}
				if(oneFound)
					Notes[x][1] = '1';
				if(holdStartFound)
					Notes[x][0] = '2';
				if(KFound)
					Notes[x][0] = 'K';
				if(holdEndFound)
					Notes[x][0] = '3';
				if(oneFound&&KFound)
					Notes[x][1] = '1';
				
				if(mineFound>0){
					Notes[x][2] = 'M';
					mineFound--;
					if(mineFound<=0){
						continue;
					}
					Notes[x][3]= 'M';
					mineFound--;
					if(mineFound<=0){
						continue;
					}
					if(!holdStartFound&&!holdEndFound){
						Notes[x][0]= 'M';
						mineFound--;
					}
					if(mineFound<=0){
						continue;
					}
					if(!oneFound){
						Notes[x][1]= 'M';
						mineFound--;
					}
				}
			}
		}
	}
	
	// remove Ks
	// search and replace all Ks with 0
	public static void removeK(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='2'&&x+1<Notes[0].length){
					int column = x+1;
					while(Notes[column][y]!='3'&&x+1<Notes[0].length){
						Notes[column][y] = '0';
						column++;
					}
				}
			}
		}
	}
	
	// add K's
	// add K's to all hold mid sections

	// if there is not a 3 in the row, place K in the currLane & more
	// onto the next row.
	// continue until it is 3 & return new 2D array
	public static void addK(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='2'	){
					int column = x;
					while(Notes[column+1][y]!='3'&&(x+1)<Notes.length){
						column++;
						Notes[column][y] = 'K';
					}	
				}
				if(Notes[x][y]=='2')
					System.out.println(x+" "+" "+y);
			}
		}
	}
	
	/* Representation of notes in the 2D array:
       y0 y1 y2 y3	
	x0 0  0  0  0
	x1 0  0  0  0
	x2 0  0  0  0
	x3 0  0  0  0
	*/
	
	public static void countTwos(){
		for(int x = 0; x<Notes.length;x++){
			for(int y = 0; y<Notes[0].length;y++){
				if(Notes[x][y]=='2')
					System.out.println("Found 2");
			}
		}
	}
	
	// converts int to hand string
	public static String handReturn (int hand){
		if(hand==0){
			return "right";
		}else{
			return "left";
		}
	}
	// This whole block of "contains" could be condensed down to take the char as input.
	//////////////////////// returns if corresponding char/note is found ////////////////	
	public static boolean containsOne(int lineNum){
		for(int x = 0; x<Notes[1].length;x++){
			if(Notes[lineNum][x]=='1')
				return true;
		}
		return false;
	}
	
	public static int containsTwo(int lineNum){
		for(int x = 0; x<Notes[0].length;x++){
			if(Notes[lineNum][x]=='2')
				return x;
		}
		return -1;
	}
	
	public static int containsThree(int lineNum){
		for(int x = 0; x<Notes[0].length;x++){
			if(Notes[lineNum][x]=='3')
				return x;
		}
		return -1;
	}
	
	public static int containsMine(int lineNum){
		for(int x = 0; x<Notes[0].length;x++){
			if(Notes[lineNum][x]=='M')
				return x;
		}
		return -1;
	}
	public static int findTwo(int lineNum){
		for(int x = lineNum-1; x<Notes.length;x--){
			for(int y = 0; y<Notes[0].length;y++){
				if(NotesCopy[x][y]=='2')
					return y;
			}
		}
		return -1;
	}
	////////////////////// End of "contains" block ///////////////////////////// 
	
	// Returns 2D array as a string with new lines included
	public static String outputNotesString(){
		String temp = "";
		for(int x = 0; x<NotesCopy.length-1;x++){
			for(int y = 0; y<NotesCopy[0].length;y++){
					temp+=NotesCopy[x][y];
					if(NotesCopy[x][y]==';'){
						continue;
					}
					if(NotesCopy[x][y]==',')
						y=4;
			}
			temp += ("\n");
		}
		temp+=';';
		return temp;
	}
	
	// Main randomize function
	public static void randomizeNotes(){
		int handFind = 0;
		String Hand = "";
		char noteChar = '1';
		int Position = 0;
		int started = 0;
		boolean holdInProgress = false;
		int holdPosition = 0;
		for(int y = 0; y<Notes.length;y++){
			if(containsOne(y))
				noteChar = '1';
			if(containsTwo(y)!=-1)
				noteChar = '2';
			if(containsOne(y)||containsTwo(y)!=-1){
				if(started==0){
					started=1;
					// finds a hand to start with for a song
					handFind = (int) Math.floor(Math.random() * 2);
					Hand = handReturn(handFind);
					char[] possible =new char[2];
					if(Hand=="right"){
						possible = new char[]{'3','4'};
					}
					if(Hand=="left"){
						possible = new char[]{'1','2'};
					}
					int temp = ((int) Math.floor(Math.random() * 2));
					Position = (int)possible[temp] - 48;
					NotesCopy[y][Position-1] = noteChar;
					handFind ++;
					if(handFind>1){
						handFind=0;
					}
				}else{
					Position = checkNextAvail(Hand,Position) - 48;
					NotesCopy[y][Position-1] = noteChar;
					Hand = handReturn(handFind);
					handFind++;
					if(handFind>1){
						handFind=0;

					}
				}
			}
			if(containsThree(y)!=-1){
				NotesCopy[y][findTwo(y)] = '3';
			} 
			// mine handling
			if(!containsOne(y)&&containsTwo(y)==-1&&containsThree(y)==-1){
				if(containsMine(y)!=-1){
					int A = (int) Math.floor(Math.random() * 4)+1;
					int B = A;
					int C = B;
					while(B==A){
						B = (int) Math.floor(Math.random() * 4)+1;
					}
					while(C==B||C==A){
						C = (int) Math.floor(Math.random() * 4)+1;
					}
					int D = 10 - A - B - C;
					A--;
					B--;
					C--;
					D--;

					NotesCopy[y][0] = Notes[y][A];
					NotesCopy[y][1] = Notes[y][B];
					NotesCopy[y][2] = Notes[y][C];
					NotesCopy[y][3] = Notes[y][D];
				}
				
				
			}
		}
		
	}
	public static char[][] Notes = {};

	;public static char[][] NotesCopy = {};

	public static int lanes = 4;
	public static FileWriter out;
	
	// main function
	// returns full string of arrays
	public static String indexify (String arg){
		// read in file
		// parse notes into a 2D char array, Notes
		// run placeHolders on Notes
		// run singularizeNotes on Notes
		// run randomizeNotes
		try{
			String[] tempy = arg.split("\n");
			int length = tempy.length;
			int line = 0;
			int cnt = 0;
			int cont = 0;
			Notes = new char[length][4];
			NotesCopy = new char[length][4];
			for (int i=0;i<tempy.length;i++){
				String temp = tempy[i];
				if(temp.length()<4||temp.charAt(0)==','){
					cnt++;
					Notes[line][0] = temp.charAt(0);
					NotesCopy[line][0] = temp.charAt(0);
				}else{
					cont++;
					if(Notes[line][0] == ','){
						Notes[line][0] = temp.charAt(0);
						NotesCopy[line][0] = temp.charAt(0);
					}else{
						Notes[line][0] = temp.charAt(0);
						Notes[line][1] = temp.charAt(1);
						Notes[line][2] = temp.charAt(2);
						Notes[line][3] = temp.charAt(3);
						NotesCopy[line][0] = '0';
						NotesCopy[line][1] = '0';
						NotesCopy[line][2] = '0';
						NotesCopy[line][3] = '0';
					}
				}
				
				line++;
			}
			try{
				singularizeNotes();
			
				holdClean();
				holdFinal();
			
				randomizeNotes();
			
				holdFinish();
				
				
				String temp ="";
				temp = outputNotesString();

				return temp;
			}catch(Exception e){System.out.println(e);}
		}catch(Exception e){System.out.println(e);}
		return "";
	}
}
