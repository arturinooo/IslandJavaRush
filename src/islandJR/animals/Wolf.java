package islandJR.animals;

import islandJR.Predator;

// Волк
public class Wolf extends Predator {
    public Wolf() {
        super("Волк");
        setProbability("Лошадь",20);
        setProbability("Олень",15);
        setProbability("Кролик",60);
        setProbability("Мышь",80);
        setProbability("Коза",60);
        setProbability("Овца",70);
        setProbability("Кабан",10);
    }

}
