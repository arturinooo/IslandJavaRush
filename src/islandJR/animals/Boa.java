package islandJR.animals;

import islandJR.Predator;

// Удав
public class Boa extends Predator {
    public Boa() {
        super("Удав");
        setProbability("Лиса",15);
        setProbability("Кролик",20);
        setProbability("Мышь",40);
    }

}
