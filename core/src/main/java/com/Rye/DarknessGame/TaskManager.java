package com.Rye.DarknessGame;
import com.Rye.DarknessGame.taskLibrary.Task;
import com.Rye.DarknessGame.taskLibrary.*;

public class TaskManager {

    Player player;
    public TaskManager(Player player) {
        this.player = player;
        initTasks();
    }

    public void updateTasks(){

    }
    public void initTasks(){
        repairReceiving firstTask = new repairReceiving("IntroTask",player);
    }
}
