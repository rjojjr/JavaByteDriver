package kirchnersolutions.javabyte.driver.enterprise;

import kirchnersolutions.javabyte.driver.common.driver.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

class Engine {

    private static Engine instance = null;

    private int port;
    private String ip;

    private static List<Session> sessions;

    private static ThreadPoolExecutor threadPoolExecutor;

    private Engine(int port, String ip, ThreadPoolExecutor threadPoolExecutor){
        this.port = port;
        this.ip = ip;
        this.sessions = Collections.synchronizedList(new ArrayList<>());
        this.threadPoolExecutor = threadPoolExecutor;
    }

    void init(int port, String ip, ThreadPoolExecutor threadPoolExecutor){
        instance = new Engine(port, ip, threadPoolExecutor);
    }

    public static Engine getInstance(){
        return instance;
    }

}
