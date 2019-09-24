package kirchnersolutions.javabyte.driver.common.driver;

import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connector {

    private User user = null;

    private String ip = "", hostname = "", username = "";
    private BigInteger password;
    private int port = 0;
    private volatile ServerClient client = null;
    private DatabaseObjectFactory databaseObjectFactory = new DatabaseObjectFactory();
    private volatile AtomicBoolean loggedOn = new AtomicBoolean(false);
    private Keys keyManager;


    protected Connector(String ip, String hostname, int port, String username, String password) throws Exception {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = new BigInteger(CryptTools.getSHA256(password));
        this.hostname = hostname;
        keyManager = new Keys();
    }

    protected boolean connect() throws Exception {
        if (!isConnected()) {
            client = new ServerClient();
            client.startConnection(hostname, ip, port);
            if (client.isConnected()) {
                try{
                    keyManager.generateRSAKeys();
                    String key = client.sendMessage(new String(Base64.getEncoder().encode(keyManager.getPublicKey()), "UTF-8"));
                    if(!keyManager.decryptAESKey(new String(Base64.getDecoder().decode(key)))){
                        throw new Exception("Failed to complete handshake.");
                    }
                    try {
                        if(logon()){
                            //System.out.println("logged on");
                            loggedOn.set(true);
                            return true;
                        }else {
                            //System.out.println("not");
                            loggedOn.set(false);
                            return false;
                        }

                    } catch (Exception e) {
                        System.out.println();
                        loggedOn.set(false);
                        //throw e;
                    }
                }catch (Exception e){
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return client.isConnected();
    }

     public String getUsername(){
        return username;
    }

    boolean logon() throws Exception {
        Transaction transaction = new Transaction(username, password);
        byte[] encrypted = keyManager.encryptAESRequest(databaseObjectFactory.databaseSerialFactory(transaction));
        if(encrypted == null){
            throw new Exception("Failed to encrypt request");
        }
        String input = sendMessage(new String(Base64.getEncoder().encode(encrypted), "UTF-8"));
        if(input == null){
            client = null;
            return false;
        }
        user = (User)databaseObjectFactory.databaseObjectFactory(keyManager.decryptAESResponse(Base64.getDecoder().decode(input)));
        return true;
    }

    protected void logout() throws Exception {
        if(user != null){
            Transaction transaction = new Transaction();
            transaction.setOperation("LOGOFF");
            transaction.setUsername(username);
            transaction.setRequestTime(System.currentTimeMillis());
            sendMessage(new String(Base64.getEncoder().encode(keyManager.encryptAESRequest(databaseObjectFactory.databaseSerialFactory(transaction))), "UTF-8"));
            user = null;
        }
        if(client.isConnected()){
            client.stopConnection();
        }
        loggedOn.set(false);
    }

    protected DatabaseResults sendTransaction(Transaction transaction) throws Exception{
        if(isConnected()){
            transaction.setUsername(username);
            String response = "";
            if((response = sendMessage(new String(Base64.getEncoder().encode(keyManager.encryptAESRequest(databaseObjectFactory.databaseSerialFactory(transaction))), "UTF-8"))) == null){
                client.stopConnection();
                client = null;
                connect();
                sendTransaction(transaction);
            }
            if(response.equals("closed") || response == null){
                client.stopConnection();
                client = null;
                connect();
                sendTransaction(transaction);
            }
            try{
                return (DatabaseResults)databaseObjectFactory.databaseObjectFactory(Base64.getDecoder().decode(keyManager.decryptAESResponse(response.getBytes("UTF-8"))));
            }catch (Exception e){
                if(logon()){
                    transaction.setUsername(username);
                    return (DatabaseResults)databaseObjectFactory.databaseObjectFactory(Base64.getDecoder().decode(sendMessage(new String(Base64.getEncoder().encode(keyManager.encryptAESRequest(databaseObjectFactory.databaseSerialFactory(transaction))), "UTF-8")).getBytes("UTF-8")));
                }
                return null;
            }
        }else {
            if(logon()){
                transaction.setUsername(username);
                return (DatabaseResults)databaseObjectFactory.databaseObjectFactory(Base64.getDecoder().decode(sendMessage(new String(Base64.getEncoder().encode(keyManager.encryptAESRequest(databaseObjectFactory.databaseSerialFactory(transaction))), "UTF-8")).getBytes("UTF-8")));
            }
            return null;
        }
    }

    protected boolean isConnected() {
        if (client != null) {
            return client.isConnected();
        } else {
            return false;
        }
    }

    protected String sendMessage(String message) throws Exception {
        if (connect()) {
            return client.sendMessage(message);
        }
        return "Not connected";
    }

}
