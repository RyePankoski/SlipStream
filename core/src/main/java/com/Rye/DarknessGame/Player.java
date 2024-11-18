package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;


import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

import static java.lang.Math.sqrt;

public class Player {

    //region Variables
// **Booleans**
    boolean sprint;
    boolean staminaPause;
    boolean staminaRegen;
    boolean canChangeGun = true;

    // **Coordinates and Positioning**
    private float coorX;
    private float coorY;
    private float faceX;
    private float faceY;
    float dx;
    float dy;
    float delX;
    float delY;
    float camX;
    float camY;

    // **Camera and Viewport**
    OrthographicCamera camera;
    float cameraZoom;

    // **Speed, Angles, and Movement Factors**
    public float speed;
    float facingAngle;
    float factor = 2.5f;

    // **Room Dimensions and Overlaps**
    int roomWidth;
    int roomHeight;

    // **Stamina and Timing Control**
    private double stamina = 100;
    double waitUntil;
    private double haltUntil;

    // **Weapon and Shooting Mechanics**
    String equippedWeaponName;
    Weapon equippedWeapon;
    SubMachineGun smg;
    Rifle rifle;
    ArrayList<Bullet> bullets;
    double timeTillChange;

    // **Sound Effects**
    private final Sound running;
    private final Sound walking;
    Sound changeGun;
    SoundPlayer soundManager;

    // **Input Handling**
    InputHandler handler;
    Set<Integer> keyStrokes;
    Set<Integer> pressedMouseHash;

    // **Graphics and Rendering**
    private final Sprite playerSprite;
    private final SpriteBatch spriteBatch;
    BitmapFont bitmapFont;
    ShapeRenderer shapeRenderer;
    Pixmap pixmap;
    Color white;

    // **Collision and Vectors**
    double[] pointInFrontVector;
    CollisionMask collisionMask;

    // **HUD and UI Elements**
    Hud hud;

    Monster monster;
    double monsterDistance;

    Sound meleeHit;
    Sound meleeMiss;
    private boolean canMelee = true;
    double timeTillMelee;
    private final Vector2 tempVector = new Vector2();

    boolean flashLightIsOn = true;
    private boolean canFlashLight;
    double timeTillCanFlash;
    Sound flashLightSound;
    double flashlightBattery;
    boolean justFired;
    double timeTillNotFired;
    public Main main;
    Music flashLightWarning;

    public float[] shapeVerticies;

    int flashLightAngle;

    //endregion
    public Player(int x, int y, int speed, SoundPlayer soundManager, InputHandler handler, Hud hud,
                  CollisionMask collisionMask, Monster monster, Main main) {

        this.main = main;
        this.handler = handler;
        this.soundManager = soundManager;
        this.coorX = x;
        this.coorY = y;
        this.speed = speed;
        this.hud = hud;
        this.collisionMask = collisionMask;
        this.monster = monster;

        cameraZoom = 3000;
        double camDimY = cameraZoom * .5625;
        roomWidth = 5000;
        roomHeight = 5000;
        flashlightBattery = 100;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraZoom, cameraZoom);

        //region Sounds init
        running = Gdx.audio.newSound(Gdx.files.internal("Running.mp3"));
        walking = Gdx.audio.newSound(Gdx.files.internal("Walking.mp3"));
        changeGun = Gdx.audio.newSound(Gdx.files.internal("changeGuns.mp3"));
        meleeHit = Gdx.audio.newSound(Gdx.files.internal("meleeSound.mp3"));
        meleeMiss = Gdx.audio.newSound(Gdx.files.internal("meleeMiss.mp3"));
        flashLightSound = Gdx.audio.newSound((Gdx.files.internal("flashLightSound.mp3")));
        flashLightWarning = Gdx.audio.newMusic((Gdx.files.internal("flashlightWarning.mp3")));
        //endregion
        //region SMG init
        ArrayList<Sound> smgSounds = new ArrayList<>();
        smgSounds.add(Gdx.audio.newSound(Gdx.files.internal("smgShot.mp3")));
        smgSounds.add(Gdx.audio.newSound(Gdx.files.internal("smgReload.mp3")));
        smgSounds.add(Gdx.audio.newSound(Gdx.files.internal("emptyGun.mp3")));
        //endregion
        //region RIFLE init
        ArrayList<Sound> rifleSounds = new ArrayList<>();
        rifleSounds.add(Gdx.audio.newSound((Gdx.files.internal("rifleShot.mp3"))));
        rifleSounds.add(Gdx.audio.newSound(Gdx.files.internal("rifleReload.mp3")));
        rifleSounds.add(Gdx.audio.newSound(Gdx.files.internal("emptyGun.mp3")));
        //endregion


        smg = new SubMachineGun(0.11f, 3, 35, 6, smgSounds, camera, this, hud, 60, this.monster, 150, 3);
        rifle = new Rifle(2, 15, 1, 25, rifleSounds, camera, this, hud, 120, this.monster, 300, 15);

        bullets = new ArrayList<>();

        equippedWeaponName = "SMG";
        equippedWeapon = smg;
        hud.updatePlayerStats(stamina, equippedWeaponName, flashlightBattery);
        hud.updateWeaponStats(equippedWeapon.getAmmo(), equippedWeapon.getMagazines(), equippedWeapon.getMagazineSize(), equippedWeapon.maxMagazines);

        Texture playerTexture = new Texture(Gdx.files.internal("PlayerChar.png"));

        pixmap = collisionMask.getPixmap();

        pointInFrontVector = new double[2];

        flashLightAngle = 20;
        shapeVerticies = new float[flashLightAngle * 4];

        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        playerSprite = new Sprite(playerTexture);
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);


    }

    public void updatePlayer() {

        keyStrokes = handler.pressedKeysHash;
        pressedMouseHash = handler.getPressedMouseHash();
        keyStrokes = handler.getPressedKeysHash();

        if (System.nanoTime() >= haltUntil) {
            staminaRegen = true;
        }

        if (System.nanoTime() >= timeTillNotFired) {
            justFired = false;
        }

        move();
        rayCast();
        facingAngle();
        updateCamera();
        drawMyself();
        weapon();
        melee();
        flashLight();
        distanceToMonster();
        pointInFront();

        hud.updatePlayerStats(stamina, equippedWeaponName, flashlightBattery);
        hud.updateWeaponStats(equippedWeapon.getAmmo(), equippedWeapon.getMagazines(), equippedWeapon.getMagazineSize(), equippedWeapon.maxMagazines);
    }

    public void distanceToMonster() {
        double x1 = coorX;
        double y1 = coorY;
        double x2 = monster.getCoorX();
        double y2 = monster.getCoorY();

        double newX = x2 - x1;
        double newY = y2 - y1;
        monsterDistance = Math.sqrt((newX * newX) + (newY * newY));
    }

    public void pointInFront() {
        tempVector.set(faceX - coorX, faceY - coorY);
        float distance = tempVector.len();

        if (distance != 0) {
            tempVector.scl(200f / distance);
            pointInFrontVector[0] = coorX + tempVector.x;
            pointInFrontVector[1] = coorY + tempVector.y;
        }
    }

    public void rayCast() {


        double[] cosines = {1.0, 0.9998476951563913, 0.9993908270190958, 0.9986295347545738, 0.9975640502598242, 0.9961946980917455, 0.9945218953682733, 0.992546151641322, 0.9902680687415704, 0.9876883405951378, 0.984807753012208, 0.981627183447664, 0.9781476007338057, 0.9743700647852352, 0.9702957262759965, 0.9659258262890683, 0.9612616959383189, 0.9563047559630355, 0.9510565162951535, 0.9455185755993168, 0.9396926207859084, 0.9335804264972017, 0.9271838545667874, 0.9205048534524404, 0.9135454576426009, 0.9063077870366499, 0.898794046299167, 0.8910065241883679, 0.882947592858927, 0.8746197071393957, 0.8660254037844387, 0.8571673007021123, 0.848048096156426, 0.838670567945424, 0.8290375725550417, 0.8191520442889918, 0.8090169943749475, 0.7986355100472928, 0.7880107536067219, 0.7771459614569709, 0.766044443118978, 0.754709580222772, 0.7431448254773942, 0.7313537016191705, 0.7193398003386512, 0.7071067811865476, 0.6946583704589974, 0.6819983600624985, 0.6691306063588582, 0.6560590289905074, 0.6427876096865394, 0.6293203910498375, 0.6156614753256583, 0.6018150231520484, 0.5877852522924731, 0.5735764363510462, 0.5591929034707468, 0.5446390350150272, 0.5299192642332049, 0.5150380749100544, 0.5000000000000001, 0.4848096202463371, 0.46947156278589086, 0.4539904997395468, 0.43837114678907746, 0.42261826174069944, 0.4067366430758004, 0.3907311284892737, 0.3746065934159122, 0.35836794954530016, 0.3420201433256688, 0.32556815445715676, 0.30901699437494745, 0.29237170472273677, 0.27563735581699916, 0.25881904510252074, 0.24192189559966767, 0.22495105434386514, 0.20791169081775923, 0.19080899537654492, 0.17364817766693041, 0.15643446504023092, 0.13917310096006547, 0.12186934340514749, 0.10452846326765346, 0.08715574274765836, 0.06975647374412523, 0.052335956242943966, 0.03489949670250108, 0.0174524064372836, 6.123233995736766E-17, -0.017452406437283477, -0.03489949670250073, -0.05233595624294384, -0.06975647374412533, -0.08715574274765824, -0.10452846326765333, -0.12186934340514737, -0.13917310096006513, -0.15643446504023104, -0.1736481776669303, -0.1908089953765448, -0.20791169081775912, -0.2249510543438648, -0.24192189559966756, -0.25881904510252085, -0.27563735581699905, -0.29237170472273666, -0.30901699437494734, -0.3255681544571564, -0.3420201433256687, -0.35836794954530027, -0.37460659341591207, -0.3907311284892736, -0.40673664307580004, -0.42261826174069933, -0.4383711467890775, -0.4539904997395467, -0.46947156278589053, -0.484809620246337, -0.4999999999999998, -0.5150380749100543, -0.5299192642332048, -0.5446390350150271, -0.5591929034707467, -0.5735764363510458, -0.587785252292473, -0.6018150231520484, -0.6156614753256583, -0.6293203910498373, -0.6427876096865394, -0.6560590289905072, -0.6691306063588579, -0.6819983600624984, -0.6946583704589974, -0.7071067811865475, -0.719339800338651, -0.7313537016191705, -0.7431448254773944, -0.754709580222772, -0.7660444431189779, -0.7771459614569709, -0.7880107536067219, -0.7986355100472926, -0.8090169943749473, -0.8191520442889919, -0.8290375725550416, -0.8386705679454239, -0.848048096156426, -0.8571673007021122, -0.8660254037844387, -0.8746197071393957, -0.882947592858927, -0.8910065241883678, -0.8987940462991668, -0.9063077870366499, -0.913545457642601, -0.9205048534524404, -0.9271838545667873, -0.9335804264972016, -0.9396926207859083, -0.9455185755993168, -0.9510565162951535, -0.9563047559630354, -0.9612616959383189, -0.9659258262890682, -0.9702957262759965, -0.9743700647852351, -0.9781476007338057, -0.981627183447664, -0.984807753012208, -0.9876883405951377, -0.9902680687415704, -0.992546151641322, -0.9945218953682733, -0.9961946980917455, -0.9975640502598242, -0.9986295347545738, -0.9993908270190958, -0.9998476951563913, -1.0, -0.9998476951563913, -0.9993908270190958, -0.9986295347545738, -0.9975640502598243, -0.9961946980917455, -0.9945218953682733, -0.992546151641322, -0.9902680687415703, -0.9876883405951378, -0.984807753012208, -0.981627183447664, -0.9781476007338057, -0.9743700647852352, -0.9702957262759965, -0.9659258262890684, -0.961261695938319, -0.9563047559630354, -0.9510565162951535, -0.9455185755993167, -0.9396926207859084, -0.9335804264972017, -0.9271838545667874, -0.9205048534524404, -0.9135454576426011, -0.90630778703665, -0.8987940462991671, -0.8910065241883681, -0.8829475928589271, -0.8746197071393956, -0.8660254037844386, -0.8571673007021123, -0.8480480961564261, -0.838670567945424, -0.8290375725550418, -0.819152044288992, -0.8090169943749476, -0.798635510047293, -0.7880107536067222, -0.777145961456971, -0.766044443118978, -0.7547095802227719, -0.7431448254773942, -0.7313537016191706, -0.7193398003386511, -0.7071067811865477, -0.6946583704589976, -0.6819983600624986, -0.6691306063588585, -0.6560590289905076, -0.6427876096865395, -0.6293203910498372, -0.6156614753256581, -0.6018150231520483, -0.5877852522924732, -0.5735764363510464, -0.5591929034707472, -0.544639035015027, -0.529919264233205, -0.5150380749100545, -0.5000000000000004, -0.4848096202463376, -0.46947156278589075, -0.4539904997395469, -0.43837114678907774, -0.42261826174069916, -0.4067366430758001, -0.3907311284892738, -0.3746065934159123, -0.3583679495453007, -0.3420201433256694, -0.32556815445715664, -0.30901699437494756, -0.2923717047227371, -0.2756373558169989, -0.25881904510252063, -0.24192189559966779, -0.22495105434386525, -0.2079116908177598, -0.1908089953765446, -0.17364817766693033, -0.15643446504023104, -0.13917310096006583, -0.12186934340514805, -0.10452846326765423, -0.08715574274765825, -0.06975647374412558, -0.052335956242943425, -0.03489949670250076, -0.017452406437283498, -1.8369701987210297E-16, 0.01745240643728313, 0.03489949670250039, 0.05233595624294306, 0.06975647374412522, 0.08715574274765789, 0.10452846326765387, 0.12186934340514768, 0.13917310096006547, 0.15643446504023067, 0.17364817766692997, 0.19080899537654425, 0.20791169081775943, 0.22495105434386492, 0.24192189559966745, 0.2588190451025203, 0.2756373558169985, 0.2923717047227367, 0.30901699437494723, 0.3255681544571563, 0.342020143325669, 0.3583679495453004, 0.37460659341591196, 0.3907311284892735, 0.40673664307579976, 0.42261826174069883, 0.4383711467890774, 0.45399049973954664, 0.4694715627858904, 0.4848096202463373, 0.5000000000000001, 0.5150380749100542, 0.5299192642332047, 0.5446390350150266, 0.559192903470747, 0.573576436351046, 0.5877852522924729, 0.6018150231520479, 0.6156614753256578, 0.6293203910498368, 0.6427876096865393, 0.656059028990507, 0.6691306063588585, 0.6819983600624986, 0.6946583704589973, 0.7071067811865474, 0.7193398003386509, 0.73135370161917, 0.7431448254773937, 0.7547095802227719, 0.7660444431189778, 0.7771459614569711, 0.788010753606722, 0.7986355100472928, 0.8090169943749473, 0.8191520442889916, 0.8290375725550414, 0.838670567945424, 0.848048096156426, 0.8571673007021121, 0.8660254037844384, 0.8746197071393954, 0.8829475928589269, 0.8910065241883678, 0.8987940462991668, 0.90630778703665, 0.913545457642601, 0.9205048534524403, 0.9271838545667873, 0.9335804264972015, 0.9396926207859081, 0.9455185755993168, 0.9510565162951535, 0.9563047559630353, 0.9612616959383189, 0.9659258262890683, 0.9702957262759965, 0.9743700647852351, 0.9781476007338056, 0.981627183447664, 0.984807753012208, 0.9876883405951377, 0.9902680687415703, 0.992546151641322, 0.9945218953682733, 0.9961946980917455, 0.9975640502598242, 0.9986295347545739, 0.9993908270190958, 0.9998476951563913,
        };
        double[] sins = {0.0, 0.01745240643728351, 0.03489949670250097, 0.05233595624294383, 0.0697564737441253, 0.08715574274765817, 0.10452846326765346, 0.12186934340514748, 0.13917310096006544, 0.15643446504023087, 0.17364817766693033, 0.1908089953765448, 0.20791169081775931, 0.22495105434386498, 0.24192189559966773, 0.25881904510252074, 0.27563735581699916, 0.2923717047227367, 0.3090169943749474, 0.3255681544571567, 0.3420201433256687, 0.35836794954530027, 0.374606593415912, 0.3907311284892737, 0.40673664307580015, 0.42261826174069944, 0.4383711467890774, 0.45399049973954675, 0.4694715627858908, 0.48480962024633706, 0.49999999999999994, 0.5150380749100542, 0.5299192642332049, 0.544639035015027, 0.5591929034707468, 0.573576436351046, 0.5877852522924731, 0.6018150231520483, 0.6156614753256583, 0.6293203910498375, 0.6427876096865393, 0.6560590289905073, 0.6691306063588582, 0.6819983600624985, 0.6946583704589973, 0.7071067811865475, 0.7193398003386511, 0.7313537016191705, 0.7431448254773941, 0.7547095802227719, 0.766044443118978, 0.7771459614569708, 0.7880107536067219, 0.7986355100472928, 0.8090169943749475, 0.8191520442889918, 0.8290375725550417, 0.8386705679454239, 0.848048096156426, 0.8571673007021122, 0.8660254037844386, 0.8746197071393957, 0.8829475928589269, 0.8910065241883678, 0.898794046299167, 0.9063077870366499, 0.9135454576426009, 0.9205048534524404, 0.9271838545667873, 0.9335804264972017, 0.9396926207859083, 0.9455185755993167, 0.9510565162951535, 0.9563047559630354, 0.9612616959383189, 0.9659258262890683, 0.9702957262759965, 0.9743700647852352, 0.9781476007338057, 0.981627183447664, 0.984807753012208, 0.9876883405951378, 0.9902680687415704, 0.992546151641322, 0.9945218953682733, 0.9961946980917455, 0.9975640502598242, 0.9986295347545738, 0.9993908270190958, 0.9998476951563913, 1.0, 0.9998476951563913, 0.9993908270190958, 0.9986295347545738, 0.9975640502598242, 0.9961946980917455, 0.9945218953682734, 0.9925461516413221, 0.9902680687415704, 0.9876883405951377, 0.984807753012208, 0.981627183447664, 0.9781476007338057, 0.9743700647852352, 0.9702957262759965, 0.9659258262890683, 0.9612616959383189, 0.9563047559630355, 0.9510565162951536, 0.9455185755993168, 0.9396926207859084, 0.9335804264972017, 0.9271838545667874, 0.9205048534524404, 0.913545457642601, 0.90630778703665, 0.8987940462991669, 0.8910065241883679, 0.8829475928589271, 0.8746197071393959, 0.8660254037844387, 0.8571673007021123, 0.8480480961564261, 0.8386705679454239, 0.8290375725550417, 0.819152044288992, 0.8090169943749475, 0.7986355100472927, 0.788010753606722, 0.777145961456971, 0.766044443118978, 0.7547095802227721, 0.7431448254773945, 0.7313537016191706, 0.7193398003386511, 0.7071067811865476, 0.6946583704589975, 0.6819983600624986, 0.669130606358858, 0.6560590289905073, 0.6427876096865395, 0.6293203910498374, 0.6156614753256584, 0.6018150231520486, 0.5877852522924732, 0.5735764363510459, 0.5591929034707469, 0.5446390350150273, 0.5299192642332049, 0.5150380749100544, 0.49999999999999994, 0.48480962024633717, 0.4694715627858907, 0.45399049973954686, 0.4383711467890777, 0.4226182617406995, 0.40673664307580004, 0.39073112848927377, 0.37460659341591224, 0.35836794954530066, 0.3420201433256689, 0.3255681544571566, 0.3090169943749475, 0.29237170472273705, 0.2756373558169992, 0.258819045102521, 0.24192189559966773, 0.2249510543438652, 0.20791169081775931, 0.19080899537654497, 0.1736481776669307, 0.15643446504023098, 0.13917310096006533, 0.12186934340514755, 0.10452846326765373, 0.0871557427476582, 0.06975647374412552, 0.05233595624294425, 0.03489949670250114, 0.01745240643728344, 1.2246467991473532E-16, -0.017452406437283192, -0.0348994967025009, -0.05233595624294356, -0.06975647374412483, -0.08715574274765794, -0.1045284632676535, -0.12186934340514774, -0.13917310096006552, -0.15643446504023073, -0.17364817766693047, -0.19080899537654472, -0.20791169081775907, -0.22495105434386498, -0.2419218955996675, -0.25881904510252035, -0.2756373558169986, -0.29237170472273677, -0.30901699437494773, -0.32556815445715676, -0.34202014332566866, -0.35836794954530043, -0.374606593415912, -0.39073112848927355, -0.4067366430757998, -0.4226182617406993, -0.43837114678907707, -0.45399049973954625, -0.4694715627858905, -0.48480962024633734, -0.5000000000000001, -0.5150380749100542, -0.5299192642332048, -0.5446390350150271, -0.5591929034707467, -0.5735764363510458, -0.587785252292473, -0.601815023152048, -0.6156614753256578, -0.6293203910498372, -0.6427876096865393, -0.6560590289905074, -0.6691306063588582, -0.6819983600624984, -0.6946583704589974, -0.7071067811865475, -0.7193398003386509, -0.7313537016191705, -0.743144825477394, -0.7547095802227717, -0.7660444431189779, -0.7771459614569711, -0.7880107536067221, -0.7986355100472928, -0.8090169943749473, -0.8191520442889916, -0.8290375725550414, -0.838670567945424, -0.848048096156426, -0.8571673007021121, -0.8660254037844384, -0.8746197071393955, -0.882947592858927, -0.8910065241883678, -0.8987940462991668, -0.90630778703665, -0.913545457642601, -0.9205048534524403, -0.9271838545667873, -0.9335804264972016, -0.9396926207859082, -0.9455185755993168, -0.9510565162951535, -0.9563047559630353, -0.961261695938319, -0.9659258262890683, -0.9702957262759965, -0.9743700647852351, -0.9781476007338056, -0.981627183447664, -0.984807753012208, -0.9876883405951377, -0.9902680687415703, -0.992546151641322, -0.9945218953682733, -0.9961946980917455, -0.9975640502598242, -0.9986295347545739, -0.9993908270190958, -0.9998476951563913, -1.0, -0.9998476951563913, -0.9993908270190958, -0.9986295347545739, -0.9975640502598243, -0.9961946980917455, -0.9945218953682733, -0.992546151641322, -0.9902680687415704, -0.9876883405951378, -0.9848077530122081, -0.9816271834476641, -0.9781476007338056, -0.9743700647852352, -0.9702957262759966, -0.9659258262890684, -0.961261695938319, -0.9563047559630354, -0.9510565162951536, -0.945518575599317, -0.9396926207859083, -0.9335804264972017, -0.9271838545667874, -0.9205048534524405, -0.9135454576426011, -0.9063077870366503, -0.898794046299167, -0.8910065241883679, -0.8829475928589271, -0.8746197071393956, -0.8660254037844386, -0.8571673007021123, -0.8480480961564262, -0.8386705679454243, -0.8290375725550416, -0.8191520442889918, -0.8090169943749476, -0.798635510047293, -0.7880107536067223, -0.7771459614569713, -0.7660444431189781, -0.7547095802227722, -0.743144825477394, -0.7313537016191703, -0.7193398003386512, -0.7071067811865477, -0.6946583704589976, -0.6819983600624989, -0.6691306063588588, -0.6560590289905074, -0.6427876096865396, -0.6293203910498372, -0.6156614753256582, -0.6018150231520483, -0.5877852522924734, -0.5735764363510465, -0.5591929034707473, -0.544639035015027, -0.529919264233205, -0.5150380749100545, -0.5000000000000004, -0.48480962024633767, -0.4694715627858908, -0.45399049973954697, -0.4383711467890778, -0.4226182617406992, -0.40673664307580015, -0.3907311284892739, -0.37460659341591235, -0.35836794954530077, -0.34202014332566943, -0.3255681544571567, -0.3090169943749476, -0.29237170472273716, -0.27563735581699894, -0.2588190451025207, -0.24192189559966787, -0.22495105434386534, -0.20791169081775987, -0.19080899537654467, -0.1736481776669304, -0.15643446504023112, -0.13917310096006588, -0.12186934340514811, -0.1045284632676543, -0.08715574274765832, -0.06975647374412564, -0.05233595624294348, -0.034899496702500823, -0.01745240643728356,
        };

        double x1;
        double y1;
        int iterator;
        int length = 1800;
        int facingAngle = (int) getFacingAngle();
        int verticesIndex = 0;

        for (int i = facingAngle - flashLightAngle; i < facingAngle + flashLightAngle; i++) {
            iterator = 0;
            x1 = coorX;
            y1 = coorY;

            while (iterator < length) {

                //check for collision, we are multiplying by 30, so it can see into the wall a little bit.
                if (white.equals(getPixelColor((int) x1, (int) y1))) {
                    if (i < 0) {
                        x1 += 30 * cosines[i + 360];
                        y1 += 30 * sins[i + 360];
                    } else {
                        x1 += 30 * cosines[i];
                        y1 += 30 * sins[i];
                    }
                    break;
                }

                //if we don't see a collision, we are changing the x and y position by incrementing using sin and cos values from a table.
                if (i < 0) {
                    x1 += cosines[i + 360];
                    y1 += sins[i + 360];
                } else {
                    x1 += cosines[i];
                    y1 += sins[i];
                }

                iterator++;
            }

            //finally, we add the collected vertices to a float array.
            shapeVerticies[(2 * (verticesIndex))] = (float) x1;
            shapeVerticies[2 * (verticesIndex) + 1] = (float) y1;


            verticesIndex++;
//            shapeRenderer.setProjectionMatrix(camera.combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.circle((float) x1, (float) y1, 5);
//            shapeRenderer.end();
        }
    }

    public void flashLight() {

        if ((int) (flashlightBattery) == 20 && flashLightIsOn) {
            if (!flashLightWarning.isPlaying()) {
                flashLightWarning.play();
            }
        }

        if (flashlightBattery <= 0) {
            flashlightBattery = 0;
            flashLightIsOn = false;
        }


        if (flashLightIsOn && flashlightBattery > 0) {
            flashlightBattery -= 0.005;
        } else if (flashlightBattery <= 100) {
            flashlightBattery += 0.02;
            if (flashlightBattery > 100){
                flashlightBattery = 100;
            }


        }
        if (System.nanoTime() >= timeTillCanFlash) {
            canFlashLight = true;
        }
        if (keyStrokes.contains(Input.Keys.F) && canFlashLight) {
            flashLightSound.play();
            flashLightIsOn = !flashLightIsOn;
            canFlashLight = false;
            timeTillCanFlash = System.nanoTime() + main.secondsToNano(1);
        }
    }

    public void weapon() {

        if (canChangeGun) {

            if (keyStrokes.contains(Input.Keys.NUM_1)) {
                changeGun.play();

                canChangeGun = false;
                timeTillChange = System.nanoTime() + main.secondsToNano(1.5f);

                equippedWeaponName = "SMG";
                equippedWeapon = smg;
            }
            if (keyStrokes.contains(Input.Keys.NUM_2)) {
                changeGun.play();

                canChangeGun = false;
                double changeTime = 100;
                timeTillChange = System.nanoTime() + main.secondsToNano(1.5f);

                equippedWeaponName = "RIFLE";
                equippedWeapon = rifle;
            }
        }

        if (System.nanoTime() >= timeTillChange) {
            canChangeGun = true;
        }

        if (pressedMouseHash.contains(Input.Buttons.LEFT) && canChangeGun) {


            equippedWeapon.fireWeapon();

            if (equippedWeapon.getAmmo() > 0) {
                staminaRegen = false;
            }
            haltUntil = System.nanoTime() + main.secondsToNano(1);
        }
        if (keyStrokes.contains(Input.Keys.R) && canChangeGun) {
            equippedWeapon.reloadWeapon();
        }


    }

    public void melee() {
        if (System.nanoTime() == timeTillMelee) {
            canMelee = true;
        }
        if (keyStrokes.contains(Input.Keys.V)) {
            if (canMelee) {
                if (monsterDistance < 200) {
                    monster.hitByMelee();
                    meleeHit.play();
                } else {
                    meleeMiss.play();
                }
                canMelee = false;
                timeTillMelee = main.secondsToNano(2);
            }
        }
    }

    public void checkBullets() {
        if (!bullets.isEmpty()) {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                if (bullets.get(i).isAlive()) {
                    bullets.get(i).castRay();
                } else {
                    bullets.remove(i);
                    System.gc();
                }
            }
        }
    }

    public void updateBullets(Bullet bulletz) {
        bullets.add(bulletz);
    }

    public void move() {

        boolean movingUp = false;
        boolean movingDown = false;
        boolean movingLeft = false;
        boolean movingRight = false;

        dy = 0;
        dx = 0;
        float moveSpeed;

        sprint = keyStrokes.contains(Input.Keys.SHIFT_LEFT);
        if (stamina <= 0) {
            sprint = false;
        }

        moveSpeed = sprint ? speed * factor : speed;
        moveSpeed *= (float) ((0.6 + 0.4 * (stamina / 100)));

        //region key input management
        if (keyStrokes.contains(Input.Keys.W)) {
            dy = moveSpeed;
            movingUp = true;
        }
        if (keyStrokes.contains(Input.Keys.S)) {
            dy = -moveSpeed;
            movingDown = true;
        }
        if (keyStrokes.contains(Input.Keys.D)) {
            dx = moveSpeed;
            movingRight = true;
        }
        if (keyStrokes.contains(Input.Keys.A)) {
            dx = -moveSpeed;
            movingLeft = true;
        }
        //endregion

        //region stamina management
        if (stamina <= 0) {
            if (!staminaPause) {
                staminaPause = true;
                waitUntil = System.nanoTime() + main.secondsToNano(3);
            }
            if (System.nanoTime() >= waitUntil) {
                staminaPause = false;
            }
        }

        if (sprint && stamina > 0 && (Math.abs(dy) + Math.abs(dx)) > 0) {
            stamina -= 0.1;
        } else if (stamina < 100 && !staminaPause && staminaRegen) {
            if (dx == 0 && dy == 0) {
                stamina += 0.03;
            } else {
                stamina += 0.01;
            }
        }
        if (stamina < 0) {
            stamina = 0;
        }
        //endregion

        //region sound management
        if (sprint) {
            soundManager.stopSound("walk", walking);
            soundManager.playSound("run", running);
        } else {
            soundManager.stopSound("run", running);
            soundManager.playSound("walk", walking);
        }

        if (dx == 0 && dy == 0) {
            soundManager.stopSound("run", running);
            soundManager.stopSound("walk", walking);
        }
        //endregion

        //region Normalization
        float inputX = Math.abs(dx);
        float inputY = Math.abs(dy);
        double magnitude = sqrt((dx * dx) + (dy * dy));

        if (magnitude != 0) {
            dx /= (float) magnitude;
            dy /= (float) magnitude;
        }
        dx *= inputX;
        dy *= inputY;
        //endregion

        //region edge of level collision
        if (coorX + dx < 80) {
            coorX = 80;
        } else if (coorY + dy < 80) {
            coorY = 80;
        } else if (coorX + dx > pixmap.getWidth() - 80) {
            coorX = pixmap.getWidth() - 80;
        } else if (coorY + dy > pixmap.getHeight() - 80) {
            coorY = pixmap.getHeight() - 80;
        }
        //endregion

        //region collision
        if (movingRight) {
            if (getPixelColor((int) coorX + 80, (int) coorY).equals(white)) {
                dx = 0;
            }
        } else if (movingLeft) {
            if (getPixelColor((int) coorX - 80, (int) coorY).equals(white)) {
                dx = 0;
            }
        }
        if (movingUp) {
            if (getPixelColor((int) coorX, (int) coorY + 80).equals(white)) {
                dy = 0;
            }
        } else if (movingDown) {
            if (getPixelColor((int) coorX, (int) coorY - 80).equals(white)) {
                dy = 0;
            }
        }
        //endregion

        coorX += dx;
        coorY += dy;


        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        setFacing(mousePos.x, mousePos.y);
    }

    public void updateCamera() {

        //Clamp camera position if it goes out of bounds.
        if (coorX + (cameraZoom / 2) > roomWidth) {
            camX = roomWidth - cameraZoom / 2;
        } else if (coorX - (cameraZoom / 2) < 0) {
            camX = cameraZoom / 2;
        } else {
            camX = coorX;
        }
        if (coorY + (cameraZoom / 2) > roomWidth) {
            camY = roomWidth - cameraZoom / 2;
        } else if (coorY - (cameraZoom / 2) < 0) {
            camY = cameraZoom / 2;
        } else {
            camY = coorY;
        }

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    private Color getPixelColor(int x, int y) {

        int pixel = pixmap.getPixel(x, pixmap.getHeight() - y);

        float r = ((pixel >> 24) & 0xFF) / 255f; // Red component
        float g = ((pixel >> 16) & 0xFF) / 255f; // Green component
        float b = ((pixel >> 8) & 0xFF) / 255f;  // Blue component
        float a = (pixel & 0xFF) / 255f;         // Alpha component

        return new Color(r, g, b, a);
    }

    public double[] lightCone() {

        double height = 1500;
        double baseLength = 1000;

        double vectorX = faceX - coorX;
        double vectorY = faceY - coorY;

        // Step 2: Normalize the direction vector to get the direction unit vector
        double magnitude = Math.sqrt(vectorX * vectorX + vectorY * vectorY);
        double unitX = vectorX / magnitude;
        double unitY = vectorY / magnitude;

        // Step 3: Calculate the perpendicular vector (perpendicular to the base direction)
        double perpX = -unitY; // 90-degree rotation to get a perpendicular direction
        double perpY = unitX;

        // Step 4: Normalize the perpendicular vector
        double perpMagnitude = Math.sqrt(perpX * perpX + perpY * perpY);
        perpX /= perpMagnitude;
        perpY /= perpMagnitude;

        // Step 5: Calculate the midpoint of the base (to adjust the base points later)
        double midBaseX = coorX + unitX * height;  // Apex moved along the direction vector by 'height'
        double midBaseY = coorY + unitY * height;

        // Step 6: Calculate the base points using the fixed base length
        double halfBaseLength = baseLength / 2;

        // Move the base points along the perpendicular vector by half the base length
        double basePoint1X = midBaseX + halfBaseLength * perpX;
        double basePoint1Y = midBaseY + halfBaseLength * perpY;
        double basePoint2X = midBaseX - halfBaseLength * perpX;
        double basePoint2Y = midBaseY - halfBaseLength * perpY;

        // Return the coordinates of the two base points
        return new double[]{basePoint1X, basePoint1Y, basePoint2X, basePoint2Y};
    }

    public void drawMyself() {
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        playerSprite.setPosition(getCoorX() - playerSprite.getWidth() / 2, getCoorY() - playerSprite.getHeight() / 2);
        playerSprite.setRotation(getFacingAngle() + 90);
        playerSprite.draw(spriteBatch);
        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.PINK);
        shapeRenderer.end();
    }

    public void facingAngle() {
        delX = getFaceX() - getCoorX();
        delY = getFaceY() - getCoorY();

        this.facingAngle = (float) Math.toDegrees(Math.atan2(delY, delX));
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getFacingAngle() {
        return facingAngle;
    }

    public float getCoorX() {
        return this.coorX;
    }

    public double[] getPointInFrontVector() {
        return pointInFrontVector;
    }

    public CollisionMask getCollisionMask() {
        return collisionMask;
    }

    public float getCoorY() {
        return this.coorY;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = stamina;
    }

    public void setFacing(float x, float y) {
        faceX = x;
        faceY = y;
    }

    public float getFaceX() {
        return faceX;
    }

    public float getFaceY() {
        return faceY;
    }

    public boolean getFlashLight() {
        return flashLightIsOn;
    }

    public double getBattery() {
        return flashlightBattery;
    }

    public double getZoom() {
        return cameraZoom;
    }

}
