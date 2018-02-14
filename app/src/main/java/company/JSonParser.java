package company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Anna on 7/9/2015.
 */
public class JSonParser {

    public WordsInfo parse (String json) {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) jsonParser.parse(json);
        }
        catch (ParseException pe) {
            return null;
        }
        WordsInfo wordsInfo = new WordsInfo();
        wordsInfo.words = new String[jsonArray.size()];
        wordsInfo.startTimes = new double[jsonArray.size()];
        wordsInfo.endTimes = new double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            wordsInfo.words[i] = (String)((JSONArray)jsonArray.get(i)).get(0);
            wordsInfo.startTimes[i] = (Double)((JSONArray)jsonArray.get(i)).get(1);
            wordsInfo.endTimes[i] = (Double)((JSONArray)jsonArray.get(i)).get(2);
        }
        return wordsInfo;
    }

    private String readFile(String FilePath) {
        BufferedReader br = null;
        String result = null;
        try {
            br = new BufferedReader(new FileReader(FilePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            result = sb.toString();
            br.close();
        } catch (Exception ex) {
            //fuck off mate
        }
        return result;
    }

    public WordsInfo parseFile(String FilePath) {
        String str = readFile(FilePath);
        return  parse(str);
    }

    public static void main(String[] args) {
        JSonParser jSonParser = new JSonParser();
        jSonParser.parseFile("C:\\Users\\Anna\\IdeaProjects\\Shingles\\section0");
        int stop = 5;
    }

}
