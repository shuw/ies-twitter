package edu.shu.nlt.crunchbase.analysis.ontotech;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.shu.nlt.crunchbase.NamedEntityRecognizer;
import edu.shu.nlt.crunchbase.NamedEntityRecognizer.CrunchbaseMatches;

public class InstanceMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		myInstance = NamedEntityRecognizer.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	NamedEntityRecognizer myInstance;

	@Test
	public void findHash() {
		CrunchbaseMatches result = myInstance.match("#facebook");

		Assert.assertTrue(result.getTotalMatches() == 1);
		Assert.assertTrue(result.getCompanyMatches().get(0).getCrunchBaseId().equals("facebook"));

	}

	@Test
	public void findMixed() {
		CrunchbaseMatches result = myInstance.match("bill gates worked at microsoft");

		Assert.assertTrue(result.getCompanyMatches().get(0).getCrunchBaseId().equals("microsoft"));
		Assert.assertTrue(result.getPersonMatches().get(0).getCrunchBaseId().equals("bill-gates"));

	}

}
