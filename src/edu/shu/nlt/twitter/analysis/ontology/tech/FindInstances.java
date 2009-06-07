package edu.shu.nlt.twitter.analysis.ontology.tech;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.nlt.shallow.data.tags.Word;
import edu.nlt.shallow.parser.WordTokenizer;
import edu.nlt.util.InputUtil;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.data.crunchbase.Company;
import edu.shu.nlt.data.crunchbase.CompanyList;
import edu.shu.nlt.data.crunchbase.Person;
import edu.shu.nlt.data.crunchbase.PersonList;
import edu.shu.nlt.data.crunchbase.Product;
import edu.shu.nlt.data.crunchbase.ProductList;

public class FindInstances implements LineProcessor {

	private CompanyList companyList;
	private PersonList personList;
	private ProductList productList;

	public FindInstances() {
		companyList = new CompanyList(new File("data/crunchbase/companies.js"));
		personList = new PersonList(new File("data/crunchbase/people.js"));
		productList = new ProductList(new File("data/crunchbase/products.js"));
	}

	public static void main(String[] args) {

		InputUtil.process(new File("output", "tweets_all.txt"), new FindInstances());

	}

	private WordTokenizer tokenizer = new WordTokenizer(false);

	private void match(String tweet, String phrase) {

		Company company = companyList.getCompany(phrase);
		Product product = productList.getProduct(phrase);
		Person person = null;

		// split firstname + lastName
		String[] words = phrase.split(" ");
		if (words.length >= 2)
			person = personList.getPerson(words[words.length - 2], words[words.length - 1]);

		
		
		int matchCount = 0;
		if (company != null) {
			matchCount++;
			System.out.println(company.getName());
		}

		if (product != null) {
			matchCount++;
			System.out.println(product.getName());
		}

		if (person != null) {
			matchCount++;
			System.out.println(person.getFirstName() + " " + person.getLastName());
		}

		if (matchCount == 0) {
			System.out.println();
		} else {
			System.out.println(matchCount + " " + tweet);
		}

	}

	@Override
	public void processLine(String value) {

		List<Word> words = new ArrayList<Word>(tokenizer.getWords(value));

		for (int i = 0; i < words.size(); i++) {

			Word currentWord = words.get(i);

			// match single word
			match(value, currentWord.toString());

			if (i > 0) {
				Word previousWord = words.get(i - 1);
				match(value, previousWord.toString() + " " + currentWord.toString());

				if (i > 1) {
					Word previousPreviousWord = words.get(i - 1);
					match(value, previousPreviousWord.toString() + " " + previousWord.toString() + " "
							+ currentWord.toString());
				}

			}

		}

	}

}
