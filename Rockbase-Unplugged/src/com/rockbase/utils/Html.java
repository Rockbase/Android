package com.rockbase.utils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 22/04/15
 * Time: 0:49
 * To change this template use File | Settings | File Templates.
 */
public class Html {

    public static String parse(String text) {
        Pattern p;
        Matcher m;
        MatchResult mr;
        String[] line = text.split("\n");
        String html = "<style> * { color: white; } </style>";
        for (int i = 0; i < line.length; i++) {
            switch (i) {
                case 0:
                    p = Pattern.compile("'([^']*)' \\(([^\\)]*)\\) ([^.]*.)");
                    m = p.matcher(line[i]);
                    if (m.find()) {
                        mr = m.toMatchResult();
                        html += "<h1><a href=\"" + mr.group(2) + "\">" + mr.group(1) + "</a></h1>";
                    }
                    break;
                case 2:
                    html += "<h3>" + line[i] + "</h3><ul>";
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                    html += "<li>" + line[i] + "</li>";
                    break;
                case 8:
                    html += "</ul><p>" + line[i] + "</p>";
                    break;
                default:
                    p = Pattern.compile("(^.*?)(http[^\\s]*)");
                    m = p.matcher(line[i]);
                    if (m.find()) {
                        mr = m.toMatchResult();
                        html += "<p>" + mr.group(1) + "<a href=\"" + mr.group(2) + "\">" + mr.group(2) + "</a>" + "</p>";
                    } else {
                        html += "<p>" + line[i] + "</p>";
                    }
                    break;
            }
        }

        return html;
    }
}
