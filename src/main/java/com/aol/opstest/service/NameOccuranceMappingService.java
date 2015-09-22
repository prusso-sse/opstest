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
	
	private String processInfo = "";
	private Map<String, List<Integer>> nameLineNumberMapping;

	public NameOccuranceMappingService(File file1, File file2, boolean caseSensitive) {
		this.file1 = file1;
		this.file2 = file2;
		this.caseSensitive = caseSensitive;
		
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
		outputInfo("Processing files." + this.processInfo + "\n\n");
		
		populateNames();
		processDataFile();
		printResults();
	}
	
	private void populateNames() throws OpsTestException {
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
		        	outputWarning("populateNames()", "Warning - The name [" + name +
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
	
	private void processDataFile() throws OpsTestException {
		int lineNumber = 1;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file1))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(!caseSensitive) {
		    		line = line.toLowerCase();
		        }
		    	
		    	lineTest(line, lineNumber++);
		    }
		} catch (FileNotFoundException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"processDataFile()", e.getMessage());
		} catch (IOException e) {
			throw new OpsTestException(OpsTestExceptionId.INITIALIZATION_FAILED, "NameOccuranceMappingService",
					"processDataFile()", e.getMessage());
		}
	}
	
	private void lineTest(String line, int lineNumber) {
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
	
	private void printResults() {
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
	
	private void outputInfo(String info) {
		System.out.print(info);
	}
	
	private void outputWarning(String method, String info) {
		System.out.print("NameOccuranceMappingService." + method + " - " + info);
	}
}
