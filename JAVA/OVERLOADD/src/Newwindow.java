package OVERLOADD.src;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Newwindow {

    JFrame Frame = new JFrame();
    JLabel Lable = new JLabel();

    Newwindow(){

        Lable.setBounds(0,0,100,50);
        Lable.setFont(new Font(null,Font.PLAIN,0));

        Frame.add(Lable);

        Frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Frame.setSize(420,420);
        Frame.setLayout(null);
        //Frame.setVisible(true);
        Frame.getContentPane().setBackground(new Color(0,0,0));

        for(int i=0; i<=666;){
            System.out.println(i);
        }
    }

}
