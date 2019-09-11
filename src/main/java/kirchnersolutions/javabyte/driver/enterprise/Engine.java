package kirchnersolutions.javabyte.driver.enterprise;

import kirchnersolutions.javabyte.driver.common.driver.Session;
import kirchnersolutions.javabyte.driver.singleclient.SingleClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

class Engine {

    private static Engine instance = null;

    private int port;
    private String ip;

    private static List<EnterpriseClient> sessions;

    private static ThreadPoolExecutor threadPoolExecutor;

    private Engine(int port, String ip, ThreadPoolExecutor threadPoolExecutor){
        this.port = port;
        this.ip = ip;
        this.sessions = Collections.synchronizedList(new ArrayList<>());
        this.threadPoolExecutor = threadPoolExecutor;
    }

    private static void init(int port, String ip, ThreadPoolExecutor threadPoolExecutor){
        instance = new Engine(port, ip, threadPoolExecutor);
    }

    /**
     * Get enterprise client engine instance.
     * @param port
     * @param ip
     * @param threadPoolExecutor
     * @return
     */
    public static Engine getInstance(int port, String ip, ThreadPoolExecutor threadPoolExecutor){
        if(instance == null){
            init(port, ip, threadPoolExecutor);
        }
        return instance;
    }

    /**
     * Create new client if it does not exist.
     * @param username
     * @param password
     * @return
     */
    public synchronized EnterpriseClient getNewClient(String username, String password){
        EnterpriseClient session = searchClients(username);
        if(session == null){
            EnterpriseClient newUser = new EnterpriseClient(ip, " ", port, username, password, threadPoolExecutor);
            List<EnterpriseClient> temp = Collections.synchronizedList(new ArrayList<>(sessions));
            temp.add(newUser);
            updateList(temp);
            return newUser;
        }else {
            return session;
        }
    }

    private EnterpriseClient searchClients(String username){
        List<EnterpriseClient> temp = new ArrayList<>(sessions);
        for(EnterpriseClient client : temp){
            if(client.getUsername().equals(username)){
                return client;
            }
        }
        return null;
    }

    /**
     * Logs out session and removes it from the session cache.
     * @param session
     * @throws Exception
     */
    public synchronized void removeSession(EnterpriseClient session) throws Exception{
        List<EnterpriseClient> temp = Collections.synchronizedList(new ArrayList<>(sessions));
        int count = 0;
        for(EnterpriseClient client : temp){
            if(client != null){
                if(client.getUsername().equals(session.getUsername())){
                    temp.set(count, null);
                    session.logout();
                    break;
                }
            }
            count++;
        }
        updateList(temp);
        trimSessions();
    }

    private synchronized void trimSessions(){
        List<EnterpriseClient> temp = Collections.synchronizedList(new ArrayList<>(sessions));
        List<EnterpriseClient> newList = Collections.synchronizedList(new ArrayList<>());
        for(EnterpriseClient client : temp){
            if(client != null){
                newList.add(client);
            }
        }
    }

    private synchronized void updateList(List<EnterpriseClient> newList){
        sessions = Collections.synchronizedList(new ArrayList<>(newList));
    }

}
