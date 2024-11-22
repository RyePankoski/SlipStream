package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;

public class LightingManager {

    StaticLightSource staticLightSource;
    ArrayList<StaticLightSource> staticLightSources;

    Pixmap pixmap;

    public LightingManager(Pixmap pixmap){
        this.pixmap = pixmap;
        instantiateLights();
    }

    public void instantiateLights(){
        staticLightSources = new ArrayList<>();
        StaticLightSource stationLight1 = new StaticLightSource(4930, 3200, .5f, MathFunctions.rayCast(200, 181, 90, 3200, 4930, 5, pixmap));
        StaticLightSource stationLight2 = new StaticLightSource(4930, 4800, .5f, MathFunctions.rayCast(200, 181, 90, 4800, 4930, 5,pixmap));
        StaticLightSource stationLight3 = new StaticLightSource(5050, 9150, .5f, MathFunctions.rayCast(250, 181, 90, 9150, 5050, 5,pixmap));
        StaticLightSource stationLight4 = new StaticLightSource(4930, 12900, .5f, MathFunctions.rayCast(200, 181, 90, 12900, 4930,5, pixmap));

        staticLightSources.add(stationLight1);
        staticLightSources.add(stationLight2);
        staticLightSources.add(stationLight3);
        staticLightSources.add(stationLight4);
    }

    public ArrayList<StaticLightSource> getStaticLightSources() {
        return staticLightSources;
    }
}
