public class WordFreq implements ITEM{

    private String word;
    private int freq;

    public WordFreq(String word, int freq){
        this.word = word;
        this.freq = freq;
    }

    public WordFreq(String word){
        this(word, 1);
    }



    public String key(){
        return word;
    }

    public void increaseFreq(){
        this.freq += 1;
    }

    public int getFreq(){
        return freq;
    }


    @Override
    public String toString(){
        return "\"" + word + "\"" +  " frequency: " + freq + " times";
    }

}
