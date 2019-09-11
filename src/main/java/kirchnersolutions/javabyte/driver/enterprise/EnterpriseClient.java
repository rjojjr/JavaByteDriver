package kirchnersolutions.javabyte.driver.enterprise;

import kirchnersolutions.javabyte.driver.common.driver.Connector;
import kirchnersolutions.javabyte.driver.common.driver.DBEntity;
import kirchnersolutions.javabyte.driver.common.driver.DatabaseResults;
import kirchnersolutions.javabyte.driver.common.driver.Transaction;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnterpriseClient {

    private volatile Connector con = null;
    private String ip = "", hostname = "", username= "", password = "";
    private int port = 0;
    private EnterpriseClientConnector connector = null;
    private volatile ConnectorThread connection = null;
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * Initiate single client connector.
     * @param ip
     * @param hostname
     * @param port
     * @param username
     * @param password
     */
    EnterpriseClient(String ip, String hostname, int port, String username, String password, ThreadPoolExecutor threadPoolExecutor){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * Logout
     * @throws Exception
     */
    public void logout() throws Exception{
        connection.stopThread();
        connector.logout();
    }

    /**
     * Start connection and logon.
     * @return
     * @throws Exception
     */
    public boolean logon() throws Exception{
        return connect();
    }

    private boolean connect() throws Exception{
        connector();
        if(connection == null && connector.connect()){
            connection = new ConnectorThread();
            threadPoolExecutor.submit(connection);
            return true;
        }else if(connection != null && connector.isConnected() && connection.running.get()){
            return true;
        }else if(connection != null && connector.isConnected() && !connection.running.get()){
            connection = new ConnectorThread();
            threadPoolExecutor.submit(connection);
            return true;
        }else if(connection != null && !connector.isConnected()){

            connection.stopThread();
            connection = new ConnectorThread();
            if(connector.connect()){
                threadPoolExecutor.submit(connection);
                return true;
            }else {
                return false;
            }
        }

        return false;
    }

    private boolean connector() throws Exception{
        if(connector == null){
            this.connector = new EnterpriseClientConnector(this.ip, this.hostname, this.port, this.username, this.password);
            return true;
        }
        return true;
    }

    private boolean isConnected(){
        return connector.isConnected();
    }

    /**
     * Send a transaction request to database.
     * @param transaction
     * @return
     * @throws Exception
     */
    public DatabaseResults sendCommand(Transaction transaction) throws Exception{
        if(connect()){
            try{
                connection.setTransaction(transaction);
                if(connection.getResults() == null){
                    if(logon()){
                        connection.setTransaction(transaction);
                    }else{
                        DatabaseResults res = new DatabaseResults();
                        res.setSuccess(false);
                        res.setMessage("Unable to reconnect");
                        return res;
                    }
                }
                return connection.getResults();
            }catch (Exception e){
                connection.stopThread();
                connection = null;
                connector = null;
                sendCommand(transaction);
            }
        }
        DatabaseResults res = new DatabaseResults();
        res.setSuccess(false);
        res.setMessage("Unable to connect");
        return res;
    }

    /**
     * Initialize DBEntity from given SELECT transaction.
     * Returns null if argument is not a SELECT request.
     * @param transaction
     * @return
     * @throws Exception
     */
    public DBEntity getEntity(Transaction transaction) throws Exception{
        if(!transaction.getOperation().split(" ")[0].equals("SELECT")){
            return null;
        }
        DBEntity entity = new DBEntity(con, transaction);
        entity.initialize();
        return entity;
    }

    String getUsername(){
        return connector.getUsername();
    }

    private DatabaseResults sendMessage(Transaction transaction) throws Exception{
        return connector.sendTransaction(transaction);
    }

    private class ConnectorThread implements Runnable{

        volatile AtomicBoolean running = new AtomicBoolean(false);
        volatile AtomicBoolean run = new AtomicBoolean(true);
        volatile AtomicBoolean processing = new AtomicBoolean(false);
        volatile AtomicBoolean command = new AtomicBoolean(false);
        volatile Transaction transaction = null;
        volatile DatabaseResults results = null;

        public void run(){
            Thread.currentThread().setName(username + " Connection Thread");
            running.set(true);
            while (run.get()){
                if(command.get() && transaction != null){
                    try{
                        processing.set(true);
                        results = sendMessage(transaction);
                        if(results == null){
                            connection = null;
                            connector = null;
                            results = new DatabaseResults();
                            results.setSuccess(false);
                            results.setMessage("Error");
                            run.set(false);
                            command.set(false);
                            processing.set(false);
                            break;
                        }
                        transaction = null;
                        command.set(false);
                        processing.set(false);
                    }catch (Exception e){
                        e.printStackTrace();
                        results = new DatabaseResults();
                        results.setSuccess(false);
                        results.setMessage(e.getMessage());
                        transaction = null;
                        command.set(false);
                        processing.set(false);
                    }
                }else if(command.get() && transaction == null){
                    processing.set(true);
                    results = new DatabaseResults();
                    results.setSuccess(false);
                    results.setMessage("No transaction set");
                    transaction = null;
                    command.set(false);
                    processing.set(false);
                }else {

                }
            }
            command.set(false);
            processing.set(false);
        }

        void  stopThread(){
            run.set(false);
        }

        void setTransaction(Transaction transaction){
            while(processing.get()){

            }
            while (command.get()){

            }
            this.transaction = transaction;
            command.set(true);
        }

        DatabaseResults getResults(){
            while(processing.get()){

            }
            while (command.get()){

            }
            return results;
        }

    }

}
