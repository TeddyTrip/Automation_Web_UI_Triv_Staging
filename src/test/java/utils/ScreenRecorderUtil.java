package utils;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;
import static org.monte.media.FormatKeys.*;

public class ScreenRecorderUtil extends ScreenRecorder {
    private static ScreenRecorder screenRecorder;
    private String name;

    public ScreenRecorderUtil(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                              Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        }
        return new File(movieFolder, name + "." + Registry.getInstance().getExtension(fileFormat));
    }

    public static void startRecord(String methodName) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/target/screen-records/");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureSize = new Rectangle(0, 0, screenSize.width, screenSize.height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();

        screenRecorder = new ScreenRecorderUtil(gc, captureSize,
                // Format file video (AVI)
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                // Menggunakan encoder bawaan Monte yang stabil
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        org.monte.media.math.Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                null, 
                null, 
                file, 
                methodName);
                
        screenRecorder.start();
    }

    public static void stopRecord() throws Exception {
        if (screenRecorder != null) {
            screenRecorder.stop();
        }
    }
}