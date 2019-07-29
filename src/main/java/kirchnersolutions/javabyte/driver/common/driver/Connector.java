package kirchnersolutions.javabyte.driver.common.driver;

import kirchnersolutions.javabyte.driver.common.utilities.CryptTools;

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


    protected Connector(String ip, String hostname, int port, String username, String password) throws Exception {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = new BigInteger(CryptTools.getSHA256(password));
        this.hostname = hostname;
    }

    protected boolean connect() throws Exception {
        if (!isConnected()) {
            client = new ServerClient();
            client.startConnection(hostname, ip, port);
            if (client.isConnected()) {
                try {
                    logon();
                    loggedOn.set(true);
                } catch (Exception e) {
                    loggedOn.set(false);
                    e.printStackTrace();
                }
            }
        }
        return client.isConnected();
    }

    private boolean logon() throws Exception {
        Transaction transaction = new Transaction(username, password);
        transaction.setRequestTime(System.currentTimeMillis());
        String input = sendMessage(new String(Base64.getEncoder().encode(databaseObjectFactory.databaseSerialFactory(new Transaction(username, password))), "UTF-8"));
        //System.out.println("input");
        user = (User)databaseObjectFactory.databaseObjectFactory(Base64.getDecoder().decode(input));
        return true;
    }

    protected void logout() throws Exception {
        if(user != null){
            Transaction transaction = new Transaction();
            transaction.setOperation("LOGOFF");
            transaction.setUsername(username);
            transaction.setRequestTime(System.currentTimeMillis());
            sendMessage(new String(Base64.getEncoder().encode(databaseObjectFactory.databaseSerialFactory(transaction)), "UTF-8"));
            user = null;
        }
        if(client.isConnected()){
            client.stopConnection();
        }
        loggedOn.set(false);
    }

    protected DatabaseResults sendTransaction(Transaction transaction) throws Exception{
        transaction.setUsername(username);
        return (DatabaseResults)databaseObjectFactory.databaseObjectFactory(Base64.getDecoder().decode(sendMessage(new String(Base64.getEncoder().encode(databaseObjectFactory.databaseSerialFactory(transaction)), "UTF-8")).getBytes("UTF-8")));
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
