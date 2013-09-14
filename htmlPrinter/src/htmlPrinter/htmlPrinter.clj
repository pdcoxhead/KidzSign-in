(ns htmlPrinter.htmlPrinter

(import java.io.StringReader)
(import java.io.FileInputStream)
(import javax.print.PrintServiceLookup)
(import javax.print.attribute.standard.PrinterResolution)
(import javax.print.attribute.HashPrintRequestAttributeSet)
(import javax.print.attribute.HashPrintServiceAttributeSet)
(import javax.print.attribute.standard.MediaSizeName)
(import javax.print.attribute.standard.PrinterName)
(import javax.swing.text.html.HTMLEditorKit)
(import javax.swing.JFrame)
(import javax.swing.JEditorPane)
(import java.text.MessageFormat)
)
(def nitro-printer-name "Nitro PDF Creator (Reader 3)")
(def my-html-doc "C:\\Users\\Peter\\Documents\\GitHub\\KidzSign-in\\htmlPrinter\\resources\\HTML\\index.html")
(def my-html-url "file:///C:/Users/Peter/Documents/GitHub/KidzSign-in/htmlPrinter/resources/HTML/index.html")
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
  ;(. textPane setSize 130 130)
  ;(. temp-frame setHeight 130)
  (. temp-frame setSize (. textPane getWidth) (. textPane getHeight))
  (. temp-frame add textPane)
  (. temp-frame setVisible true)
  )

;print from html string
(defn print-html-by-print-service [printer-service html-url show-print-dialog? print-attributes interactive?]
    (let [textPane (new JEditorPane my-html-url)]
        (. textPane setSize 2480 1610)
        (show-text-pane textPane)
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
(defn print-html-by-printer-name [printer-name html-url show-print-dialog? print-attributes interactive?]
    (print-html-by-print-service (first (get-named-printer-service printer-name))
                             html-url
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
;see http://www.cs.mun.ca/java-api-1.5/guide/jps/spec/attributes.fm5.html for more attributes
(defn get-print-request-attributes [page-size x-feed feed]
  (let [attributes (new HashPrintRequestAttributeSet)]
    (if (= page-size MediaSizeName/INVOICE) (. attributes add (. MediaSizeName MediaSizeName/ISO_A4))
           (. attributes add (. MediaSizeName MediaSizeName/ISO_A4)))
    (. attributes add (new PrinterResolution x-feed feed PrinterResolution/DPCM)) 
    attributes
 ))
 
;print to nitro pdf (quiet and less paper :))
(print-html-by-printer-name
    nitro-printer-name 
    my-html-url 
    false 
    (get-print-request-attributes MediaSizeName/ISO_A4 120 100)
    false)
;print to the default local printer
(print-html-by-default-printer 
    my-html-url 
    false 
    (get-print-request-attributes MediaSizeName/ISO_A4 100 100)
    false)
