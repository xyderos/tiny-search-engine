package DocComparator;

import WordContainer.WordContainer;
import se.kth.id1020.util.*;

import java.util.Comparator;

public class DocComparator implements Comparator<Document> {

    private static final String POPULARITY="popularity";

    private static final String RELEVANCE="relevance";

    private boolean asc;

    private String arg;

    private WordContainer wrd;

    public DocComparator(String arg, WordContainer wrd, boolean asc) {

        this.arg = arg;

        this.wrd = wrd;

        this.asc = asc;
    }

    private int popularity(Document doc1, Document doc2) {

        int diff = doc1.popularity - doc2.popularity;

        diff = asc ? diff : -diff;

        return choice(diff);

    }

    private int relevance(Document doc1, Document doc2) {

        int diff = wrd.getRelevance(doc1).compareTo(wrd.getRelevance(doc2));

        diff = asc ? diff : -diff;

        return choice(diff);
    }

    public int compare(Document doc1, Document doc2) {

        switch (arg) {

            case POPULARITY: return popularity(doc1, doc2);

            case RELEVANCE:  return relevance(doc1, doc2);

            default:         return 0;
        }

    }

    private int choice(int num){
        return Integer.compare(num,0);
    }
}

