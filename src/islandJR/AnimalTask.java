package islandJR;

// жизненный цикл животного
public class AnimalTask implements Runnable {
    private Animal animal;
    private Cell cell;

    public AnimalTask(Animal animal, Cell cell) {
        this.animal = animal;
        this.cell = cell;
    }

    @Override
    public void run() {
        try {
            // блокируем доступ к животному
            animal.lock();
            // обновляем уровень сытости и проверяем не умерло ли животное от голода
            if(!animal.hungry()) animal.setDead(true);
            if(animal.isDead()) {
                return;
            }

            animal.live(cell);

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            // разблокируем доступ
            animal.unlock();
        }
    }
}
