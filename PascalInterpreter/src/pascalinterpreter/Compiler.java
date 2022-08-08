package pascalinterpreter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
public class Compiler {
    private Statement head = null;
    private Dictionary<String,Object> var;
    private Queue<String> srcCode;
    private Calculator calc;
    private String result;
    
    public Compiler(String code){
        this.srcCode = getProperCode(code);
        var = new Hashtable<>();
        calc = new Calculator(var);
        result = "";
    }
    
    public Queue<String> getProperCode(String oldCode){
        String fCode = ""; boolean coma = false;
        // fCode = filtered code
        for (char c : oldCode.toCharArray()) {
            if(c == '\''){
                coma = !coma;
                fCode += c;
            }else if(coma){
                fCode += c;
            }else if(c == '\t' || c == ' ' || c == '\n'){
                fCode += '@';
            }else
                fCode+=c;
        }
        fCode = fCode.replace("@var@", "@var;");
        fCode = fCode.replace("@begin@", "@begin;");
        fCode = fCode.replace("@end@", "@end;");
        
        fCode = fCode.replace("@then@", "@then;");
        fCode = fCode.replace("@else@", "@else;");
        fCode = fCode.replace("@repeat@", "@repeat;");
        fCode = fCode.replace("@do@", "@do;");
        fCode = fCode.replace("@downto@", "_downto_");
        fCode = fCode.replace("@to@", "_to_");
        fCode = fCode.replace(";", ";@");
        
        String tempCode;
        do{
            tempCode = fCode;
            fCode = fCode.replace("@@", "@");
        }while (!fCode.equals(tempCode));
        fCode = fCode.replace("@end.@", "@end");
        fCode = fCode.replace("@end.", "@end");
        fCode = fCode.replace("@", " ");
        
        Queue<String> lastCode = new LinkedList<>();
        String newCode[] = fCode.split(";");
        for (int i = 0; i < newCode.length; i++) {
            newCode[i] = "@" + newCode[i].substring(1) + ";";
            lastCode.add(newCode[i]);
        }
        return lastCode;
    }
   
    public int getDataType(String word){
        // 1 - concat
        // 2 - plus(Float)
        // 3 - plus(Integer)
        
        if(word.equals("") || word.contains("'")) return 1;
        
        int type = 3;
        String temp = "";
        char list_operator[] = {'+','-','*','/','%','^','$','(',')','>','<','='};
        for (char c : (word+"$").toCharArray()) {
            boolean isOperator = false;
            for (char op : list_operator) 
                isOperator = c == op ? true : isOperator;
            if(isOperator){
                if(!temp.equals("")){
                    boolean isVariable = var.get(temp) != null;
                    if(isVariable){
                        if(var.get(temp) instanceof String)
                            return 1;
                        else if(var.get(temp) instanceof Float)
                            type = 2;
                    }else if(temp.contains(".")) type = 2;
                    temp = "";
                }
            }else
                temp += c+"";
        }
        return type;
    }
    
    String removeSpace(String word){
        boolean coma = false;
        String newWord = "";
        for (char c : word.toCharArray()) {
            if(c == '\''){
                coma = !coma;
                newWord += c;
            }else if(coma || c != ' '){
                newWord += c;
            }
        }
        return newWord;
    }
    
    String getString(String word){
        String result = "";
        String vari = "";
        boolean coma = false;
        int openParanth = 0;
        for (char c : (word+"+").toCharArray()) {
            if(c=='\''){
                coma = !coma;
            }else if(coma){
                result += c;
            }else if(c == '+' && openParanth == 0){
                if(!vari.equals("")){
                    int type = getDataType(vari);
                    String newResult = "";
                    if(type == 1){
                        boolean isVari = !vari.contains("+");
                        if(isVari){
                            newResult = (String)var.get(vari);
                            if(newResult == null) throw new NullPointerException();
                        }else{
                            newResult = getString(vari);
                        }
                    }else if(type == 2){
                        newResult = calc.result(vari) + "";
                    }else{
                        newResult = (int)calc.result(vari) + "";
                    }result += newResult;
                }vari = "";
            }else{
                if(c == '('){
                    if(openParanth > 0)
                        vari+=c;
                    openParanth++;
                }
                else if(c == ')'){
                    openParanth--;
                    if(openParanth > 0)
                        vari += c;
                }else
                    vari += c;
            }
        }
        if(openParanth != 0) throw new NullPointerException();
        return result;
    }
    
    class Statement{
        String word;
        Statement next;
        public Statement(String word){this.word = word;}
        void read(){
            String temp = word;
            if(temp.contains("@write")){
                temp = temp.replace(");", "");
                boolean printLine = false;
                if(printLine = temp.contains("@writeln("))
                    temp = temp.replace("@writeln(", "");
                else
                    temp = temp.replace("@write(", "");
                
                String output = "";
                if(getDataType(temp) == 1)
                    output = getString(temp);
                else if(getDataType(temp) == 2)
                    output = calc.result(temp) + "";
                else if(getDataType(temp) == 3)
                    output = (int)calc.result(temp) + "";
                result += (output + (printLine ? "\n" : ""));
            }else{
                temp = temp.replace("@", "");
                temp = temp.replace(";", "");
                String tempVar = "";
                boolean readVar = true;
                while(temp.length()>0 && readVar){
                    char c = temp.charAt(0);
                    temp = temp.substring(1);
                    if(readVar){
                        if(c == ':' && temp.charAt(0) == '='){
                            temp = temp.substring(1);
                            readVar = false;
                        }else{
                            tempVar += c;
                        }
                    }                   
                }
                // untuk cek type data suatu variable
                // setelah itu o di assign ke dict
                Object o = var.get(tempVar);
                if(o instanceof Integer)
                    o = (int)(calc.result(temp));
                else if(o instanceof Float)
                    o = calc.result(temp);
                else if(o instanceof String)
                    o = getString(temp);                
                var.put(tempVar, o);
            }
            runNext();
        }
        
        String conditionToInfix(String word){
            String result = ""; boolean makeSure = false;
            if(word.contains(" and ") || word.contains(" or ")){
                String read = "";
                int openParanthesis = 0;
                for (char c : (word+" ").toCharArray()) {
                    if(c == '('){
                        //if(openParanthesis > 0)
                            read += c;
                        openParanthesis++;
                    }else if(openParanthesis > 0){
                        if(c == ')')
                            openParanthesis--;
                        if(openParanthesis == 0)
                            makeSure = true;
                        else
                            read += c;
                    }else if(makeSure){
                        char list_operator[] = {'=','>','<','+','*','-','/','%','^'};
                        boolean isOperator = false;
                        for (char op : list_operator) 
                            isOperator = c == op ? true : isOperator;
                        if(!isOperator){
                            result += "(" + conditionToInfix(read.substring(1)) + ")";
                            read = "";
                        }else
                            read+=")"+c;
                        makeSure = false;
                    }else if(c == ' '){
                        if(!read.equals("")){
                            if(read.equals("and"))
                                result += "*";
                            else if(read.equals("or"))
                                result += "+";
                            else 
                                result += conditionToInfix(read);
                            read = "";
                        }
                    }else{
                        read += c;
                    }
                }
            }else{
                word = removeSpace(word);
                int dataType = getDataType(word);
                
                char list_operator[] = {'>','<','='};
                String data1,data2,operator;
                data1 = data2 = operator = "";
                for (char c : word.toCharArray()) {
                    boolean isOperator = false;
                    for (char op : list_operator)
                        isOperator = c == op ? true : isOperator;
                    if(isOperator)
                        operator += c;
                    else if(operator == "")
                        data1 += c;
                    else
                        data2 += c;
                }
                if(dataType == 1){
                    if(operator.equals("="))
                        result = getString(data1).equals(getString(data2)) ? "1" : "0";
                    else if(operator.equals("<>"))
                        result = !getString(data1).equals(getString(data2)) ? "1" : "0";
                    else
                        result = "#error#";
                }else{
                    float a = calc.result(data1);
                    float b = calc.result(data2);
                    a = dataType == 3 ? (int)a : a;
                    b = dataType == 3 ? (int)b : b;
                    if(operator.equals("<="))
                        result = a <= b ? "1" : "0";
                    else if(operator.equals("<"))
                        result = a < b ? "1" : "0";
                    else if(operator.equals("="))
                        result = a == b ? "1" : "0";
                    else if(operator.equals(">"))
                        result = a > b ? "1" : "0";
                    else if(operator.equals(">="))
                        result = a >= b ? "1" : "0";
                    else if(operator.equals("<>"))
                        result = a != b ? "1" : "0";
                    else 
                        result = "#error#";
                }
                
            }
            return result;
        }
        
        boolean isConditionMet(){
            boolean conditionMet;
            String infix = conditionToInfix(word);
            conditionMet = calc.result(infix) > 0;
            
            return conditionMet;
        }
        
        void runNext(){
            if(next!=null)
                next.read();
        }
    }
    
    
    class Declaration extends Statement{
        public Declaration(String word) {
            super(word);
        }

        @Override
        void read(){
            ArrayList<String> listVar = new ArrayList<>();
            word = word.substring(1);
            String tempVar = "";
            String tempType = "";
            String value = "";
            int phase = 1;
            // 1 - read variable name
            // 2 - read the data type,   k, 
            // 3 - read default value(if exist)
            for (char c : word.toCharArray())
                if(c == ';')
                    phase = 4;
                else if(phase == 1)
                    if(c!=':'){
                        if(c == ','){
                            listVar.add(tempVar);
                            tempVar = "";
                        }else
                            tempVar += c;
                    }else{
                        listVar.add(tempVar);
                        phase++;
                    }
                else if(phase == 2)
                    if(c!='=')
                        tempType += c;
                    else 
                        phase++;
                else if(phase == 3)
                    value += c;
            
            Object o = null; 
            boolean valid = true; // true jika tipe data terdaftar
            if(tempType.equals("integer"))
                o = value!="" ? (int)(calc.result(value)) : (Integer)0;
            else if(tempType.equals("float"))
                o = value!="" ? (Float)calc.result(value) : (Float)0f;
            else if(tempType.equals("string"))
                o = getString(value);
            else
                valid = false;
            
            for (String s : listVar)
                if(valid)var.put(s, o);
            runNext();
        }
    }
    
    class Selection extends Statement{
        Statement nextTrue;
        Statement nextFalse;
        public Selection(String cond,Statement ifTrue,Statement ifFalse){
            super(cond);
            nextTrue = ifTrue;
            nextFalse = ifFalse;
        }
        
        void read(){
            Statement temp;
            temp = isConditionMet() ? nextTrue : nextFalse;
            if(temp!=null)temp.read();
            runNext();
        }

    }
    
    class Iteration extends Statement{
        Statement loop;
        boolean TopTested;
        
        public Iteration(String word,Statement loop,boolean TopTested) {
            super(word);
            this.loop = loop;
            this.TopTested = TopTested;
        }
        
        void read(){
            if(TopTested){
                while(isConditionMet()){
                    loop.read();
                }
            }else{
                do{
                    loop.read();
                }while(isConditionMet());
            }runNext();
        }
    }
    
    
    void addBlock(Statement head, Statement newBlock){
        Statement temp = head;
        while(temp.next != null)
            temp = temp.next;
        temp.next = newBlock;
    } 
    
    private Statement declareVariable(Statement head){
        Statement newStatement;
        String line;
        while(!srcCode.peek().contains("@begin;")){
            line = srcCode.poll();
            if(!line.equals("@var;")){
                line = removeSpace(line);
                newStatement = new Declaration(line);
                if(head == null) head = newStatement;
                else addBlock(head, newStatement);
            }
        }
        return head;
    }
    
    private Statement readStatement(Statement head,boolean isLimited){
        Statement newStatement;
        Statement ifTrue,ifFalse;
        String line; boolean exit = false;
        while(!srcCode.isEmpty() && !exit){
            newStatement = null;
            line = srcCode.poll();
            if(line.equals("@begin;")){
                newStatement = readStatement(newStatement, false);
            }else if(line.equals("@end;")){
                exit = true;
            }else if(line.contains("@if ")){
                line = line.replaceAll("@if ", "");
                line = line.replaceAll(" then;", "");
                ifFalse = null;
                ifTrue = readStatement(null, true);
                if(srcCode.peek().equals("@else;")){
                    srcCode.poll();
                    ifFalse = readStatement(null, true);
                }newStatement = new Selection(line, ifTrue, ifFalse);
            }else if(line.contains("@while ")){
                line = line.replaceAll("@while ", "");
                line = line.replaceAll(" do;", "");
                ifTrue = readStatement(null, true);
                newStatement = new Iteration(line, ifTrue, true);
            }else if(line.equals("@repeat;")){
                ifTrue = readStatement(null, true);
                line = srcCode.poll();
                line = line.replaceAll("@while ", "");
                line = line.replaceAll(" ;", "");
                line = line.replaceAll(";", "");
                newStatement = new Iteration(line, ifTrue, false);
            }else if(line.contains("@for ")){
                line = line.replaceAll("@for ", "");
                line = line.replaceAll(" do;", "");
                String for_part[] = line.split("_");
                
                String assignment,operator,criteria;
                assignment = removeSpace(for_part[0]);
                operator = for_part[1];
                criteria = removeSpace(for_part[2]);
                
                String identifier;
                identifier = "";
                for (int i = 0; i < assignment.length(); i++) {
                    char c = assignment.charAt(i);
                    if(c != ':')
                        identifier += c;
                    else
                        i = assignment.length();
                }
                
                if(operator.equals("downto"))
                    operator = ">=";
                else if(operator.equals("to"))
                    operator = "<=";
                else 
                    operator = "#error#";
                
                String condition = identifier + operator + criteria;
                String increment = "@" + identifier + ":=" + identifier + (operator.equals("<=")? "+" : "-") + "1;";
                
                Statement increStatement = new Statement(increment);
                newStatement = new Statement(assignment);
                
                ifTrue = readStatement(null, true);
                Iteration for_statement = new Iteration(condition, ifTrue, true);
                addBlock(ifTrue, increStatement);
                
                newStatement.next = for_statement;
            } else {
                line = removeSpace(line);
                newStatement = new Statement(line);
            }
            if(head!=null)addBlock(head, newStatement);
            else head = newStatement;
            if(isLimited)
                exit = true;
        }return head;
    }
    
    void execute(){
        result = "";
        try{
            head = declareVariable(null);
            head = readStatement(head, false);
            head.read();
            OutputText.output = result;
        }catch (Exception e){
            OutputText.output = "Error Compile";
        }
    }
    
}
