package de.labystudio.game.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class TextureManager {
    private static int lastId = Integer.MIN_VALUE;

    /**
     * Load a texture into OpenGL
     *
     * @param resourceName Resource path of the image
     * @param mode         Texture filter mode (GL_NEAREST, GL_LINEAR)
     * @return Texture id of OpenGL
     */
    public static int loadTexture(String resourceName, int mode) {
        // Generate a new texture id
        int id = glGenTextures();

        // Bind this texture id
        bind(id);

        // Set texture filter mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mode);

        // Read from resources
        InputStream inputStream = TextureManager.class.getResourceAsStream(resourceName);

        try {
            // Read to buffered image
            assert inputStream != null;
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            // Get image size
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Write image pixels into array
            int[] pixels = new int[width * height];
            bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

            // Flip RGB order of the integers
            for (int i = 0; i < pixels.length; i++) {
                int alpha = pixels[i] >> 24 & 0xFF;
                int red = pixels[i] >> 16 & 0xFF;
                int green = pixels[i] >> 8 & 0xFF;
                int blue = pixels[i] & 0xFF;

                // ARGB to ABGR
                pixels[i] = alpha << 24 | blue << 16 | green << 8 | red;
            }

            // Create bytebuffer from pixel array
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);
            byteBuffer.asIntBuffer().put(pixels);

            // Write texture to OpenGL
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
        } catch (IOException exception) {
            throw new RuntimeException("Could not load texture " + resourceName, exception);
        }

        return id;
    }

    /**
     * Bind the texture to OpenGL using the id from {@link #loadTexture(String, int)}
     *
     * @param id Texture id
     */
    public static void bind(int id) {
        if (id != lastId) {
            glBindTexture(GL_TEXTURE_2D, id);
            lastId = id;
        }
    }
}