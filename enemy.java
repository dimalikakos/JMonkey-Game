/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Alikakos_Game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;

/**
 *
 * @author Xenor
 */
public class enemy {
    int id;
    int life = 20;
    int stamina = 100;;    
    Spatial enemy_model;
    Node rootNode;
    HashMap enemies = new HashMap();
    public enemy(int id, Spatial enemy_model,Node rootNode,HashMap enemies){
        this.id = id;
        this.enemy_model = enemy_model;
        this.enemies = enemies;
    }
    public void remove_health(int damage){
        life = life-damage;        
    }
    public void remove_enemy(){
        enemies.remove(id);
        enemy_model.setLocalTranslation(-1, -1, -1);
    }
    
}
