package predix.ge.com.referenceapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by jeremyosterhoudt on 10/19/16.
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationConfigurationTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testThePMAppNameIsCorrectlySetUsingProperties() throws IOException {
        //TODO: No way to verify this via the SDK yet since nothing in the SDK is using CoreConfigurationLoader to load the webapp or properties into the config...  Story will be created - JO
        Properties properties = new Properties();
        properties.load(activityRule.getActivity().getAssets().open("config.properties"));
        Assert.assertEquals("Sample1", properties.get("pmapp_name"));
    }

    @Test
    public void testThePMAppVersionIsCorrectlySetUsingProperties() throws IOException {
        //TODO: No way to verify this via the SDK yet since nothing in the SDK is using CoreConfigurationLoader to load the webapp or properties into the config...  Story will be created - JO
        Properties properties = new Properties();
        properties.load(activityRule.getActivity().getAssets().open("config.properties"));
        Assert.assertEquals("1.0", properties.get("pmapp_version"));
    }

}
