(ns htmlPrinter.htmlPrinter


(import java.awt.image.BufferedImage)
(import java.io.ByteArrayOutputStream)
(import java.io.StringReader)
(import java.io.File)
(import java.io.FileInputStream)
(import java.lang.System)
(import javax.imageio.ImageReader)
(import javax.imageio.spi.ImageReaderSpi)
(import javax.print.PrintServiceLookup)
(import javax.print.PrintService)
(import javax.print.attribute.HashPrintRequestAttributeSet)
(import javax.print.attribute.HashPrintServiceAttributeSet)
(import javax.print.attribute.standard.MediaSizeName)
(import javax.print.attribute.standard.PrinterName)
(import javax.swing.text.html.HTMLDocument)
(import javax.swing.text.html.HTMLEditorKit)
(import javax.swing.text.JTextComponent)
(import javax.swing.JFrame)
(import javax.swing.JTextPane)
(import javax.swing.JEditorPane)
(import java.util.Hashtable)
(import java.text.MessageFormat)
(import java.net.URL)
(import org.apache.commons.codec.binary.Base64)
(import org.apache.commons.codec.binary.Base64OutputStream)
)
;see http://www.cs.mun.ca/java-api-1.5/guide/jps/spec/attributes.fm5.html for more attributes

;PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
;aset.add(MediaSizeName.INVOICE)
;
;(def print-attributes (new HashPrintRequestAttributeSet))
;Setup the size
;(. printAttributes add (. MediaSizeName INVOICE))
;(. printAttributes clear) ;clear attributes



(def nitro-printer-name "Nitro PDF Creator (Reader 3)")
(def kids-church-image-file-name "H:\\Users\\Peter\\Clojure Workspace\\Sandbox\\HTML\\KidsChurchNameStickers.png")

(def myHtml "<html>\n<body>\n<h1>The Title</h1>\n<h2>The subtitle</h2>\n<p>The paragraph</p>\n</body>\n</html>")
(def my-html-doc "H:\\Users\\Peter\\Clojure Workspace\\Sandbox\\HTML\\index.html")
(def my-html-url "file:///H:/Users/Peter/Clojure%20Workspace/Sandbox/HTML/index.html")
(def myHtml (slurp my-html-doc))

(defn create-html-doc-from-string [htmlStr] 
    (let [stringReader (new StringReader htmlStr)
          htmlKit (new HTMLEditorKit)
          myHtmlDoc (. htmlKit createDefaultDocument)]
    (. htmlKit read stringReader myHtmlDoc 0)
    myHtmlDoc)
)
;This might be the way to go long term
;http://stackoverflow.com/questions/12141155/printing-an-html-file-in-java
                  ;FileInputStream fis = new FileInputStream(doc);
                  ;DocAttributeSet das = new HashDocAttributeSet();
                  ;Doc document = new SimpleDoc(fis, flavor, das);
                  ;job.print(document, pras);


(defn show-text-pane [textPane]
  (def temp-frame (new JFrame))
  ;(. textPane setWidth 130)
  (. textPane setSize 130 130)
  ;(. temp-frame setHeight 130)
  (. temp-frame setSize 130 130)
  (. temp-frame add textPane)
  (. temp-frame setVisible true)
  )


(defn load-image [image-name]
  (let [readers (. javax.imageio.ImageIO getImageReadersBySuffix "png")
        firstreader (. readers next )
        fin (new FileInputStream kids-church-image-file-name)
        iis (. javax.imageio.ImageIO createImageInputStream fin)
        ]
    (. firstreader setInput iis false)
    (def image-buffer (. firstreader read 0))
    (. fin close)
    image-buffer
;(def )

;(def )

;;      FileInputStream fin = new FileInputStream(filename);
;(def )
;;      ImageInputStream iis = ImageIO.createImageInputStream(fin);
;(def )
;;      imageReader.setInput(iis, false);
;
;;      int num = imageReader.getNumImages(true);
;;      images = new BufferedImage[num];
;;      for (int i = 0; i < num; ++i) {
;;        images[i] = imageReader.read(i);
;(def image-buffer (. firstreader read 0))
;;      }
;;ByteArrayOutputStream os = new ByteArrayOutputStream();
;(def os (new ByteArrayOutputStream))
;;OutputStream b64 = new Base64.OutputStream(os);
;(def b64 (new org.apache.commons.codec.binary.Base64OutputStream os))
;              
;;ImageIO.write(bi, "png", b64);
;(. javax.imageio.ImageIO write image-buffer "png" b64)
;
;;String result = os.toString("UTF-8");
;;(println )
;(def image64 (. os toString "UTF-8"))
;
;(. fin close)
 ) )

(defn add-image [text-pane image]
            ;Dictionary cache=(Dictionary)edit.getDocument().getProperty("imageCache");
  (def cache (. (. text-pane getDocument) getProperty "imageCache"))
     
            ;if (cache==null) {
            ;    cache=new Hashtable();
            ;    edit.getDocument().putProperty("imageCache",cache);        
            ;}
            ;(if 
            ;  (nil? cache) 
            ;  (
                (def cache (new Hashtable))

            (. (. text-pane getDocument) putProperty "imageCache" cache)
            ;)
;) ;if
            (. cache put (new URL "http://KidsChurchNameStickers.png") image)
)

;print from html string
(defn print-html-by-print-service [printer-service html-string show-print-dialog? print-attributes interactive?]
    ;(let [textPane (new JTextPane)]
    (let [textPane (new JEditorPane my-html-url)]
        ;(. textPane setContentType "text/html")
        ;(. textPane setEditorKit (new HTMLEditorKit)) 
        ;(. textPane setDocument (create-html-doc-from-string html-string))
        ;(. textPane setText  html-string)
        ;(add-image textPane (load-image kids-church-image-file-name))
        ;(show-text-pane textPane)
        (. textPane print
            (new MessageFormat "") 
            (new MessageFormat "") 
            show-print-dialog? 
            printer-service
            print-attributes 
            interactive?
        )
    )
)


;grab a list of PrinterServices by name
(defn get-named-printer-service [printer-name] (let [
        selected-printer-name (new PrinterName printer-name nil)
        printer-attributes (new HashPrintServiceAttributeSet)    ]
    (. printer-attributes add selected-printer-name)
    (. PrintServiceLookup lookupPrintServices nil printer-attributes))
)

;Use the printer name to automatically select the printer to use
(defn print-html-by-printer-name [printer-name html-string show-print-dialog? print-attributes interactive?]
    (print-html-by-print-service (first (get-named-printer-service printer-name))
                             html-string
                             show-print-dialog?
                             print-attributes
                             interactive?)
)
;Use the default printer
(defn print-html-by-default-printer [htmlString show-print-dialog? print-attributes interactive?]
    (print-html-by-print-service (. PrintServiceLookup lookupDefaultPrintService)
                             htmlString
                             show-print-dialog?
                             print-attributes
                             interactive?)
)

(defn get-print-request-attributes [page-size]
  (let [attributes (new HashPrintRequestAttributeSet)]
    (. attributes add (. MediaSizeName ISO_A4))
    attributes
 ))
 
;printer-name htmlString show-print-dialog? print-attributes interactive?
(print-html-by-printer-name
  ;print-html-by-default-printer
    nitro-printer-name 
    myHtml 
    false 
    (get-print-request-attributes INVOICE)
    false)

;import java.awt.Graphics;
;import java.awt.Panel;
;import java.awt.image.BufferedImage;
;import java.io.FileInputStream;
;import java.util.Iterator;
;
;import javax.imageio.ImageIO;
;import javax.imageio.ImageReader;
;import javax.imageio.stream.ImageInputStream;
;import javax.swing.JFrame;
;
;public class Main extends Panel{
;  private BufferedImage images[];
;
;  private int imageIndex = 0;
;
;  public Main(String filename) throws Exception{
;      FileInputStream fin = new FileInputStream(filename);
;      String suffix = filename.substring(filename.lastIndexOf('.') + 1);
;      System.out.println("suf " + suffix);
;      Iterator readers = ImageIO.getImageReadersBySuffix(suffix);
;      ImageReader imageReader = (ImageReader) readers.next();
;      ImageInputStream iis = ImageIO.createImageInputStream(fin);
;      imageReader.setInput(iis, false);
;      int num = imageReader.getNumImages(true);
;      images = new BufferedImage[num];
;      for (int i = 0; i < num; ++i) {
;        images[i] = imageReader.read(i);
;      }
;      fin.close();
;  }
;
;  public void paint(Graphics g) {
;    if (images == null)
;      return;
;    g.drawImage(images[imageIndex], 0, 0, null);
;    imageIndex = (imageIndex + 1) % images.length;
;  }
;
;  static public void main(String args[]) throws Exception {
;    JFrame frame = new JFrame("ShowImageIR.java");
;    Panel panel = new Main(args[0]);
;    frame.getContentPane().add(panel);
;    frame.setSize(400, 400);
;    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
;    frame.setVisible(true);
;  }
;}
;(base64-encode unencoded)

;(def readers (. javax.imageio.ImageIO getImageReadersBySuffix "png"))

;(def firstreader (. readers next ))

;;      FileInputStream fin = new FileInputStream(filename);
;(def fin (new FileInputStream kids-church-image-file-name))
;;      ImageInputStream iis = ImageIO.createImageInputStream(fin);
;(def iis (. javax.imageio.ImageIO createImageInputStream fin))
;;      imageReader.setInput(iis, false);
;(. firstreader setInput iis false)
;;      int num = imageReader.getNumImages(true);
;;      images = new BufferedImage[num];
;;      for (int i = 0; i < num; ++i) {
;;        images[i] = imageReader.read(i);
;(def image-buffer (. firstreader read 0))
;;      }
;;ByteArrayOutputStream os = new ByteArrayOutputStream();
;(def os (new ByteArrayOutputStream))
;;OutputStream b64 = new Base64.OutputStream(os);
;(def b64 (new org.apache.commons.codec.binary.Base64OutputStream os))
;              
;;ImageIO.write(bi, "png", b64);
;(. javax.imageio.ImageIO write image-buffer "png" b64)
;
;;String result = os.toString("UTF-8");
;;(println )
;(def image64 (. os toString "UTF-8"))
;
;(. fin close)
