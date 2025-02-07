import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class ExceptionHandling {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try{
            System.out.print("Enter a whole number to divide: ");
            int num1 = scanner.nextInt();
    
            System.out.print("Enter a whole number to divide by: ");
            int num2 = scanner.nextInt();
    
            int result = num1/num2;
            System.out.println("result = " + result);
        }
        catch(ArithmeticException e){
            JOptionPane.showMessageDialog(null,"Can't Divide a Number by Zero", "Error!!",JOptionPane.ERROR_MESSAGE);
        }
        catch(InputMismatchException e){
            JOptionPane.showMessageDialog(null,"You Didn't Enter a Number", "Error!!",JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"something went wrong!!", "Error!!",JOptionPane.ERROR_MESSAGE);
        }
        finally{
            scanner.close();
        }
    }
}
