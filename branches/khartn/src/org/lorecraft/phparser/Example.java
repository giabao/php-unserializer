package org.lorecraft.phparser;

/**
 *
 * @author AKhusnutdinov
 */
public class Example {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String example = "a:4:{i:0;a:3:{i:0;i:100010001804;i:1;s:3:\"www\";i:2;s:3:\"eee\";}i:1;a:3:{i:0;s:4:\"qqq1\";i:1;s:4:\"www1\";i:2;s:4:\"eee1\";}i:2;a:3:{i:0;s:4:\"qqq2\";i:1;s:4:\"www2\";i:2;s:4:\"eee2\";}i:3;a:3:{i:0;s:4:\"qqq3\";i:1;s:4:\"www3\";i:2;s:4:\"eee3\";}}";
        SerializedPhpParser serializedPhpParser = new SerializedPhpParser(example);
        Object result = serializedPhpParser.parse();
        System.out.println(result);
         result = serializedPhpParser.parse();
        System.out.println(result);
    }
}
