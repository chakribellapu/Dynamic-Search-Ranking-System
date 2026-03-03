import java.util.*;

// Document class representing a single search result
class Document {
    String id;
    String content;
    int keywordMatch;
    int freshness;
    int engagement; // wikipedia
    double finalScore;

    public Document(String id, String content, int keywordMatch, int freshness, int engagement) {
        this.id = id;
        this.content = content;
        this.keywordMatch = keywordMatch;
        this.freshness = freshness;
        this.engagement = engagement;
        calculateScore();
    }

    public void calculateScore() {
        // Weighted scoring system
        this.finalScore = 0.6 * keywordMatch + 0.25 * freshness + 0.15 * engagement;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Score: %.2f | %s", id, finalScore, content);
    }
}

// Search engine class that holds query-to-document mapping and ranking logic
class SearchEngine {
    Map<String, List<Document>> queryIndex;

    public SearchEngine() {
        queryIndex = new HashMap<>();
    }

    public void addDocuments(String query, List<Document> docs) {
        queryIndex.put(query.toLowerCase(), docs);
    }

    public void search(String query, int topK) {
        query = query.toLowerCase();
        if (!queryIndex.containsKey(query)) {
            System.out.println("No results found for query: " + query);
            return;
        }

        List<Document> docs = queryIndex.get(query);
        PriorityQueue<Document> maxHeap = new PriorityQueue<>(
                (a, b) -> Double.compare(b.finalScore, a.finalScore)
        );

        for (Document doc : docs) {
            doc.calculateScore(); // ensure scores are up-to-date
            maxHeap.offer(doc);
        }

        System.out.println("\nTop Results for query: " + query);
        for (int i = 0; i < topK && !maxHeap.isEmpty(); i++) {
            Document doc = maxHeap.poll();
            System.out.println((i + 1) + ". " + doc);
        }
    }

    public void simulateClick(String query, String docId) {
        query = query.toLowerCase();
        if (!queryIndex.containsKey(query)) {
            System.out.println("Query not found.");
            return;
        }

        List<Document> docs = queryIndex.get(query);
        for (Document doc : docs) {
            if (doc.id.equalsIgnoreCase(docId)) {
                doc.engagement += 10; // simulate user engagement
                doc.calculateScore(); // recalculate score
                System.out.println("✅ Engagement increased for document: " + doc.id);
                return;
            }
        }

        System.out.println("Document ID not found in this query.");
    }
}

// Main CLI class
public class DynamicRankingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SearchEngine engine = new SearchEngine();

        // Adding some sample documents for "AI"
        engine.addDocuments("AI", Arrays.asList(
                new Document("D1", "AI in Healthcare", 90, 85, 70),
                new Document("D2", "Future of AI", 88, 75, 65),
                new Document("D3", "Basics of AI", 75, 90, 80)
        ));

        // Adding some sample documents for "C Programming"
        engine.addDocuments("C Programming", Arrays.asList(
                new Document("C1", "Pointers in C", 85, 75, 60),
                new Document("C2", "C vs Java", 78, 70, 65),
                new Document("C3", "Intro to C Programming", 95, 80, 70)
        ));

        // CLI loop
        while (true) {
            System.out.println("\n===== Dynamic Search Ranking CLI =====");
            System.out.println("1. Search");
            System.out.println("2. Simulate Click (Engagement)");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int option;
            try {
                option = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            if (option == 1) {
                System.out.print("Enter your query: ");
                String query = sc.nextLine();
                engine.search(query, 3); // Show top 3 results
            } else if (option == 2) {
                System.out.print("Enter query: ");
                String query = sc.nextLine();
                System.out.print("Enter Document ID to simulate click: ");
                String docId = sc.nextLine();
                engine.simulateClick(query, docId);
            } else if (option == 3) {
                System.out.println("Exiting. Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }

        sc.close();
    }
}