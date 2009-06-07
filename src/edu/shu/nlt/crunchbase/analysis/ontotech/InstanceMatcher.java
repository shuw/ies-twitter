package edu.shu.nlt.crunchbase.analysis.ontotech;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import edu.nlt.shallow.data.tags.Word;
import edu.nlt.shallow.parser.WordTokenizer;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Employee;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.crunchbase.data.lists.CompanyList;
import edu.shu.nlt.crunchbase.data.lists.PersonList;
import edu.shu.nlt.crunchbase.data.lists.ProductList;

/**
 * Matches string with company/product/person instance
 * 
 * Not thread safe
 * 
 * @author shu
 * 
 */
public class InstanceMatcher {
	private CompanyList companyList = CompanyList.getInstance();

	private PersonList personList = PersonList.getInstance();
	private ProductList productList = ProductList.getInstance();

	private StopWords stopWords = new StopWords();
	private WordTokenizer tokenizer = new WordTokenizer(false);

	private void matchCompanyProduct(String phrase, LinkedList<Company> companyMatches,
			LinkedList<Product> productMatches) {

		Company company = companyList.getCompany(phrase);
		Product product = productList.getProduct(phrase);

		if (company != null)
			companyMatches.add(company);

		if (product != null)
			productMatches.add(product);

	}

	public MatchResult match(String sentence) {
		List<Word> words = new ArrayList<Word>(tokenizer.getWords(sentence));

		LinkedList<Company> companyMatches = new LinkedList<Company>();
		LinkedList<Product> productMatches = new LinkedList<Product>();
		LinkedList<Person> personMatches = new LinkedList<Person>();

		for (int i = 0; i < words.size(); i++) {

			Word currentWord = words.get(i);

			String matchWith = currentWord.toString();

			// match unigram & ignore stop words
			if (!stopWords.isStopWord(matchWith)) {
				matchCompanyProduct(matchWith, companyMatches, productMatches);
			}

			// match bigram
			if (i > 0) {
				matchWith = words.get(i - 1) + " " + matchWith;
				matchCompanyProduct(matchWith, companyMatches, productMatches);

				// match tri-gram
				if (i > 1) {
					matchWith = words.get(i - 2) + " " + matchWith;
					matchCompanyProduct(matchWith, companyMatches, productMatches);
				}
			}
		}

		// Match names
		//
		// full-names for any person
		//
		// first/last name match for people related to companies, products
		// mentioned in the sentence
		//
		{
			Hashtable<String, Person> firstNameTable = new Hashtable<String, Person>();
			Hashtable<String, Person> lastNameTable = new Hashtable<String, Person>();

			boolean doPartialNameMatch = false;
			for (Company company : companyMatches) {
				if (company.getCompanyInfo() != null) {
					for (Employee employee : company.getCompanyInfo().getEmployees()) {
						doPartialNameMatch = true;

						firstNameTable.put(employee.getPerson().getFirstName().toLowerCase(), employee.getPerson());
						lastNameTable.put(employee.getPerson().getLastName().toLowerCase(), employee.getPerson());
					}
				}
			}

			for (Product product : productMatches) {

				if (product.getProductInfo() != null) {
					Company company = product.getProductInfo().getCompany();

					if (company != null) {
						for (Employee employee : company.getCompanyInfo().getEmployees()) {
							doPartialNameMatch = true;

							firstNameTable.put(employee.getPerson().getFirstName().toLowerCase(), employee.getPerson());
							lastNameTable.put(employee.getPerson().getLastName().toLowerCase(), employee.getPerson());
						}
					}
				}
			}

			for (int i = 0; i < words.size(); i++) {

				String lastName = words.get(i).toString();

				if (i > 0) {
					String firstName = words.get(i - 1).toString();

					Person personMatch = personList.getPerson(firstName, lastName);

					if (personMatch != null) {
						// Found full-name match
						personMatches.add(personMatch);
					} else if (doPartialNameMatch) {

						// Find partial name matches for companies, products
						// referenced

						if (firstNameTable.contains(firstName))
							personMatches.add(firstNameTable.get(firstName));

						if (firstNameTable.contains(lastName))
							personMatches.add(lastNameTable.get(lastName));

					}
				}
			}
		}

		return new MatchResult(companyMatches, personMatches, productMatches);
	}

	public static class MatchResult {

		public Collection<Company> getCompanyMatches() {
			return companyMatches;
		}

		public Collection<Person> getPersonMatches() {
			return personMatches;
		}

		public Collection<Product> getProductMatches() {
			return productMatches;
		}

		public MatchResult(Collection<Company> companyMatches, Collection<Person> personMatches,
				Collection<Product> productMatches) {
			super();
			this.companyMatches = companyMatches;
			this.personMatches = personMatches;
			this.productMatches = productMatches;
		}

		private Collection<Company> companyMatches;
		private Collection<Person> personMatches;
		private Collection<Product> productMatches;
	}

}
