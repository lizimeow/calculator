package calculator;

import javax.swing.JFrame;
public class Main {
	public static void main(String[] args){
		Cal calculator = new Cal();        
		calculator.setLocationRelativeTo(null);    
		calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calculator.setSize(285,320);
		calculator.setVisible(true);
		calculator.setTitle("¼ÆËãÆ÷");
	}
}