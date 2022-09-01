package islandJR.animals;

import islandJR.Animal;
import islandJR.Cell;
import islandJR.Herbivore;

// Утка
public class Duck extends Herbivore {
    public Duck() {
        super("Утка");
    }

    @Override
    // Утка травоядная но ест гусениц
    public void eat(Cell cell) {
        Animal food = null;
        double hungry = getMaxFood() - getSatiety();
        // делаем до 10 попыток найти гусеницу
        for(int i=0; i<10 && hungry>0; i++) {
            try {
                food = cell.getRandom("Гусеница");
                if (food != null) {
                    food.setDead(true);

                    double weight = food.getWeight();
                    double satiety = getSatiety() + ((hungry > weight) ? weight : hungry);
                    setSatiety(satiety);
                    hungry = getMaxFood() - satiety;

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // разблокируем добычу
                if (food != null) food.unlock();
            }
        }
        super.eat(cell);
    }
}
