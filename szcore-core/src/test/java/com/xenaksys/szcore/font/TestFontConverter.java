package com.xenaksys.szcore.font;

import com.xenaksys.szcore.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestFontConverter {

    @Test
    public void testWholeNoteUnicode(){
        int c = FontLoader.getIntCodePoint("1d158");
        Assert.assertEquals(119128, c);
    }


    @Test
    public void testWriteFile(){
        FileUtil.writeToFile("This is test string asdf asdflk ja sdf  \nwith new line", "/Volumes/DataDrive/Music/phd/svg/test.txt");
    }
}
