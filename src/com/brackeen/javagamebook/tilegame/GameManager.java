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
    private Sound shootingSound;
    private Sound bugsShouting;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction shoot;
    private GameAction falldown;

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
        shootingSound = soundManager.getSound("sounds/shooting.wav");
        bugsShouting = soundManager.getSound("sounds/shouting.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
                midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
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
        falldown = new GameAction("falldown");
        inputManager = new InputManager(
                screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_S);
        inputManager.mapToKey(falldown, KeyEvent.VK_DOWN);
    }

    public boolean shooting = false;
    private long currentTime = System.currentTimeMillis();
    private int shooting_ct = 0;
    private boolean frozen = false;
    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
        }
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            float velocityY = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            if (falldown.isPressed()) {
                velocityY+=player.getMaxSpeed();
                player.setVelocityY(velocityY + 1);
            }
            if (shoot.isPressed()){
                //create Bullet
                if(shooting_ct == 0){
                    if(!frozen){
                        shooting = true;
                        shooting_ct++;
                        currentTime = System.currentTimeMillis();
                    }else{
                        if(System.currentTimeMillis() - currentTime >= 1000){
                            shooting = true;
                            shooting_ct++;
                            currentTime = System.currentTimeMillis();
                            frozen = false;
                        }
                    }
                }else if(shooting_ct == 1){
                    if(System.currentTimeMillis() - currentTime >= 1000){
                        shooting = true;
                        shooting_ct++;
                        currentTime = System.currentTimeMillis();
                    }
                }else if(shooting_ct <= 10){
                    if(System.currentTimeMillis() - currentTime >= 400){
                        shooting = true;
                        shooting_ct++;
                        currentTime = System.currentTimeMillis();
                    }
                }else{
                    frozen = true;
                    if(System.currentTimeMillis() - currentTime >= 1000){
                        shooting = true;
                        shooting_ct = 2;
                        currentTime = System.currentTimeMillis();
                        frozen = false;
                    }
                }
            }else{
                shooting_ct = 0;
            }
            player.setVelocityX(velocityX);
        }

    }

    public void draw(Graphics2D g) {
        renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());
        // draw health
        Player player = (Player)map.getPlayer();
        g.setColor(Color.red);
        g.setFont(new Font("Arial",0,50));
        g.drawString("hp:" + player.getHp() ,50,50);
        if(player.getHp() == 0) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial",0,100));
            g.drawString("WASTED", 200, 300);
        }
        if(frozen){
            g.setColor(Color.cyan);
            g.setFont(new Font("Arial",0,50));
            g.drawString("Reloading", 500, 50);
        }
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
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
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
            soundManager.play(shootingSound);
        }
        // update other sprites
        Iterator i = map.getSprites();;
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    evils = updateCreature(creature, elapsedTime);
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
    private EvilBullet updateCreature(Creature creature,
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
            if(creature.travel_accumulation(Math.abs(dx)) < creature.range){
                creature.setX(newX);
            }else{
                creature.setState(Creature.STATE_DEAD);
                creature.travel_length = 0;
            }
            checkBeingShot((Bullet)creature, (Player)map.getPlayer());
            return null;//Bullet will not bounce back if hit the edge of the map
        }
        if(creature instanceof EvilBullet){
            if(creature.travel_accumulation_bug(Math.abs(dx)) < creature.bug_range){
                creature.setX(newX);
            }else{
                creature.setState(Creature.STATE_DEAD);
                creature.travel_length = 0;
            }
            return null;//No bouncing
        }
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
            if(creature.getVelocityX() != 0f){
                if((creature.evilsct > 0 &&
                        System.currentTimeMillis() - creature.bugsct > 800) ||
                        (creature.evilsct == 0 &&
                                ((map.getPlayer().getVelocityX()==0 && System.currentTimeMillis() - creature.bugsct > 2000) ||
                                        (map.getPlayer().getVelocityX()!=0 && System.currentTimeMillis() - creature.bugsct > 500)))){
                    EvilBullet evils =
                            (EvilBullet) resourceManager.getEvilBullet().clone();
                    if(!creature.face_left){
                        evils.setX(creature.getX() + 10);
                        evils.setY(creature.getY() + 10);
                        evils.setVelocityX(0.7f);
                    }else{
                        evils.setX(creature.getX() - 10);
                        evils.setY(creature.getY() + 10);
                        evils.setVelocityX(-0.7f);
                    }
                    creature.bugsct = System.currentTimeMillis();
                    creature.evilsct++;
                    return evils;
                }
            }else{
                creature.bugsct = System.currentTimeMillis();
            }
        }
        return null;

    }
    public void checkBeingShot(Bullet bullet, Player player){
        Sprite collisionSprite = getSpriteCollision(bullet);
        if(collisionSprite != null){
            bullet.setState(2);
            if(collisionSprite instanceof Grub){
                soundManager.play(bugsShouting);
                Creature badguy = (Creature)collisionSprite;
                badguy.setState(1);
                player.hp += 10;
            }
            if(collisionSprite instanceof EvilBullet){
                ((Creature) collisionSprite).setState(Creature.STATE_DEAD);
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
            critical_time = 0;
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
            player.hp -= 5;
            if(player.getHp() == 0){
                player.setState(Creature.STATE_DYING);
            }
        }
        else if (collisionSprite instanceof Bullet){
            //do nothing
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                player.hp += 10;
            }
            else {
                // player dies!
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
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                    new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }

}