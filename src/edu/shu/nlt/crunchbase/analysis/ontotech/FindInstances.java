package edu.shu.nlt.crunchbase.analysis.ontotech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.nlt.shallow.data.table.KeyCounterTable;
import edu.nlt.shallow.data.tags.Word;
import edu.nlt.shallow.parser.WordTokenizer;
import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.crunchbase.data.lists.CompanyList;
import edu.shu.nlt.crunchbase.data.lists.PersonList;
import edu.shu.nlt.crunchbase.data.lists.ProductList;

public class FindInstances implements LineProcessor {

	public static void main(String[] args) throws FileNotFoundException {

		FindInstances finder = new FindInstances();

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();
		finder.printResults();

	}

	private KeyCounterTable<Company> companyCounter = new KeyCounterTable<Company>();
	private CompanyList companyList;

	private int hasAtLeastOneMatch = 0;

	private KeyCounterTable<Person> personCounter = new KeyCounterTable<Person>();

	private PersonList personList;
	private KeyCounterTable<Product> productCounter = new KeyCounterTable<Product>();
	private ProductList productList;

	private StopWords stopWords = new StopWords();

	private WordTokenizer tokenizer = new WordTokenizer(false);

	private int totalLinesProcessed = 0;

	public FindInstances() {
		companyList = CompanyList.getInstance();
		personList = PersonList.getInstance();
		productList = ProductList.getInstance();
	}

	private int match(String tweet, String phrase) {

		Company company = companyList.getCompany(phrase);
		Product product = productList.getProduct(phrase);
		Person person = null;

		// split firstname + lastName
		String[] words = phrase.split(" ");
		if (words.length >= 2)
			person = personList.getPerson(words[words.length - 2], words[words.length - 1]);

		int matchCount = 0;
		if (company != null) {
			companyCounter.add(company);

			matchCount++;
			// System.out.println(company.getName());
		}

		if (product != null) {
			productCounter.add(product);

			matchCount++;
			// System.out.println(product.getName());
		}

		if (person != null) {
			personCounter.add(person);
			matchCount++;
			// System.out.println(person.getFirstName() + " " +
			// person.getLastName());
		}
		return matchCount;

		// if (matchCount == 0) {
		// System.out.println();
		// } else {
		// System.out.println(matchCount + " " + tweet);
		// }

	}

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

	@Override
	public void processLine(String value) {
		totalLinesProcessed++;
		List<Word> words = new ArrayList<Word>(tokenizer.getWords(value));

		int matchCount = 0;

		for (int i = 0; i < words.size(); i++) {

			Word currentWord = words.get(i);

			String matchWith = currentWord.toString();

			// match unigram & ignore stop words
			if (!stopWords.isStopWord(matchWith)) {
				matchCount += match(value, matchWith);
			}

			// match bigram
			if (i > 0) {
				matchWith = words.get(i - 1) + " " + matchWith;
				matchCount += match(value, matchWith);

				// match tri-gram
				if (i > 1) {
					matchWith = words.get(i - 2) + " " + matchWith;
					matchCount += match(value, matchWith);
				}
			}

		}

		if (matchCount > 0) {
			hasAtLeastOneMatch++;
		}
		if (matchCount > 3) {
			System.out.println(value);
		}
	}
}
