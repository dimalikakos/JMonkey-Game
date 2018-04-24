/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Alikakos_Game;

/**
 *
 * @author Dimitrios
 */

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

import com.jme3.audio.AudioSource;
import com.jme3.audio.AudioSource.Status;

import com.jme3.collision.CollisionResults;
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
 * if the NPC is in pursuitMode it emits a sound. If not it stops emiting the
 * sound:  23-11-2016  D.Vogiatzs
 */
public class NPCSoundControl extends AbstractControl {

    private boolean pursuitModeInt[];
    boolean[] enemy_fireball;
    private Node npcAudioNode;
    private AudioNode audio,fireball_audio,water_audio,heal_audio,taken_damage_audio;
    private Node rootNode;

    public NPCSoundControl(boolean[] pursuitMode, Node rootNode, AssetManager assetManager, Node model,boolean[] enemy_fireball) {

        pursuitModeInt = pursuitMode;
        this.npcAudioNode = npcAudioNode;
        System.out.println(pursuitModeInt[0]);

        audio = new AudioNode(assetManager, "Sounds/Effects/fireballShoot4.wav");
//        fireball_audio = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
//        water_audio = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
//        heal_audio = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
//        taken_damage_audio = new AudioNode(assetManager, "Sounds/Environment/River.ogg");
        
        audio.setLooping(true);
        audio.setPositional(true);

        audio.setRefDistance(10f);
        audio.setMaxDistance(10000f);
        audio.setVolume(0.6f);
        this.rootNode = rootNode;
        this.enemy_fireball = enemy_fireball;

        //   rootNode.attachChild(audio);
        // audio.play();


    }

    @Override
    protected void controlUpdate(float tpf) {
        
        if (enemy_fireball[0]) {
            if (audio.getStatus() == Status.Stopped) {
                audio.play();
                System.out.println("Sound is started");
            }
            // System.out.println("control update " + audio);
        } else if (!pursuitModeInt[0]) {
            if (audio.getStatus() != Status.Stopped) {
                System.out.println("control update " + audio);
                audio.stop();
            }



        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
