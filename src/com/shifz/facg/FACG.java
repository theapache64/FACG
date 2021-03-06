package com.shifz.facg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Singleton class to generate FontAwesomeCheatcodes
 * FACG = Font Awesome Cheatsheet Generator
 *
 * @author Shifar Shifz
 */
public class FACG {

    private static final String CHEATSEET_URL = "http://fontawesome.io/cheatsheet/",
            STRING_RESOURCE_FORMAT = "\t\t<string name=\"%s\">%s</string>\n",
            XML_RESOURCE_FORMAT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\t<resources>\n %s </resources>",
            MAIN_DELIMETER = "<div class=\"col-md-4 col-sm-6 col-lg-3\">",
            NODES_DELIMETER = "<i class=\"fa fa-fw\" aria-hidden=\"true\" >",
            KEY_VALUE_DELIMETER = "</i>",
            FINAL_VALUE_DELIMETER = "<span class=\"muted\">",
            FINAL_VALUE_DELIMETER_2 = " <span class=\"text-muted\">",

    ENUM_FORMAT = "\n\t%s(%s),",
            ENUM_CLASS_FORMAT = "public enum FaIcon {" +

                    "\n%s" +

                    "\n\n\tpublic int cheatCode;" +

                    "\n\n\tprivate FaIcon(int cheatCode){" +
                    "\n\t\tthis.cheatCode = cheatCode;" +
                    "\n\t}" +
                    "\n}",

    XML_ENUM_FORMAT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "\n<resources>" +
            "\n\t<declare-styleable name=\"FaTextView\">" +
            "\n\t\t<attr name=\"iconCheat\" format=\"enum\">" +
            "\t\t\t%s" +
            "\n\t\t</attr>" +
            "\n\t</declare-styleable>" +
            "\n</resources>",
            XML_ENUM_ROW_FORMAT = "\n\t\t\t<enum name=\"%s\" value=\"%s\" />";


    //Static instance
    private static FACG instance;

    private static String html;
    private static Map<String, String> cheatHash;
    private StringBuilder stringBuilder = new StringBuilder();

    //Securing exterior object creation
    private FACG() {
    }

    ;

    //Getting instance of FACG
    public static FACG getInstance() throws Exception {

        if (instance == null) {
            instance = new FACG();
        }

        if (html == null) {
            //html = new HtmlGrabber(CHEATSEET_URL).getHtml(false);
            html = getHardCodedHtml();
        }

        if (cheatHash == null) {
            cheatHash = getCheatHash();
        }

        return instance;
    }

    public static String getHardCodedHtml() {
        final StringBuilder html = new StringBuilder();
        try {
            final BufferedReader br = new BufferedReader(new FileReader("html.html"));
            String line = null;
            while ((line = br.readLine()) != null) {
                html.append(line).append("\n");
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return html.toString();
    }

    //Generating cheatcodes
    public String getXmlString2() throws Exception {

        stringBuilder.delete(0, stringBuilder.length());

        //Creating each string resource
        for (Entry<String, String> entry : cheatHash.entrySet()) {
            stringBuilder.append(String.format(STRING_RESOURCE_FORMAT, entry.getKey().replaceAll("-", "_"), entry.getKey()));
        }

        //returning final xml
        return String.format(XML_RESOURCE_FORMAT, stringBuilder.toString());
    }

    private static Map<String, String> getCheatHash() {

        Map<String, String> cheatHash = new LinkedHashMap<>();

        html = html.replaceAll("\\s{2,}", " ");

        //Generating key-value pair from html

        String[] nodes = html.split(MAIN_DELIMETER);
        System.out.println((nodes.length - 1) + " icons found");


        for (int i = 1; i < nodes.length; i++) {
            System.out.println(nodes[i]);
            String o12 = nodes[i].split(NODES_DELIMETER)[1];
            String value = o12.split(KEY_VALUE_DELIMETER)[0] + ";";
            String key = o12.split(KEY_VALUE_DELIMETER)[1];
            String finalDel = key.contains(FINAL_VALUE_DELIMETER_2) ? FINAL_VALUE_DELIMETER_2 : FINAL_VALUE_DELIMETER;
            key = key.split(finalDel)[0];
            key = key.replace(" ", "");
            cheatHash.put(key, value);
        }

        return cheatHash;
    }


    //Return Enum Class
    public String getJavaEnum() throws Exception {

        stringBuilder.delete(0, stringBuilder.length());

        for (Entry<String, String> entry : cheatHash.entrySet()) {
            String key = entry.getKey().replaceAll("-", "_");
            stringBuilder.append(String.format(ENUM_FORMAT, key.toUpperCase(), "R.string." + key.toLowerCase()));
        }

        String finalEnumClass = stringBuilder.toString();

        int lastCommanAt = finalEnumClass.lastIndexOf(",");
        finalEnumClass = finalEnumClass.substring(0, lastCommanAt).concat(";");

        return String.format(ENUM_CLASS_FORMAT, finalEnumClass);
    }


    //Return XML Enum
    public String getXmlEnum() throws Exception {

        stringBuilder.delete(0, stringBuilder.length());

        for (Entry<String, String> entry : cheatHash.entrySet()) {
            stringBuilder.append(String.format(XML_ENUM_ROW_FORMAT, entry.getKey(), entry.getValue()));
        }

        return String.format(XML_ENUM_FORMAT, stringBuilder.toString());
    }

}
