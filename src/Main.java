import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.jootnet.m2client.map.Map;
import org.jootnet.m2client.map.internal.Maps;
import org.jootnet.m2client.texture.internal.Textures;
import org.jootnet.m2client.ui.GamePanel;

public class Main {

	public static void main(String[] args) throws IOException {
		System.setProperty("org.jootnet.m2client.data.dir", "D:\\Program Files (x86)\\盛大网络\\热血传奇\\Data");
		System.setProperty("org.jootnet.m2client.map.dir", "D:\\Program Files (x86)\\盛大网络\\热血传奇\\Map");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("测试地图渲染");
		//frame.setLocationRelativeTo(null);
		frame.setSize(1280, 720);
		Map map = Maps.get("0");
		map.move(330, 330);
		frame.setContentPane(new GamePanel(map));
		frame.setVisible(true);
		//ImageIO.write(Textures.getTextureImmediately("npc", 1992).toBufferedImage(true), "jpg", new File("C:\\Users\\云\\Desktop\\1.jpg"));
	}

}
