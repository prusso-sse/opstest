package com.aol.opstest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aol.opstest.Exception.OpsTestException;
import com.aol.opstest.Exception.OpsTestExceptionId;

public class NameOccuranceMappingService {	
	private File file1				= null;		// File containing name data
	private File file2				= null;		// File containing names (one per line) 
	private boolean caseSensitive	= false;	// case sensitive matching
	private boolean distinct		= false;	// distinct matching
	
	private String processInfo = "";
	private Map<String, List<Integer>> nameLineNumberMapping;

	public NameOccuranceMappingService(File file1, File file2, boolean caseSensitive, boolean distinct) {
		this.file1 = file1;
		this.file2 = file2;
		this.caseSensitive = caseSensitive;
		this.distinct = distinct;
		
		/*
		 * Create our mapping structure so we only have to parse File1 once
		 */
		this.nameLineNumberMapping = new HashMap<String, List<Integer>>();
		
		this.processInfo = "\nFile1:\t" + this.file1.getAbsolutePath() +
						   "\nFile2:\t" + this.file2.getAbsolutePath();
	}

	/**
	 * Process the files and output the results to stdout
	 */
	public void processFiles() throws OpsTestException {
		isInitialized();
		
		outputInfo("Processing files." + this.processInfo + "\n\n");
		
		populateNames();
		processDataFile();
		printResults();
	}
	
	public void populateNames() throws OpsTestException {
		isInitialized();
		
		nameLineNumberMapping.clear();
		
		try(BufferedReader br = new BufferedReader(new FileReader(file2))) {
		    for(String line; (line = br.readLine()) != null; ) {
		        String name = line.trim();
		        
		        if(name.isEmpty()) {
		        	continue;
		        }
		        
		        if(!caseSensitive) {
		        	name = name.toLowerCase();
		        }
		        
		        if(nameLineNumberMapping.containsKey(name)) {
		        	outputWarning("populateNames()", "The name [" + name +
		        			"] occurs multiple times in the file [" + file2.getName() + "].  Compiling " +
		        			"all entries into a single result.");
		        }
		        
		        nameLineNumberMapping.put(name, new ArrayList<Integer>());
		    }
		} catch (FileNotFoundException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"populateNames()", e.getMessage());
		} catch (IOException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"populateNames()", e.getMessage());
		}
	}
	
	public void processDataFile() throws OpsTestException {
		isInitialized();
		
		int lineNumber = 1;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file1))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(!caseSensitive) {
		    		line = line.toLowerCase();
		        }
		    	
		    	if(distinct) {
		    		lineTestDistinct(line, lineNumber++);
		    	} else {
		    		lineTest(line, lineNumber++);
		    	}
		    }
		} catch (FileNotFoundException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"processDataFile()", e.getMessage());
		} catch (IOException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"processDataFile()", e.getMessage());
		}
	}
	
	public void lineTest(String line, int lineNumber) {
		/*
		 * Since its possible for a name to occur within a "word" in the line (i.e.
		 * the name we're looking for is conjoined with other characters) we have
		 * no choice but to iterate the keyset of the hash against the line
		 */
		for (Map.Entry<String, List<Integer>> nameEntries : nameLineNumberMapping.entrySet()) {
			String name = nameEntries.getKey();
			
			if(line.contains(name)) {
				nameEntries.getValue().add(lineNumber);
			}
		}
		
		return;
	}
	
	public void lineTestDistinct(String line, int lineNumber) {
		/*
		 * This method looks for exact matches of each value in the line.  Meaning,
		 * we only match the full words exactly.  Any other variation will not match.
		 * 		Example:
		 * 			Looking for the word "Bob"
		 * 				- "this line has Bob in it" MATCHES
		 * 				- "this line hasBobin it" NO MATCH
		 */
		String[] lineWords = line.split("\\s+");
		
		for(String word : lineWords) {
			List<Integer> lineNumbers = nameLineNumberMapping.get(word);
			
			if(lineNumbers != null) {
				lineNumbers.add(lineNumber);
			}
		}
		
		return;
	}
	
	public void printResults() {
		for (Map.Entry<String, List<Integer>> nameEntries : nameLineNumberMapping.entrySet()) {
			String name = nameEntries.getKey();
			List<Integer> lines = nameEntries.getValue();
			
			StringBuilder results = new StringBuilder(name + ": ");
			Iterator<Integer> itr = lines.iterator();
			while(itr.hasNext()) {
				results.append(itr.next());
				if(itr.hasNext()) {
					results.append(", ");
				}
			}
			results.append("\n");
			outputInfo(results.toString());
		}
	}
	
	public File getFile1() {
		return file1;
	}

	public void setFile1(File file1) {
		this.file1 = file1;
	}

	public File getFile2() {
		return file2;
	}

	public void setFile2(File file2) {
		this.file2 = file2;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public Map<String, List<Integer>> getNameLineNumberMapping() {
		return nameLineNumberMapping;
	}
	
	private void isInitialized() throws OpsTestException {
		if((this.file1 == null) || (!this.file1.exists())) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"isInitialized()", "File1 has not been initialized.");
		}
		
		if((this.file2 == null) || (!this.file2.exists())) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"isInitialized()", "File2 has not been initialized.");
		}
		
		return;
	}

	private void outputInfo(String info) {
		System.out.print(info);
	}
	
	private void outputWarning(String method, String info) {
		System.out.print("NameOccuranceMappingService." + method + " WARNING - " + info);
	}
}
