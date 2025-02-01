import java.util.InputMismatchException;
import java.util.Scanner;

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
            System.out.println("can't divide a number by '0'!");
        }
        catch(InputMismatchException e){
            System.out.println("you didn't Enter a number!");
        }
        catch(Exception e){
            System.out.println("something went wrong!!");
        }
        finally{
            scanner.close();
        }
    }
}
