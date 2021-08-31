import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Main {

    static Main obj = new Main();

    public static void main(String[] args) {
        List<String> ingredients = Arrays.asList(
                "flour",
                "salt",
                "baking powder",
                "butter",
                "eggs",
                "milk");

        System.out.println("Using forEach loop.");
        for (String ingredient : ingredients) {
            System.out.println(ingredient);
        }

        System.out.println("Using anonymous class.");
        ingredients.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

        System.out.println("Using lambda with type declaration.");
        ingredients.forEach((String ingredient) -> {
            System.out.println(ingredient);
        });

        System.out.println("Using lambda without type declaration.");
        ingredients.forEach((ingredient) -> {
            System.out.println(ingredient);
        });

        System.out.println("Using lambda compact syntax.");
        ingredients.forEach(s ->
                System.out.println(s)
        );

        System.out.println("Using lambda compact syntax. Taking out consumer");
        Consumer<String> printer = s -> System.out.println(s);
        ingredients.forEach(printer);

        System.out.println("Using lambda with function reference.");
        ingredients.forEach(System.out::println);

        System.out.println("Using function reference with function declaration outside.");
        Consumer<String> printerFunctionReference = System.out::println;
        ingredients.forEach(printerFunctionReference);

        yellOut("yellout static method");
        ingredients.forEach(Main::yellOut);

        System.out.println("Just like sysout.");
        ingredients.forEach(Main.obj::doubleYell);

        yellOut(null);
    }


    private static void yellOut(String word) {
        Objects.requireNonNull(word, ()-> "Created issue: "+createIssue());
        System.out.printf("%s! %n", word.toUpperCase(Locale.ROOT));
    }

    private static String createIssue() {

        System.out.println("Making new issue request...");
        int issueId = ThreadLocalRandom.current().nextInt();
        return "#" + issueId;
    }

    public void doubleYell(String word) {
        System.out.printf("%s!! %n", word.toUpperCase(Locale.ROOT));
    }
}
