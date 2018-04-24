
package Alikakos_Game;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;


public class fireball_projectile {
    
    Geometry fireball;
    Node rootNode;
    MotionPath fire_path;
    MotionEvent fire_motion_control;
    ParticleEmitter fireEffect;
    AssetManager assetManager;
    int damage =1;
    public boolean fireball_sent = true;
    
    
    public fireball_projectile(Node rootNode,AssetManager assetManager){
        this.rootNode = rootNode;                
        this.assetManager = assetManager;
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
        Material stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey tex_key = new TextureKey("Textures/fireball_tex.jpg");
        tex_key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(tex_key);
        stone_mat.setTexture("ColorMap", tex);
        
        fireEffect.setLowLife(0.5f);
        fireEffect.setHighLife(3f);
        
        
        Sphere s = new Sphere(8, 8, 0.8f);
        Box b = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
        fireball = new Geometry("Box", s);
        
        
        //fireball = new Geometry("cannon ball", sphere);
        fireball.setMaterial(stone_mat);
        rootNode.attachChild(fireball);
        
    }
    
    public void fireball_motion_path(Vector3f start, Vector3f end,final wall_cube target) {
        fire_path = new MotionPath();
        fire_path.addWayPoint(new Vector3f(start));
        fire_path.addWayPoint(new Vector3f(end));
        
        // fire_path.enableDebugShape(assetManager, rootNode);

        fire_motion_control = new MotionEvent(fireball, fire_path);
        fire_motion_control.setDirectionType(MotionEvent.Direction.Path);
        fire_motion_control.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));

        //fire_motion_control.setInitialDuration(30f);
        
        fire_motion_control.setSpeed(6f);

        

        fire_path.addListener(new MotionPathListener() {
            

            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                
                if (fire_path.getNbWayPoints() == wayPointIndex + 1) {     
                    fireEffect.setLocalTranslation(new Vector3f(-100,-100,-100));
                    //rootNode.detachChild(fireEffect);
                    
                    fireball_sent = false;
                    target.remove_health(damage);                    
                    fireball.setLocalTranslation(-100f, -100f, -100f);
                    rootNode.detachChild(fireball);
                    rootNode.detachChild(fireEffect);
                }
                
            }
        });
        
    }
    public boolean get_fireball_status(){
        return fireball_sent;
    }
    
}
