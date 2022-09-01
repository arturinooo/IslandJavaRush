package islandJR;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

// класс для хищных животных
abstract public class Predator extends Animal{

    // вероятности съесть другое животное
    private HashMap<String, Integer> hmProb = new HashMap<>();

    // конструктор
    public Predator(String name) {
        super(name);
    }

    // вероятность съесть животное name
    public int getProbability(String name){
        return hmProb.getOrDefault(name,0);
    }

    // задать вероятность съесть животное name
    protected void setProbability(String name, int value){
        hmProb.put(name, value);
    }

    // список потенциальной еды
    protected String[] getFoodList(){
        return hmProb.keySet().toArray(new String[0]);
    }

    // алгоритм питания
    public void eat(Cell cell) {
        // сколько может съесть
        double hungry = getMaxFood() - getSatiety();
        Animal food = null;
        // список потенциальной еды
        String[] foodList = getFoodList();

        // счетчик попыток
        int attempts = 0;

        do {
            try {
                // находим случайное животное из списка еды
                food = cell.getRandom(foodList);

                if (food != null) {
                    int prb = getProbability(food.getName());
                    // проверяем вероятность съесть
                    boolean res = (ThreadLocalRandom.current().nextInt(100) < prb);
                    if (res) // удачная охота
                    {
                        food.setDead(true); // отмечаем что жертва мертва
                        double weight = food.getWeight();
                        double satiety = getSatiety() + (Math.min(hungry, weight));
                        // обновляем значение сытости
                        setSatiety(satiety);
                        hungry = getMaxFood() - satiety;
                    }
                } else {
                    //System.out.println(getName()+" eat Failed");
                }
                attempts++;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // разблокируем объект жертвы
                if (food != null) food.unlock();
            }
            // если еще голодны то охотимся еще до 3 раз
        }while(attempts<3 && hungry>0);
    }

}
