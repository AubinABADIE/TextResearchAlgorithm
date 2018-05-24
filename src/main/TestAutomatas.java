package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestAutomatas {
	
	public static ArrayList<String> suffixesListFor(String M){
		ArrayList<String> suffixes = new ArrayList<String>();
		for(int i=0;i<M.length();i++) {
			suffixes.add(M.substring(i));
		}
		return suffixes;
	}
	
	public static int[][] generateDeltaForM(String word, String alphabet) {
		int[][] automata = new int[word.length()+1][alphabet.length()+1];
		for(int i=0;i<8;i++) {
			for(int j=0;j<alphabet.length();j++) {
				String Mil = word.substring(0,i) + alphabet.charAt(j);
				ArrayList<String> s = suffixesListFor(Mil);
				for(int k=word.length();k>=0;k--) {
					if(s.contains(word.substring(0, k))) {
						automata[i][j] = word.substring(0, k).length();
						break;
					}
				}
			}
		}
		return automata;
	}
	
	public static int[] rechMotifs(String text, String word, int[][] delta){
		int[] occ = new int[text.length() / word.length() + 2]; // the maximum size is the maximum no. of occurences of the word in the text
		int etat = 0;
		int noOcc = 0;
		for(int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			switch(c) {
				case 'A':
					etat = delta[etat][0];
					break;
				case 'C':
					etat = delta[etat][1];
					break;
				case 'G':
					etat = delta[etat][2];
					break;
				case 'T':
					etat = delta[etat][3];
					break;
			}
			if(etat == 7) {
				occ[noOcc+1] = i - word.length() + 1;
				noOcc++;
			}
		}
		occ[0] = noOcc;
		return occ;
	}
	
	public static void main(String args[]) throws IOException {
		String word;
		String alphabet = "ACGT";
		
//		System.out.println("   A  C  G  T");
//		for(int i=0;i<8;i++){
//			System.out.print(i);
//			for(int j=0;j<alphabet.length();j++){
//				System.out.print("  "+automata[i][j]);
//			}
//			System.out.println();
//		}
//		System.out.println();
		
//		String text = "agagacaacagagacagagcagacgagacgagagacacacac";
		// initialization of the I/O
		File tagsFile = new File("tags.txt");
		File inputFile = new File("chr22.fa");
		File outputFile = new File("output.txt");
		FileReader frTags = new FileReader(tagsFile);
		FileReader frInput = new FileReader(inputFile);
		FileWriter fwOutput = new FileWriter(outputFile);
		
		String text = "";
		ArrayList<String> tags = new ArrayList<String>();
		// reading and saving the tags
		try (BufferedReader br = new BufferedReader(frTags)) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	tags.add(line);
		    }
		} catch(Exception e) {
			System.out.println("Exception caught while reading from 'tags.txt'!");
		}
		
		// reading and creating the text
		int lines = 0;
		try (BufferedReader br = new BufferedReader(frInput)) {
			String line;
			while ((line = br.readLine()) != null) {
				// process the line
				System.out.println("Processing line no. "+lines);
				lines++;
				text += line;
			}
		}
		
		
		// iterate the tags and search for each one
		try {
			BufferedWriter bw = new BufferedWriter(fwOutput);
			for(int i=0;i<tags.size();i++) {
				int[][] automata = generateDeltaForM(tags.get(i).toUpperCase(), alphabet);
				int[] noOcc = rechMotifs(text.toUpperCase(), tags.get(i).toUpperCase(), automata);
				if(noOcc.length > 0) {
					bw.write(tags.get(i)+" : ");
					bw.newLine();
					for(int j=1;j<=noOcc[0];j++) {
						bw.write(" - "+noOcc[j]);
						bw.newLine();
					}
				} else { 
					System.out.println(" - No occurences of the word have been found in the text!");
				}
			}
			// release all bytes to the underlying stream
			bw.flush();
			
			try {

				if (bw != null)
					bw.close();

				if (fwOutput != null)
					fwOutput.close();

			} catch (IOException ex) {

//				ex.printStackTrace();
				System.out.println("An exception was caught when trying to close the FileWriters!");

			}
		} catch(Exception e) {
			System.out.println("An exception was caught trying to write into the file!");
		}
		
	}
}
