
package Alikakos_Game;

/**
 *
 * @author Dimitrios Alikakos
 */

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

import com.jme3.audio.AudioSource;
import com.jme3.audio.AudioSource.Status;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * if the user presses L, he shhots
 */
public class UserSoundControl extends AbstractControl implements ActionListener {

    private Node npcAudioNode;
    private AudioNode fireball,water;
    private Node rootNode;
    private InputManager inputManager;
    private boolean targeted_target = false;
    private boolean once = true;
    boolean [] water_bool;

    public UserSoundControl(Node rootNode, AssetManager assetManager, InputManager inputManager, Node model, boolean [] water_bool) {
        this.inputManager = inputManager;
        this.npcAudioNode = npcAudioNode;
        this.water_bool = water_bool;

        fireball = new AudioNode(assetManager, "Sounds/Effects/fireballShoot4.wav");
        water = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
        
        fireball.setLooping(true);
        fireball.setPositional(true);
        
        water.setLooping(true);
        water.setPositional(true);

        fireball.setRefDistance(10f);
        fireball.setMaxDistance(10000f);
        fireball.setVolume(0.6f);
        
        water.setRefDistance(10f);
        water.setMaxDistance(10000f);
        water.setVolume(0.6f);
        
        this.rootNode = rootNode;

        setupKeys();
        //   rootNode.attachChild(audio);
        //audio.play();


    }

    private void setupKeys() {
        this.inputManager.addMapping("Fireball", new KeyTrigger(KeyInput.KEY_1));  //It's for activating NPC; it follows a motion path
        this.inputManager.addListener(this, "Fireball");
        this.inputManager.addMapping("Cycle", new KeyTrigger(KeyInput.KEY_TAB));  //It's for activating NPC; it follows a motion path
        this.inputManager.addListener(this, "Cycle");
        this.inputManager.addMapping("Water", new KeyTrigger(KeyInput.KEY_2));  //It's for activating NPC; it follows a motion path
        this.inputManager.addListener(this, "Water");
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Fireball") && targeted_target ) {
            fireball.playInstance();            
        }
        if (binding.equals("Cycle") ) {
            targeted_target = true;
        }
        if (binding.equals("Water") ) {
            if(once){
               // water.play(); 
            }else{
                water.stop();
            }
            once = !once;
            
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
