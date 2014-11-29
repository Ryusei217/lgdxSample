package com.badlogic.drop;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	
	private ArrayList<Rectangle> raindrops;
	private long lastDropTime;
	
	/**
	 * Crea una nueva gota de agua y la agrega a lista de gotas.
	 */
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0,800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void create () {
		//Cargamos los sonidos y graficos
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		//configuramos la musica pra que se repita.
		rainMusic.setLooping(true);
		rainMusic.play();
		
		//Establecemos la camera donde vamos a crear la accion
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		batch = new SpriteBatch();
		
		//Inicializamos la cubeta de agua.
		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
		
		//Creamos la lista de rectanculos y agregamos la primer gota de agua.
		raindrops = new ArrayList<Rectangle>();
		this.spawnRaindrop();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    camera.update();
	    
	    batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    batch.draw(bucketImage, bucket.x, bucket.y);
	    for(Rectangle raindrop: raindrops) {
	         batch.draw(dropImage, raindrop.x, raindrop.y);
	    }
	    batch.end();
	    
	    if(Gdx.input.isTouched()) {
	    	Vector3 touchPos = new Vector3();
	    	touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	    	camera.unproject(touchPos);
	    	bucket.x = touchPos.x - 64/2;
	    }
	    
	    //Verifica cuanto tiempo ha pasado desde la ultima gota y genera una dependiendo del resultado
	    if(TimeUtils.nanoTime() - lastDropTime > 1000000000)
	    	this.spawnRaindrop();
	    
	    Iterator<Rectangle> iter = raindrops.iterator();
	    while(iter.hasNext()){
	    	Rectangle raindrop = iter.next();
	    	raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
	    	if(raindrop.y + 64 < 0) 
	    		iter.remove();
	    	
	    	if(raindrop.overlaps(bucket)) {
	    		dropSound.play();
	    		iter.remove();
	    	}
	    }
	}
	
	@Override
	   public void dispose() {
	      // dispose of all the native resources
	      dropImage.dispose();
	      bucketImage.dispose();
	      dropSound.dispose();
	      rainMusic.dispose();
	      batch.dispose();
	   }
}
