(ns htmlPrinter.htmlPrinter
(require: clojure.pprint)
(import java.io.StringReader)
(import java.io.File)
(import java.io.FileInputStream)
(import javax.print.PrintServiceLookup)
(import javax.print.PrintService)
(import javax.print.StreamPrintService)
(import javax.print.attribute.standard.PrinterResolution)
(import javax.print.attribute.HashPrintRequestAttributeSet)
(import javax.print.attribute.HashPrintServiceAttributeSet)
(import javax.print.attribute.HashDocAttributeSet)
(import javax.print.attribute.standard.MediaSizeName)
(import javax.print.attribute.standard.PrinterName)
(import javax.print.attribute.standard.Copies)
(import javax.swing.text.html.HTMLEditorKit)
(import javax.print.DocFlavor)
(import javax.print.DocFlavor$INPUT_STREAM)
(import javax.print.SimpleDoc)
(import javax.swing.JFrame)
(import javax.swing.JEditorPane)
(import java.text.MessageFormat)

)


  (require '[clojure.reflect :as r])
(use '[clojure.pprint :only [print-table]])
;(defn sorted-reflection [_class]
(print-table
  (sort-by :name 
    (filter :exception-types (:members (r/reflect "PrinterJob")))));)

(def nitro-printer-name "Nitro PDF Creator (Reader 3)")
(def canon-printer-name "CanonMX330")
(def my-html-doc "C:\\Users\\Peter\\Documents\\GitHub\\KidzSign-in\\htmlPrinter\\resources\\HTML\\index.html")
(def my-html-url "file:///C:/Users/Peter/Documents/GitHub/KidzSign-in/htmlPrinter/resources/HTML/index.html")
(def myHtml (slurp my-html-doc))
;(println myHtml)

(defn create-html-doc-from-string [htmlStr] 
    (let [stringReader (new StringReader htmlStr)
          htmlKit (new HTMLEditorKit)
          myHtmlDoc (. htmlKit createDefaultDocument)]
    (. htmlKit read stringReader myHtmlDoc 0)
    myHtmlDoc)
)


(defn show-text-pane [textPane]
  (def temp-frame (new JFrame))
  ;(. textPane setWidth 130)
  ;(. textPane setSize 130 130)
  ;(. temp-frame setHeight 130)
  (. temp-frame setSize (. textPane getWidth) (. textPane getHeight))
  (. temp-frame add textPane)
  (. temp-frame setVisible true)
  )

;print from html url
(defn print-html-url-by-print-service [printer-service html-url show-print-dialog? print-attributes interactive?]
    (let [textPane (new JEditorPane my-html-url)]
        (. textPane setSize 2480 1610)
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

;(.createPrintJob (get-named-printer-service nitro-printer-name) )

;grab a list of PrinterServices by name
(defn get-named-printer-service [printer-name] (let [
        selected-printer-name (new PrinterName printer-name nil)
        printer-attributes (new HashPrintServiceAttributeSet)    ]
    (. printer-attributes add selected-printer-name)
    (. PrintServiceLookup lookupPrintServices nil printer-attributes))
)

;see http://www.cs.mun.ca/java-api-1.5/guide/jps/spec/attributes.fm5.html for more attributes
(defn get-print-request-attributes [page-size]
  (let [attributes (new HashPrintRequestAttributeSet)]
    (if (= page-size MediaSizeName/INVOICE) (. attributes add (. MediaSizeName MediaSizeName/ISO_A4))
           (. attributes add (. MediaSizeName MediaSizeName/ISO_A4)))
    ;(. attributes add (new PrinterResolution x-feed feed PrinterResolution/DPCM)) 
    (. attributes add (new Copies 1))
    attributes
 ))

;print from html string
(defn print-file-by-print-service [printer-service html-doc print-attributes]
     (let [job (. printer-service createPrintJob) file-input-str (new FileInputStream html-doc)]
       (. job        
         print
         (new SimpleDoc file-input-str  
             ;DocFlavor$INPUT_STREAM/AUTOSENSE 
             DocFlavor$INPUT_STREAM/TEXT_HTML_US_ASCII
             (new HashDocAttributeSet))
         print-attributes)
         (. file-input-str close )
     )
)
(loop [x 10]
  (when (> x 1)
    (println x)
    (recur (- x 2))))



(defn get-flavors-supported-by-printer-service [print-service]
  (let [flavors (. print-service getSupportedDocFlavors)]
    (clojure.pprint/pprint flavors)
  )
)
#_
(get-flavors-supported-by-printer-service (first (get-named-printer-service nitro-printer-name)))

#_
(get-flavors-supported-by-printer-service (first (get-named-printer-service canon-printer-name)))

#_ 
(print-file-by-print-service
   (first (get-named-printer-service nitro-printer-name)) 
    my-html-doc 
    (get-print-request-attributes MediaSizeName/ISO_A4))

;This might be the way to go long term
;http://stackoverflow.com/questions/12141155/printing-an-html-file-in-java
                  ;DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                  ;FileInputStream fis = new FileInputStream(doc);
                  ;DocAttributeSet das = new HashDocAttributeSet();
                  ;Doc document = new SimpleDoc(fis, flavor, das);
                  ;job.print(document, pras);


;Use the printer name to automatically select the printer to use
(defn print-html-by-printer-name [printer-name html-url show-print-dialog? print-attributes interactive?]
    (print-html-url-by-print-service (first (get-named-printer-service printer-name))
                             html-url
                             show-print-dialog?
                             print-attributes
                             interactive?)
)
;Use the default printer
(defn print-html-by-default-printer [htmlString show-print-dialog? print-attributes interactive?]
    (print-html-url-by-print-service (. PrintServiceLookup lookupDefaultPrintService)
                             htmlString
                             show-print-dialog?
                             print-attributes
                             interactive?)
)
#_ 
;print to nitro pdf (quiet and less paper :))
(print-html-by-printer-name
  nitro-printer-name
  ;canon-printer-name
    my-html-url 
    false 
    (get-print-request-attributes MediaSizeName/ISO_A4)
    false)
#_
;print to the default local printer
(print-html-by-default-printer 
    my-html-url 
    false 
    (get-print-request-attributes MediaSizeName/ISO_A4)
    false)

;(get-flavors-supported-by-printer-service (first (get-named-printer-service nitro-printer-name)))
; #<BYTE_ARRAY image/gif; class="[B">,
; #<INPUT_STREAM image/gif; class="java.io.InputStream">,
; #<URL image/gif; class="java.net.URL">,
; #<BYTE_ARRAY image/jpeg; class="[B">,
; #<INPUT_STREAM image/jpeg; class="java.io.InputStream">,
; #<URL image/jpeg; class="java.net.URL">,
; #<BYTE_ARRAY image/png; class="[B">,
; #<INPUT_STREAM image/png; class="java.io.InputStream">,
; #<URL image/png; class="java.net.URL">,
; #<SERVICE_FORMATTED application/x-java-jvm-local-objectref; class="java.awt.print.Pageable">,
; #<SERVICE_FORMATTED application/x-java-jvm-local-objectref; class="java.awt.print.Printable">,
; #<BYTE_ARRAY application/octet-stream; class="[B">,
; #<URL application/octet-stream; class="java.net.URL">,
; #<INPUT_STREAM application/octet-stream; class="java.io.InputStream">
;(get-flavors-supported-by-printer-service (first (get-named-printer-service canon-printer-name)))
; #<BYTE_ARRAY image/gif; class="[B">,
; #<INPUT_STREAM image/gif; class="java.io.InputStream">,
; #<URL image/gif; class="java.net.URL">,
; #<BYTE_ARRAY image/jpeg; class="[B">,
; #<INPUT_STREAM image/jpeg; class="java.io.InputStream">,
; #<URL image/jpeg; class="java.net.URL">,
; #<BYTE_ARRAY image/png; class="[B">,
; #<INPUT_STREAM image/png; class="java.io.InputStream">,
; #<URL image/png; class="java.net.URL">,
; #<SERVICE_FORMATTED application/x-java-jvm-local-objectref; class="java.awt.print.Pageable">,
; #<SERVICE_FORMATTED application/x-java-jvm-local-objectref; class="java.awt.print.Printable">,
; #<BYTE_ARRAY application/octet-stream; class="[B">,
; #<URL application/octet-stream; class="java.net.URL">,
; #<INPUT_STREAM application/octet-stream; class="java.io.InputStream">
