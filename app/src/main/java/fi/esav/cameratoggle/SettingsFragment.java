package fi.esav.cameratoggle;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by E on 14.6.2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
