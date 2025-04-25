import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;

    //Bird 
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    //Game loop
    Timer gameLoop;
    Timer placePipesTimer;

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }
    
    int velocityY = 0;
    int gravity = 1;
    int velocityX = -4;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    boolean gameOver = false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);
        //Importing images 
        backgroundImage = new ImageIcon(getClass().getResource("./bg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./bird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./top-pipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottom-pipe.png")).getImage();
        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placePipesTimer.start();
        gameLoop = new Timer(1000/60,this);
        gameLoop.start();

    }
    
    public void placePipes(){
        int randomPipeY = (int)(pipeY- pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
    
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //bg
        g.drawImage(backgroundImage,0,0,boardWidth,boardHeight,null);
        //bird
        g.drawImage(birdImage, birdX, birdY, birdWidth, birdHeight, null);
        //top pipe
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN,32));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int)score), 10, 25);
        }
    }

    public void move(){
        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY,0);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            boolean collision = birdX < pipe.x + birdWidth && birdX + birdWidth > pipe.x && birdY < pipe.y + pipe.height && birdY + birdHeight > pipe.y;

            if(!pipe.passed && birdX > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if(collision){
                gameOver = true;
            }
        }
        
        //falls off the bottom of the screen
        if(birdY > boardHeight){
            gameOver = true;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                velocityY = 0;
                birdY = boardHeight/2;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();


            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyReleased(KeyEvent e) {}
}
