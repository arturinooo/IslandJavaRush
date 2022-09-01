package islandJR;

// класс для травоядных животных
abstract public class Herbivore extends Animal{
    public Herbivore(String name) {
        super(name);
    }

    @Override
    // Травоядные поедают растительность столько насколько голодны
    public void eat(Cell cell) {
        // вычисляем сколько животное может съесть
        double hungry = getMaxFood() - getSatiety();
        // увеличиваем сытоность на столько сколько получилось съесть
        setSatiety(getSatiety()+cell.eatGrass(hungry));
    }

}
