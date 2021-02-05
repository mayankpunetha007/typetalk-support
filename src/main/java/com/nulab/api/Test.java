package com.nulab.api;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {

    public static final String[] relavent = new String[]{"ask","bid","tradePrice1","oneMinEMA","fiveMinEMA","tenMinEMA","fifteenMinEMA","thirtyMinEMA","oneHourEMA"};
    public static final SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss,SSS");

    public List<Object[]> readData(){
        List<Object[]> toReturn = new ArrayList<>();
        File file = new File("D:\\Develop\\revenue\\UserDashboardStocks\\argo_sir.txt");
        Arrays.sort(relavent);
        Map<String, List<Object>> fullData = new HashMap<>();
        fullData.put("time", new ArrayList<>());
        int count =0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String data = null;
            while ((data = br.readLine())!=null){
                if(data.contains("Taking quote MicroQuote[genericCode=")){
                    String val[] = data.split("Taking quote MicroQuote");
                    System.err.println(val);
                    String propertyKeyString = val[1].substring(1,701);
                    String time = val[0].substring(0,12);
                    Long timeMillis = simple.parse(time).getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeMillis);
                    Calendar newcalendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, newcalendar.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, newcalendar.get(Calendar.MONTH));
                    calendar.set(Calendar.DATE, newcalendar.get(Calendar.DATE));

                    timeMillis = calendar.getTimeInMillis();

                    Map<String, String> breakIntoProperty = createData(propertyKeyString,',');
                    if(breakIntoProperty.get("genericCode").contains("FX_BTC_JPY")) {
                        count++;
                        fullData.get("time").add(timeMillis.longValue());
                        for(String key: breakIntoProperty.keySet()){
                            if(Arrays.binarySearch(relavent, key)>=0){
                                if(!fullData.containsKey(key)){
                                    fullData.put(key, new ArrayList<>());
                                }
                                if(!breakIntoProperty.get(key).equals("<null>")){
                                    fullData.get(key).add(Double.valueOf(breakIntoProperty.get(key)));
                                }else {
                                    fullData.get(key).add(null);
                                }

                            }
                        }
                    }
                }
                //Uncomment to limit and see fast
                /*if(count > 1000){
                    break;
                }*/
                count++;
            }


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        String[] headers = new String[fullData.keySet().size()];
        int i=0;
        headers[i++]="time";
        for(String s: fullData.keySet()){
            if(s.equals("time")){
                continue;
            }
            headers[i] =s;
            i++;
        }
        toReturn.add(headers);
        Object data[][] = new Object[fullData.get("time").size()][fullData.keySet().size()];
        List<String> keys = new ArrayList<>(fullData.keySet());
        for(int j=0;j<fullData.get("time").size();j++) {
            data[j][0]=fullData.get("time").get(j);
            int val = 1;
            for (i = 0; i < keys.size(); i++) {
                if(keys.get(i).equals("time")){
                    val--;
                    continue;
                }
                data[j][i+val] = fullData.get(keys.get(i)).get(j);
            }
        }
        for(i=0;i<data.length;i++){
            toReturn.add(data[i]);
        }
        return toReturn;
    }

    private Map<String, String> createData(String toParse, char separator) {
        Map<String, String> data = new HashMap<>();
        String value = "";
        for(int i=0;i<toParse.length();i++){
            if(toParse.charAt(i) == separator){
                String keyValue[] = value.split("=");
                if(keyValue.length == 2)
                    data.put(keyValue[0], keyValue[1]);
                else
                    data.put(keyValue[0], "");
                value = "";
            }else {
                value += toParse.charAt(i);
            }
        }
        if(value!=null && value.length()>0) {
            String keyValue[] = value.split("=");
            if(keyValue.length > 1) {
                data.put(keyValue[0], keyValue[1]);
            }else {
                data.put(keyValue[0], "");
            }
        }
        return data;
    }

    public static void main(String[] args) {
        new Test().readData();
    }
}

