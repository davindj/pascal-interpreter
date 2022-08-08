package pascalinterpreter;import java.util.*;
class Calculator {
    Dictionary<String,Object> var;
    //<editor-fold defaultstate="collapsed" desc="Class Variable">
    class Variable implements Comparable<Variable>{
        String var;
        public Variable(String var) {this.var = var;} 
        public String getVar(){return var;}
        @Override
        public int compareTo(Variable t) {     
            if (var.length() > t.var.length()) 
              return -1;
            else if(var.length() < t.var.length())
              return 1;
            else
              return var.compareTo(t.var);
        }
    }
//</editor-fold>
    public Calculator(Dictionary<String,Object> var){this.var=var;}
    private Boolean check(int st,int temp){
        if(st==-1 && temp==-1)return false;
        else if(st==-1 && temp==0)return false;
        else if(st==10 && temp==6)return false;
        return true;
    }
    private Integer in(String s){
        if(s.equals("+")||s.equals("-")) return 2;
        else if(s.equals("*")||s.equals("/")||s.equals("%")||s.equals("^")) return 4;
        else if(s.equals("(")) return 0;
        else if(s.equals(")")) return 6;
        else if(s.equals("$")) return -1;
        else return 10;
    }
    private Integer out(String s){
        if(s.equals("+")||s.equals("-")) return 1;
        else if(s.equals("*")||s.equals("/")||s.equals("%")) return 3;
        else if(s.equals("^")) return 5;
        else if(s.equals("(")) return 6;
        else if(s.equals(")")) return 0;
        else if(s.equals("$")) return -1;
        else return 10;
    }
    private boolean isOperator(String str){
        String list_operator[] = {"+","-","*","/","%","^","$",")","("};
        for (String operator : list_operator)
            if(str.equals(operator))
                return true;
        return false;
    };   
    public ArrayList<String> generateFixedInfix(String oldInfixStr){
        // Apply Variable
        Iterator<String> v = var.size()>0 ? (Iterator<String>) var.keys() : null;
        ArrayList<Variable> vars = new ArrayList<>();
        while(v!=null && v.hasNext()){
            String key = v.next();
            vars.add(new Variable(key));
        }
        vars.sort(null); // Longest name priorities
        String newInfixStr = oldInfixStr;
        for (Variable var1 : vars) {
            newInfixStr = newInfixStr.replaceAll(var1.getVar(),var.get(var1.getVar())+"");
        }
        
        // Split into Array
        ArrayList<String> infix = new ArrayList<>();
        String temp = "";
        for(int i=0;i<newInfixStr.length();i++){
            String currCharStr = newInfixStr.charAt(i)+"";
            if(isOperator(currCharStr)){
                if(!temp.equals("")){
                    infix.add(temp);
                    temp = "";
                    infix.add(currCharStr);
                }else if(currCharStr.equals("-")){
                    String[] listPassiveOperators = { "+", "-", "*", "/", "%", "^", "(" };
                    if(infix.size() <= 0 || Arrays.asList(listPassiveOperators).contains(infix.get(infix.size()-1))){
                        // Act Ast Negation Operator
                        infix.add("-1");
                        infix.add("*");
                    }else{
                        infix.add(currCharStr);
                    }
                }else{
                    infix.add(currCharStr);
                }
            }else{
                temp += currCharStr;
                if(i == newInfixStr.length()-1){ // Last Index
                    infix.add(temp);
                }
            }
        }
        infix.add("$");
        return infix;
    }
    
    public List<String> convertToPostfix(ArrayList<String> infix, Stack<String> st){
        List<String> postfix = new ArrayList<>();
        int i=0;        
        String temp=infix.get(i++);
        Boolean error = false;
        do{
            if(in(st.peek()+"")<=out(temp)&&check(in(st.peek()+""),out(temp))){
                st.push(temp);//Baca temp selanjutnya
                temp = infix.get(i++);
            }else if(in(st.peek()+"")>out(temp)&&check(in(st.peek()+""),out(temp))){
                String last;//Masukin ke postfix
                do{
                    last = st.pop();
                    if(!last.equals("(")&&!last.equals(")")){
                        postfix.add(last);
                    }
                }while(in(st.peek()+"")>out(last));
            }else{
                error = true;
            }
        }while((!st.peek().equals("$")||!temp.equals("$"))&&!error);
        return postfix;
    }   
    
    float result(String s){
        Float res = null;
        Integer topI, botI;
        Float topF, botF;
        Stack<String> st = new Stack<>();
        st.push("$");
        ArrayList<String> infix = generateFixedInfix(s);
        List<String> postfix = convertToPostfix(infix, st);
        Iterator<String> k = st.iterator();
        for(int i=0;i<postfix.size();i++){
            if(postfix.get(i).equals("+")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((Float)botF+(Float)topF+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((Float)botF+(Integer)topI+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((Integer)botI+(Integer)topI+"");
                    }
                }
            }else if(postfix.get(i).equals("-")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((Float)botF-(Float)topF+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((Float)botF-(Integer)topI+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((Integer)botI-(Integer)topI+"");
                    }
                }
            }else if(postfix.get(i).equals("*")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((Float)botF*(Float)topF+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((Float)botF*(Integer)topI+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((Integer)botI*(Integer)topI+"");
                    }
                }
            }else if(postfix.get(i).equals("/")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((Float)botF/(Float)topF+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((Float)botF/(Integer)topI+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((Integer)botI/(Integer)topI+"");
                    }
                }
            }else if(postfix.get(i).equals("%")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((Float)botF%(Float)topF+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((Float)botF%(Integer)topI+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((Integer)botI%(Integer)topI+"");
                    }
                }
            }else if(postfix.get(i).equals("^")){
                if(st.peek().contains(".")){
                    topF=Float.parseFloat(st.pop());
                    botF=Float.parseFloat(st.pop());
                    st.push((float)(Math.pow((Float)botF,(Float)topF))+"");
                }else {
                    topI=Integer.parseInt(st.pop());
                    if(st.peek().contains(".")){
                        botF = Float.parseFloat(st.pop());
                        st.push((float)(Math.pow((Float)botF,(Integer)topI))+"");
                    }else{
                        botI = Integer.parseInt(st.pop());
                        st.push((int)(Math.pow((Integer)botI,(Integer)topI))+"");
                    }
                }
            }else{st.push(postfix.get(i));}
        }
        res=res.parseFloat(st.pop());
        return res;
    }
}