
import javax.swing.JOptionPane;

public class dialogBox {
    public static void main(String[] args) {
       
        //JOptionPane.showMessageDialog(null,"GENERATING A VIRUS", "title",JOptionPane.QUESTION_MESSAGE);
        //JOptionPane.showMessageDialog(null,"GENERATING A VIRUS", "title",JOptionPane.INFORMATION_MESSAGE);
        //JOptionPane.showMessageDialog(null,"GENERATING A VIRUS", "title",JOptionPane.ERROR_MESSAGE);

       /*  while (true) {
            JOptionPane.showMessageDialog(null,"GENERATING A VIRUS", "title",JOptionPane.WARNING_MESSAGE);
        } */

        int Answer = JOptionPane.showConfirmDialog(null,"CHOSE WISELY", "titie", JOptionPane.YES_NO_OPTION);

        if (Answer == 0) {
            for(int p=0; p<=1;){
                System.out.println(p);
            }
        }else if (Answer == 1){
            for(int p=0; p<=1;){
                System.out.println(p);
            } 
        }else{
            System.out.println(JOptionPane.showConfirmDialog(null,"Why Bothe Open It Then STUPID!!", "titie", JOptionPane.PLAIN_MESSAGE));
        }
    }    
}
