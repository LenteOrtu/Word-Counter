import java.util.Comparator;

public class WordFreqComparator implements Comparator<WordFreq> {

    @Override
    public int compare(WordFreq wf1, WordFreq wf2) {

        return wf1.getFreq() - wf2.getFreq();
    }
}
