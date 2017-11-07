package org.jootnet.m2client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.map.Map;

public class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8375507540560760796L;

	private Map map = null;
	private GraphicsContextImpl ctx;
	class GraphicsContextImpl implements GraphicsContext {
		float deltaTime = 0;
		long frameStart = 0;
		int frames = 0;
		int maxFps = 60;
		int fps = maxFps;
		long lastTime = System.nanoTime();
		int tickFrames = 0;
		
		@Override
		public int getWidth() {
			return GamePanel.this.getWidth();
		}

		@Override
		public int getHeight() {
			return GamePanel.this.getHeight();
		}

		@Override
		public int getMaxFps() {
			return maxFps;
		}

		@Override
		public float getDeltaTime() {
			return deltaTime;
		}

		@Override
		public int getFramesPerSecond() {
			return fps;
		}

		@Override
		public int getCurrentFrame() {
			return frames;
		}

		@Override
		public int getTickFrames() {
			return tickFrames;
		}
		
		void updateTime () {
			long time = System.nanoTime();
			deltaTime = (time - lastTime) / 1000000000.0f;
			lastTime = time;
			if(tickFrames > maxFps) tickFrames = 0;
			tickFrames++;

			if (time - frameStart >= 1000000000) {
				fps = frames;
				frames = 0;
				frameStart = time;
			}
			frames++;
		}
	}

	private JLabel lblFPS = new JLabel();
	public GamePanel(Map map) {
		this.map = map;
		ctx = new GraphicsContextImpl();
		lblFPS.setSize(60, 28);
		lblFPS.setLocation(20, 20);
		lblFPS.setForeground(Color.WHITE);
		setLayout(null);
		add(lblFPS);
		new Timer(16, this).start();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ctx.updateTime();
		lblFPS.setText("FPS: " + ctx.fps);
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(map.adjust(ctx)) {
			g.drawImage(map.content().toBufferedImage(false), map.offsetX(), map.offsetY(), null);
		}
	}
}
