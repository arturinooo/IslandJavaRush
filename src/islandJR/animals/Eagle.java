package islandJR.animals;

import islandJR.Predator;

// Орел
public class Eagle extends Predator {
    public Eagle() {
        super("Орел");
        setProbability("Лиса",10);
        setProbability("Кролик",90);
        setProbability("Мышь",90);
    }

}
