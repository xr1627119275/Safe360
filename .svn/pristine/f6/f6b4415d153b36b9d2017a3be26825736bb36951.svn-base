package com.xr.safe360;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xr.safe360.db.dao.BlackNumberDao;
import com.xr.safe360.db.domain.BlackNumberInfo;
import com.xr.safe360.utils.ToastUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);
        for (int i=0;i<100;i++){
            dao.insert(i+"",1+new Random().nextInt(3)+"");
        }
//dao.delete("110");
//        dao.update("110","2");
//        List<BlackNumberInfo> all = dao.findAll();
//        System.out.println(all.get(1).phone);
//        assertEquals("com.xr.safe360", appContext.getPackageName());

    }
}
