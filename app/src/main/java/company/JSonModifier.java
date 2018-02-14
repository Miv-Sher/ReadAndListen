package company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Anna on 7/9/2015.
 */
public class JSonModifier {



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

    public String[] modifyJsonStrings(String[] jsons) {
        String[] modified = new String[jsons.length];
        for (int i = 0; i < jsons.length; i++) {
            WordsInfo parsed = parseFromString(jsons[i]);
            modified[i] = createJsonSection(parsed, i).toJSONString();
        }
        return modified;
    }

    public String[] modifyJsonFiles(String[] filePaths) {
        String[] modified = new String[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            WordsInfo parsed = parseFromFile(filePaths[i]);
            modified[i] = createJsonSection(parsed, i).toJSONString();
        }
        return modified;
    }

    public void writeSections(String[] sectionJsons, String folderPath) {
        for (int i = 0; i < sectionJsons.length; i++) {
            BufferedWriter writer = null;
            try {
                //create a temporary file
                writer = new BufferedWriter(new FileWriter(folderPath + "\\section" + i));
                writer.write(sectionJsons[i]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public JSONArray createJsonSection(WordsInfo wordsInfo, int sectionNumber) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < wordsInfo.words.length; i++) {
            JSONArray entry = new JSONArray();
            entry.add(wordsInfo.words[i]);
            entry.add(wordsInfo.startTimes[i]);
            entry.add(wordsInfo.endTimes[i]);
            jsonArray.add(entry);
        }
        return jsonArray;
    }

    public WordsInfo parseFromFile(String FilePath) {
        String originalJson = readFile(FilePath);
        return  parseFromString(originalJson);
    }
    public WordsInfo parseFromString(String originalJson) {

        LinkedList<String> wordList = new LinkedList<String>();
        LinkedList<Double> startTimeList = new LinkedList<Double>();
        LinkedList<Double> endTimeList = new LinkedList<Double>();
        JSONParser parser = new JSONParser();
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(originalJson);
            while (builder.indexOf("{") >= 0) {
                String j = builder.substring(0, builder.indexOf("}", builder.indexOf("result_index")) + 1);
                Object obj = parser.parse(j);
                builder.delete(0, builder.indexOf("}", builder.indexOf("result_index")) + 1);
                JSONObject jsonObj = (JSONObject) obj;
                JSONArray results = (JSONArray) jsonObj.get("results");
                Boolean isFinal = (Boolean) ((JSONObject) results.get(0)).get("final");
                if (!isFinal) {
                    continue;
                }
                JSONArray alternatives = (JSONArray) ((JSONObject) results.get(0)).get("alternatives");
                JSONArray timeStamps = (JSONArray) ((JSONObject) alternatives.get(0)).get("timestamps");
                for (int i = 0; i < timeStamps.size(); i++) {
                    JSONArray timeStamp = (JSONArray) timeStamps.get(i);
                    String w = (String) timeStamp.get(0);
                    wordList.add(StringCleaner.clearText(w)[0]);
                    if(timeStamp.get(1).getClass() == Double.class)
                        startTimeList.add((double) timeStamp.get(1));
                    else
                        startTimeList.add(((Long) timeStamp.get(1)).doubleValue());
                    if(timeStamp.get(2).getClass() == Double.class)
                        endTimeList.add((double) timeStamp.get(2));
                    else
                        endTimeList.add(((Long) timeStamp.get(2)).doubleValue());
                }
                String stop = "";
            }
            WordsInfo wordsInfo = new WordsInfo();
            wordsInfo.words = toArrayString(wordList);
            wordsInfo.startTimes = toArrayDouble(startTimeList);
            wordsInfo.endTimes = toArrayDouble(endTimeList);
            return  wordsInfo;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public String[] toArrayString(LinkedList<String> linkedList) {
        String[] array = new String[linkedList.size()];
        for (int i = 0; i < linkedList.size(); i++) {
            array[i] = linkedList.get(i);
        }
        return array;
    }
    public  double[] toArrayDouble(LinkedList<Double> linkedList) {
        double[] array = new double[linkedList.size()];
        for (int i = 0; i < linkedList.size(); i++) {
            array[i] = linkedList.get(i);
        }
        return array;
    }


    public ShinglesInfo createShingles(WordsInfo wordsInfo) {
        ShinglesInfo shinglesInfo = new ShinglesInfo();
        shinglesInfo.startTimes = wordsInfo.startTimes;
        shinglesInfo.endTimes = wordsInfo.endTimes;
        shinglesInfo.shingles = Shingles.getShingleHashSums(wordsInfo.words, Shingles.SHINGLE_SIZE);
        return  shinglesInfo;
    }


    public static void main(String[] args) {
        JSonModifier jsm = new JSonModifier();
        String[] strings = jsm.modifyJsonFiles(new String[]{"C:\\Users\\Anna\\IdeaProjects\\Shingles\\test1.txt",
                "C:\\Users\\Anna\\IdeaProjects\\Shingles\\test2.txt"});
        jsm.writeSections(strings, "C:\\Users\\Anna\\IdeaProjects\\Shingles");
        int stop = 5;
    }


}
