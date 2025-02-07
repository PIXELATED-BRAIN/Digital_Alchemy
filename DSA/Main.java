import java.util.Stack;

public class Main{
    public static void main(String[] args) {
        
        Stack<String> game = new Stack<String>();

        //LIFO STRUCTURE

        game.push("SEKIRO");
        game.push("ELDEN RING");
        game.push("SUPER HOT");
        game.push("FAR CRY 3");

        System.out.println(game.empty());

        System.out.println(game);

        //String myFavGame = game.pop();

        //System.out.println(myFavGame);
        //System.out.println(game.peek());
        //System.out.println(game.search("WITCHER"));
    }
}
