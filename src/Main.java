import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jootnet.m2client.texture.internal.Textures;

public class Main {

	public static void main(String[] args) throws IOException {
		System.setProperty("org.jootnet.m2client.data.dir", "D:\\Program Files (x86)\\Ê¢´óÍøÂç\\ÈÈÑª´«Ææ\\Data");
		ImageIO.write(Textures.getTextureImmediately("npc", 1992).toBufferedImage(true), "jpg", new File("C:\\Users\\ÔÆ\\Desktop\\1.jpg"));
	}

}
