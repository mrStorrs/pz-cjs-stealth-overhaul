import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class CreatePoster {
    private CreatePoster() {}

    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setPaint(new Color(12, 18, 20));
        graphics.fillRect(0, 0, 512, 512);
        graphics.setPaint(new Color(24, 43, 42));
        graphics.fillRect(0, 330, 512, 182);

        graphics.setPaint(new Color(112, 199, 143));
        graphics.fill(new Ellipse2D.Double(126, 72, 260, 260));
        graphics.setPaint(new Color(12, 18, 20));
        graphics.fill(new Ellipse2D.Double(172, 104, 214, 214));

        graphics.setPaint(new Color(229, 235, 230));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 43));
        graphics.drawString("STEALTH", 153, 405);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        graphics.setPaint(new Color(150, 179, 164));
        graphics.drawString("LIGHT  •  COVER  •  DISTANCE", 65, 452);

        graphics.dispose();
        ImageIO.write(image, "png", new File(args[0]));
    }
}
