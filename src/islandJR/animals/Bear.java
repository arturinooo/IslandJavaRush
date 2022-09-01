package islandJR.animals;

import islandJR.Predator;

// Медведь
public class Bear extends Predator {
    public Bear() {
        super("Медведь");
        setProbability("Удав",80);
        setProbability("Лошадь",40);
        setProbability("Олень",80);
        setProbability("Кролик",80);
        setProbability("Мышь",90);
        setProbability("Коза",70);
        setProbability("Овца",70);
    }

}
