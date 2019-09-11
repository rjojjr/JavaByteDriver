package kirchnersolutions.javabyte.driver.enterprise;

import kirchnersolutions.javabyte.driver.common.driver.Connector;
import kirchnersolutions.javabyte.driver.common.driver.DatabaseResults;
import kirchnersolutions.javabyte.driver.common.driver.Transaction;

class EnterpriseClientConnector extends Connector {

    EnterpriseClientConnector(String ip, String hostname, int port, String username, String password) throws Exception{
        super(ip, hostname, port, username, password);
    }

    public boolean connect() throws Exception{
        return super.connect();
    }

    public boolean isConnected(){
        return super.isConnected();
    }

    public String sendMessage(String msg) throws Exception{
        return super.sendMessage(msg);
    }

    public void logout() throws Exception{
        super.logout();
    }

    public DatabaseResults sendTransaction(Transaction transaction) throws Exception{
        return super.sendTransaction(transaction);
    }

    public String getUsername(){
        return super.getUsername();
    }

}
