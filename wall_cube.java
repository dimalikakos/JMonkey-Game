
package Alikakos_Game;


import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;


public class wall_cube {
    public int id;  // 0 - 11
    int max_box_life = 10;
    public RigidBodyControl physics;
    public Spatial box;
    public int box_life=0;
    public Vector3f box_location;
    public int wall_side;
    public boolean exists = true;
    public boolean burning = false;
    AudioNode burning_audio;
    ParticleEmitter fireEffect;
    AssetManager assetManager;
    Node rootNode;
    
    
    public wall_cube(Spatial cube,int life, Vector3f location, int side,int key, RigidBodyControl vault_phy,AssetManager assetManager,Node rootNode){
        box = cube;
        box_life = life;
        max_box_life = life;
        box_location = location;
        wall_side = side;
        id = key;
        physics = vault_phy;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        
        burning_audio = new AudioNode(assetManager, "Sounds/Effects/burning.wav");
        burning_audio.setLooping(true);
        burning_audio.setPositional(true);
        burning_audio.setRefDistance(10f);
        burning_audio.setMaxDistance(10000f);
        burning_audio.setVolume(5f);
        
        
        fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setImagesX(2);
        fireEffect.setImagesY(2); // 2x2 texture animation
        fireEffect.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fireEffect.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        // fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fireEffect.setStartSize(5f);
        fireEffect.setEndSize(10f);
        fireEffect.setLowLife(2f);
        fireEffect.setHighLife(3f);
        Material stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey tex_key = new TextureKey("Textures/fireball_tex.jpg");
        tex_key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(tex_key);
        stone_mat.setTexture("ColorMap", tex);
        
        fireEffect.setLowLife(0.5f);
        fireEffect.setHighLife(3f);
    }
    public void remove_health(int damage){
        box_life = box_life - damage;
        if(box_life<=0){
            exists = false;
            rootNode.attachChild(fireEffect);
            fireEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
            burning = false;
            burning_audio.stop();
        }else {
            fireEffect.setLowLife(0.5f);
            fireEffect.setHighLife(3f);
            fireEffect.setGravity(0,20f,0);
                //fireEffect.getParticleInfluencer().setVelocityVariaftion(0.3f);
            rootNode.attachChild(fireEffect);
            fireEffect.setLocalTranslation(new Vector3f(box.getLocalTranslation().x,box.getLocalTranslation().y+3f,box.getLocalTranslation().z));
            burning = true;
            burning_audio.play();
        }
    }
    public void heal_cube(int heal){
        box_life = box_life+heal;
        if(box_life >= max_box_life){
            box_life = max_box_life;
                       
        }        
    }
    public void extinguish_fire(){
        rootNode.attachChild(fireEffect);
        fireEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
        burning = false;
        burning_audio.stop();
        
    }
    
    
    
    
    
}
