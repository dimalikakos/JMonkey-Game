package Alikakos_Game;



import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;




public class Alikakos_main extends SimpleApplication implements ActionListener, AnimEventListener {
    
    boolean healing_character = false;
    float updatedTimeElapsed = 0;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    
    CharacterControl character, ninja,companion,sentinel;
    Vector3f [] character_location_for_enemy;
    Node model, modelNPC, modelNPC2, modelNPC3,companion_model,sentinel_model;
    Node shootables;   // Object that can be shot
    float displacement = 60;
    RigidBodyControl terrainPhysicsNode;
    private MotionPath path, path2,fire_path;
    private MotionEvent motionControl, motionControl2,fire_motion_control;
    Vector3f walkDirection = new Vector3f();
    Vector3f ninj_walk = new Vector3f();
    Vector3f character_origin = new Vector3f(105f, 35f, -9f);
    wall_cube closest_cube = null;
    boolean once;
    
    AnimChannel animationChannel, animationChannel2, animationChannelNPC, animationChannelNPC2,
            shootingChannel, animationChannelNPC3,animationChannelCompanion,animationChannelSentinel;
    AnimControl animationControl, animationControlNPC, animationControlNPC2, animationControlNPC3,animationControlCompanion,animationControlSentinel;
    float airTime = 0;
    //camera
    boolean left = false, right = false, up = false, down = false,sneak = false;;
    boolean pursuit = false;
    //Variables for controllers
    boolean [] pursuitMode;    
    boolean [] enemy_fireball;    
    boolean [] water;    
    boolean [] lost;
    //Spawn Variables
    boolean replenish_rock = false;
    boolean fireball_sent = false;
    boolean spawn_ninjas = false;
    int number_of_ninjas = 3;
    int ninja_id = 10;
    //Round Counter
    int round = 1;
    ChaseCamera chaseCam;
    private Spatial sceneModel,rock_pile,vault_test,tower,door,well;
    FilterPostProcessor fpp;
    private static final Sphere sphere;
    final float PROXIMITY = 4.0f; //specifies the minimum distatance between 2 characters  
    //EFFECT Variables
    ParticleEmitter fireEffect;
    ParticleEmitter waterEffect;
    ParticleEmitter repairEffect;
    ParticleEmitter companionEffect;
    //Day And Night Variables
    float [] amountOfLight = new float[1];
    float upThreshold = 2f;
    float downThreshold = 0.1f;
    boolean upPhase = true;
    boolean heal_cube = false;
    float speed = 0.2f;
    int updateTimeElapsed=0;
    int burning_boxes_time = 0;
    Vector3f wall_origin;
    
    //NPC PATROLING variables
    Vector3f companion_walk;
    Vector3f sentinel_walk;
    
    //Wall Variables
    int stories_high=3;
    int building_current_side = 0;
    int max_box_health = 9;    
    float wall_origin_x = 85f;
    float wall_origin_y = 2f;
    float wall_origin_z = -20f;
    //Char Life Variables
    int[] life_amount = {10};    
    int max_life_amount = 100;
    
    //Spawn Variables
    int rock_amount = 10;    
    int water_amount=10;
    float night_amount = 0;
    
    //Gui Variables
    int score = 0;
    BitmapText life_text;
    BitmapText log_text;
    BitmapText night_text;
    BitmapText score_text;
    BitmapText rock_text;
    BitmapText water_text;
    int width;        //width is the width of the gui
    int height; 
    
    
    
        
    int counter = 0;
    Geometry nightBar;
    Geometry heal_skill;
    Geometry fireball_skill;
    Geometry water_skill;
    Geometry lifeBar;
    Geometry nightBarBackground;
    Geometry lifeBarBackground;
    Geometry ball_geo;
    Geometry fireball;
    Geometry heal_back;
    Geometry fireball_back;
    Geometry waterball_back;
    Geometry game_over;
    Geometry marker;
    
    RigidBodyControl ball_phy;
    
    Vector3f next_point = new Vector3f();
    
    DirectionalLight dl;
    HashMap rock_locations = new HashMap();
    HashMap rock_piles = new HashMap();
    ArrayList<Integer> rock_to_replenish = new ArrayList<Integer>();
    ArrayList<Integer> current_layer_shuffled = new ArrayList<Integer>();
    ArrayList<Integer> current_layer_ordered = new ArrayList<Integer>();
    ArrayList<Integer> destroyed = new ArrayList<Integer>();
    ArrayList<Vector3f> companion_points = new ArrayList<Vector3f>();
    int companion_points_counter = 0;
    ArrayList<Vector3f> sentinel_points = new ArrayList<Vector3f>();
    int sentinel_points_counter = 0;
    
    
    ArrayList<wall_cube> targeted = new ArrayList<wall_cube>();
    public ArrayList<Integer> enemies_array = new ArrayList<Integer>();

    public HashMap <Integer, wall_cube> wall_boxes = new HashMap<Integer, wall_cube>();
    public HashMap <Integer, wall> walls = new HashMap<Integer, wall>();    
    public HashMap <Integer, enemy> enemies = new HashMap<Integer, enemy>();
    
    float sentinel_range;
    
    
    
    int key=0;
    int max;
    int min;
    wall_cube target;
    int ordered_index;
    wall_cube weakest_box;
    
    Random random_generator = new Random();
    int  random_number;
    int active_side = 0;
    boolean tab_pressed = true;
    
     int cycle_target = 0;
     enemy character_target = null;
     int je_enemy = -1;
     boolean extinguish = false;
     boolean repair = false;
     private AudioNode water_node,music;
    static {
        // Initialize the cannon ball geometry
        sphere = new Sphere(32, 32, 1f, true, false);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
    }

    public static void main(String[] args) {
        Alikakos_main app = new Alikakos_main();
        app.start();
    }
    ArrayList<JmeCursor> cursors = new ArrayList<JmeCursor>();

    
    public void simpleInitApp() {
        //Locations Companion Patrols
        companion_points.add( new Vector3f(121.04831f, 19.598217f, -28.08691f));
        companion_points.add( new Vector3f(97.52049f, 19.595303f, -27.0668f));
        companion_points.add( new Vector3f(87.41028f, 19.598267f, -17.924274f));
        companion_points.add( new Vector3f(87.74878f, 19.599998f, 6.2637157f)); //3
        companion_points.add( new Vector3f(98.63524f, 19.897305f, 16.968002f)); //4
        companion_points.add( new Vector3f(121.29399f, 19.893895f, 16.42531f)); // 5
        companion_points.add( new Vector3f(131.81805f, 19.599998f, 7.7434416f));
        companion_points.add( new Vector3f(131.48077f, 19.599998f, -14.723125f));
        
        //Locations Sentinel Patrols
        sentinel_points.add( new Vector3f(-88.72858f, 3.3008661f, -151.6748f));
        sentinel_points.add( new Vector3f(-150.10706f, 3.4163036f, -150.51581f));
        sentinel_points.add( new Vector3f(-160.14844f, 2.8067136f, -37.865578f));
        sentinel_points.add( new Vector3f(-108.65552f, 2.6369157f, 30.944912f)); //3
        
        
        //Audio Inits
        music = new AudioNode(assetManager, "Sounds/Music/music.wav"); 
        music.setLooping(true);
        music.setPositional(true);
        music.setRefDistance(10f);
        music.setMaxDistance(10000f);
        music.setVolume(0.6f);
        music.play();
        
        water_node = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
        water_node.setLooping(true);
        water_node.setPositional(true);
        water_node.setRefDistance(10f);
        water_node.setMaxDistance(10000f);
        water_node.setVolume(0.6f);
        //water_node.play();
        
        
        //Game Lost
        lost = new boolean[1];
        lost[0] = false;
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        shootables = new Node("Shootables");
        rootNode.attachChild(shootables);
        pursuitMode = new boolean[1];
        pursuitMode[0] = pursuit;
        
        water = new boolean[1];
        water[0] = false;
        
        enemy_fireball = new boolean[1];
        enemy_fireball[0] = false;
        
        inputManager.setCursorVisible(true);
        cursors.add((JmeCursor) assetManager.loadAsset("Textures/cursor/game_cursor.ico"));
        inputManager.setMouseCursor(cursors.get(0));
        
        setupKeys();
        createLight();
        createSky();
        createTerrain();
        
        
        //Construct tower;
        wall_origin = new Vector3f(wall_origin_x, wall_origin_y, wall_origin_z);    
        next_point = make_wall(wall_origin,5f,3f,1);
        next_point = new Vector3f(next_point.x+10f,wall_origin_y,next_point.z+10f);
        next_point = make_wall(next_point,5f,3f,2);
        next_point = new Vector3f(next_point.x+10f,wall_origin_y,next_point.z-10f);
        next_point = make_wall(next_point,5f,3f,3);
        next_point = new Vector3f(next_point.x-10f,wall_origin_y,next_point.z-10f);
        next_point = make_wall(next_point,5f,3f,4);
        make_tower(new Vector3f(84.22427f, -10f, 20.0f));
        make_tower(new Vector3f(84.22427f, -10f, -30.0f));
        make_tower(new Vector3f(136.22427f, -10f, -30.0f));
        make_tower(new Vector3f(136.22427f, -10f, 20.0f));
        max = key;
        min = 1+key-(key/stories_high);

        
        createCharacter("User");
        create_ninja(0, -135, 5.1f, -133);
        create_ninja(3, -135, 5.1f, -123);
        create_ninja(5, -135, 5.1f, -117);
        
        //ROck Piles SPawn Locations
        rock_locations.put(1,new Vector3f(-137.05557f, 0.2f, -24.48642f) );
        rock_locations.put(2,new Vector3f(-160.69534f, 0.9f, -67.36072f) );
        rock_locations.put(3,new Vector3f(-56.103508f, 0.2f, -118.54907f) );
        rock_locations.put(4,new Vector3f(-150.12396f, 0.5f, -157.9074f) );
        rock_locations.put(5,new Vector3f(27.199997f, 0.2f, -19.199993f) );
        
        
        gui_init();
        
        //Initialize rocks
        for(int i = 1;i<=rock_locations.size();i++){
            make_rock((Vector3f)rock_locations.get(i),i);
        }
        
        //rootNode.detachChild((Spatial)wood_piles.get(5));
        
        
        
        setupChaseCamera();
        set_up_fireEffect(); //Effect For Fire
        set_up_waterEffect(); //Effect for Extinguish
        set_up_repairEffect(); //Effect for Repair
        set_up_companionEffect();
        create_companion();
        create_sentinel();
        
        //GUI SETUPS
        heal_skill();
        fireball_skill();
        waterball_skill();
        heal_back = heal_back();
        fireball_back = fireball_back();
        waterball_back = waterball_back();
        game_over = game_over();
        
       
       
       
       
       
       make_door();
       make_well();
       
       marker = create_marker();
       
        

    }
    
    protected void set_up_fireEffect(){
        fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setImagesX(2);
        fireEffect.setImagesY(2); // 2x2 texture animation
        fireEffect.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fireEffect.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        // fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fireEffect.setStartSize(2f);
        fireEffect.setEndSize(3f);
        fireEffect.setLowLife(2f);
        fireEffect.setHighLife(3f);
    }
    
    protected void set_up_waterEffect(){
        waterEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material waterMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        waterMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        waterEffect.setMaterial(waterMat);
        waterEffect.setImagesX(2);
        waterEffect.setImagesY(2); // 2x2 texture animation
        waterEffect.setEndColor(new ColorRGBA(0, 0f, 1f, 1f));   // red
        waterEffect.setStartColor(new ColorRGBA(0, 0.8f, 0.8f, 0.5f)); // yellow
        // fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        waterEffect.setStartSize(2f);
        waterEffect.setEndSize(3f);
        waterEffect.setLowLife(2f);
        waterEffect.setHighLife(3f);
    }
    protected void set_up_repairEffect(){
        repairEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material waterMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        waterMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        repairEffect.setMaterial(waterMat);
        repairEffect.setImagesX(2);
        repairEffect.setImagesY(2); // 2x2 texture animation
        repairEffect.setEndColor(new ColorRGBA(0, 1f, 0f, 1f));   // red
        repairEffect.setStartColor(new ColorRGBA(0, 0.8f, 0.1f, 0.5f)); // yellow
        // fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        repairEffect.setStartSize(2f);
        repairEffect.setEndSize(3f);
        repairEffect.setLowLife(2f);
        repairEffect.setHighLife(3f);
    }
    protected void set_up_companionEffect(){
        companionEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material waterMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        waterMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        companionEffect.setMaterial(waterMat);
        companionEffect.setImagesX(2);
        companionEffect.setImagesY(2); // 2x2 texture animation
        companionEffect.setEndColor(new ColorRGBA(0, 1f, 0f, 1f));   // red
        companionEffect.setStartColor(new ColorRGBA(0, 0.8f, 0.1f, 0.5f)); // yellow
        // fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        companionEffect.setStartSize(2f);
        companionEffect.setEndSize(3f);
        companionEffect.setLowLife(2f);
        companionEffect.setHighLife(3f);
    }
    

    
    protected void gui_init(){      
        
        width = settings.getWidth();           //width is the width of the gui
        height = settings.getHeight(); 

        nightBarBackground = makeLine("Background To Night Bar", 0f, 0f, 0f,ColorRGBA.Blue);        
        nightBarBackground.setLocalTranslation(width * 0.5f, height-10, 1);
        nightBarBackground.setLocalScale(100, 10, 1);
        guiNode.attachChild(nightBarBackground);
        
        nightBar = makeLine("Vertical Gui Line", 0f, 0f, 0f,ColorRGBA.Orange);
        //nightBar.setLocalTranslation(width * 0.05f, height-10, 1);
        nightBar.setLocalTranslation(width * 0.5f, height-10, 1);
        nightBar.setLocalScale(100, 10, 1);
        guiNode.attachChild(nightBar);
        
        night_text = new BitmapText(guiFont, false);
        night_text.setSize(guiFont.getCharSet().getRenderedSize());
        night_text.setText(night_amount+"%");
        night_text.setLocalTranslation((width * 0.5f)-25, height-1, 2);
        guiNode.attachChild(night_text);
        
        
        lifeBarBackground = makeLine("Background To Night Bar", 0f, 0f, 0f,ColorRGBA.Gray);        
        lifeBarBackground.setLocalTranslation((width * 0f)+100, height-10, 1);   
        lifeBarBackground.setLocalScale(100, 10, 1);
        guiNode.attachChild(lifeBarBackground);
        
        lifeBar = makeLine("Vertical Gui Line", 0f, 0f, 0f,ColorRGBA.Green);
        lifeBar.setLocalTranslation((width * 0.05f), height-10, 1);        
        lifeBar.setLocalScale(100, 10, 1);
        guiNode.attachChild(lifeBar);
        
        life_text = new BitmapText(guiFont, false);
        life_text.setSize(guiFont.getCharSet().getRenderedSize());
        life_text.setText(life_amount[0]+"");
        life_text.setLocalTranslation((width * 0f)+100, height-1, 2); 
        guiNode.attachChild(life_text);
        
        score_text = new BitmapText(guiFont, false);
        score_text.setSize(guiFont.getCharSet().getRenderedSize());
        score_text.setText("SCORE: " + score);
        score_text.setLocalTranslation((width * 0.90f), height-1, 2); 
        guiNode.attachChild(score_text);
        
        rock_text = new BitmapText(guiFont, false);
        rock_text.setSize(guiFont.getCharSet().getRenderedSize());
        rock_text.setText("Rock: " + rock_amount);
        rock_text.setLocalTranslation((0f), height-30, 2); 
        guiNode.attachChild(rock_text);
        
        
        water_text = new BitmapText(guiFont, false);
        water_text.setSize(guiFont.getCharSet().getRenderedSize());
        water_text.setText("Water: " + water_amount);
        water_text.setLocalTranslation((0f), height-60, 2); 
        guiNode.attachChild(water_text);
        
        
    }
    //MAKE IMAGE REPAIR SKILL
    protected void heal_skill(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/heal.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.6f, 45, 2);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        
    }
    //MAKE IMAGE FIREBALL SKILL
    protected void fireball_skill(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/fireball.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.4f, 45, 2);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        
    }
    //MAKE IMAGE WATERBALL SKILL
    protected void waterball_skill(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/waterball.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.5f, 45, 2);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        
    }
    //MAKE BACKGROUND OF REPAIR SKILL
    protected Geometry heal_back(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/heal_back.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.6f, 45, 1);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        return geom;
        
    }
    //MAKE BACKGROUND OF FIREBALL SKILL
    protected Geometry fireball_back(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/fireball_back.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.4f, 45, 1);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        return geom;
    }
    //MAKE BACKGROUND OF WATERBALL SKILL
    protected Geometry waterball_back(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/waterball_back.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.5f, 45, 1);
        geom.setLocalScale(35, 35, 1);
        guiNode.attachChild(geom);
        return geom;
    }
    //MAKE BACKGROUND WHEN GAME IS OVER
    protected Geometry game_over(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/game_over.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(width * 0.5f, height*0.5f, 4);
        geom.setLocalScale(600, 300, 1);
        
        return geom;
    }
    
    protected Geometry makeLine(String name, float x, float y, float z, ColorRGBA color) {
        Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
        Geometry cube = new Geometry(name, box);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", color);
        cube.setMaterial(mat1);
        return cube;
    }
    //MAKE THE WALL
    protected Vector3f make_wall(Vector3f origin,float half_width, float half_height, int side){
        Vector3f starting_point;
        Vector3f last_cube_point = new Vector3f();
        for(int i=0;i<3;i++){
            starting_point = origin;
            for(int k=0;k<4;k++){
                makeCube(starting_point,half_width,half_height,side);
                 last_cube_point = starting_point;
                
                if (side == 1){
                    starting_point = new Vector3f(starting_point.getX(),starting_point.getY(),starting_point.getZ()+2*half_width-0.001f);
                } else if (side == 2){
                    starting_point = new Vector3f(starting_point.getX()+2*half_width,starting_point.getY(),starting_point.getZ());
                } else if (side == 3){
                    starting_point = new Vector3f(starting_point.getX(),starting_point.getY(),starting_point.getZ()-2*half_width);
                } else if (side == 4){
                    starting_point = new Vector3f(starting_point.getX()-2*half_width,starting_point.getY(),starting_point.getZ());
                }
            }
            origin = new Vector3f(origin.getX(),origin.getY()+2*half_height-0.1f,origin.getZ());
        }
        
        return last_cube_point;
    }
    
//    protected Vector3f make_wall_v2(Vector3f origin,float half_width, float half_height, int side){
//        Vector3f starting_point;
//        Vector3f last_cube_point = new Vector3f();
//        
//            starting_point = origin;
//            for(int k=0;k<4;k++){
//                makeCube(starting_point,half_width,half_height,side);
//                 last_cube_point = starting_point;
//                
//                if (side == 1){
//                    starting_point = new Vector3f(starting_point.getX(),starting_point.getY(),starting_point.getZ()+2*half_width-0.001f);
//                } else if (side == 2){
//                    starting_point = new Vector3f(starting_point.getX()+2*half_width,starting_point.getY(),starting_point.getZ());
//                } else if (side == 3){
//                    starting_point = new Vector3f(starting_point.getX(),starting_point.getY(),starting_point.getZ()-2*half_width);
//                } else if (side == 4){
//                    starting_point = new Vector3f(starting_point.getX()-2*half_width,starting_point.getY(),starting_point.getZ());
//                }
//            }
//            origin = new Vector3f(origin.getX(),origin.getY()+2*half_height-0.1f,origin.getZ());
//        
//        
//        return last_cube_point;
//    }

    
    //MAKE CUBE THAT WALL IS CONSISTED OF
    protected void makeCube(Vector3f starting_point, float half_width, float half_height, int side) {
        Box b = new Box(5, half_height, half_width); // create cube shape
        Geometry vault = new Geometry("Vault", b);  // create cube geometry from the shapek
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        RigidBodyControl vault_phy = new RigidBodyControl(0);
        vault.addControl(vault_phy);
        bulletAppState.getPhysicsSpace().add(vault_phy);
        vault_phy.setPhysicsLocation(starting_point);
        mat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        vault.setMaterial(mat);                   // set the cube's material

        Texture cubeTex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat.setTexture("ColorMap", cubeTex);
        rootNode.attachChild(vault);   
        
        wall_cube box = new wall_cube(vault,max_box_health,starting_point,side,key,vault_phy,assetManager,rootNode);
        if(side > building_current_side){
            building_current_side = side;
            wall wall_side = new wall(side,box);
            walls.put(side,wall_side);
            
        }else{
            wall temp = walls.get(side);
            temp.boxes.add(box);
            
        }

        key++;
        if(key == 12){
            key = 0;
        }

        
    }
    // This is the non-player character; it follows the motion path once "P" is pressed
    
    //CREATE FIREBALL
    private Spatial create_fireball() {
        Material stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey tex_key = new TextureKey("Textures/fireball_tex.jpg");
        tex_key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(tex_key);
        stone_mat.setTexture("ColorMap", tex);       
        
        Sphere s = new Sphere(8, 8, 0.8f);
        Box b = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
        fireball = new Geometry("Box", s);   
        
        //fireball = new Geometry("cannon ball", sphere);
        fireball.setMaterial(stone_mat);
        rootNode.attachChild(fireball);      

        // Position of the cannon balls
        
        return fireball;
    }
    //CREATE TARGETING MARKER, FOR WHEN A PLAYER TARGETS WITH TAB BUTTON
     private Geometry create_marker() {
        Material stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");        
        stone_mat.setColor("Color", ColorRGBA.Red);
        Sphere s = new Sphere(8, 8, 1f);
        Box b = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
        marker = new Geometry("Box", s);

        marker.setMaterial(stone_mat);
        rootNode.attachChild(marker);

        return marker;
    }

   //CREATION OF NPCs
    private Spatial create_ninja(int id, float x, float y, float z) { 
        CapsuleCollisionShape capsuleNPC3 = new CapsuleCollisionShape(0.5f, 0.5f, 1);
        modelNPC3 = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        modelNPC3.scale(0.03f);
        shootables.attachChild(modelNPC3);

        animationControlNPC3 = modelNPC3.getControl(AnimControl.class);
        animationControlNPC3.addListener(this);
        animationChannelNPC3 = animationControlNPC3.createChannel();
        animationChannelNPC3.setAnim("Idle1");

        ninja = new CharacterControl(capsuleNPC3, 2.75f);
        ninja.setGravity(30f);
        ninja.setFallSpeed(30f);

        modelNPC3.addControl(ninja);
        ninja.setPhysicsLocation(new Vector3f(x, y, z));
        rootNode.attachChild(modelNPC3);

        //modelNPC3
        getPhysicsSpace().add(ninja);

        //add audio Control
        modelNPC3.addControl(new NPCSoundControl (pursuitMode, rootNode,assetManager, modelNPC3,enemy_fireball));
        enemy this_enemy = new enemy(id,modelNPC3,rootNode,enemies);
        modelNPC3.addControl(new enemy_control (rootNode,assetManager, ninja,character,animationChannelNPC3,walls,bulletAppState,this_enemy,life_amount,targeted,lost,amountOfLight,enemy_fireball));
        
        enemies.put(id, this_enemy);
        enemies_array.add(id);
        animationControlNPC3.getAnimationNames();
        
        
        return modelNPC3;
    }
    //COMPAION THAT HEALS THE CHARACTER PLAYER
    private Spatial create_companion() {
        CapsuleCollisionShape capsule_companion = new CapsuleCollisionShape(0.5f, 0.5f, 1);
        companion_model = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        
        companion_model.scale(0.03f);
        shootables.attachChild(companion_model);

        animationControlCompanion = companion_model.getControl(AnimControl.class);
        animationControlCompanion.addListener(this);
        animationChannelCompanion = animationControlCompanion.createChannel();
        //animationChannelCompanion.setAnim("Idle");
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture companion_text = assetManager.loadTexture("Textures/logs_texture.jpg");
        companion_text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", companion_text);
//        
//        RigidBodyControl oto_body = new RigidBodyControl(0);
//        companion_model.addControl(oto_body);
//        bulletAppState.getPhysicsSpace().add(oto_body);
//        oto_body.setPhysicsLocation(new Vector3f(79, 0f, -8.354963f));
        mat.setColor("Color", ColorRGBA.Green);   // set color of material to blue
        companion_model.setMaterial(mat); 
        //repairEffect.setGravity(character.getPhysicsLocation().subtract(closest_cube.box_location).mult(10f));
        //System.out.println("SELECTED CUBE FOR HEAL: "+closest_cube.wall_side+"--"+closest_cube.id);
        companionEffect.setLowLife(1f);
        companionEffect.setHighLife(1f);
        //companionEffect.setGravity(0,-1000f,0);
        //companion_model.attachChild(companionEffect);

        companion = new CharacterControl(capsule_companion, 2.75f);
        companion.setGravity(30f);
        companion.setFallSpeed(30f);

        companion_model.addControl(companion);
        companion.setPhysicsLocation(character_origin);
        animationChannelCompanion.setAnim("Walk");
        //companion_model.setLocalTranslation(new Vector3f(companion.getPhysicsLocation().x,companion.getPhysicsLocation().y+15f,companion.getPhysicsLocation().z));
        rootNode.attachChild(companion_model);
            
 
        getPhysicsSpace().add(companion);

        //add audio Control
        //modelNPC3.addControl(new NPCSoundControl (pursuitMode, rootNode,assetManager, modelNPC3));
        //enemy this_enemy = new enemy(id,modelNPC3,rootNode,enemies);
        //modelNPC3.addControl(new enemy_control (rootNode,assetManager, ninja,character,animationChannelNPC3,walls,bulletAppState,this_enemy,life_amount,targeted,lost,amountOfLight));
        
        
        
        animationControlNPC3.getAnimationNames();
        
        
        return companion_model;
    }
    //SENTINEL IS ENEMY PATROLLER
    private Spatial create_sentinel() {
        CapsuleCollisionShape capsule_sentinel = new CapsuleCollisionShape(0.5f, 0.5f, 1);
        sentinel_model = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        
        sentinel_model.scale(0.1f);
        shootables.attachChild(sentinel_model);

        animationControlSentinel = sentinel_model.getControl(AnimControl.class);
        animationControlSentinel.addListener(this);
        animationChannelSentinel = animationControlSentinel.createChannel();
        //animationChannelSentinel.setAnim("Idle");
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture sentinel_text = assetManager.loadTexture("Textures/logs_texture.jpg");
        sentinel_text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", sentinel_text);
//        
//       
        mat.setColor("Color", ColorRGBA.Red);   // set color of material to blue
        sentinel_model.setMaterial(mat); 
        

        sentinel = new CharacterControl(capsule_sentinel, 2.75f);
        sentinel.setGravity(30f);
        sentinel.setFallSpeed(30f);

        sentinel_model.addControl(sentinel);
        sentinel.setPhysicsLocation(sentinel_points.get(0));
        animationChannelSentinel.setAnim("Walk");
        
        rootNode.attachChild(sentinel_model);
            
 
        getPhysicsSpace().add(sentinel);

        //add audio Control
        //modelNPC3.addControl(new NPCSoundControl (pursuitMode, rootNode,assetManager, modelNPC3));
        //enemy this_enemy = new enemy(id,modelNPC3,rootNode,enemies);
        //modelNPC3.addControl(new enemy_control (rootNode,assetManager, ninja,character,animationChannelNPC3,walls,bulletAppState,this_enemy,life_amount,targeted,lost,amountOfLight));
        
        
        
        animationControlSentinel.getAnimationNames();
        
        
        return sentinel_model;
    }
    private void createCharacter(String name) {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        character = new CharacterControl(capsule, 0.1f);
        model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        model.setLocalScale(0.5f);
        character.setJumpSpeed(10f);
        character.setGravity(20f);
        character.setFallSpeed(40f);

        model.addControl(character);
        model.addControl(new UserSoundControl (rootNode,assetManager, inputManager, model,water));
        //model.addControl(new UserFreezeControl (rootNode, assetManager, inputManager, model,character, ninja));
        character.setPhysicsLocation(new Vector3f(105f, 35f, -9f));

        rootNode.attachChild(model);
        getPhysicsSpace().add(character);

        animationControl = model.getControl(AnimControl.class);
        animationControl.addListener(this);
        animationChannel = animationControl.createChannel();
        animationChannel2 = animationControl.createChannel();
        animationChannel2.setAnim("IdleTop");
    }

    // A motion path for the NPC
    
//MOTIONPATH OF FIREBALL
    private void fireball_motion_path(Vector3f start, Vector3f end,final enemy targeted_enemy) {
        fire_path = new MotionPath();
        fire_path.addWayPoint(new Vector3f(start));
        fire_path.addWayPoint(new Vector3f(end));
        
        // fire_path.enableDebugShape(assetManager, rootNode);

        fire_motion_control = new MotionEvent(fireball, fire_path);
        fire_motion_control.setDirectionType(MotionEvent.Direction.Path);
        fire_motion_control.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));

        //fire_motion_control.setInitialDuration(30f);
        
        fire_motion_control.setSpeed(20f);

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        final BitmapText wayPointsText = new BitmapText(guiFont, false);
        wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());

        guiNode.attachChild(wayPointsText);

        fire_path.addListener(new MotionPathListener() {
            final BitmapText wayPointsText = new BitmapText(guiFont, false);

            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                
                if (fire_path.getNbWayPoints() == wayPointIndex + 1) {
                    wayPointsText.setText(control.getSpatial().getName() + " Finish!!! ");
                    
                    fireball_sent = false;
                    targeted_enemy.remove_health(3);
                    rootNode.detachChild(fireball);
                } else {
                    wayPointsText.setText(control.getSpatial().getName() + " Reached way-point " + wayPointIndex);
                    System.out.println("Way point  " + wayPointIndex + "reached,  object moving " + control.getSpatial().getName());
                }
                wayPointsText.setLocalTranslation((cam.getWidth() - wayPointsText.getLineWidth()) / 2, cam.getHeight(), 0);
            }
        });
    }
    
    
//MAKE ROCK PILES
  
    public void make_rock(Vector3f location, int i) {        
        rock_pile = assetManager.loadModel("Models/RockJ/Rock_6.j3o");   
        rock_pile.scale(1.2f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture rock_text = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        rock_text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", rock_text);
        //mat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        rock_pile.setMaterial(mat);  
        rootNode.attachChild(rock_pile);
        rock_pile.setLocalTranslation(location);
        rock_piles.put(i,rock_pile);
    }
    
    public void make_tower(Vector3f location) {        
        tower = assetManager.loadModel("Models/tower/tower.j3o");   
        tower.scale(0.07f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture tower_text = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        tower_text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tower_text);
        
        RigidBodyControl tower_phy = new RigidBodyControl(0);
        tower.addControl(tower_phy);
        bulletAppState.getPhysicsSpace().add(tower_phy);
        tower_phy.setPhysicsLocation(location);
        mat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        tower.setMaterial(mat);                   // set the cube's material

        
        
        rootNode.attachChild(tower);
        
        
        
        
        
                     // make the 
        
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharDown", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("CharSpace", new KeyTrigger(KeyInput.KEY_SPACE)); 
        inputManager.addMapping("Sneak", new KeyTrigger(KeyInput.KEY_LSHIFT));               
        inputManager.addMapping("Cycle", new KeyTrigger(KeyInput.KEY_TAB)); 
        inputManager.addMapping("Fireball", new KeyTrigger(KeyInput.KEY_1));  
        inputManager.addMapping("Extinguish", new KeyTrigger(KeyInput.KEY_2));  
        inputManager.addMapping("Repair", new KeyTrigger(KeyInput.KEY_3));  
        inputManager.addListener(this, "CharLeft");
        inputManager.addListener(this, "CharRight");
        inputManager.addListener(this, "CharUp");
        inputManager.addListener(this, "CharDown");
        inputManager.addListener(this, "CharSpace");      //JUMP  
        inputManager.addListener(this, "Sneak");   // MAKE CHARACTER WALL SLOWLY    
        inputManager.addListener(this, "Cycle");   //CYCLES THROUGH ENEMIES
        inputManager.addListener(this, "Fireball"); // CAST FIREBALL
        inputManager.addListener(this, "Extinguish"); // EXTINGUISH FLAME
        inputManager.addListener(this, "Repair"); //HEAL BOX OF WALL
    }

    // A directional light
    private void createLight() {
        Vector3f direction = new Vector3f(-0.1f, -0.7f, -1).normalizeLocal();
        dl = new DirectionalLight();
        dl.setDirection(direction);
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f).multLocal(1));
        //  dl.getColor().multLocal(1f);
        rootNode.addLight(dl);
    }

    private void createSky() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));
    }

    private void createTerrain() {
        sceneModel = assetManager.loadModel("Scenes/TestScene2.j3o");
        sceneModel.setLocalScale(1.0f);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        amountOfLight[0] = 1;
       sceneModel.addControl(new Alikakos_Game.WorldDayNightControl(dl, upThreshold, downThreshold,amountOfLight));

        rootNode.attachChild(sceneModel);
        getPhysicsSpace().add(sceneModel);
    }
    
    // This is the user controlled character, it has physical properties.
    

    private void setupChaseCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, model, inputManager);
        chaseCam.setMaxDistance(80);
    }
    public enemy cycle_enemies(){
        
        cycle_target++;
        if(cycle_target >= enemies.size() ){
            cycle_target = 0;
        }       
        je_enemy = enemies_array.get(cycle_target);        
        return enemies.get(je_enemy);
    }
    public void companion_actions(float tpf){
        
        if(character.getPhysicsLocation().distance(new Vector3f(109.599884f, 19.58582f, -5.999937f))<27f && life_amount[0] < 100 ){
            companion_walk = character.getPhysicsLocation().subtract(companion.getPhysicsLocation()).normalize();
            healing_character = true;
            if(!animationChannelCompanion.getAnimationName().equals("Idle1")){
                animationChannelCompanion.setAnim("Idle1");
                
            }
            companionEffect.setLowLife(2f);
            companionEffect.setHighLife(3f);
            companionEffect.setGravity(companion.getPhysicsLocation().subtract(character.getPhysicsLocation().mult(1f)));
            companion_model.attachChild(companionEffect);
            companionEffect.setLocalTranslation(companion.getPhysicsLocation());
            
        }else{
            companion_walk = companion_points.get(companion_points_counter).subtract(companion.getPhysicsLocation()).normalize();
            companionEffect.setLocalTranslation(0,-100,0);
            companionEffect.setGravity(0,0,0);
            healing_character = false;
            
        }
        
        
        companion_walk = companion_walk.multLocal(0.1f);
        companion.setViewDirection(new Vector3f(companion_walk.x,0f,companion_walk.z).mult(-1.1f));
        companion.setWalkDirection(companion_walk);
        repairEffect.setLocalTranslation(companion.getPhysicsLocation());
        
        if(companion.getPhysicsLocation().distance(companion_points.get(companion_points_counter))< 4f){
            companion_points_counter++;
            
        }
        if(companion_points_counter==companion_points.size()){
            companion_points_counter = 0;
        }
        
        if(!animationChannelCompanion.getAnimationName().equals("Walk")){
            animationChannelCompanion.setAnim("Walk");
        }
    }
    public void sentinel_actions(){
        if(sneak){
            sentinel_range = 25f;
        }else{
            sentinel_range = 70f;
        }
        if(character.getPhysicsLocation().distance(sentinel.getPhysicsLocation()) < sentinel_range){
            System.out.println("SENTINEL ATTACKING");
            sentinel_walk = character.getPhysicsLocation().subtract(sentinel.getPhysicsLocation()).normalize();     
            sentinel_walk = sentinel_walk.multLocal(0.2f);
            if(character.getPhysicsLocation().distance(sentinel.getPhysicsLocation()) < 5f){
                life_amount[0]=0;
            }
        }else{
            if(sentinel.getPhysicsLocation().distance(sentinel_points.get(sentinel_points_counter))< 4f){
            sentinel_points_counter++;
            }
            if(sentinel_points_counter==sentinel_points.size()){
                sentinel_points_counter = 0;
            }
            sentinel_walk = sentinel_points.get(sentinel_points_counter).subtract(sentinel.getPhysicsLocation()).normalize();        
            sentinel_walk = sentinel_walk.multLocal(0.2f);
            
            
        }
        if(!animationChannelCompanion.getAnimationName().equals("Walk")){
            animationChannelCompanion.setAnim("Walk");
        }
        
        sentinel.setViewDirection(new Vector3f(sentinel_walk.x,0f,sentinel_walk.z).mult(-1.1f));
        sentinel.setWalkDirection(sentinel_walk);
        
        
    }

    public void simpleUpdate(float tpf) {
        companion_actions(tpf);
        sentinel_actions();
        //companionEffect.setLocalTranslation(new Vector3f(companion.getPhysicsLocation().x,companion.getPhysicsLocation().y,companion.getPhysicsLocation().z));
        //companion_model.setLocalTranslation(new Vector3f(companion.getPhysicsLocation().x,companion.getPhysicsLocation().y+5f,companion.getPhysicsLocation().z));
        if(healing_character){
            updatedTimeElapsed += (tpf * 1000f)/1000;        
            life_amount[0] += updatedTimeElapsed;
        }
       
        
        Vector3f camDir = cam.getDirection().clone().multLocal(speed); //speed
        Vector3f camLeft = cam.getLeft().clone().multLocal(speed);
        if(lost[0] || life_amount[0] <=0){
            guiNode.attachChild(game_over);
        }
        
        //System.out.println(character.getPhysicsLocation());
//        System.out.println(enemies.get(1).enemy_model.getLocalTran1slation());
        
        if (character_target != null){
            System.out.println("TARGETING ENEMY: "+character_target.id);
            System.out.println("TARGET ENEMY LIFE: "+character_target.life);
            marker.setLocalTranslation(character_target.enemy_model.getLocalTranslation());
        }else{
            marker.setLocalTranslation(0,-100,0);
        }
        if(character_target != null && character_target.life<=0){
            enemies_array.remove(new Integer(character_target.id));
            character_target.remove_enemy();
            
            System.out.println("ENEMY DEAD");
            if (enemies.size()>0){
                System.out.println("MORE ENEMIES:"+enemies.size());
                character_target = cycle_enemies();
            }else{
                character_target = null;
            }            
        }
        
        
        
        
        //GUI STUFF
        nightBar.setLocalScale((amountOfLight[0]/2)*100, 10, 1);
        nightBar.setLocalTranslation((width * 0.42f)+((amountOfLight[0]/2)*100), height-10, 1);
        
        lifeBar.setLocalScale(life_amount[0], 10, 1);
        lifeBar.setLocalTranslation((width * 0f)+(life_amount[0]), height-8, 1);
        life_text.setText(life_amount[0]+"");
        score_text.setText("SCORE: "+score);
        rock_text.setText("Rock: "+rock_amount);
        water_text.setText("Water: "+water_amount);
        night_text.setText("Daylight: "+Math.round((amountOfLight[0]/2)*10));
        //END GUI STUFF
        camDir.y = 0;
        camLeft.y = 0;
        //Walk direction for player
        walkDirection.set(0, 0, 0);
        
        if(rock_amount>0){
            heal_back.setLocalTranslation(width * 0.6f, 45, 1);
        }else{
            heal_back.setLocalTranslation(width * 0.6f, 45, 3);
        }
        if(water_amount>0){
            waterball_back.setLocalTranslation(width * 0.5f, 45, 1);
        }else{
            waterball_back.setLocalTranslation(width * 0.5f, 45, 3);
        }
        
            
        
        
        
//        target = select_target();
//        System.out.println(target);
//        remove_target(target);
        
        
        for(int j = 1; j<=rock_locations.size();j++){
            Spatial check = (Spatial)rock_piles.get(j);
            if(character.getPhysicsLocation().distance((Vector3f)rock_locations.get(j)) < 5f && check.getParent() == rootNode ){                
                rootNode.detachChild((Spatial)rock_piles.get(j));                
                rock_to_replenish.add(j);
                rock_amount+= 10;             
                score+=10;
            }
        }
        //System.out.println(wood_to_replenish.size());
        if(replenish_rock && (amountOfLight[0]/2) <= 0.5f){
            for(int k = 0; k<=rock_to_replenish.size()-1;k++){
                //make_wood((Vector3f)wood_locations.get(wood_to_replenish.get(k)),k);
                rootNode.attachChild((Spatial)rock_piles.get(rock_to_replenish.get(k)));                
            }
            replenish_rock = false;
        }
        if((amountOfLight[0]/2) > 0.5f && !replenish_rock){
            replenish_rock = true;
        }
        
        if(spawn_ninjas && (amountOfLight[0]/2) >= 0.5f){
            round++;
            number_of_ninjas++; 
            for(int i = 0;i<= number_of_ninjas;i++){                
                create_ninja(ninja_id, -135, 5.1f, -133);
                ninja_id++;
            }
            spawn_ninjas = false;
        }
        if((amountOfLight[0]/2) < 0.5f && !spawn_ninjas){
            spawn_ninjas = true;
        }

//        if(character.getPhysicsLocation().distance(new Vector3f(ball_phy.getPhysicsLocation())) < 6f){
//            rootNode.detachChild(ball_geo);            
//        }
        
        ninj_walk.set(0, 0, 0);
        
        if (sneak) {
            speed = 0.1f;
            animationChannel.setSpeed(0.5f);
            animationChannel2.setSpeed(0.5f);
        }else {
            speed = 0.2f;
            animationChannel.setSpeed(1f);
            animationChannel2.setSpeed(1f);
        }

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir.normalize().mult(speed));
            
        }
        if (down) {
            walkDirection.addLocal(camDir.negate().normalize().mult(speed));
        }
        if(extinguish && water_amount >0){
            //updateTimeElapsed += (int)(tpf * 1000f);
                       
            if(closest_cube != null && closest_cube.exists){
                waterEffect.setGravity(character.getPhysicsLocation().subtract(closest_cube.box_location).mult(10f));
                System.out.println("SELECTED CUBE FOR WATER: "+closest_cube.wall_side+"--"+closest_cube.id);
                waterEffect.setLowLife(1f);
                waterEffect.setHighLife(1f);
                rootNode.attachChild(waterEffect);
                water[0] = true;
                water_amount--;
                System.out.println(water_amount);
                closest_cube.extinguish_fire();
                if (water_node.getStatus () == AudioSource.Status.Stopped) {
                   water_node.play();
                }
                
                
                
            }else{
                water[0] = false;
                water_node.stop();
                
            }
                
            waterEffect.setLocalTranslation(character.getPhysicsLocation());     
            
        }else {
            closest_cube = null;
            waterEffect.setGravity(0,0,0);
            waterEffect.setLowLife(0.0f);
            waterEffect.setHighLife(0.0f);
            //rootNode.detachChild(waterEffect);
            waterEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
        }
        if(repair && rock_amount > 0){            
            //updateTimeElapsed += (int)(tpf * 1000f);
            for(int i = 0;i<targeted.size();i++){
                if(character.getPhysicsLocation().distance(targeted.get(i).box_location) < 20f && targeted.get(i).burning){
                    closest_cube = targeted.get(i);
                }else{
                    closest_cube = null;
                } 
            }         
            //System.out.println("TRYING TO HEAL: " +closest_cube.id);
            if(closest_cube != null){
                repairEffect.setGravity(character.getPhysicsLocation().subtract(closest_cube.box_location).mult(10f));
                //System.out.println("SELECTED CUBE FOR HEAL: "+closest_cube.wall_side+"--"+closest_cube.id);
                repairEffect.setLowLife(1f);
                repairEffect.setHighLife(1f);
                rootNode.attachChild(repairEffect);
                if (heal_cube){
                  closest_cube.heal_cube(3); 
                  System.out.println("HEALING CUBE"+closest_cube.wall_side+"----"+closest_cube.id+" FOR 3"+" REMAINING LIFE: "+closest_cube.box_life);
                  heal_cube =false;
                  rock_amount--;
                }
                //
            }
                
            repairEffect.setLocalTranslation(character.getPhysicsLocation());     
            
        }else {
            closest_cube = null;
            repairEffect.setGravity(0,0,0);
                repairEffect.setLowLife(0.0f);
                repairEffect.setHighLife(0.0f);
                //rootNode.detachChild(waterEffect);
                repairEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
                
        }
        burning_boxes_time += (tpf * 1000f);
        
        if(burning_boxes_time/100>=20){
            for(int i = 0; i<targeted.size();i++){
                if(targeted.get(i).burning){
                    targeted.get(i).remove_health(1);
                    System.out.println("BURNING BOX: "+targeted.get(i).wall_side+"-"+targeted.get(i).id);
                }
            }
            burning_boxes_time = 0;
        }
        
        if (!character.onGround()) {
            airTime = airTime + tpf;
        } else {
            airTime = 0;
        }
        if (walkDirection.length() == 0) {
            if (!"IdleTop".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("IdleTop", 1f);
            }
        } else {
            character.setViewDirection(walkDirection);
            if (airTime > 1f) {
                if (!"JumpLoop".equals(animationChannel.getAnimationName())) {
                    animationChannel.setAnim("JumpLoop");
                }
            } else if (!"RunBase".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("RunBase", 0.7f);
                animationChannel2.setAnim("RunTop", 0.7f);
            }
        }

                if (pursuit) {
                  pursuitMode[0]=pursuit;
                  animationChannelNPC3.setAnim("Walk");
                  
                }
        
//        
//          
              if (fireball_sent){
                //fireEffect.setGravity(ninja.getPhysicsLocation().subtract(character.getPhysicsLocation()));
                fireball_back.setLocalTranslation(width * 0.4f, 45, 3);
                fireEffect.setLowLife(0.5f);
                fireEffect.setHighLife(3f);
            
                //fireEffect.getParticleInfluencer().setVelocityVariaftion(0.3f);
                rootNode.attachChild(fireEffect);
                fireEffect.setLocalTranslation(fireball.getLocalTranslation());
              }else{
                  fireball_back.setLocalTranslation(width * 0.6f, 45, 1);
                  fireEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
              }


        //ninja.setWalkDirection(ninj_walk);
        character.setWalkDirection(walkDirection);
        
        //System.out.println(character.getPhysicsLocation().distance(new Vector3f(77.29529f, 2f, -4.121178f)));
        
        //IF CHARACTER IS NEAR DOOR, TELEPORT HIM ON TOP
        teleport_listener();
        //IF CHAR IS NEAR WELL, MAXIMIZE HIS WATER
        if(character.getPhysicsLocation().distance(new Vector3f(47.284966f, 0.0f, 87.669785f)) < 8.5f){
            if (water_amount < 50){
                water_amount = 50;
            }
            
        }
        
        
        //System.out.println(character.getPhysicsLocation());
        
        
        
        
    }
    public void teleport_listener(){
        if(character.getPhysicsLocation().distance(new Vector3f(77.29529f, 1f, -4.121178f)) < 5.5f || character.getPhysicsLocation().distance(new Vector3f(90.39795f, 1f, -7.83638f)) < 4.5f){
            System.out.println(character.getPhysicsLocation());
            character.setPhysicsLocation(character_origin);
        }
    }
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("CharRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("CharUp")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("CharDown")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        }else if (binding.equals("Sneak")) {
            if (value) {
                sneak = true;
            } else {
                sneak = false;
            }
        } else if (binding.equals("CharSpace")) {
            character.jump();
            //System.out.println(character.getPhysicsLocation());
        } else if (binding.equals("Cycle")) {
            tab_pressed = !tab_pressed;
            if(enemies.size()>0 && !tab_pressed){
                character_target = cycle_enemies();
                
            }
            
            //System.out.println(character.getPhysicsLocation());
        } else if (binding.equals("Fireball") && !fireball_sent ) {
            
            
            if (character_target != null){
                fireball_sent = true;
                create_fireball();
                fireball_motion_path(character.getPhysicsLocation(),character_target.enemy_model.getLocalTranslation(),character_target);
                fire_motion_control.play();
                
                
//                fireball_projectile this_fireball = new fireball_projectile(rootNode,fireEffect,assetManager);
//                fireball_sent = this_fireball.fireball_motion_path(character.getPhysicsLocation(), character_target.enemy_model.getLocalTranslation(), character_target);
//                this_fireball.fire_motion_control.play();
                
            }
            
        } else if (binding.equals("Extinguish")) {
            extinguish = !extinguish;            
            for(int i = 0;i<targeted.size();i++){
                if(character.getPhysicsLocation().distance(targeted.get(i).box_location) < 13f && targeted.get(i).box_life < targeted.get(i).max_box_life){
                    closest_cube = targeted.get(i);
                }
            } 
            if(!animationChannel2.getAnimationName().equals("DrawSwords")){
                animationChannel2.setAnim("DrawSwords",0.7f);
                animationChannel.setAnim("DrawSwords",0.7f);
            }
        } else if (binding.equals("Repair")) {
            repair = !repair;   
            heal_cube = true;
            if(!animationChannel2.getAnimationName().equals("DrawSwords")){
                animationChannel2.setAnim("DrawSwords",0.7f);
                animationChannel.setAnim("DrawSwords",0.7f);
            }
            
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    //This is the vault that is going to be defended SinBad
    protected void make_door() {
        
        door = assetManager.loadModel("Models/door/door.j3o");   
        door.scale(0.05f);
        /* We start out with a horizontal object */ 



        Quaternion pitch90 = new Quaternion();
        pitch90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));

        door.setLocalRotation(pitch90);


        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture door_text = assetManager.loadTexture("Textures/logs_texture.jpg");
        door_text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", door_text);
        
        RigidBodyControl door_phy = new RigidBodyControl(0);
        door.addControl(door_phy);
        bulletAppState.getPhysicsSpace().add(door_phy);
        door_phy.setPhysicsLocation(new Vector3f(79, 0f, -8.354963f));
        mat.setColor("Color", ColorRGBA.Brown);   // set color of material to blue
        door.setMaterial(mat); 
        rootNode.attachChild(door);
    }
    protected void make_well() {
        
        well = assetManager.loadModel("Models/well/well.j3o");   
        well.scale(0.02f);
        /* We start out with a horizontal object */ 
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
//        Texture well_text = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
//        well_text.setWrap(Texture.WrapMode.Repeat);
//        mat.setTexture("ColorMap", well_text);
        
        RigidBodyControl well_phy = new RigidBodyControl(0);
        well.addControl(well_phy);
        bulletAppState.getPhysicsSpace().add(well_phy);
        well_phy.setPhysicsLocation(new Vector3f(47.284966f, 0.0f, 87.669785f));
//        mat.setColor("Color", ColorRGBA.Brown);   // set color of material to blue
//        well.setMaterial(mat); 
        rootNode.attachChild(well);
    }
}
