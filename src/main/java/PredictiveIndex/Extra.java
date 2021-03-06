package PredictiveIndex;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
//import org.lemurproject.kstem.KrovetzStemmer;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by aalto on 10/1/16.
 */
public class Extra extends WWW {

    /*You have to decide which kind of "terms" you want to use
    *
    * singleterms
    * bigrams
    * single + bigrams = aguterms*/

    public static void uniquePairs() throws IOException {
        Long2IntOpenHashMap accMap = new Long2IntOpenHashMap();
        BufferedReader br = new BufferedReader(new FileReader(TRAINQAGUMENTED));
        String line;
        long [] field;

        while((line = br.readLine())!= null) {
            field = utilsClass.string2LongArray(line.split(":")[1]," ");

            for (int i = 0; i < field.length; i++) {
                    if (accMap.putIfAbsent(field[i], 1) != null)
                        accMap.merge(field[i], 1, Integer::sum);
                }
            }
        serialize(accMap, AGUTERMACCES);
    }

    private static int[] getQueryInts(String[] queryTerms) {
        int[] queryInt = new int[queryTerms.length];
        for (int i = 0; i < queryTerms.length; i++) queryInt[i] = term2IdMap.get(queryTerms[i]);
        return queryInt;
    }

    private static int[] getQueryTopkDocIDs(String[] topk) {
        //declare a new object every time
        int[] topkInt = new int[topk.length];

        // We convert our String [] to int [] using the term-termID map
        for (int i = 0; i < topk.length; i++) {
            topkInt[i] = 1; //docIDmap.get(String);
        }
        return topkInt;
    }

    static void  getBigFilterSet() throws IOException {
        if (term2IdMap == null) getTerm2IdMap();
        LongOpenHashSet fSet = new LongOpenHashSet();
        BufferedReader br = getBuffReader(TRAIN_TESTQ);
        String line;
        String [] field;
        int t1, t2, removed = 0;
        for(line = br.readLine(); line!=null; line = br.readLine()){
            field = line.split(" ");
            try{
                t1 = term2IdMap.get(field[0]);
                t2 = term2IdMap.get(field[1]);
                fSet.add(getPair(t1,t2));
            }catch (NullPointerException e){
                removed++;
            }

        }
        System.out.println("Absent Terms: " + removed + " removed.");
        System.out.println("Filter Set Size: " + fSet.size());
        serialize(fSet, BIG_FILTER_SET);
    }


    static void getDocIDMap() throws IOException {
        BufferedReader br = getBuffReader(DIDNAMEMAP);
        Object2IntOpenHashMap<String> doc2IDmap = new Object2IntOpenHashMap<>();
        String line;
        String [] field;
        System.out.println("Fetching didNameMap...");
        for (line = br.readLine(); line != null ; line = br.readLine()) {
            field = line.split(" ");
            doc2IDmap.put(field[1], Integer.valueOf(field[0]).intValue());
        }
        br.close();
        for (int i = 0; i < 4 ; i++) {
            br = getBuffReader(docInfo[i]);
            BufferedWriter bw = getBuffWriter(docInfo[i]+"new");
            System.out.println("Fetching oldDocInfo and merging the two in finalDocInfo...");
            for (line = br.readLine(); line != null ; line = br.readLine()) {
                field = line.split(" ");
                bw.write(field[0] + " " + /*doc2IDmap.get(field[0]) +*/ " " + field[2] + " " + field[3] + " " + field[4] + "\n");
            }
            bw.close();
            br.close();
            System.out.println("Merging Completed");
        }
    }

    public static void november13(){
        IntOpenHashSet sTerms = (IntOpenHashSet) deserialize(FILTER_SETS);
        System.out.println(sTerms.size());
        System.exit(1);
    }






}
