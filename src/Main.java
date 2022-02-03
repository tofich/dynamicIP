import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        DynamicIP dynamicIP = new DynamicIP();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(()->getIP(dynamicIP), 0, 30, TimeUnit.SECONDS);

    }

    public static void getIP(DynamicIP dynamicIP){
        dynamicIP.configure();
        String myIP = dynamicIP.getDynamicIP();
        dynamicIP.sendDynamicIP(myIP);
    }
}
