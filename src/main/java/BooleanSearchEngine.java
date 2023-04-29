import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    //???

    private final Map<String, List<PageEntry>> words = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        File[] pdfs = pdfsDir.listFiles();
        for (File pdf : pdfs) {
            var doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 1; i < doc.getNumberOfPages(); i++) {
                Map<String, Integer> freqs = new HashMap<>(); //Количество слов на 1 странице
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                var wordsFromPage = text.split("\\P{IsAlphabetic}+");
                for (var word : wordsFromPage) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (var word : freqs.keySet()) {
                    words.computeIfAbsent(word, k -> new ArrayList<>());
                    words.get(word).add(new PageEntry(pdf.getName(), i, freqs.get(word)));
                }
            }
            for (String word : words.keySet()) {
                Collections.sort(words.get(word));
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return words.getOrDefault(word, Collections.emptyList());
    }
}