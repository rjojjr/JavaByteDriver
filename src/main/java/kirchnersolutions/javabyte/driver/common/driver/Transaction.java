package kirchnersolutions.javabyte.driver.common.driver;
/**
 *2019 Kirchner Solutions
 * @Author Robert Kirchner Jr.
 *
 * This code may not be decompiled, recompiled, copied, redistributed or modified
 * in any way unless given express written consent from Kirchner Solutions.
 */
import kirchnersolutions.javabyte.driver.common.driver.Serializer;
import kirchnersolutions.javabyte.driver.common.driver.DatabaseObjectInterface;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction implements DatabaseObjectInterface {


    public Transaction(){
        this.requestTime = System.currentTimeMillis();
    }

    public Transaction(String username, BigInteger password){
        this.username = username;
        this.password = password;
        this.operation = new String("LOGON");
        this.requestTime = System.currentTimeMillis();
    }

    /**
     * Field Types
     * I = Integer
     * D = Decimal
     * S = String
     * B = Boolean
     */
    private List<String> select = null;
    private List<Map<BigInteger, Map<String, String>>> oldValues = null;
    private List<Map<String, String>> results = null;
    private Map<String, String> put = null, where = null, put2 = null, where2 = null;
    private List<Map<String, String>> newRows = null, newRows2 = null;
    /**
     * LOGON
     * LOGOUT
     * CREATE TABLE tableName fieldName1-type;fieldNamen-type:(Optional)indexedFieldName1;indexedFieldNamen
     * CREATE ROWS ADVANCED tableName1:tableName2
     * CREATE ROWS tableName1 fieldName1.value;fieldNamen.value:fieldName1.value;fieldNamen.value (Optional) ALSO tableName2 tableName1.fieldNamen=tableName2.fieldNamen fieldName1.value;fieldNamen.value:fieldName1.value;fieldNamen.value
     * SELECT ADVANCED  tableName1:tableName2
     * SELECT howMany tableName fieldName1;fieldName: WHERE fieldName1=value1;fieldNamen=vanuen: (Optional)JOIN tableName2 fieldName1;fieldName WHERE tableName1.fieldName1=tableName2.fieldname1:
     * PUT ADVANCED tableName1:tableName2
     * PUT tableName1 fieldName1=value;fieldNamen=value WHERE fieldName1=value1;fieldNamen=vanuen (Optional) ALSO tableName2 fieldName2=value;fieldNamen=value WHERE tableName1.fieldNamen=tableName2.fieldNamen
     * DELETE ADVANCED FROM tableName1:tableName2
     * DELETE FROM tableName1 WHERE fieldName1=value1;fieldNamen=vanuen (Optional) ALSO tableName2 WHERE tableName1.fieldNamen=tableName2.fieldNamen:
     * DELETE TABLE tableName
     */
    private String operation = new String("");
    private String failMessage = new String("");
    private String username = new String("");
    private BigInteger password = new BigInteger("-1"), transactionID = new BigInteger("-1"), userIndex = new BigInteger("-1");
    private Long requestTime = Long.parseLong("0"), finishTime = Long.parseLong("0");
    //-1 for all;
    private BigInteger howMany = new BigInteger("-1");
    private boolean successfull = false;

    public void setUsername(String username) {
        this.username = username;
    }void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public void setNewRows(List<Map<String, String>> newRows) {
        this.newRows = newRows;
    }

    public void setNewRows2(List<Map<String, String>> newRows) {
        this.newRows2 = newRows;
    }

    public void setHowMany(BigInteger howMany) {
        this.howMany = howMany;
    }

    public void setUserIndex(BigInteger userIndex) {
        this.userIndex = userIndex;
    }

    public void setPut2(Map<String, String> put2) {
        this.put2 = put2;
    }

    public void setWhere2(Map<String, String> where2) {
        this.where2 = where2;
    }

    public void setWhere(Map<String, String> where) {
        this.where = where;
    }

    public void setPut(Map<String, String> put) {
        this.put = put;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public void setSelect(List<String> select) {
        this.select = select;
    }

    //-1 for all;
    BigInteger getHowMany() {
        return new BigInteger(howMany.toByteArray());
    }

    BigInteger getUserIndex() {
        return userIndex;
    }

    String getFailMessage() {
        return failMessage;
    }

    BigInteger getTransactionID() {
        return new BigInteger(transactionID.toByteArray());
    }

    List<Map<BigInteger, Map<String, String>>> getOldValues() {
        if(oldValues != null){
            return new ArrayList<>(oldValues);
        }else{
            return null;
        }
    }

    List<Map<String, String>> getNewRows() {
        if(newRows != null){
            return new ArrayList<>(newRows);
        }else{
            return null;
        }
    }

    List<Map<String, String>> getNewRows2() {
        if(newRows2 != null){
            return new ArrayList<>(newRows2);
        }else{
            return null;
        }
    }

    Map<String, String> getPut2() {
        if(put2 != null){
            return new HashMap<String, String>(put2);
        }else{
            return null;
        }
    }

    Map<String, String> getWhere2() {
        if(where2 != null){
            return new HashMap<String, String>(where2);
        }else{
            return null;
        }
    }

    List<Map<String, String>> getResults() {
        if(results != null){
            return new ArrayList<>(results);
        }else{
            return null;
        }
    }

    List<String> getSelect() {
        if(select != null){
            return new ArrayList<>(select);
        }else{
            return null;
        }
    }

    Long getFinishTime() {
        return finishTime;
    }

    Long getRequestTime() {
        return requestTime;
    }

    boolean isSuccessfull() {
        return successfull;
    }

    Map<String, String> getPut() {
        if(put != null){
            return new HashMap<String, String>(put);
        }else{
            return null;
        }
    }

    Map<String, String> getWhere() {
        if(where != null){
            return new HashMap<String, String>(where);
        }else{
            return null;
        }
    }

    public String getOperation() {
        return new String(operation);
    }

    String getUsername() {
        return new String(username);
    }

    BigInteger getPassword() {
        return new BigInteger(password.toByteArray());
    }

    @Override
    public byte getHeader() throws Exception{
        return "1".getBytes("UTF-8")[0];
    }


}