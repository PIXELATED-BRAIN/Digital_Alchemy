package polymorphism;

public class App {

    public static void main(String[] args) {   

        Luffy luffy = new Luffy();
        Zoro zoro = new Zoro();
        Sanji sanji = new Sanji();

        Attack[] combinedAttack = {luffy,zoro,sanji};

        for(Attack x : combinedAttack){
            
            x.specialMove();

        }
    }
}
