package kirchnersolutions.javabyte.driver.common.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBEntity {

    private Connector connector;
    private DatabaseResults databaseResults;
    private Transaction transaction;
    private String tableName;
    private List<String> fields;

    /**
     * Construct with select operation.
     * @param connector
     * @param transaction
     */
    public DBEntity(Connector connector, Transaction transaction){
        this.connector = connector;
        //this.databaseResults = databaseResults;
        this.transaction = transaction;
        tableName = transaction.getOperation().split(" ")[2];
    }

    /**
     * Send SELECT request.
     * @return
     * @throws Exception
     */
    public boolean initialize() throws Exception{
        databaseResults = connector.sendTransaction(transaction);
        if(databaseResults.isSuccess()){
            fields = (List<String>)databaseResults.getResults().get(0).keySet();
            return true;
        }
        fields = new ArrayList<>();
        return false;
    }

    /**
     * Send new values to DB and update DatabaseResults.
     * Returns false if database operation fails or a given new value is not contained in the result set.
     * @param newValues
     * @return
     * @throws Exception
     */
    public boolean update(List<Map<String, String>> newValues) throws Exception{
        for(Map<String, String> map : newValues){
            boolean contain = false;
            for(Map<String, String> old : databaseResults.getResults()){
                if(map.get("index").equals(old.get("index"))){
                    contain = true;
                    break;
                }
            }
            if(!contain){
                return false;
            }
            Transaction req = new Transaction();
            req.setUsername(connector.getUsername());
            req.setRequestTime(System.currentTimeMillis());
            req.setPut(map);
            req.setOperation("PUT ADVANCED " + tableName);
            DatabaseResults res = connector.sendTransaction(req);
            if(!res.isSuccess()){
                for(Map<String, String> old : databaseResults.getResults()){
                    Transaction req2 = new Transaction();
                    req2.setUsername(connector.getUsername());
                    req2.setRequestTime(System.currentTimeMillis());
                    req2.setPut(map);
                    req2.setOperation("PUT ADVANCED " + tableName);
                    DatabaseResults roll = connector.sendTransaction(req);
                }
                return false;
            }
        }
        transaction.setRequestTime(System.currentTimeMillis());
        databaseResults = connector.sendTransaction(transaction);
        return true;
    }

    /**
     * Get results.
     * @return
     */
    public DatabaseResults getDatabaseResults(){
        return databaseResults;
    }

}
