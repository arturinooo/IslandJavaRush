package islandJR.animals;

import islandJR.Animal;
import islandJR.Cell;
import islandJR.Herbivore;

// Кабан
public class Boar extends Herbivore {
    public Boar() {
        super("Кабан");
    }

    @Override
    // Кабан травоядный, но есть мышей
    public void eat(Cell cell) {
        Animal food = null;
        double hungry = getMaxFood() - getSatiety();
        // делаем до 10 попыток поймать мышь
        for(int i=0; i<10 && hungry>0; i++) {
            try {
                // получаем случайную мышь
                food = cell.getRandom("Мышь");
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
        // если еще голодны едим растительность
        super.eat(cell);
    }
}
