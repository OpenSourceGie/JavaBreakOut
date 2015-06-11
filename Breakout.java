/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.org.apache.xpath.internal.operations.String;

public class Breakout extends GraphicsProgram 
{
	/** adding audio */
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	/** some extras varables*/
	private static double vx, vy;
	private static int xx;
	private static RandomGenerator rgen = RandomGenerator.getInstance();
	private static RandomGenerator rgen1 = RandomGenerator.getInstance();
	private static int dis = 0;
	private static final int MOVESIZE = 200;
	
	/** set size of the window fixer*/
    private static final int PAUSE = 10;
    private static final double adspeed = 1.5;
    /** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 800;
	public static final int APPLICATION_HEIGHT = 800;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
	/** pyhics varables*/
	private static int ballmoveing = 0;
	private static GCompound bricks[] = new GCompound[NBRICKS_PER_ROW * NBRICK_ROWS];
	
	/** Number of turns */
	/* do you care if it is not final */
	private static int NTURNS = 3;
	
	/** setting the life icons size and Life vrables */
	private static final int Life_Size = 50;
	private static GImage lifes[] = new GImage[NTURNS];
	
	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() 
	{
		/* adding background and board */
        this.resize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
        pause(PAUSE);
        GRect borad = new GRect(0,0,WIDTH,HEIGHT);
        add(borad);
        GImage background = new GImage("background.jpg");
        background.setSize(WIDTH,HEIGHT);
        add(background);
        /* getting mouse events and making the paddle*/
        creatpaddel(PADDLE_WIDTH, PADDLE_HEIGHT,0,0);
        paddel.setLocation(WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET);
        addMouseListeners();
        lvlbuilder();
        makeball(0, 0, BALL_RADIUS, BALL_RADIUS);
        ball.setLocation(WIDTH/2 - BALL_RADIUS/2, HEIGHT - PADDLE_Y_OFFSET - 20);
        makelifeicons();
        /* da game */
        while(NTURNS > 0)
        {
        	if (ball.getY() + BALL_RADIUS >= HEIGHT)
        	{
        		ballmoveing = 0;
        		remove(lifes[NTURNS - 1]);
        		NTURNS--;
        		remove(ball);
        		makeball(0, 0, BALL_RADIUS, BALL_RADIUS);
        	    ball.setLocation(WIDTH/2 - BALL_RADIUS/2, HEIGHT - PADDLE_Y_OFFSET - 20);
        	}
        	ballmovement();
        	pyths();
        	if (bricks.length == 0)
        	{
        		maketex("you win you can close the game and open it agen to play agen");
        		dis = 1;
        		NTURNS = 0;
        	}
        }
        while (NTURNS == 0)
        {
        	remove(ball);
        	/* add restart buttion */
        	if (dis != 1)
        	{
        		maketex("close this app and reopen it to retry");
        	}
        }
	}
	
	/*makeing it so you can move the bar*/
	public void mouseMoved(MouseEvent e)
	{
		if (e.getY() >= HEIGHT - MOVESIZE && e.getY() <= HEIGHT - PADDLE_Y_OFFSET)
		{
			paddel.setLocation(e.getX() - PADDLE_WIDTH/2,e.getY() - PADDLE_HEIGHT/2);
		}
	}
	
	private GCompound creatpaddel(double width, double height, double x, double y)
	{
		/* makeing the paddel */
		GRect paddelOutline = new GRect(x, y, width, height);
		GImage paddeltex = new GImage("paddel.png");
		paddeltex.setSize(width, height);
		paddeltex.setLocation(x, y);
		paddel = new GCompound();
		paddel.add(paddelOutline);
		paddel.add(paddeltex);
		add(paddel);
		return paddel;
	}
	
	/* makeing an instance varable to use*/
	private GCompound paddel;
	
	private void lvlbuilder()
	{
		int k=0;
		/* this will build the lvl*/
		/* the nuber of rows*/
		for (int row = 0; row < NBRICK_ROWS; row++)
		{
			int sep=0;
			int offset=BRICK_Y_OFFSET;
			/* the nuber of bricks in ech row*/
			for (int col = 0; col < NBRICKS_PER_ROW; col++)
			{
				bricks[k] = makebrick(col*BRICK_WIDTH+sep, row*BRICK_HEIGHT + offset, BRICK_WIDTH, BRICK_HEIGHT);
                k++;
                sep += BRICK_SEP;
			}
		}
	}
	
	/* brick contruter*/
	private GCompound makebrick(double x, double y, double width, double height)
	{
		GRect bricklvl1 = new GRect(x,y,width,height);
		GImage bricktex = new GImage("brick.png");
		bricktex.setSize(width, height);
		bricktex.setLocation(x, y);
		brick = new GCompound();
		brick.add(bricklvl1);
		brick.add(bricktex);
		add(brick);
		return brick;
	}
	
	/* makeing a noter instacne varable*/
	private GCompound brick;

	/* make the ball */
	private GCompound makeball(double x, double y, double width, double height)
	{
		GOval ballhitbox = new GOval(x,y,width,height);
		GImage balltex = new GImage("bball.png");
		balltex.setSize(width, height);
		balltex.setLocation(x, y);
		ball = new GCompound();
		ball.add(ballhitbox);
		ball.add(balltex);
		add(ball);
		return ball;
	}
	/* do i even have to tell you why this is here... */
	private GCompound ball;
	/* make your life icons*/
	private void makelifeicons()
	{
		int Life_add = 0;
		int l = 0;
		for (int i = 0; i < NTURNS; i++)
		{
			if (i == 0)
			{
				lifes[l] = life = new GImage("life.png");
				life.setLocation(0, 0);
				life.setSize(Life_Size, Life_Size);
				add(life);
				l++;
			}
			else
			{
				lifes[l] = life = new GImage("life.png");
				life.setLocation(Life_add, 0);
				life.setSize(Life_Size, Life_Size);
				add(life);
				l++;
			}
			Life_add += Life_Size;
		}
	}
	/* no */
	private GImage life;
	private void pyths()
	/* adds pyhics */
	{
		if (paddelthere())
		{
			//asking for random varables
			vx = rgen.nextDouble(0.1, 0.9);
			vy = 1;
			xx = rgen1.nextInt(0, 3);
			bounceClip.play();
			//sending message to the ballmovemnt
			ballmoveing = 1;
			if (xx == 1 || xx == 3)
			{
				vx = -vx;
			}
		}
		if (brickinfrount() != null)
		{
			vx = rgen.nextDouble(0.3, 0.9);
			vy = 1;
			xx = rgen1.nextInt(0, 3);
			bounceClip.play();
			ballmoveing = 2;
			if (xx == 2 || xx == 0)
			{
				vx = -vx;
			}
		}
		if (notinbounds())
		{
			vx = rgen.nextDouble(0.5, 0.9);
			vy = 1;
			xx = rgen1.nextInt(0, 3);
			bounceClip.play();
			ballmoveing = 3;
			if (ball.getX() + BALL_RADIUS >= WIDTH)
			{
				vx = -vx;
			}
			if (ball.getY() >= 0 && xx == 1)
			{
				vy = -vy;
			}
			if (ball.getY() >= 0 && xx == 0)
			{
				vx = -vx;
			}
			if (ball.getY() >= 0 && ball.getX() + BALL_RADIUS <= WIDTH && xx == 3)
			{
				vx = -vx;
				vy = -vy;
			}
		}
	}
	private GCompound brickinfrount()
	/* checks for a brick */
	{
		GCompound tempBrick;
		for(int i = 0; i < bricks.length; i++)
		{
			if (bricks[i] != null && ball.getBounds().intersects(bricks[i].getBounds()))
			{
				tempBrick = bricks[i];
				remove(bricks[i]);
				bricks[i] = null;
				return tempBrick;
			}
		}
		return null;
	}
	private Boolean paddelthere()
	{
		if (ball.getBounds().intersects(paddel.getBounds()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	private Boolean notinbounds()
	{
		if(ball.getX() + BALL_RADIUS >= WIDTH || ball.getX() <= 0 || ball.getY() <= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/* adds ball movement */
	private void ballmovement()
	{
		if (ballmoveing == 1)
		{
			pause(adspeed);
			ball.move(vx,-vy);
			}
		if (ballmoveing == 2)
		{
			pause(adspeed);
			ball.move(vx,vy);
		}
		if (ballmoveing == 3)
		{
			pause(adspeed);
			ball.move(vx, vy);
		}
	}
	private GLabel text;
	private void maketex(java.lang.String tex)
	{
		GLabel text = new GLabel(tex);
		text.setFont("SansSerif-bold-25");
		text.setLocation(WIDTH/2 - text.getWidth()/2, HEIGHT/2 - text.getHeight()/2);
		text.setColor(Color.WHITE);
		add(text);
	}
	private void deltex()
	{
		remove(text);
	}
}