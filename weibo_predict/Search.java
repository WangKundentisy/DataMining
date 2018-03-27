
package microblog;
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileWriter;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.HashSet;  
  
public class Search {  
    public static void main(String[] args) throws Exception {  
        HashMap<String, ArrayList<String>> map=new HashMap<>();  
        String line="";  
        //读入训练数据  
        BufferedReader br=readFile("C:\\Users\\WK\\Desktop\\新浪微博互动预测\\weibo_train_data.txt");  
        while((line=br.readLine())!=null){  
            String uid=line.split("\t")[0];  
            String fcl=line.split("\t")[3]+","+line.split("\t")[4]+","+line.split("\t")[5];  
            //按用户分组保存  
            if (map.containsKey(uid)) {  
                map.get(uid).add(fcl);  
            }else{  
                ArrayList<String> list=new ArrayList<>();  
                list.add(fcl);  
                map.put(uid, list);  
            }  
        }  
        //拟合每一个用户  
        HashMap<String, String> answer=new HashMap<>();  
        for(String uid:map.keySet()){  
            answer.put(uid, fit(map.get(uid)));  
        }  
        //读入预测数据  
        br=readFile("C:\\Users\\WK\\Desktop\\新浪微博互动预测\\weibo_predict_data.txt");  
        PrintWriter pt=writeFile("C:\\Users\\WK\\Desktop\\新浪微博互动预测\\weibo_result_data.csv");  
        //直接抄答案  
        while((line=br.readLine())!=null){  
            String temp[]=line.split("\t");  
            String uid=temp[0];  
            String mid=temp[1];  
            String predict=answer.get(uid)==null?"0,0,0":answer.get(uid);  
            pt.println(uid+"\t"+mid+"\t"+predict);  
        }  
        pt.close();  
        br.close();  
    }  
  
    public static String fit(ArrayList<String> list) {  
        HashSet<String> set = new HashSet<>();  
        int n = 0;  
        while (n < list.size()) {  
            set.add(list.get(n));  
            n++;  
        }  
        double max_precision = 0;  
        int best_f = 0, best_c = 0, best_l = 0;  
        for (String line_ : set) {  
            double sum_denom = 0;// 分母  
            double sum_number = 0;// 分子  
            int i = Integer.parseInt(line_.split(",")[0]);  
            int j = Integer.parseInt(line_.split(",")[1]);  
            int k = Integer.parseInt(line_.split(",")[2]);  
            for (String line : list) {  
                String trueStr[] = line.split(",");  
                int fr = Integer.parseInt(trueStr[0]);  
                int cr = Integer.parseInt(trueStr[1]);  
                int lr = Integer.parseInt(trueStr[2]);  
                double df = Math.abs(i - fr) / (fr + 5.0);  
                double dc = Math.abs(j - cr) / (cr + 3.0);  
                double dl = Math.abs(k - lr) / (lr + 3.0);  
                double precision_i = 1 - 0.5 * df - 0.25 * dc - 0.25 * dl;  
                double count_i = fr + cr + lr;  
                if (count_i > 100)  
                    count_i = 100;  
                double sgnValue_i = 0.0;  
                if (precision_i > 0.8)  
                    sgnValue_i = 1;  
                sum_denom += count_i + 1;  
                sum_number += (count_i + 1) * sgnValue_i;  
            }  
            if (sum_number / sum_denom > max_precision) {  
                max_precision = sum_number / sum_denom;  
                best_f = i;  
                best_c = j;  
                best_l = k;  
            }  
        }  
        return best_f + "," + best_c + "," + best_l;  
    }  
      
    public static BufferedReader readFile(String src) throws Exception {  
        // return new BufferedReader(new FileReader(new File(src)));  
        return new BufferedReader(new InputStreamReader(new FileInputStream(new File(src)), "UTF-8"));  
    }  
  
    public static PrintWriter writeFile(String dst) throws Exception {  
        return new PrintWriter(new FileWriter(dst));  
    }  
}  
