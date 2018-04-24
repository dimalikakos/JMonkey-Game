package Alikakos_Game;

/**
 *
 * @author Dimitrios Alikakos, Fall 2016
 */

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class enemy_control extends AbstractControl {
    boolean [] lost;
    float [] amountOfLight = new float[1];
    BulletAppState bulletAppState;
    AssetManager assetManager;
    Vector3f enemy_walk = new Vector3f(0,0,0);
    Node rootNode;
    CharacterControl enemy;
    CharacterControl character;
    AnimChannel anim_channel;
    boolean  enemy_fireball [];
    boolean pursuit;
    int random_number;
    Random random_generator = new Random();
    Random random_generator2 = new Random();
    wall_cube target;
    public HashMap <Integer, wall> walls = new HashMap<Integer, wall>();
    public enemy this_enemy;
    ArrayList<wall_cube> targeted = new ArrayList<wall_cube>();
    int max_box_health = 9;
    wall_cube weakest_box;
    boolean attacking = false;
    boolean point1 = false;
    boolean point2 = false;
    boolean point1_return = false;
    boolean point2_return = false;
    boolean fireball_sent = false;
    boolean arrived_at_closest_wallside = false;
    boolean arrived_at_closest_wallside_return = false;
    int closest_wallside = 0;
    boolean found_right_location = false;
    int random_number2 = random_generator2.nextInt(5) + 1;
    Vector3f point1_location = new Vector3f(-126.12691f, 2.913722f, -80.99899f+random_number2);
    Vector3f point2_location = new Vector3f(33.03443f, 3.1291838f, -26.36737f+4*random_number2);
    Vector3f wallside2 = new Vector3f(104.60103f, 3.0285974f, 58.421227f+random_number2); //WALLSIDE 2 POINT
    Vector3f wallside3 = new Vector3f(164.73788f, 6.139429f, 5.245569f); //WALLSIDE 3 POINT
    Vector3f wallside4 = new Vector3f(155.0075f, 3.80113f, -86.44238f-random_number2); //WALLSIDE 4 POINT
    Vector3f ninja_house = new Vector3f(-133.4438f, 5.8996134f, -122.24991f);
    
    Vector3f[] wallside_locations = {new Vector3f(0,0,0),point2_location,wallside2,wallside3,wallside4};
    
    int[] life_amount;
    boolean[] wallsides_reached = new boolean[10];
    
    int current_standing_wall = 0;
    wall_cube selected;
    fireball_projectile this_fireball;
    
    int attack_timer = 0;
    
    boolean attacking_character;
    
    public enemy_control(Node rootNode,AssetManager assetManager,CharacterControl enemy,CharacterControl character,AnimChannel anim_channel,HashMap walls,BulletAppState bulletAppState,enemy this_enemy,int[] life_amount,ArrayList targeted,boolean [] lost,float [] amountOfLight,boolean[] enemy_fireball){
        this.character = character;
        this.enemy = enemy;        
        this.anim_channel = anim_channel;
        this.walls = walls;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;
        this.this_enemy = this_enemy;
        this.assetManager = assetManager;
        this.life_amount = life_amount;
        this.targeted = targeted;
        this.lost = lost;
        this.amountOfLight = amountOfLight;
        this.enemy_fireball = enemy_fireball;
        life_amount[0] = 90;
                     
        
        
    }
    //METHOD TO REMOVE CUBE
    protected void remove_target(wall_cube target){
        //wall_cube j = wall_boxes.get(target);
        
        walls.get(target.wall_side).remove_box(target);
        target.exists = false;
        rootNode.detachChild(target.box);
        bulletAppState.getPhysicsSpace().remove(target.physics);

    }
    //METHOD FOR ENEMY TO SELECT CUBE, BASED ON WALL HEIGHT, CUBE LIFE. METHOD TO CHANGE WALL IF DEFENDERS ARE STANDING ON WALL IS FURTHER DOWN
   public wall_cube select_target_v2(){
        
        random_number = random_generator.nextInt(walls.size()) + 1;
        //int random_number2 = random_generator.nextInt(10) + 1;
//        if(random_number2 == 1){                                    //ADDING 1 IN 8 PROBABILITY OF TARGETING RANDOM BRICK
//            if(walls.get(random_number).boxes.size()>0){
//                target = walls.get(random_number).get_highest_box();    //HERE IS CODE FOR THE ENEMY TO SELECT THE TOP BRICK OF A RANDOM WALL. IT SEEMS LIKE A BUG SO I REMOVED IT.
//            }
/*}else*/ if(find_lowest_wall().boxes.size()<12){               //ADDING 7 IN 8 PROBABILITY OF TARGETING LOWEST WALL BRICK
            wall selected_wall = find_lowest_wall();
            if(selected_wall.boxes.size()>0){
                target = selected_wall.get_highest_box();
                System.out.println("SYSTEM SELECTING LOWEST BOX "+ target.id );
            }
            
        } else if(targeted.size()>0 && find_weakest_box().box_life < max_box_health){  // IN THE BEGGINING, IF THE ENEMIES HAVE TARGETED AND DAMAGED SOME BOXES, THE SHOULD GO TO THOSE BOXES AND DAMAGE THEM AGAIN IF THEY LEFT.
            target = find_weakest_box();  
        }else{
            target = walls.get(random_number).get_highest_box();
        }
        targeted.add(target);
        return target;
        
        
            
    }        
   //METHOD TO CHANGE WALL IF DEFENDERS ARE STANDING ON THE WALL
    protected wall_cube find_alternative_target(int currently_attacking_side){
        boolean first = true;
        wall min_wall = null;
        int min_boxes = 1000;
        for(int i = 1; i<=walls.size();i++){
            if (i!=currently_attacking_side){
                
                wall a_wall = walls.get(i);
                if(first){
                    min_wall = a_wall;
                    first = false;
                }
                if(a_wall.boxes.size()<min_boxes){
                    min_wall = a_wall;
                    min_boxes = a_wall.boxes.size();
                }
            }            
        }
        
        return min_wall.get_highest_box();
    }
    //METHOD TO FIND LOWEST WALL
    protected wall find_lowest_wall(){
        wall min_wall = walls.get(random_generator.nextInt(walls.size()) + 1);
        int min_boxes = 1000;
        for(int i = 1; i<=walls.size();i++){
            wall a_wall = walls.get(i);
            if(a_wall.boxes.size()<min_boxes){
                min_wall = a_wall;
                min_boxes = a_wall.boxes.size();
            }
            
        }
        
        return min_wall;
    }
    //METHOD TO FIND CUBE WITH LESS LIFE
    protected wall_cube find_weakest_box(){
        
        weakest_box =  targeted.get(0);
        for(int i = 1;i<targeted.size();i++){
            wall_cube j = targeted.get(i);
            if(j.box_life<weakest_box.box_life){
                weakest_box = j;
            }
        }
        return weakest_box;
    }
    //METHOD TO GET WHERE DEFENDERS ARE STANDING
    public int get_wall_standing(){
        
        if(character.getPhysicsLocation().x<79f || character.getPhysicsLocation().x>141f || character.getPhysicsLocation().z>25f || character.getPhysicsLocation().z<-35f){
            return 5;
        }else if(character.getPhysicsLocation().x<90f && character.getPhysicsLocation().x>80f && character.getPhysicsLocation().z<13f && character.getPhysicsLocation().z>-23f){
            return 1;
        }else if(character.getPhysicsLocation().x<127f && character.getPhysicsLocation().x>91f && character.getPhysicsLocation().z<25f && character.getPhysicsLocation().z>14f){
            return 2;
        }else if(character.getPhysicsLocation().x<140f && character.getPhysicsLocation().x>130f && character.getPhysicsLocation().z<12f && character.getPhysicsLocation().z>-23f){
            return 3;
        }else if(character.getPhysicsLocation().x<130f && character.getPhysicsLocation().x>91f && character.getPhysicsLocation().z<-25f && character.getPhysicsLocation().z>-35f){
             return 4;       
        }
        
        
        
        return 0;
    }
    //RESET VARIABLES FOR ENEMY PATH
    public void reset_going_path(){
        point1 = false;
        point2 = false;
        attacking_character = false;
        pursuit = false;
        arrived_at_closest_wallside = false;
        found_right_location = false;
    }
    //RESET VARIABLES FOR ENEMY REUTRN PATH
    public void reset_return_path(){
        point1_return = false;
        point2_return = false;
        arrived_at_closest_wallside_return = false;
        
    }
    

    @Override
    protected void controlUpdate(float tpf) {
        if(pursuit){
            //pursuitMode[0] = true;
        }
        //System.out.println(get_wall_standing());
        attack_timer += (tpf*1000); // The enemy attacks once every 3 seconds
        //System.out.println(attack_timer);
        
        
        
        if (!lost[0]){ // IF GAME IS NOT LOST
            if (this_enemy.life <=0){ // IF ENEMY LIFE IS NOT 0
                
                this_enemy.enemy_model.setLocalTranslation(-1, -100, -1);
                
            }else if(this_enemy.stamina <=0 || Math.round((amountOfLight[0]/2)*10) < 5){      
                reset_going_path();
                if(!anim_channel.getAnimationName().equals("Walk")){
                    anim_channel.setAnim("Walk");
                }
                if(!arrived_at_closest_wallside_return){
                    closest_wallside = find_closest_wallside();
                    enemy_move_to_point(wallside_locations[closest_wallside]);
                    if(enemy.getPhysicsLocation().distance(wallside_locations[closest_wallside]) < 4.5f){
                        arrived_at_closest_wallside_return = true;
                        closest_wallside++;
                        if (closest_wallside > 4){
                            closest_wallside = 0;
                        }
                    }  
                }
                
                if (arrived_at_closest_wallside_return && !point2_return){
                    if (enemy.getPhysicsLocation().distance(point2_location) < 4.5f){
                        point2_return = true;
                    }else{
                        enemy_move_to_point(wallside_locations[closest_wallside]);                
                        if (enemy.getPhysicsLocation().distance(wallside_locations[closest_wallside]) < 4.5f){
                            closest_wallside++;
                            if (closest_wallside > 4){
                                closest_wallside = 0;
                            }
                        }
                    }
                }
                if(point2_return && !point1_return){
                    enemy_move_to_point(point1_location);
                    if(enemy.getPhysicsLocation().distance(point1_location) < 4.5f){
                        point1_return = true;
                    }          
                }
                if(point1_return){
                    if(enemy.getPhysicsLocation().distance(ninja_house) > 4.5f){
                        enemy_move_to_point(ninja_house);
                    }else{
                        this_enemy.stamina = 100;
                    }
                }
                
                enemy.setWalkDirection(enemy_walk);
            }else{
                reset_return_path();
                enemy_walk.set(0, 0, 0);
                if (!attacking){
                    selected = select_target_v2();
                    current_standing_wall = get_wall_standing();
                        if (selected.wall_side == current_standing_wall){
                            selected = find_alternative_target(current_standing_wall);
                        }
                    attacking = true;    
                    arrived_at_closest_wallside = false;
                    found_right_location = false;
                    System.out.println("NINJA ID: "+this_enemy.id+" WALL: "+selected.wall_side+" TARGETING: "+selected.id);
                }
                if(selected != null && !selected.exists && attacking){
                    remove_target(selected);
                    //targeted.remove(selected);
                    if(walls.get(selected.wall_side).boxes.size()==0){
                        lost[0] = true;
                    }else{
                        selected = select_target_v2();
                        current_standing_wall = get_wall_standing();
                        if (selected.wall_side == current_standing_wall){
                            selected = find_alternative_target(current_standing_wall);
                        }
                        arrived_at_closest_wallside = false;
                        found_right_location = false;
                    }
                }
                //System.out.println("NINJA ID:"+this_enemy.id+" WALL:"+selected.wall_side+" ID:"+selected.id);

                if(get_wall_standing() == 5 && !attacking_character){
                    pursuit = true;
                    if(!anim_channel.getAnimationName().equals("Walk")){
                        anim_channel.setAnim("Walk");
                    }
                }else{
                    pursuit = false;
                }
                if (pursuit){            
                    enemy_walk = character.getPhysicsLocation().subtract(enemy.getPhysicsLocation()).normalize();
                    enemy_walk = enemy_walk.multLocal(0.25f);
                    enemy.setViewDirection(enemy_walk.mult(-1.1f));
                    arrived_at_closest_wallside = false;
                    found_right_location = false;
                }
                if (character.getPhysicsLocation().distance(enemy.getPhysicsLocation()) <= 5f) {
                    attacking_character = true;
                    enemy_walk = character.getPhysicsLocation().subtract(enemy.getPhysicsLocation()).normalize();
                    enemy_walk = enemy_walk.mult(0.0001f) ;
                    enemy.setViewDirection(enemy_walk.mult(-1.1f));
                    if(attack_timer/100>2+random_number){           // ENEMY ATTACKS EVERY 3-7 SECONDS DEPENDING RANDOM NUMBER
                        attack_timer = 0;
                        life_amount[0] -= 10;
                        this_enemy.stamina -= 5+random_number2*2; // STAMINA DEPLEATED AND A DIFFERENT RATE FOR EACH ENEMY
                        System.out.println("NINJA: "+this_enemy.id+" IS ATTACKING");
                    }
                    if(!anim_channel.getAnimationName().equals("Attack1")){
                        anim_channel.setAnim("Attack1");
                    }
                    pursuit = false;
                        //pursuitMode[0]=pursuit;
                    point1 = true;
                    point2 = true;
                }else{
                    attacking_character = false;
                    
                }      
                if (!point1 && !pursuit && !attacking_character){
                    if(!anim_channel.getAnimationName().equals("Walk")){
                        anim_channel.setAnim("Walk");
                    }
                    enemy_walk = point1_location.subtract(enemy.getPhysicsLocation()).normalize();
                    enemy_walk = enemy_walk.multLocal(0.25f);
                    enemy.setViewDirection(enemy_walk.mult(-1.1f));
                    if(enemy.getPhysicsLocation().distance(point1_location) < 4.5f){
                        point1 = true;
                    }
                }
                if(point1 && !point2 && !pursuit && !attacking_character){
                    if(!anim_channel.getAnimationName().equals("Walk")){
                        anim_channel.setAnim("Walk");
                    }
                    enemy_walk = point2_location.subtract(enemy.getPhysicsLocation()).normalize();
                    enemy_walk = enemy_walk.multLocal(0.25f);
                    enemy.setViewDirection(enemy_walk.mult(-1.1f));
                    if(enemy.getPhysicsLocation().distance(point2_location) < 4.5f){
                        point2 = true;
                    }            
                }
                if(point2 && !pursuit && !arrived_at_closest_wallside && !attacking_character){
                    if(!anim_channel.getAnimationName().equals("Walk")){
                        anim_channel.setAnim("Walk");
                    }
                    closest_wallside = find_closest_wallside();
                    enemy_move_to_point(wallside_locations[closest_wallside]);
                    if(enemy.getPhysicsLocation().distance(wallside_locations[closest_wallside]) < 4.5f){
                        arrived_at_closest_wallside = true;
                        closest_wallside++;
                        if (closest_wallside > 4){
                            closest_wallside = 0;
                        }
                    }  
                }
                if (arrived_at_closest_wallside && !found_right_location && !attacking_character && !pursuit){
                    if (enemy.getPhysicsLocation().distance(wallside_locations[selected.wall_side]) < 4.5f){
                        found_right_location = true;
                    }else{
                        enemy_move_to_point(wallside_locations[closest_wallside]);                
                        if (enemy.getPhysicsLocation().distance(wallside_locations[closest_wallside]) < 4.5f){
                            closest_wallside++;
                            if (closest_wallside > 4){
                                closest_wallside = 0;
                            }
                        }
                    }
                }
                
                enemy.setWalkDirection(enemy_walk);

                if(found_right_location && !fireball_sent && !pursuit){
                    
                    
                    if(!anim_channel.getAnimationName().equals("Attack2")){
                        anim_channel.setAnim("Attack2");
                    }
                    enemy.setViewDirection(selected.box_location.mult(-1f));
                    
                    this_fireball = new fireball_projectile(rootNode,assetManager);
                    this_fireball.fireball_motion_path(enemy.getPhysicsLocation(), selected.box_location, selected);
                    this_fireball.fire_motion_control.play();
                    fireball_sent = true;       
                    enemy_fireball[0] = true;
                    this_enemy.stamina -= 5+random_number2*2; // STAMINA DEPLEATED AND A DIFFERENT RATE FOR EACH ENEMY
                    attacking = false;
                    
                }
            }
   
            if (fireball_sent){
                fireball_sent = this_fireball.get_fireball_status();
                enemy_fireball[0] = fireball_sent;
                this_fireball.fireEffect.setLowLife(0.5f);
                this_fireball.fireEffect.setHighLife(3f);
                //fireEffect.getParticleInfluencer().setVelocityVariaftion(0.3f);
                this_fireball.rootNode.attachChild(this_fireball.fireEffect);
                this_fireball.fireEffect.setLocalTranslation(this_fireball.fireball.getLocalTranslation());

            }
            
        }else{
            System.out.println("GAME LOST");
        }
        
       
    
    }
    
    
    public int find_closest_wallside(){
        float min_distance = 1000f;
        int closest_wallside = 0;
        float distance = 0f;
        for (int i = 1; i<5;i++){
            distance = enemy.getPhysicsLocation().distance(wallside_locations[i]);
            if(distance < min_distance){
                min_distance = distance;
                closest_wallside = i;
            }
        }
        return closest_wallside;
    }
    
    public void enemy_move_to_point(Vector3f destination){
        enemy_walk = destination.subtract(enemy.getPhysicsLocation()).normalize();
        enemy_walk = enemy_walk.multLocal(0.5f);
        enemy.setViewDirection(enemy_walk.mult(-1.1f));
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
