package rssminer.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ccil.cowan.tagsoup.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rssminer.jsoup.CompactHtmlVisitor;
import rssminer.jsoup.HtmlUtils;

class Cmp implements Comparator<Entry<String, Integer>> {

    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }

}

class TextHandler3 extends DefaultHandler {
    private int i, end;
    private boolean keep = true;
    private boolean prev = false, current = false;
    private StringBuilder sb = new StringBuilder();

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (keep) {
            end = start + length;
            for (i = start; i < end; ++i) {
                current = Character.isWhitespace(ch[i]);
                if (!prev || !current) {
                    sb.append(ch[i]);
                }
                prev = current;
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        keep = true;
    }

    public String getText() {
        return sb.toString();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (localName.equalsIgnoreCase("script")
                || localName.equalsIgnoreCase("style")) {
            keep = false;
        }
    }
}

public class HtmlParserBench {

    static List<File> files = new ArrayList<File>();
    static Logger logger = LoggerFactory.getLogger(HtmlParserBench.class);
    static List<String> summarys = new ArrayList<String>();

    @BeforeClass
    public static void setup() throws SQLException {
        File folder = new File("/home/feng/Downloads/htmls");
        for (File f : folder.listFiles()) {
            if (f.length() < 1024 * 1024) { // 1M
                files.add(f);
            }
        }
        logger.info("total files: " + files.size());
        Connection con = Utils.getRssminerDB();
        Statement stat = con.createStatement();
        ResultSet rs = stat
                .executeQuery("select summary from feed_data limit 1000");
        while (rs.next()) {
            String str = rs.getString(1);
            if (str != null && !str.isEmpty()) {
                summarys.add(str);
            }
        }
    }

    @Test
    public void testTagsoupSummary() {
        Parser p = new Parser();
        for (String html : summarys) {
            try {
                TextHandler3 handler = new TextHandler3();
                StringReader sr = new StringReader(html);
                p.setContentHandler(handler);
                p.parse(new InputSource(sr));
            } catch (Exception e) {
                logger.error(html, e);
            }
        }
    }

    private String to_html(Document doc) throws URISyntaxException {
        StringBuilder sb = new StringBuilder(256);
        CompactHtmlVisitor vistor = new CompactHtmlVisitor(sb,
                "http://google.com");
        Elements elements = doc.body().children();
        for (Element e : elements) {
            new NodeTraversor(vistor).traverse(e);
        }
        System.out.println(vistor);
        return null;
    }

    @Test
    public void testJsouSummary() throws SQLException, IOException,
            SAXException {

        Connection con = Utils.getRssminerDB();
        Statement stat = con.createStatement();
        ResultSet rs = stat
                .executeQuery("select d.id, d.summary, link from feed_data d join feeds f on f.id = d.id");
        // PreparedStatement ps = con
        // .prepareStatement("update feed_data set jsoup=?, tagsoup=?, compact=? where id = ?");

        PreparedStatement ps = con
                .prepareStatement("update feed_data set compact=? where id = ?");

        int orignalLength = 0;
        int compactLength = 0;
        while (rs.next()) {
            int id = rs.getInt(1);
            String html = rs.getString(2);
            if (html == null || html.isEmpty()) {
                continue;
            }
//            System.out.println("=================" + id
//                    + "=====================");
            String compact = HtmlUtils.compact(html, rs.getString(3));
            orignalLength += html.length();
            compactLength += compact.length();
            try {
                ps.setString(1, compact);
                ps.setInt(2, id);
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        System.out.println(CompactHtmlVisitor.all_attrs);
        Map<String, Integer> attrs = CompactHtmlVisitor.all_attrs;

        ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
                attrs.entrySet());
        Collections.sort(list, new Cmp());
        for (Entry<String, Integer> l : list) {
            // System.out.println(l);
        }
        System.out.println();
        System.out.println("orignal: " + orignalLength);
        System.out.println("compact: " + compactLength);
        System.out.println(compactLength / (double) orignalLength);
    }

    @Test
    public void testTagsoup() {
        Parser p = new Parser();
        for (File f : files) {
            try {
                TextHandler3 handler = new TextHandler3();
                FileInputStream fs = new FileInputStream(f);
                p.setContentHandler(handler);
                p.parse(new InputSource(fs));
                fs.close();
            } catch (Exception e) {
                logger.error(f.toString(), e);
            }
        }
    }

    @Test
    public void testJsoup() {
        for (File f : files) {
            try {
                Document d = Jsoup.parse(f, "utf8");
            } catch (Exception e) {
                logger.error(f.toString(), e);
            }
        }
    }
}