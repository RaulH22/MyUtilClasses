package me.RaulH22.PluginDeTestes.zzzz;

import java.util.HashMap;
import java.util.Map;

public class LinearData {

    // Example: AAA:aaa,BBBB:[CCC:ccc, DDD:[EEE:eee, FFF:fff]]
    

    private Map<String,Object> dataMap;
    private String thisLine = null;
    private int brackets = 0;
    private int openedBrackets = 0;
    private int i;
    private LinearData parent;
    private char stringChar = ' ';
    
    private boolean isInAString() {
        return !(stringChar==' ');
    }
    
    public LinearData(String string) {
        build(string, null);
    }
    
    public LinearData(String string, LinearData parent) {
        build(string, parent);
    }
    
    
    private void build(String string, LinearData parent) {
        this.parent = parent;
        dataMap = new HashMap<>();
        this.thisLine = string;
        
        String current = null;
        String key = null;
        boolean ignoreNext = false;
        
        
        for( i = 0; i<this.thisLine.length(); i++) {
            boolean openingString = false;
            char c = string.charAt(i);
            // Ignore unnecessary spaces
            if(!isInAString() && (c==' ' || c=='\r' || c=='\n' || c=='    ')) {
                continue;
            }
            
            
            if(brackets>0 && c!=']') {
                continue;
            }
            else if(c==']' && !ignoreNext && !isInAString()) {
                brackets--;
            }
            // Check commands
            if(!ignoreNext && !isInAString()) {
                switch (c) {
                    case '[': {
                        brackets++;
                        openedBrackets++;
                        LinearData ld = new LinearData(string.substring(i+1, string.length()),this);
                        if(this.parent!=null) {
                            this.parent.brackets+=openedBrackets;
                        }
                        dataMap.put(key, ld); 
                        key=null;
                        continue;
                    }
                    case ']': {
                        if(brackets==-1) { 
                            if(parent!=null)thisLine = string.substring(0,i);
                        }
                        if(key!=null) dataMap.put(key, current); 
                        key=null;
                        current=null;
                        continue;
                    }
                    case '/': {
                        ignoreNext = true;
                        continue;
                    }
                    case '"': {
                        stringChar = '"';
                        openingString = true;
                        continue;
                    }
                    case '\'': {
                        stringChar = '\'';
                        openingString = true;
                        continue;
                    }
                    case ':': {
                        if(current!=null) {
                            key = current;
                            current = null;                            
                        }else {
                            //TODO: Error
                        }
                        continue;
                    }
                    case ';': {
                        if(key!=null) {
                            if(current==null) {
                                //TODO: Error value not founded
                            }
                            dataMap.put(key, current);
                            key = null;
                            current = null;
                            continue;
                        }else {
                            //TODO: Error key not founded
                        }
                        continue;
                    }
                    default:
                        break;
                }
            }
            //Add to string
            if(isInAString() && c==stringChar && !ignoreNext && !openingString) {
                stringChar = ' ';
                dataMap.put(key, current);
                key = null;
                current = null;
                continue;
            }
            else {
                if(current==null) current = "";
                current+=c;
            }
            
            if(i==string.length()-1) {
                dataMap.put(key, current);
                key = null;
                current = null;
            }
        }        
    }

    
    @Override
    public String toString() {
        if(dataMap==null) return null;
        String string = "";
        int i = 0;
        for(String s : dataMap.keySet()) {
            boolean closed = false;
            string+=s+":";
            Object obj = dataMap.get(s);
            if(obj instanceof LinearData) {
                string+="[";
                string+=((LinearData)obj).toString();
                string+="]";
                closed = true;
            }
            else {
                boolean isString = false;
                if((obj instanceof String) && ((String)obj).contains(" ")) isString = true;
                if(isString) {
                    string+="\""+dataMap.get(s)+"\"";
                }
                else string+= dataMap.get(s);
            }
            if(!closed && i<dataMap.size()-1) string+=";";
            i++;
        }
        return string;
    }

    public String formatedString() {
        return formatedString("",false);
    }
    
    public String coloredFormatedString() {
        return formatedString("",true);
    }
    
    private String formatedString(String spaces, boolean colored) {
        if(dataMap==null) return null;
        String keyColor = "";
        String commandColor = "";
        String valueColor = "";
        String stringColor = "";
        if(colored) {
            keyColor = "§a";
            commandColor = "§7";
            valueColor = "§b";
            stringColor = "§e";
        }
        
        String string = "";
        int i = 0;
        for(String s : dataMap.keySet()) {
            boolean closed = false;
            string+=spaces+keyColor+s+commandColor+":";
            Object obj = dataMap.get(s);
            if(obj instanceof LinearData) {
                string+=commandColor+"[\n";
                string+= ((LinearData)obj).formatedString(spaces+"    ",colored);
                string+="\n"+spaces+commandColor+"]"+"\n";
                closed = true;
            }
            else {
                boolean isString = false;
                if((obj instanceof String) && ((String)obj).contains(" ")) isString = true;
                if(isString) {
                    string+=stringColor+"\""+dataMap.get(s)+"\"";
                }else {
                    string+= valueColor+dataMap.get(s);
                }
            }
            if(!closed && i<dataMap.size()-1) {
                string+=commandColor+";\n";
            }
            i++;
        }
        return string;
    }
    
}
