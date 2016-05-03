package calculator;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack; 
import java.util.Map;
import java.util.EmptyStackException;
import java.util.HashMap; 

public class Cal extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private final String[] KEYS = {"←", "CE", "C", "±", "√" ,"7", "8", "9","(",")",  
			"4", "5", "6","/", "%","1", "2", "3", "+", "*",".","0","1/x","-",  "=" };   
	/** 初始值*/
    private boolean firstKey = true;//display是否为空
    
    private static Stack<Character> sk; 
    private static char charKey=(char)97;  
    private static Map<Character,String>  hashMap=new HashMap<Character,String>();  
    private static String result;  
    
	private JButton[] buttons ;
	JPanel commandsPanel = new JPanel();
	JTextField display ;
	private Container container;
	//菜单栏
	JMenuBar menuBar;
	JMenu watchMenu,editMenu,helpMenu;
	JMenuItem copyItem, pasteItem,normalItem,helpItem;
	
	Pattern isnum = Pattern.compile("(-?\\d+)(\\.\\d*)?|\\."); //数字或者小数点
	
	public Cal(){
		 super();
		 init();
	 }
	
	 /** 初始化*/
	 public void init(){
		 commandsPanel.setLayout(new GridLayout(5, 5, 3, 3));
		 container = getContentPane(); 
		 buttons = new JButton[KEYS.length];
	     display = new JTextField("0");
	     display.setHorizontalAlignment(JTextField.RIGHT);
	     display.setEditable(false);
	     display.setBackground(Color.WHITE);
	     display.setPreferredSize(new Dimension(0, 40));
	     
		 for(int i = 0; i < KEYS.length ; i++){
			 buttons[i] = new JButton(KEYS[i]);
			 if(isnum.matcher(KEYS[i]).matches()&& !KEYS[i].equals("."))
				 buttons[i].setBackground(new Color(192,192,192));
			 else
				 buttons[i].setBackground(Color.white);
			 buttons[i].addActionListener(this);
			 commandsPanel.add(buttons[i]);
		 }
		 createMenu();
        container.setLayout(new BorderLayout(2, 2));
        container.add("North", display);
        container.add("Center", commandsPanel);
	 }
	 public static void showErrorDialog(){
	    	JOptionPane.showMessageDialog(null, "计算式有误！");
	   }
	 void createMenu() {
		 menuBar = new JMenuBar();
		 setJMenuBar(menuBar);
		 watchMenu = new JMenu("查看 (V)"); 
		 watchMenu.setMnemonic('V');
		 menuBar.add(watchMenu);
		 normalItem = new JMenuItem("标准型 (T)");
		 normalItem.setMnemonic('T');
		 watchMenu.add(normalItem);
		 
		 editMenu = new JMenu("编辑 (E)");
		 editMenu.setMnemonic('E');
		 menuBar.add(editMenu);
		 copyItem  = new JMenuItem("复制 (Alt+C)");
		 copyItem.setMnemonic('C');
		 editMenu.add(copyItem);
		 pasteItem = new JMenuItem("粘贴 (Alt+P)");
		 pasteItem.setMnemonic('P');
		 editMenu.add(pasteItem);
		 copyItem.addActionListener(this);
		 pasteItem.addActionListener(this);
		 
		 helpMenu = new JMenu("帮助 (H)");
		 helpMenu.setMnemonic('H');
		 menuBar.add(helpMenu);
		 helpItem = new JMenuItem("关于计算器");
		 helpMenu.add(helpItem);
		 helpItem.addActionListener(this);
		}
	 
	 /** 事件处理*/
	 public void actionPerformed(ActionEvent e) {
		    /** 菜单*/
		 	if(e.getSource() == helpItem) {
			  String message,title;
			  title=new String(" 关于计算器");
			  message=new String("简易计算器 version：1.0.0");
			  int messagetype=JOptionPane.INFORMATION_MESSAGE;
			  JOptionPane.showMessageDialog(this,message,title,messagetype);
			} else if(e.getSource() == copyItem) {
				exeCopy();
			} else if(e.getSource() == pasteItem) {
				exePaste();
			} else {
		    /** 功能*/
		        String label = e.getActionCommand();
		        if (label.equals("←")) {
		            // 用户按了"←"键
		            handleBackspace();
		        } else if (label.equals("CE")) {
		            // 用户按了"CE"键
		            display.setText("0");
		            initValue();
		        } else if (label.equals("C")) {
		            // 用户按了"C"键
		        	initValue();
		        } else if (label.equals("=")) {
		        	// 用户按了"="键
		    		try{
		    			showResult();
		    		}catch (EmptyStackException e1){
		    			showErrorDialog();
		    		}
		        	firstKey = true;
		        } else if (label.equals("±")) {
		        	// 用户按了"±"键
		        	changePlusMinus();
		        } else if (label.equals("1/x")) {
		        	// 用户按了"1/x"键
		        	calReciprocal();
		        }else if (label.equals("√")) {
		        	// 用户按了"√"键
		        	exeSquareRoot();
		        }else if (label.equals("%")) {
		        	// 用户按了"%"键
		        	exePercentage();
		        }else {
		            // 用户按了数字键或者小数点键 或运算符
		            handleNumberOrOperator(label);
		        }
			}
	 }
	 
	 private void handleBackspace() {
		 String str = display.getText();
		 int len = str.length();
		 if(len > 0){
			 str = str.substring(0,len-1);
			 if(str.length() == 0)
				 initValue();
			 else
				 display.setText(str);
		 }
	 }
	 
	 private void initValue() {
		    display.setText("0");
	        firstKey = true;
	 }
	 
	 private void showResult() {
		 if(firstKey){
			 display.setText("0");
		 }
		 String tem = changeUnaryOperator();
		 String str =  numToWord(tem);
		 String infixStr = infixToPostfix(str);
		 double res = wordToNum(infixStr);
		 display.setText(res+"");
	 }
	 
	 private String changeUnaryOperator(){
		 String str = display.getText();
		 for(int i =0 ;i<str.length();i++){
			 if (str.charAt(i) == '-' && (i == 0 || isOperator(str.charAt(i-1)))){
					StringBuffer tmp = new StringBuffer(str);
					tmp.insert(i, '0');
					str = tmp.toString();
				}
				if (str.charAt(i) == '+' && (i == 0 || isOperator(str.charAt(i-1)))){
					StringBuffer tmp = new StringBuffer(str);
					tmp.insert(i, '0');
					str = tmp.toString();
				}
		 }
		 return str;
	 }
	 
	 private double getNumberFromText() {
	        double result = 0;
	        try {
	            result = Double.valueOf(display.getText()).doubleValue();
	        } catch (NumberFormatException e) {
	        }
	        return result;
	 }

	 private static boolean isOperator(char ch){
		    switch(ch){
		        case '+':
		        case '-':
		        case '*':
		        case '/':
		        case '(':
		            return true;
		        default:
		            return false;
		    }
		}
	 
	 private void changePlusMinus(){
		double result = getNumberFromText();
		result = -result;
		display.setText(result + "");
	 }
	 
	 private void calReciprocal(){
		double result = getNumberFromText();
		result = 1/result;
		display.setText(result + "");
	 }
	 
	 private void exeSquareRoot(){
		double result = getNumberFromText();
		result = Math.sqrt(result);
		display.setText(result + "");
	 }
	 
	 private void exePercentage(){
		 double result = getNumberFromText();
		 result = 100*result;
		 display.setText(result + "%");
		 firstKey = true;
	 }
	 
	 public void exeCopy(){
		Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable text = new StringSelection(display.getText());  
        clipboard.setContents(text, null);
        JOptionPane.showMessageDialog(null, "已成功复制到剪贴板！");
        
	 }
	 public void exePaste(){
		String s = "";
		Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable clipTf = clipboard.getContents(null);  
        
        if (clipTf != null) {  
            // 检查内容是否是文本类型  
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {  
                try {  
                	s = (String)clipTf.getTransferData(DataFlavor.stringFlavor);
                	if (!"".equals(s)){
                		//expression = new StringBuffer(s);
                		firstKey = true;
                		display.setText(s);
                	}
                }catch (Exception e) {  
                    e.printStackTrace();
                } 
            }
        }  
	 }

	 private void handleNumberOrOperator(String key) {
			if (firstKey){
				if(key.equals(".")){
					display.setText("0"+key);
				} else
					display.setText(key);
				firstKey = false;
			} else
				display.setText(display.getText() + key);
	 }
	 
	 private String numToWord(String str){ //用符号代替数字
		 StringBuffer sbuf = new StringBuffer();  
	     Matcher m=Pattern.compile("\\d+(\\.\\d+)?").matcher(str);  
	        while(m.find()) {  
	            String temp=m.group(0);  
	            hashMap.put(charKey,temp);  
	            m.appendReplacement(sbuf,charKey+"");  
	            charKey=(char)(charKey+1);  
	        }  
	        m.appendTail(sbuf);  
	        return sbuf.toString();  
	 }
	 
	 private String infixToPostfix(String str){ //前缀转中缀
		 sk = new Stack<Character>();  
		 result = "";
		 for(int j=0; j<str.length(); j++) {  
	            char ch=str.charAt(j);  
	            switch(ch) {  
	            case '+':  
	            case '-':  
	            case '*':  
	            case '/': 
	                gotOpe(ch);  
	                break;  
	            case '(':  
	                sk.push(ch);  
	                break;  
	            case ')':  
	                gotParen(ch);  
	                break;  
	            default :  
	                result+=ch;  
	                break;  
	            }  //end switch  
	        } //end for loop  
	        while(!sk.empty()) {  
	            result+=sk.pop();  
	        }  
	        return result;  
	 }
	 
	 private static void gotOpe(char opThis) { 
	        while(!sk.empty()) {  
	            char opTop=sk.pop();  
	            if(opTop=='(') {  
	                sk.push(opTop);  
	                break;  
	            } else {  
	                if(opThis=='*'||opThis=='/') {
	                    if(opTop=='+'||opTop=='-') {  
	                        sk.push(opTop);  
	                    } else {  
	                        result+=opTop;  
	                    }  
	                    break;  
	                } else {
	                    result+=opTop;  
	                }  
	            } //end else  
	        }//end while  
	        sk.push(opThis);  
	} 
	  
    private static void gotParen(char ch) {  
        while(!sk.empty()) {  
            char tem=sk.pop();  
            if(tem=='(')  
                break;  
            else  
                result+=tem;  
        } 
    }  
    
    private static double wordToNum(String str) {  //把数字代入
        char ch;  
        double num1,num2,answer=0;  
        String str1,str2;  
        sk=new Stack<Character>();  
        for(int j=0; j<str.length(); j++) {  
            ch=str.charAt(j);  
            if(ch>=97) { 
                sk.push(ch);  
            } else {  
                str2 = hashMap.get(sk.pop());
                str1 = hashMap.get(sk.pop());  
                num2 = Double.parseDouble(str2);  
                num1 = Double.parseDouble(str1);  
                switch(ch) {  
                case '+':  
                	answer=num1+num2;  
                    break;  
                case '-':  
                	answer=num1-num2;  
                    break;  
                case '*':  
                	answer=num1*num2;  
                    break;  
                case '/':  
                	answer=num1/num2;  
                    break; 
                default :  
                	answer=0;  
                    break;  
                } //end switch  
                hashMap.put(charKey,answer+"");  
                sk.push(charKey);  
                charKey=(char)(charKey+1);  
            } //end else  
        } //end for loop  
        str1=hashMap.get(sk.pop());  
        answer=Double.parseDouble(str1);    
        return answer;  
    }  
	    
}
