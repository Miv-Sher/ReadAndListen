package company;
import java.util.Arrays;
import  java.util.zip.CRC32;

public class Shingles {


    public static  final int SHINGLE_SIZE = 5;

    public static long[] getShingleHashSums(String[] words, int shingleSize)
    {
        long[] sums = new long[words.length];
        CRC32 crc32 = new CRC32();

        for (int i = 0; i < words.length; i++) {

            String shingle = getShingle(words, i, shingleSize);
            crc32.update(shingle.getBytes());
            sums[i] = crc32.getValue();
            crc32.reset();
        }
        return sums;
    }


    static int ceilSearch(double[] arr, double x) {
        int low = 0;
        int high = arr.length - 1;
        return ceilSearch(arr, low, high, x);
    }
    /* Function to get index of ceiling of x in arr[low..high]*/
    static int ceilSearch(double arr[], int low, int high, double x)
    {
        int mid;

  /* If x is smaller than or equal to the first element,
    then return the first element */
        if(x <= arr[low])
            return low;

  /* If x is greater than the last element, then return -1 */
        if(x > arr[high])
            return -1;

  /* get the index of middle element of arr[low..high]*/
        mid = (low + high)/2;  /* low + (high - low)/2 */

  /* If x is same as middle element, then return mid */
        if(arr[mid] == x)
            return mid;

  /* If x is greater than arr[mid], then either arr[mid + 1]
    is ceiling of x or ceiling lies in arr[mid+1...high] */
        else if(arr[mid] < x)
        {
            if(mid + 1 <= high && x <= arr[mid+1])
                return mid + 1;
            else
                return ceilSearch(arr, mid+1, high, x);
        }

  /* If x is smaller than arr[mid], then either arr[mid]
     is ceiling of x or ceiling lies in arr[mid-1...high] */
        else
        {
            if(mid - 1 >= low && x > arr[mid-1])
                return mid;
            else
                return ceilSearch(arr, low, mid - 1, x);
        }
    }

    public static String getShingle(String[] words, int index, int shingleSize)
    {
        StringBuilder shingle = new StringBuilder();
        for (int i = index; i < index + shingleSize; i++) {
            if (i == words.length) {
                break;
            }
            shingle.append(words[i] + " ");
        }
        return  shingle.toString().trim();
    }

    public static double countMatch(String text1, String text2) {
        String[] clearedText1 = StringCleaner.clearText(text1);
        String[] clearedText2 = StringCleaner.clearText(text2);

        int shingleSize = Math.min(SHINGLE_SIZE, Math.min(clearedText1.length, clearedText2.length));

        long[] text1Sums =getShingleHashSums(clearedText1, shingleSize);
        long[] text2Sums = getShingleHashSums(clearedText2,shingleSize);

        return countMatch(text1Sums, text2Sums);
    }

    public static  double countMatch(long[] text1, long[] text2) {
        int count = 0;
        for (int i = 0; i < text1.length; i++) {
            if (search(text2, text1[i]) >=0) {
                count++;
            }
        }
        return  count * 2.0 / (text1.length + text2.length);
    }

    public static int search(long[] text, long shingle) {
        for (int i = 0; i < text.length; i++) {
            if (text[i] == shingle) {
                return i;
            }
        }
        return  -1;
    }

    public static int search (long[] text, long[] sentence) {
        int startIndex = -1;
        int count = 0;
        for (int i = 0; i < sentence.length; i++) {
            int foundIndex =  search(text, sentence[i]);
            if (foundIndex >=0) {
                if (startIndex < 0) {
                    startIndex = foundIndex - i;
                }
                count++;
            }
        }
        return startIndex;
        //if (count >= sentence.length/2) {
        //    return startIndex;
        //}
        //else {
        //    return  -1;
        //}
    }

    public static int search(String[] transcript, String sentence) {
        String[] clearedSentence = StringCleaner.clearText(sentence);
        int shingleSize = Math.min(SHINGLE_SIZE, Math.min(transcript.length, clearedSentence.length));

        long[] transcriptSums = getShingleHashSums(transcript, shingleSize);
        long[] sentenceSums = getShingleHashSums(clearedSentence, shingleSize);
        int found = search(transcriptSums, sentenceSums);
        return found;
    }


    public static int search(long[] transcriptSums, String sentence) {
        String[] clearedSentence = StringCleaner.clearText(sentence);
        long[] sentenceSums = getShingleHashSums(clearedSentence, SHINGLE_SIZE);

        return search(transcriptSums, sentenceSums);
    }

    public static int search (String text, String sentence ) {

        String[] clearedText = StringCleaner.clearText(text);
        String[] clearedSentence = StringCleaner.clearText(sentence);

        int shingleSize = Math.min(SHINGLE_SIZE, Math.min(clearedText.length, clearedSentence.length));

        long[] transcriptSums = getShingleHashSums(clearedText, shingleSize);
        long[] sentenceSums = getShingleHashSums(clearedSentence, shingleSize);

        return search(transcriptSums, sentenceSums);
    }

    public static int search(String text, String[] transcriptChunk) {
        String[] clearedText = StringCleaner.clearText(text);
        int index = findInText(clearedText);
        int shingleSize = Math.min(SHINGLE_SIZE, Math.min(clearedText.length, transcriptChunk.length));

        long[] textSums = getShingleHashSums(clearedText, shingleSize );
        long[] chunkSums = getShingleHashSums( transcriptChunk, shingleSize);

        return search(textSums, chunkSums);
    }

    public static String glue (String[] text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length; i++) {
            sb.append(text[i] + " ");
        }
        return sb.toString();
    }

    public static int findInText(String[] clearedText) {
        for (int i = 0; i < clearedText.length; i++) {
            if (clearedText[i].equals( "paul")) {
                return i;
            }
        }
        return  -1;
    }

    public static int searchFromTime(String text, WordsInfo wordsInfo, double time, int shingleSize) {
        int index = ceilSearch(wordsInfo.endTimes, time);
        int size = shingleSize * 5 - 1;
        size = (size + index) >= wordsInfo.words.length ?( wordsInfo.words.length - index) : size;
        String[] chunk = new String[size];
        for (int i = 0; i < size; i++) {
            chunk[i] = wordsInfo.words[index + i];
        }
        return search(text, chunk);
    }

    public  static double searchTime(WordsInfo transcript, String fragment, int shingleSize) {
        int index = search(transcript.words, fragment);
        if (index < 0) return  index;
        return  transcript.startTimes[index];
    }

    public static void main(String[] args) {
        JSonModifier jSonModifier = new JSonModifier();
        WordsInfo wordsInfo = jSonModifier.parseFromFile("C:\\Users\\Anna\\IdeaProjects\\Shingles\\test.txt");
        String part = "As much as I’d pulled away from him in the years after my mother’s death, I’d also leaned hard into him.";
        int index = search(wordsInfo.words, part);
        int stop = 4;
    }
}
