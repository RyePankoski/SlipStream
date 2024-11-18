package com.Rye.DarknessGame;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;



public class RoomManager {
    Texture image;
    ArrayList<Room> rooms;
    public RoomManager(){
        rooms = new ArrayList<Room>();
    }
    public void addRoom(Room room){
        rooms.add(room);
    }
    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
