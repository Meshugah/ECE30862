package com.brackeen.javagamebook.tilegame;

        import java.awt.*;
        import java.awt.event.KeyEvent;
        import java.util.Iterator;

        import javax.sound.midi.Sequence;
        import javax.sound.midi.Sequencer;
        import javax.sound.sampled.AudioFormat;

        import com.brackeen.javagamebook.graphics.*;
        import com.brackeen.javagamebook.sound.*;
        import com.brackeen.javagamebook.input.*;
        import com.brackeen.javagamebook.test.GameCore;
        import com.brackeen.javagamebook.tilegame.sprites.*;

/**
 GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

    private boolean shooting;
    private int ct = 0;
    private boolean stopShooting = false;

    //private int wt = 0;
    
    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
            new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;
    private Sound shootSound;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction shoot;


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
                screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
                resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");
        shootSound = soundManager.getSound("sounds/gun.mid");

        // start music
//        midiPlayer = new MidiPlayer();
//        Sequence sequence =
//                midiPlayer.getSequence("sounds/music.midi");
//        midiPlayer.play(sequence, false);
//        toggleDrumPlayback();
    }


    /**
     Closes any resources used by the GameManager.
     */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        shoot = new GameAction("shoot");

        inputManager = new InputManager(
                screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_S);

    }

    //managin time
    private long currentTime = System.currentTimeMillis();
    private long waitTime = System.currentTimeMillis();

    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
        }
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                waitTime = System.currentTimeMillis();
            	player.hCount();								
            	//increases health over time while walking
                velocityX-=player.getMaxSpeed();
                player.stopWaiting();							
                //stops the counter while standing still
            }
            if (moveRight.isPressed()) {
                waitTime = System.currentTimeMillis();
            	player.hCount();
                velocityX+=player.getMaxSpeed();
                player.stopWaiting();
            }
            if (jump.isPressed()) {
                waitTime = System.currentTimeMillis();
                player.jump(false);
                player.stopWaiting();
            }

            if (shoot.isPressed()){
                int ct=0;
                //midiPlayer.close();
                midiPlayer = new MidiPlayer();

                Sequence sequence = midiPlayer.getSequence("sounds/gun.mid");
                midiPlayer.play(sequence, false);

                //toggleDrumPlayback();
                //create Bullet
                if(ct == 0){
                    if(!stopShooting){
                        shooting = true;
                        ct++;
                        currentTime = System.currentTimeMillis();
                    }else{
                        if(System.currentTimeMillis() - currentTime >= 1000){
                            shooting = true;
                            ct++;
                            currentTime = System.currentTimeMillis();
                            stopShooting = false;
                        }
                    }
                }else if(ct == 10){
                    if(System.currentTimeMillis() - currentTime >= 1000){			
                    	//time after 10 shots is 1 sec
                        shooting = false;
                        ct = 0;
                        currentTime = System.currentTimeMillis();
                        stopShooting = true;

                    }
                }else if(ct < 10){
                    if(System.currentTimeMillis() - currentTime >= 300){			
                    	//time between shots is 300 ms
                        shooting = true;
                        ct++;
                        currentTime = System.currentTimeMillis();
                    }
                }else{
                    stopShooting = true;
                    if(System.currentTimeMillis() - currentTime >= 1000){			
                        shooting = true;
                        ct = 2;
                        currentTime = System.currentTimeMillis();
                        stopShooting = false;
                    }
                }
            }
            if(System.currentTimeMillis() - currentTime >= 1000){					
            	//if you don't shoot 10 sec, count is reset
            	ct = 0;
            }
            if(System.currentTimeMillis() - waitTime >= 1000){						
            	//if you wait 1 sec, health is increased
                //wt = 0;
                waitTime = System.currentTimeMillis();
                player.Health(5);
            }
            player.setVelocityX(velocityX);
        }

    }

    public void draw(Graphics2D g) {
        renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());

    }


    /**
     Gets the current map.
     */
    public TileMap getMap() {
        return map;
    }


    /**
     Turns on/off drum playback in the midi music (track 1).
     */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                    !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
     Gets the tile that a Sprites collides with. Only the
     Sprite's X or Y should be changed, not both. Returns null
     if no collision is detected.
     */
    public Point getTileCollision(Sprite sprite,
                                  float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
                toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
                toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                        map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
     Checks if two Sprites collide with one another. Returns
     false if the two Sprites are the same. Returns false if
     one of the Sprites is a Creature that is not alive.
     */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect

        return (s1x < s2x + s2.getWidth() &&
                s2x < s1x + s1.getWidth() &&
                s1y < s2y + s2.getHeight() &&
                s2y < s1y + s1.getHeight());
    }


    /**
     Gets the Sprite that collides with the specified Sprite,
     or null if no Sprite collides with the specified Sprite.
     */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }
    /**
     Updates Animation, position, and velocity of all Sprites
     in the current map.
     */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();
        Sprite myBullet = (Sprite) resourceManager.getBullet().clone();
        Sprite evils;
        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            player.healthint();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, player, elapsedTime);
        player.update(elapsedTime);

        // update bullet
        if(shooting){
            shooting = false;
            myBullet.setY(player.getY());
            if(player.face_right){
                myBullet.setVelocityX(1.0f);
                myBullet.setX(player.getX());
            }else{
                myBullet.setVelocityX(-1.0f);
                myBullet.setX(player.getX());
            }
            myBullet.setVelocityY(0);
            map.addSprite(myBullet);
            soundManager.play(shootSound);
        }
        // update other sprites
        Iterator i = map.getSprites();;
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
            	//if(sprite.getX() < )
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    evils = updateCreature(player, creature, elapsedTime);
                    if(evils != null){
                        map.addEvilBullet(evils);
                    }
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
        map.transfer_buffer();
    }

    /**
     Updates the creature, applying gravity for creatures that
     aren't flying, and checks collisions.
     */
    private EvilBullet updateCreature(Creature player, Creature creature,
                                      long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            if(creature instanceof Bullet
                    || creature instanceof EvilBullet){
                //do nothing
            }else{
                creature.setVelocityY(creature.getVelocityY() +
                        GRAVITY * elapsedTime);
            }
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        if(creature instanceof Bullet){									
        	//this is how the game handles player bullets
            Point tile =
                    getTileCollision(creature, newX, creature.getY());
                    soundManager.play(shootSound);
            if(tile != null){
            	creature.setState(creature.STATE_DEAD);								
            	//if it hits something, it dies
            }
            if(creature.travel_accumulation(Math.abs(dx)) < creature.range){
                creature.setX(newX);
            }else{
                creature.setState(Creature.STATE_DEAD);								
                //if it goes too far, it dies
                creature.travel_length = 0;
            }
            checkBeingShot((Bullet)creature, (Player)map.getPlayer());
            return null;//Bullet will not bounce back if hit the edge of the map
        }
        if(creature instanceof EvilBullet){								
        	//this is how the game handles evil bullets
            Point tile =
                    getTileCollision(creature, newX, creature.getY());
            if(tile != null){
            	creature.setState(creature.STATE_DEAD);								
            	//if it hits something, it dies
            }
            if(creature.travel_accumulation_bug(Math.abs(dx)) < creature.bug_range){		
                creature.setX(newX);
            }else{
                creature.setState(Creature.STATE_DEAD);								
                //if it goes too far, it dies
                creature.travel_length = 0;
            }
            return null;//No bouncing
        }
        
        if(creature instanceof Grub){									
        	//this is how the game handles creatures(bad guys)
	        dx = creature.getVelocityX();
        	newX = oldX + dx * elapsedTime;
	        Point tile =
	                getTileCollision(creature, newX, creature.getY());				
	        //Grub grub = (Grub)creature;
	        if (tile == null) {
	        	int dd;
	        	if(map.getPlayer().getX() - creature.getX() < 0){dd = -1;}
	        	else{dd = 1;}
	            creature.setX(newX);
	            	//grub.setVelX(dd * dx);
            	if(creature.wait == 0)
            	{
            		creature.wait = map.getPlayer().getX();
            		creature.setVelocityX(0);
            	}
            	else if(map.getPlayer().getX() - creature.wait > 1000)
            	{
            		creature.setVelocityX(Math.abs(dx) * dd);				
            		//creature comes after player when the creature is loaded, doesn't start firing til later
            	}
            	else{
            		creature.setVelocityX(0);
            	}
	    

	        }
	        
	        else {
	        	if(map.getPlayer().getX() - creature.getX() < 0){dx = -1;}
	        	else{dx = 1;}
	            // line up with the tile boundary
	            if (dx > 0) {
	                creature.setX(
	                        TileMapRenderer.tilesToPixels(tile.x) -
	                                creature.getWidth());
	            }
	            else if (dx < 0) {
	                creature.setX(
	                        TileMapRenderer.tilesToPixels(tile.x + 1));
	            }
	            creature.collideHorizontal();
	        }
	        
        }else{
	        Point tile =
	                getTileCollision(creature, newX, creature.getY());
	        if (tile == null) {
	            creature.setX(newX);
	        }
	        else {
	            // line up with the tile boundary
	            if (dx > 0) {
	                creature.setX(
	                        TileMapRenderer.tilesToPixels(tile.x) -
	                                creature.getWidth());
	            }
	            else if (dx < 0) {
	                creature.setX(
	                        TileMapRenderer.tilesToPixels(tile.x + 1));
	            }
	            creature.collideHorizontal();
	        }
	        if (creature instanceof Player) {
	            checkPlayerCollision((Player)creature, false);
	        }
        }
        Point tile =
                getTileCollision(creature, newX, creature.getY());
        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                        TileMapRenderer.tilesToPixels(tile.y) -
                                creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                        TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

        if(creature instanceof Grub){							
        	//how the enemies shoot
            if(creature.getVelocityX() != 0f){
                if((creature.evilsct > 0 &&
                        System.currentTimeMillis() - creature.bugsct > 800) ||
                        (creature.evilsct == 0 &&																					
                        //checks to see if evil
                                ((map.getPlayer().getVelocityX()==0 && System.currentTimeMillis() - creature.bugsct > 2000) ||		
                                		//if player stands still, wait 2 sec
                                        (map.getPlayer().getVelocityX()!=0 && System.currentTimeMillis() - creature.bugsct > 500)))){
                							//if player is moving, wait .5 sec
                    EvilBullet evilBullet =
                            (EvilBullet) resourceManager.getEvilBullet().clone();			
                    //spawn bullet
                    if(!creature.face_left){						
                    	///set velocity
                        evilBullet.setX(creature.getX() + 10);
                        evilBullet.setY(creature.getY() + 10);
                        evilBullet.setVelocityX(0.7f);
                    }else{
                        evilBullet.setX(creature.getX() - 10);
                        evilBullet.setY(creature.getY() + 10);
                        evilBullet.setVelocityX(-0.7f);
                    }
                    creature.bugsct = System.currentTimeMillis();
                    creature.evilsct++;
                    return evilBullet;
                }
            }else{
                creature.bugsct = System.currentTimeMillis();
            }
        }
        return null;

    }
    public void checkBeingShot(Bullet bullet, Player player){			
    	//check if creature shot
        Sprite collisionSprite = getSpriteCollision(bullet);
        if(collisionSprite != null){
            bullet.setState(2);
            if(collisionSprite instanceof Grub){						
            	//if grub shot, kill grub and give player 10 hp
                Creature badguy = (Creature)collisionSprite;
                badguy.setState(1);
                player.Health(10);
                //do hp calculations here
            }
            if(collisionSprite instanceof EvilBullet){					
            	// if bullet hits evil bullet, kill evil bullet
                ((Creature) collisionSprite).setState(Creature.STATE_DEAD);
            }
            if(collisionSprite instanceof Fly){							
            	//if bullet hits fly, kill fly, but not used
                Creature badguy2 = (Creature)collisionSprite;
                badguy2.setState(1);
                //do hp calculations here
            }
        }
    }

    /**
     Checks for Player collision with other Sprites. If
     canKill is true, collisions with Creatures will kill
     them.
     */
    public void checkPlayerCollision(Player player,
                                     boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof EvilBullet){
            Creature evils = (Creature)collisionSprite;
            evils.setState(Creature.STATE_DEAD);					
            //if player runs into evil bullet, hurts player and kills bullet
            //Done: hp calculations here(per shot damage and also set death here if hp is at 0  DONE
            if(player.Health(-5) <= 0){
            	player.setState(Creature.STATE_DYING);				
            	//check to see if player died
            
            }
        }
        else if (collisionSprite instanceof Bullet){				
        	//shouldn't matter
            //do nothing
        }
        else if (collisionSprite instanceof Creature) {	
        	//if player hits creature..
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce if player jumps on it
                soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                //:does hp need to increase when shot  DONE
            }
            else {
            	// player dies! if the creature runs into him
                player.setState(Creature.STATE_DYING);
            }
        }
    }


    /**
     Gives the player the speicifed power up and removes it
     from the map.
     */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music not used
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // end goal
            soundManager.play(prizeSound,
                    new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
            map.getPlayer().win();
            map.getPlayer().healthint();
        }
    }

}