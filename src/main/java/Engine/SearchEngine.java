package Engine;

import DocComparator.DocComparator;
import WordContainer.WordContainer;
import se.kth.id1020.TinySearchEngineBase;
import se.kth.id1020.util.Attributes;
import se.kth.id1020.util.Document;
import se.kth.id1020.util.Sentence;
import se.kth.id1020.util.Word;

import java.util.*;

public class SearchEngine implements TinySearchEngineBase {

    private HashMap<String, WordContainer> wordIndex;

    private HashMap<Document, Integer> docWordCount;

    public HashMap<Document, Integer> getDocWordCount() {
        return docWordCount;
    }

    public void preInserts() {

        wordIndex = new HashMap<String, WordContainer>();

        docWordCount = new HashMap<>();
    }

    private void insert(Word word, Attributes attr) {

        String wordName = word.word;

        Document doc = attr.document;

        boolean docExists = docWordCount.containsKey(doc);

        if (docExists) docWordCount.put(doc, docWordCount.get(doc) + 1);

        else docWordCount.put(doc, 1);

        boolean wordExists = wordIndex.containsKey(wordName);

        if (wordExists) {

            WordContainer wrd = wordIndex.get(wordName);

            wrd.add(doc);
        }
        else wordIndex.put(wordName, new WordContainer(word, attr,this));

    }

    public void insert(Sentence sentence, Attributes attr) {
        for (Word word : sentence.getWords()) insert(word, attr);
    }

    public void postInserts() {

        double numberOfDocuments = docWordCount.size();

        System.out.println("Number of documents: " + numberOfDocuments);

    }

    public List<Document> search(String query) {

        try {

            String[] q = query.split("\\s+");

            int size = q.length;

            int ob = query.lastIndexOf("orderby");

            boolean order;

            String p;

            if (ob > 0 && "orderby".equals(q[size - 3])) {
                p = parse(query.substring(0, ob));
                order = true;
            } else {
                p = parse(query);
                order = false;
            }

            WordContainer result = wordIndex.get(p);

            if (result == null) result = new WordContainer(this);

            ArrayList<Document> results = result.get();

            if (order) {

                String arg = q[size - 2];

                boolean asc = true;

                if ("desc".equals(q[size - 1])) asc = false;


                Comparator<Document> cmp = new DocComparator(arg, result, asc);

                Collections.sort(results, cmp);
            }

            return results;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public String infix(String arg) {
        try {
            String[] q = arg.split("\\s+");
            int size = q.length;
            int orderby = arg.lastIndexOf("orderby");
            boolean order;
            String p;

            if (orderby > 0 && "orderby".equals(q[size - 3])) {
                p = parse(arg.substring(0, orderby));
                order = true;
            } else {
                p = parse(arg);
                order = false;
            }
            if (order) p = p + " " + arg.substring(orderby).toUpperCase();

            return p;
        } catch (NoSuchElementException e) {
            return "INVALID ARGUMENT(s)";
        }
    }

    private String parse(String query) {

        String[] q = query.split("\\s+");

        WordContainer result;

        Deque<String> deque = new ArrayDeque<>();

        for (int i = q.length - 1; i >= 0; i--) {

            String s = q[i];

            switch (s) {
                case "+": {
                    String first = deque.removeFirst();
                    String second = deque.removeFirst();
                    String arg = "(" + first + " + " + second + ")";
                    String revArg = "(" + second + " + " + first + ")";

                    if (wordIndex.containsKey(arg)) deque.addFirst(arg);

                    else if (wordIndex.containsKey(revArg)) deque.addFirst(revArg);

                    else {

                        result = wordIndex.get(first);

                        if (result == null) result = new WordContainer(this);

                        result = result.intersection(wordIndex.get(second));

                        wordIndex.put(arg, result);

                        deque.addFirst(arg);
                    }
                    break;
                }
                case "-": {

                    String first = deque.removeFirst();

                    String second = deque.removeFirst();

                    String arg = "(" + first + " - " + second + ")";

                    if (wordIndex.containsKey(arg)) deque.addFirst(arg);

                    else {

                        result = wordIndex.get(first);

                        if (result == null) result = new WordContainer(this);

                        result = result.difference(wordIndex.get(second));

                        wordIndex.put(arg, result);

                        deque.addFirst(arg);
                    }
                    break;
                }
                case "|": {

                    String first = deque.removeFirst();

                    String second = deque.removeFirst();

                    String arg = "(" + first + " | " + second + ")";

                    String revArg = "(" + second + " | " + first + ")";

                    if (wordIndex.containsKey(arg)) deque.addFirst(arg);

                    else if (wordIndex.containsKey(revArg)) deque.addFirst(revArg);

                    else {

                        result = wordIndex.get(first);

                        if (result == null) result = new WordContainer(this);

                        result = result.union(wordIndex.get(second),this);

                        wordIndex.put(arg, result);

                        deque.addFirst(arg);
                    }
                    break;
                }
                default:
                    deque.addFirst(s);
                    break;
            }
        }
        return deque.removeFirst();

    }

}
