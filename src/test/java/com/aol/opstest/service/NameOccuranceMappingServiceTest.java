package com.aol.opstest.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aol.opstest.Exception.OpsTestException;

public class NameOccuranceMappingServiceTest {
	private NameOccuranceMappingService nfm;
	
	private static final String[] FILE2_NAMES = new String[] {"Alice", "John", "Jon",
		"Mary", "Michael", "Sue", "Bryan", "Samantha", "Lee", "Holly"};
	
	@Before
	public void init() throws Exception {
		URL file1Url = this.getClass().getResource("/randomData-file2_10.txt");
		File file1 = new File(file1Url.getFile());
		
		URL file2Url = this.getClass().getResource("/file2_10.txt");
		File file2 = new File(file2Url.getFile());
		
		nfm = new NameOccuranceMappingService(file1, file2, false, false);
	}
	
	@After
	public void destroy() throws Exception {}
	
	@Test
	public void testPopulateNames() {
		try {
			nfm.populateNames();
		} catch (OpsTestException e) {
			fail();
		}
		
		/*
		 * Spot check case insensitivity
		 */
		Map<String, List<Integer>> populatedNames = nfm.getNameLineNumberMapping();
		for(String name : FILE2_NAMES) {
			if(!populatedNames.containsKey(name.toLowerCase())) {
				fail("Populated names does not contain [" + name + "]");
			}
		}
		
		nfm.setCaseSensitive(true);
		try {
			nfm.populateNames();
		} catch (OpsTestException e) {
			fail();
		}
		
		/*
		 * Spot check case sensitivity
		 */
		populatedNames = nfm.getNameLineNumberMapping();
		for(String name : FILE2_NAMES) {
			if(!populatedNames.containsKey(name)) {
				fail("Populated names does not contain [" + name + "]");
			}
		}
	}
}
