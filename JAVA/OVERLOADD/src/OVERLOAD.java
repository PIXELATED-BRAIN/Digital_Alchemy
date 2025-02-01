package OVERLOADD.src;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class OVERLOAD implements ActionListener {

    JFrame Frame = new JFrame();
    JButton Button = new JButton("DON'T CLICK");

    OVERLOAD(){

        Button.setBounds(130, 160, 160, 40);
        Button.setFocusable(false);
        Button.addActionListener(this);
        Button.setForeground(new Color(0x00FF00));
        Button.setBackground(Color.BLACK);

        Frame.add(Button);

        Frame.setTitle("OVERRLOADD");
        Frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Frame.setSize(444,444);
        Frame.setLayout(null);
        Frame.setVisible(true);
        Frame.getContentPane().setBackground(new Color(255,25,25));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==Button);
            //Frame.dispose();
        @SuppressWarnings("unused")
        Newwindow newwindow = new Newwindow();

        for(int i=0; i<=666;){
            System.out.println(i);
        } 
    }

}
