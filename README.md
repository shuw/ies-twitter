migrated from Google code. this was for a school project in 2010.

# Twitter Information Extractor
Dependencies: http://code.google.com/p/nltprojects/

## Introduction
The goal of IESTwitter is to extract references to named entities in Twitter status messages (or Tweet) relating to the Technology industry. The IE system uses an ontology of the Technology Industry constructed using Crunchbase and adds instances and axioms for each relevant Tweet it finds. A particular problem with the Twitter corpus used is the brevity of the messages; each is limited to 140 characters. Because these messages are often written on mobile devices for a casual Internet audience, the grammar is often half-formed and very vernacular. This challenges most Named Entity recognizer which was trained on newspaper articles. Since the list of Companies, Products & famous people are finite; IESTwitter is able to use a dictionary approach to named entity search. In addition, once a relevant Tweet is found; a second pass using the Stanford CRF (Conditional Random Field) NER is executed to find additional non-overlapping entities.

### System overview
An overview of the major components is given here.

### Extraction ontology
Location: data/ontology/IESTwitter.owl IESTwitter.owl is a domain ontology of concepts to be populated by the Ontology Builder.

### Twitter crawler system
Java package: edu.shu.nlt.twitter.crawler Libraries used: jtwitter (http://www.winterwell.com/software/jtwitter.php) Twitter (http://www.twitter.com) is a popular web-based public messaging system. Messages on twitter, or tweets, make up the corpus used by the IESTwitter system. TimelineCrawler? began with a root user (shanselman was picked) and crawled the profile data and twitter timeline of his social network in a breadth-first fashion. It used DiskCache? to serialize and persist the data in a plain text format on the hard drive. The crawler was also used to iteratively update user timelines using a algorithm that predicted whether a timeline was out-of-date by calculating it’s past update frequency. The crawler was throttled to the Twitter API’s limit of 100 requests per hour. Since each request can retrieve either 1 user profile or 20 recent tweets from 1 user. The crawler was executed for several weeks collecting around 878K tweets from 32K users. StatusPrinter? was used to process the crawled data and print out a large text file containing 1 tweet/line. This pre-processed file was used for the IE task since user profiles, relationships & other information were not used during the IE task.

### Crunchbase crawler
Java package: edu.shu.nlt.crunchbase Libraries used: http://www.json.org/java/ Crunchbase (http://www.crunchbase.com/) is a folksonomy database of companies, products and people in the technology industry. CrunchbaseScraper? was used to download information on 19K Companies 32K people & 10K products. Approximately 6K companies had information indicating employees >= 2; information about these filtered companies, their products & employees were added to NamedEntityRecognizer?. Of relevance to this project, Crunchbase contains the following data for each company:
- List of top employees & their positions
- List of products
- List of Competitors

### Crunchbase Named Entity Recognizer
Java package: edu.shu.nlt.crunchbase The NamedEntityRecognizer? breaks an input sentence into unigrams, bigrams & trigrams. It returns matches these N-grams against dictionaries of Crunchbase products, companies and employees.

A StopWords? list was used to filter out common English stop words & around 10 Crunchbase entity names which were common English words like “like”. FindNamedEntities? was a utility written to find the top referenced Company/Product/Person in terms of tweet mentions.

### Ontology builder
Java package: edu.shu.nlt.ontology.ontotech Libraries used: Stanford NER (http://nlp.stanford.edu/ner/index.shtml) PopulateTweets? populates the Extraction Ontology with individuals using the following steps:

- For each Tweet
  - If Technology Individuals found using NamedEntityRecognizer?
    - Find other named entities (i.e. location) using Stanford NER - For each NamedEntity?
      - Assert axioms using data from Crunchbase.
    - For each ExtractionRule?, pass it the sentence + named entities
      - Each asserts 1 or more axioms
  - For each found company, also assert axioms about its employees & products.

### Axiom extraction rules
Java package: edu.shu.nlt.ontology.ontotech.extractionrules

## Output
For 878K tweets, 80K tweets contained references to a known (Crunchbase) technology named entity. References to 168 companies were found. This populated (output) ontology is very large and is close to unusable in the Protégé Ontology tool. The submitted ontology is a toy version containing 100 tweets & references to 25 companies. The Pellet reasoning tool is able to successfully make inferences over the populated ontology.

## Non-source Files

### Ontology
data/ontology/IESTwitter.owl - Unpopulated “class” ontology output/Tweets.owl - Populated ontology

### Informational
output/companies_found.txt – Top company references found in Tweets output/products_found.txt – Top product references found in Tweets output/people_found.txt – Top people references found in Tweets output/urls_sorted_unique.txt – Top URL references (generated via FindURLs)

### Not included
500MB > of cached Twitter & Crunchbase files.

## Evaluation Metrics
I am still working on this. I don’t know of any comparable results for corpuses like mine & with similar ontologies. I ran out of time to manually evaluate a sufficient number of results. I will speak with the professor about this at our next meeting.

## Future Work
- Use Twitter Profiles, relationships, conversations & timestamps to infer additional axioms
-  Intelligent Twitter crawler that stays around people talking about relevant topics
-  Continue investigation of grammatical parsers o Initial results were dissatisfying
-  Improve open Named Entity Recognition o The Stanford parser was often inaccurate
-  Some of my axiom extraction rules are very primitive; they can be improved with additional research
- Sentiment detection
- Additional domains (beyond Technology Company/Products/People)