package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.test.GameCore;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    public int hp = 20;
    private final int maxhp = 40;
    public Player(Animation left, Animation right,
                  Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    public int getHp(){
        //dead, return 0
        if(!isAlive()){return 0;}
        //Moving or Still?
        int v_x = Math.round(Math.abs(getVelocityX()));
        //Still
        if(v_x == 0 && onGround == true){
            if(GameCore.critical_time >= 1000){
                hp+=5;
                GameCore.critical_time = 0;
            }
        }
        //Moving
        else if(v_x != 0 || onGround == false){
            if(GameCore.critical_time >= 1000){
                hp+=1;
                GameCore.critical_time = 0;
            }
        }
        //Shooting
        //never exceed 40
        if(hp > maxhp){hp = maxhp;}
        if(hp < 0){hp = 0;}
        return hp;
    }
    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }

    public void wakeUp() {
        // do nothing
    }


    /**
     Makes the player jump if the player is on the ground or
     if forceJump is true.
     */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }


}