package islandJR;

// класс с основными харектеристиками животного
public class Info {
    private double weight;          // вес
    private int maxCount, speed;    // максимальное количество на клетке, скорость передвижения
    private double maxFood;         // максимальный вес пищи
    private int childs;             // максимальное число детей в приплоде
    private int initCount;          // начальное количество животных в клетке случайное от 0 до initCount
    private int probMove;           // вероятность что животное отправится на другие клетки

    // конструкторы

    public Info() {
    }

    public Info(double weight, int maxCount, int speed, double maxFood, int childs, int initCount, int probMove) {
        this.weight = weight;
        this.maxCount = maxCount;
        this.speed = speed;
        this.maxFood = maxFood;
        this.childs = childs;
        this.initCount = initCount;
        this.probMove = probMove;
    }

    // геттеры и сеттеры

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getMaxFood() {
        return maxFood;
    }

    public void setMaxFood(double maxFood) {
        this.maxFood = maxFood;
    }

    public int getChilds() {
        return childs;
    }

    public void setChilds(int childs) {
        this.childs = childs;
    }

    public int getInitCount() {
        return initCount;
    }

    public void setInitCount(int initCount) {
        this.initCount = initCount;
    }

    public int getProbMove() {
        return probMove;
    }

    public void setProbMove(int probMove) {
        this.probMove = probMove;
    }

    @Override
    public String toString() {
        return "Info{" +
                "weight=" + weight +
                ", maxCount=" + maxCount +
                ", speed=" + speed +
                ", maxFood=" + maxFood +
                ", childs=" + childs +
                ", initCount=" + initCount +
                ", probMove=" + probMove +
                '}';
    }
}
