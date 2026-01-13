package classe;

import classe.DataRetriever;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();

        //a) test findDishByID
        Dish dish = retriever.findDishById(1);
        System.out.println(dish.getName());
        // ingredient qui le compose à faire: System.out.println(dish.setIngredients(dish.getIngredients()));

        //test b)

        List<Ingredient> ingredientList = new ArrayList<>();

        //test de createIngredient
        System.out.println("Test de createIngredients");
       //ingredient 1
        Ingredient ing1 = new Ingredient();
        //ing1.setId();
        ing1.setName("fromage");
        ing1.setPrice(1200.0);
        ing1.setCategory(CategoryEnum.DAIRY);

        //ingredient 2
        Ingredient ing2 = new Ingredient();
        ing2.setName("oignon");
        ing2.setPrice(500.0);
        ing2.setCategory(CategoryEnum.VEGETABLE);

        //ingredient 3
        Ingredient ing3 = new Ingredient();
        ing3.setName("laitue");
        ing3.setPrice(2000.0);
        ing3.setCategory(CategoryEnum.VEGETABLE);

        //liste attendu/le type de retour pour mettre les ingredients


        //ajout des ingredients dans la liste
        ingredientList.add(ing1);

        ingredientList.add(ing2);
        ingredientList.add(ing3);
    }


}