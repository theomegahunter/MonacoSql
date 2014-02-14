package org.malibu.monacosql.util.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SqlSyntaxUtil {
	// needs to be modified to be able to handle comments inside quotes
	public static String removeCommentsFromSql(String sql) {
		if(sql == null) return null;
		StringBuilder buffer = new StringBuilder();
		try {
		    Pattern multilineCommentPattern = Pattern.compile("(?:/\\*[^;]*?\\*/)", Pattern.DOTALL | Pattern.MULTILINE);
		    Pattern doubleDashCommentPattern = Pattern.compile("[^'\"]*(?:'[^']*'\"[^\"]*\")*(?:(?!--).)*(--[^\n]*)*$");
		    Matcher regexMatcher = multilineCommentPattern.matcher(sql);
		    int previousEnd = 0;
		    while (regexMatcher.find()) {
		    	buffer.append(sql.substring(previousEnd, regexMatcher.start()));
		    	previousEnd = regexMatcher.end();
		    }
		    sql = buffer.toString();
		    
		    buffer = new StringBuilder();
		    regexMatcher = doubleDashCommentPattern.matcher(sql);
		    previousEnd = 0;
		    while (regexMatcher.find()) {
		    	buffer.append(sql.substring(previousEnd, regexMatcher.start()));
		    	previousEnd = regexMatcher.end();
		    }
		} catch (PatternSyntaxException ex) { /* shouldn't ever happen */ }
		return buffer.toString();
	}
	
	public static void main(String[] args) {
//		Pattern doubleDashCommentPattern = Pattern.compile("[^'\"]*(?:'[^']*'\"[^\"]*\")*(?:(?!--).)*(--[^\n]*)*$");
		Pattern doubleDashCommentPattern = Pattern.compile("[^'\"]*(?:'[^']*'|\"[^\"]*\")*(?:(?!--).)*(--.*)");
		Matcher regexMatcher = doubleDashCommentPattern.matcher("SELECT *\nFROM STUFF.BLAH B WHERE B.X = 'dinnar'; -- lol fag\n--more noobs\n'--dont hit me!'");
		while (regexMatcher.find()) {
			for(int i = 1; i <= regexMatcher.groupCount(); i++) {
				System.out.println(regexMatcher.group(i));
			}
		}
	}
}
