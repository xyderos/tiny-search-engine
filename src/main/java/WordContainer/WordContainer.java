package WordContainer;

import Engine.SearchEngine;
import se.kth.id1020.util.Attributes;
import se.kth.id1020.util.Document;
import se.kth.id1020.util.Word;

import java.util.ArrayList;
import java.util.HashMap;

public class WordContainer {

    private boolean initiated;

    private HashMap<Document, Double> relevance = new HashMap<>();

    private ArrayList<Document> documents = new ArrayList<>();

    private SearchEngine se;

    public WordContainer(Word word, Attributes attr, SearchEngine se) {

        System.out.println(word);

        initiated = false;

        Document doc = attr.document;

        relevance.put(doc, 1.0);

        documents.add(doc);

        this.se=se;
    }

    private WordContainer(WordContainer wrd, SearchEngine se) {

        this.relevance = new HashMap<>(wrd.relevance);

        this.documents = wrd.get();

        initiated = true;

        this.se=se;
    }

    public WordContainer(SearchEngine se) {

        initiated = true;

        this.se=se;
    }

    public void add(Document doc) {

        boolean documentExists = relevance.containsKey(doc);

        if (documentExists) relevance.put(doc, relevance.get(doc) + 1);

        else {

            relevance.put(doc, 1.0);

            documents.add(doc);
        }
    }

    public ArrayList<Document> get() {

        if (documents == null) return new ArrayList<>();

        else return new ArrayList<>(documents);
    }

    private void addDocAndRelevance(Document doc, Double d) {

        if (!initiated) setRelevance();

        relevance.put(doc, d);

        documents.add(doc);
    }

    private void sumRelevance(Document doc, Double d) {

        if (!initiated) setRelevance();

        relevance.put(doc, relevance.get(doc) + d);
    }

    private void setRelevance() {

        for (Document doc : documents) {

            double docTerm=se.getDocWordCount().size();

            double termFreq = relevance.get(doc) / docTerm;

            double totalSize =se.getDocWordCount().size();

            double docsSize = documents.size();

            double invTermFreq = Math.log10(totalSize / docsSize);

            double res = termFreq * invTermFreq;

            relevance.put(doc, res);
        }
        initiated = true;
    }

    public Double getRelevance(Document doc) {

        if (!initiated) setRelevance();

        return relevance.get(doc);
    }

    private boolean hasDocument(Document doc) {
        return relevance.containsKey(doc);
    }

    public WordContainer intersection(WordContainer wrd) {

        WordContainer result = new WordContainer(se);

        if (wrd == null) wrd = new WordContainer(se);


        for (Document doc : wrd.get()) {

            if (this.hasDocument(doc)) {

                result.addDocAndRelevance(doc, wrd.getRelevance(doc));
                result.sumRelevance(doc, this.getRelevance(doc));
            }
        }
        return result;
    }

    public WordContainer difference(WordContainer wrd) {

        WordContainer result = new WordContainer(se);

        if (wrd == null) wrd = new WordContainer(se);

        for (Document doc : this.get()) {

            if (!wrd.hasDocument(doc)) result.addDocAndRelevance(doc, this.getRelevance(doc));
        }
        return result;
    }

    public WordContainer union(WordContainer wrd, SearchEngine se) {

        if (!initiated) this.setRelevance();

        WordContainer result = new WordContainer(this,se);

        if (wrd == null) wrd = new WordContainer(se);

        for (Document doc : wrd.get()) {

            if (result.hasDocument(doc)) result.sumRelevance(doc, result.getRelevance(doc));

            else result.addDocAndRelevance(doc, wrd.getRelevance(doc));
        }
        return result;
    }
}

