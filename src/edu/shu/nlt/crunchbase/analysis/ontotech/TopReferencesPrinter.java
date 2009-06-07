package edu.shu.nlt.crunchbase.analysis.ontotech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.nlt.shallow.data.table.KeyCounterTable;
import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.analysis.ontotech.InstanceMatcher.MatchResult;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;

/**
 * Finds the top references for company/person/product
 * 
 * @author shu
 * 
 */
public class TopReferencesPrinter implements LineProcessor {

	public static void main(String[] args) throws FileNotFoundException {

		TopReferencesPrinter finder = new TopReferencesPrinter();

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();
		finder.printResults();

	}

	private KeyCounterTable<Company> companyCounter = new KeyCounterTable<Company>();

	private int hasAtLeastOneMatch = 0;

	private KeyCounterTable<Person> personCounter = new KeyCounterTable<Person>();

	private KeyCounterTable<Product> productCounter = new KeyCounterTable<Product>();

	private int totalLinesProcessed = 0;

	private void printResults() throws FileNotFoundException {
		System.out.println("Total lines processed: " + totalLinesProcessed);
		System.out.println("Lines with at least one match: " + hasAtLeastOneMatch);

		companyCounter.printEntries(new PrintStream(new FileOutputStream(new File("output", "companies_found.txt"))),
				Integer.MAX_VALUE);

		personCounter.printEntries(new PrintStream(new FileOutputStream(new File("output", "people_found.txt"))),
				Integer.MAX_VALUE);

		productCounter.printEntries(new PrintStream(new FileOutputStream(new File("output", "products_found.txt"))),
				Integer.MAX_VALUE);
	}

	private InstanceMatcher matcher = new InstanceMatcher();

	@Override
	public void processLine(String value) {
		totalLinesProcessed++;

		MatchResult results = matcher.match(value);

		for (Company company : results.getCompanyMatches())
			companyCounter.add(company);

		for (Product product : results.getProductMatches())
			productCounter.add(product);

		for (Person person : results.getPersonMatches())
			personCounter.add(person);

	}
}
