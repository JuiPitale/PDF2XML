import Models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class HTMLformatter {
    static List<HTMLobject> formatHTMLList(List<HTMLobject> htmlObjectList) {
        List<HTMLobject> formattedHTMLList = new ArrayList<>();
        if(htmlObjectList.size()==0)
        {
            System.out.println("empty pdf");
            System.exit(0);
        }
        formattedHTMLList.add(htmlObjectList.get(0));
        for (int i=1;i<htmlObjectList.size();i++)
        {
            double prevTop = formattedHTMLList.get(formattedHTMLList.size()-1).getTop();
            double curTop = htmlObjectList.get(i).getTop();
            String prevfont = formattedHTMLList.get(formattedHTMLList.size()-1).getFont_family();
            String curfont = htmlObjectList.get(i).getFont_family();
            double prevfontSize = formattedHTMLList.get(formattedHTMLList.size()-1).getFont_size();
            double curfontSize = htmlObjectList.get(i).getFont_size();
            String prevFontWeight = formattedHTMLList.get(formattedHTMLList.size()-1).getFont_weight();
            String curFontWeight = htmlObjectList.get(i).getFont_weight();
            if(prevTop==curTop && curfont.equals(prevfont) && prevfontSize==curfontSize && prevFontWeight.equals(curFontWeight))
            {
                HTMLobject object = formattedHTMLList.get(formattedHTMLList.size()-1);
                object.setWidth(object.getWidth()+htmlObjectList.get(i).getWidth());
                object.setValue(object.getValue()+" "+htmlObjectList.get(i).getValue());
            }
            else
                formattedHTMLList.add(htmlObjectList.get(i));

        }
        return formattedHTMLList;
    }

    public static String generateHTMLFromPDF(PDDocument pdf) throws IOException, ParserConfigurationException {
        Writer output = new StringWriter();
        new PDFDomTree().writeText(pdf,output);
        output.close();
        pdf.close();
        return output.toString();
    }

    public static List<HTMLobject> parseHTML(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        Elements divs = doc.getElementsByClass("p");
        List<HTMLobject> htmlObjectList = new ArrayList<>();
        for(Element div:divs)
        {
            if(div.hasText())
                htmlObjectList.add(htmlObjectCoverter(div));
        }
        return htmlObjectList;
    }

    public static HTMLobject htmlObjectCoverter(Element element)
    {
        HTMLobject object= new HTMLobject(0,0," ",0," "," "," ",0);
        object.setValue(element.text());

        String style = element.attr("style");
        String[] styleAttrs = style.split(";");
        for(String styleAttr : styleAttrs)
        {
            String[] arr = styleAttr.split(":");
            if(arr[0].equals("top"))
            {
                object.setTop(Double.parseDouble(arr[1].substring(0,arr[1].length()-2)));
            }
            else if(arr[0].equals("left"))
            {
                object.setLeft(Double.parseDouble(arr[1].substring(0,arr[1].length()-2)));
            }
            else if(arr[0].equals("font-family"))
            {
                object.setFont_family(arr[1]);
            }
            else if(arr[0].equals("font-size"))
            {
                object.setFont_size(Double.parseDouble(arr[1].substring(0,arr[1].length()-2)));
            }
            else if(arr[0].equals("font-weight"))
            {
                object.setFont_weight(arr[1]);
            }
            else if (arr[0].equals("color"))
            {
                object.setColor(arr[1]);
            }
            else if(arr[0].equals("width"))
            {
                object.setWidth(Double.parseDouble(arr[1].substring(0,arr[1].length()-2)));
            }
        }
        return object;
    }
}
