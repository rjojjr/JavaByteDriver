package kirchnersolutions.javabyte.driver.common.driver;
/**
 *2019 Kirchner Solutions
 * @Author Robert Kirchner Jr.
 *
 * This code may not be decompiled, recompiled, copied, redistributed or modified
 * in any way unless given express written consent from Kirchner Solutions.
 */


import kirchnersolutions.javabyte.driver.common.utilities.ByteTools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Field implements Serializable, DatabaseObjectInterface {
    private FieldSerializer fieldSerializer = new FieldSerializer();

    private String name = new String(""), value = new String("");
    private File file = null;
    private boolean fileSupplied = true;
    /**
     * I = Integer
     * D = Decimal
     * S = String
     * B = Boolean
     */
    private String type = "";

    /**
     * Initialize a field that already exists.
     * @param file
     * @throws Exception
     */
    public Field(File file) throws Exception  {
        if(!file.exists()){
            throw new Exception("Field " + file.getName() + " at index " + file.getParent() + " not found");
        }
        this.file = file;
    }

    /**
     * Initialize a new field.
     * @param file
     * @param name
     * @param value
     * @param type
     * @throws Exception
     */
    public Field(File file, String name, String value, String type) throws Exception {
        this.file = file;
        this.name = name;
        this.value = value;
        this.type = type;
        if(file.exists()){
            throw new Exception("Field " + name.toString() + " at index " + file.getParent() + " already exists");
        }
        try{
            file.createNewFile();
        }catch(IOException e) {
            throw e;
        }
    }

    /**
     * To be used by serializer.
     * @throws Exception
     */
    public Field(){
        fileSupplied = false;
    }

    /**
     * To be used by serializer
     * @param file
     */
    public void setFile(File file) throws Exception {
        if(!fileSupplied){
            this.file = file;
            if(!file.exists()){
                /*
                if(devVars.isDevExceptions()){
                    System.out.println("Field file not found: Class Field Method setFile");
                    throw new DevelopementException("Field file " + file.getParent() + "/" + file.getName() + " not found: Class Field Method setFile");
                }else{
                    throw new FieldException("Field file " + file.getParent() + "/" + file.getName() + " not found");
                }
                */
                try{
                    file.createNewFile();
                }catch (Exception e){
                    if(true){
                        System.out.println("IOException");
                        System.out.println(e);
                        throw new Exception("IOException: Class Field Method setFile" + System.lineSeparator() + e);
                    }else{
                        throw new Exception("Failed to create field file " + file.getName() + " index " + file.getParent() + ": IOException");
                    }
                }
            }
        }else{
            throw new Exception("Operation is only for serializer.");
        }
    }

    public String getName() {
        return new String(name);
    }

    public String getValue() {
        return new String(value);
    }

    public String getType() {
        return type;
    }

    public File getFile() {
        return file;
    }

    public void setName(String name) throws Exception {
        this.name = name;
        if(file != null){
        }
    }

    public void setValue(String value) throws Exception  {
        this.value = value;
        if(file != null){
        }
    }

    public void setType(String type) throws Exception  {
        this.type = type;
        if(file != null){
        }
    }

    @Override
    public byte getHeader() throws Exception{
        return "2".getBytes("UTF-8")[0];
    }

    /*
    private byte[] serialize() throws SerialException, DevelopementException {
        byte[] nameBytes, valueBytes, typeBytes;
        nameBytes = Serializer.serializeObject(name);
        valueBytes = Serializer.serializeObject(value);
        try{
            typeBytes = type.getBytes(sysVars.getCharacterEncoding());
            byte[] serial = new byte[nameBytes.length + typeBytes.length + valueBytes.length + 2];
            int count = 0;
            for(int i = 0; count < nameBytes.length; count++){
                serial[count] = nameBytes[count];
            }
            char breakTag = '\n';
            serial[count] = (byte)breakTag;
            count++;
            for(int i = 0; i < valueBytes.length; count++){
                serial[count] = nameBytes[i];
                i++;
            }
            serial[count] = (byte)breakTag;
            count++;
            for(int i = 0; i < typeBytes.length; count++){
                serial[count] = typeBytes[i];
                i++;
            }
            return serial;
        } catch (UnsupportedEncodingException e){
            throw new SerialException("Field type encoding not supported: " + sysVars.getCharacterEncoding());
        } catch (ArrayIndexOutOfBoundsException ex){
            //Rollback imp
            if(devVars.isDevExceptions()){
                System.out.println("Serial index count is off");
                System.out.println(ex);
                throw new DevelopementException("Serial index count is off: Class Field Method serialize()");
            }
        }
        return null;

    }

    private void deserialize(byte[] serial) throws SerialException, DevelopementException {
        byte[] nameBytes, valueBytes, typeBytes;
        char breakTag = '\n';
        byte bTag = (byte)breakTag;
        try{
            boolean first = true;
            int count = -1, nameLength = 0, valueLength = 0, typeLength = 0;
            for(int i = 0; i < serial.length; i++){
                if(serial[i] == bTag){
                    if(nameLength == 0){
                        nameLength = i;
                        first = false;
                    }else if(valueLength == 0){
                        valueLength = count;
                        typeLength = (serial.length - i) + 1;
                        break;
                    }
                }
                if(!first){
                    count++;
                }
            }
            nameBytes = new byte[nameLength];
            valueBytes = new byte[valueLength];
            typeBytes = new byte[typeLength];
            count = 1;
            for(int i = 0; i < nameLength; i++){
                nameBytes[i] = serial[i];
                count++;
            }
            int count1 = 0;
            for(int i = count; count1 < valueLength; i++){
                valueBytes[count1] = serial[i];
                count++;
                count1++;
            }
            count1 = 0;
            for(int i = count; count1 < typeLength; i++){
                typeBytes[count1] = serial[i];
                count++;
                count1++;
            }
            try{
                name = (String)Serializer.deserializeObject(nameBytes);
            }catch(SerialException ex1){

                if(devVars.isDevExceptions()){
                    System.out.println("Failed to deserialize field name");
                    throw new DevelopementException("Failed to deserialize field name: Class Field Method deserialize()");
                }
                throw new SerialException("Failed to deserialize field name");
            }
            try{
                value = (String)Serializer.deserializeObject(valueBytes);
            }catch(SerialException ex1){

                if(devVars.isDevExceptions()){
                    System.out.println("Failed to deserialize field value");
                    throw new DevelopementException("Failed to deserialize field value: Class Field Method deserialize()");
                }
                throw new SerialException("Failed to deserialize field value");
            }
            type = new String(typeBytes, sysVars.getCharacterEncoding());
        } catch (UnsupportedEncodingException e){
            if(devVars.isDevExceptions()){
                System.out.println("Field type encoding not supported: " + sysVars.getCharacterEncoding());
                System.out.println(e);
                throw new DevelopementException("Field type encoding not supported: " + sysVars.getCharacterEncoding() + " Class Field Method deserialize()");
            }else{
                throw new SerialException("Field type encoding not supported: " + sysVars.getCharacterEncoding());
            }

        } catch (ArrayIndexOutOfBoundsException ex){
            //Rollback imp
            if(devVars.isDevExceptions()){
                System.out.println("Serial index count is off");
                System.out.println(ex);
                throw new DevelopementException("Serial index count is off: Class Field Method deserialize()");
            }
        }

    }*/

    public void write() throws Exception{
        try{
            if(fieldSerializer == null){
                fieldSerializer = new FieldSerializer();
            }
            ByteTools.writeBytesToFile(file, fieldSerializer.serialize(this));
        }catch (IOException e){
            if(true){
                System.out.println("IOException");
                System.out.println(e);
                throw new Exception("IOException: Class Field Method write()" + System.lineSeparator() + e);
            }else{
                throw new Exception("Failed to write field " + file.getName() + " index " + file.getParent() + " to disk: IOException");
            }
        }catch (Exception ex){
            System.out.println(ex);
            throw ex;
        }
    }



    private void read() throws Exception  {
        byte[] serial;
        try{
            Field temp = fieldSerializer.deserialize(this.file);
            this.setType(temp.getType());
            this.setValue(temp.getValue());
            this.setName(temp.getName());
        }catch (IOException e){
            if(true){
                System.out.println("IOException");
                System.out.println(e);
                throw new Exception("IOException: Class Field Method read()" + System.lineSeparator() + e);
            }else{
                throw new Exception("Failed to read field " + file.getName() + " index " + file.getParent() + " to disk: IOException");
            }
        }catch (Exception ex){
            System.out.println("Exception hereL ");
            ex.printStackTrace();
        }
    }


}