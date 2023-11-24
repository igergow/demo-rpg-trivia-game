import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добре дошли в играта!");
        System.out.println("Въведете име на герой: ");
        Scanner scanner = new Scanner(System.in);
        String heroName = scanner.next();
        Hero hero = new Hero(heroName);
        GameServer gameServer = new GameServer(hero);
        gameServer.start();
    }
}