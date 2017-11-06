import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainConverter {

    public static final String SRC = "./src/main/resources/pdfs/rom_nomefile.pdf";
    public static final String EXPECTED_TEXT = "Country List\n" +
            "Internet Movie Database";

    public MainConverter()  {

    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        File file = new File(SRC);
        file.getParentFile().mkdirs();
    }


    public void manipulatePdf() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));
        Rectangle rect = new Rectangle(36, 750, 523, 56);

        FontFilter fontFilter = new FontFilter(rect);
        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy extractionStrategy = listener.attachEventListener(new LocationTextExtractionStrategy(), fontFilter);
        PdfCanvasProcessor parser = new PdfCanvasProcessor(extractionStrategy);
        parser.processPageContent(pdfDoc.getPage(2));


        String actualText = extractionStrategy.getResultantText();
        List<String> myList = new ArrayList<String>(Arrays.asList(actualText.split("\n")));
        System.out.println("Text: " + actualText);

        pdfDoc.close();

        //Assert.assertEquals(EXPECTED_TEXT, actualText);
    }

    class FontFilter extends TextRegionEventFilter {
        public FontFilter(Rectangle filterRect) {
            super(filterRect);
        }

        @Override
        public boolean accept(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;

                PdfFont font = renderInfo.getFont();
                if (null != font) {
                    String fontName = font.getFontProgram().getFontNames().getFontName();
                    return fontName.endsWith("Bold") || fontName.endsWith("Oblique");
                }
            }
            return false;
        }
    }

    public static void main (String args[]){
        System.out.println("Hello world");
        MainConverter converter = new MainConverter();
        try {
            converter.manipulatePdf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
