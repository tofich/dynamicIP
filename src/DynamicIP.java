import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class DynamicIP {

    private static String SERVER_IP;
    private static String CLIENT_ID = "";
    private static String CLIENT_NAME = "";

    public void configure(){
        Properties props = new Properties();
        String path = new File("").getAbsolutePath();
        //System.out.println(path);
        String configFilePath = path + "\\config\\configDynamicIP.ini";
        try {
            props.load(new FileInputStream(new File(configFilePath)));
            if (props.getProperty("SERVER_IP") != null){
                SERVER_IP = props.getProperty("SERVER_IP");
            }
            else{
                SERVER_IP = "0";
                System.out.println(getCurrentDate() + "\033[0;31m !!!!ДАННЫЕ НЕ ПЕРЕДАНЫ! В файле конфигурации отсутствует параметр SERVER_IP. \033[0m");
            }
            if (props.getProperty("CLIENT_ID") != null){
                CLIENT_ID = props.getProperty("CLIENT_ID");
            }
            else{
                CLIENT_ID = "";
                System.out.println(getCurrentDate() + " !!!!В файле конфигурации отсутствует параметр CLIENT_ID");
            }
            if (props.getProperty("CLIENT_NAME") != null){
                CLIENT_NAME = props.getProperty("CLIENT_NAME");
            }
            else{
                CLIENT_NAME = "";
                System.out.println(getCurrentDate() + " !!!!В файле конфигурации отсутствует параметр CLIENT_NAME");
            }
            /*System.out.println(SERVER_IP);
            System.out.println(CLIENT_ID);
            System.out.println(CLIENT_NAME);*/
        }
        catch (IOException e){
            //e.printStackTrace();
            System.out.println(getCurrentDate() + " !!!!НЕ НАЙДЕН ФАЙЛ КОНФИГУРАЦИИ в каталоге " + configFilePath);
            System.out.println("В КОНФИГУРАЦИИ ЗАПОЛНИТЕ ПАРАМЕТРЫ: SERVER_IP, CLIENT_ID и CLIENT_NAME");
            System.out.println("Пример содержимого конфигурационного файла configDynamicIP.ini:");
            System.out.println("SERVER_IP = 127.0.0.1");
            System.out.println("CLIENT_ID = 0000XX");
            System.out.println("CLIENT_NAME = Абонентский_пункт_1");
        }
    }


    public String getDynamicIP(){
        String myDynamicIp="";
        try {
            URL urlToGetIP = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlToGetIP.openStream()));
            myDynamicIp = br.readLine();
            //System.out.println("Текущий белый IP:" + myDynamicIp);
            br.close();
            return myDynamicIp;
        }
        catch(IOException e){
            //e.printStackTrace();
            System.out.println(getCurrentDate() + " !!!!Недоступен сервис получения внешнего IP-адреса, проверьте соединение с Интернетом.");
            myDynamicIp="Сервис получения IP был недоступен с АП Интернет";
        }
        return myDynamicIp;
    }

    public void sendDynamicIP(String myDynamicIP){
        try {
            URL url = new URL("http://" + SERVER_IP + "/dynamicIP/test.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            Map<String,String> arguments = new HashMap<>();
            arguments.put("ip", myDynamicIP);
            arguments.put("client_id", CLIENT_ID);
            arguments.put("name", CLIENT_NAME);
            arguments.put("csrf", "dkldkvemc3c093493mf9043f39rtg9g34j9f0rgjrt9gj3490jfrf");
            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
                System.out.println(getCurrentDate() + " На сервер передан следующий IP: " + myDynamicIP);
            }
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String s;
            while((s = reader.readLine()) != null){
                System.out.println("Данные переданы, получен ответ: " + s);
            }
            reader.close();*/
        } catch (IOException e) {
            System.out.println(getCurrentDate() + " !!!!Недоступен принимающий сервер. Проверьте соединение с Интернетом.");
            //e.printStackTrace();
        }
    }

    private String getCurrentDate(){
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        //System.out.println("Текущая дата " + formatForDateNow.format(dateNow));
        return formatForDateNow.format(dateNow);
    }

}
